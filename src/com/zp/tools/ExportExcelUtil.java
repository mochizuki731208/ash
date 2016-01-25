package com.zp.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;

/**
 * 执行导出文件的操作
 * 
 * @author GengSan
 * 
 */
@SuppressWarnings("deprecation")
public class ExportExcelUtil {
	private HSSFWorkbook wb = null;
	private HSSFSheet sheet = null;
	
	public static void main(String[] args) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		ExportExcelUtil wwu = new ExportExcelUtil(wb,sheet);
		wwu.createNormalHead("订单汇总",20);//首行
		wwu.createNormalTwoRow(new String[]{"2015年10月1日","2016年12月4日"}, 20);//第二行
		wwu.createColumHeader(new String[]{"订单编号","商家","总金额"});//表头
		HSSFRow row = sheet.createRow(3);
		wwu.createCell(wb, row, 0, (short)1, "哈哈");
		String name = "订单明细.xls";
		String path = wwu.getClass().getResource("/").toString().replace("WEB-INF/classes/", "").replace("file:/", "");
		wwu.outputExcel(path + "admin/order/" + name);//生成文件
		System.out.println(path + "admin/order/" + name);
	}
	
	public static boolean doExportToExcel(String path,String sql,int curPage,String starttime,String endtime,String tableName){
		List<Record> columns = Db.find("SELECT COLUMN_NAME,COLUMN_COMMENT FROM information_schema.COLUMNS WHERE table_name = '" + tableName +"'  AND table_schema = 'ash'");
		String[] headerArr = new String[columns.size()];
		String[] headerArrMsg = new String[columns.size()];
		for(int i = 0; i < columns.size(); i++){
			headerArr[i] = columns.get(i).getStr("COLUMN_NAME");
			headerArrMsg[i] = StringUtil.isNull(columns.get(i).getStr("COLUMN_COMMENT")) ? columns.get(i).getStr("COLUMN_NAME") : columns.get(i).getStr("COLUMN_COMMENT");
		}
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		ExportExcelUtil wwu = new ExportExcelUtil(wb,sheet);
		wwu.createNormalHead("订单详情",columns.size());//首行
		wwu.createNormalTwoRow(new String[]{starttime,endtime}, columns.size());//第二行
		wwu.createColumHeader(headerArrMsg);//表头
		int pageSize = Db.queryLong("select count(1) as totalcount from (" +sql + ") a").intValue();
		PageUtil recordList = DbPage.paginate(curPage, pageSize + 1,sql);
		List<Record> list =  recordList.getList();
		for(int i = 0; i < list.size(); i++){
			HSSFRow row = sheet.createRow(i + 3);
			Record temp = list.get(i);
			Map map = temp.getColumns();
			for(int j = 0; j < (headerArr.length - 1); j++){
				wwu.createCell(wb, row, j, (short)1, String.valueOf(map.get(headerArr[j])));
			}
		}
		wwu.outputExcel(path);//生成文件
		return true;
	}
	
	public ExportExcelUtil(HSSFWorkbook wb,HSSFSheet sheet){
		this.wb = wb;
		this.sheet = sheet;
	}
	/**创建通用的excel头部:头部显示的字符，该报表的列数**/
	public void createNormalHead(String headString,int colSum){
		HSSFRow row = sheet.createRow(0);
		row.setHeight((short) 1000);  
		//设置第一行
		HSSFCell cell = row.createCell(0);
		cell.setCellType(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue(new HSSFRichTextString(headString));
		sheet.addMergedRegion(new Region(0,(short)0,0,(short)colSum));
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cellStyle.setWrapText(true);// 指定单元格自动换行 
		HSSFFont font = wb.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  
		font.setFontName("宋体");  
		font.setFontHeight((short) 600);  
		cellStyle.setFont(font);  
		cell.setCellStyle(cellStyle);
	}
	/**创建通用报表第二行**/
	public void createNormalTwoRow(String []params, int colSum){
		HSSFRow row1 = sheet.createRow(1);
		row1.setHeight((short)400);
		HSSFCell cell2  = row1.createCell(0);
		cell2.setCellType(HSSFCell.ENCODING_UTF_16);
		cell2.setCellValue(new HSSFRichTextString("时间：" + params[0] + "至" + params[1]));
		sheet.addMergedRegion(new Region(1,(short)0,1,(short)colSum));
		HSSFCellStyle cellStyle  = wb.createCellStyle();
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cellStyle.setWrapText(true);
		HSSFFont font = wb.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setFontName("宋体");
		font.setFontHeight((short) 250);  
		cellStyle.setFont(font);  
		cell2.setCellStyle(cellStyle);  
	}
	
	/**设置报表标题**/
	public void createColumHeader(String[] columHeader){
		HSSFRow row2 = sheet.createRow(2);  
		row2.setHeight((short) 600);  
		HSSFCellStyle cellStyle = wb.createCellStyle();  
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 指定单元格居中对齐 
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 指定单元格垂直居中对齐  
		cellStyle.setWrapText(true);// 指定单元格自动换行 
		HSSFFont font = wb.createFont();  
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  
		font.setFontName("宋体");  
		font.setFontHeight((short) 250);  
		cellStyle.setFont(font);  
		cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);  
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
		HSSFCell cell3 = null;  
		for (int i = 0; i < columHeader.length; i++) {  
			cell3 = row2.createCell(i); 
			cell3.setCellType(HSSFCell.ENCODING_UTF_16);  
			cell3.setCellStyle(cellStyle);  
			cell3.setCellValue(new HSSFRichTextString(columHeader[i]));  
		}
	}
	/**创建单元格**/
	public void createCell(HSSFWorkbook wb, HSSFRow row, int col, short align, String val){
		HSSFCell cell = row.createCell(col);  
		cell.setCellType(HSSFCell.ENCODING_UTF_16);  
		cell.setCellValue(new HSSFRichTextString(val));  
		HSSFCellStyle cellstyle = wb.createCellStyle();  
		cellstyle.setAlignment(align);  
		cell.setCellStyle(cellstyle);  
	}
	/**创建合计行**/
	public void createLastSumRow(int colSum, String[] cellValue){
		HSSFCellStyle cellStyle = wb.createCellStyle();  
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 指定单元格居中对齐
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 指定单元格垂直居中对齐  
		cellStyle.setWrapText(true);// 指定单元格自动换行  
		HSSFFont font = wb.createFont();  
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  
		font.setFontName("宋体");  
		font.setFontHeight((short) 250);  
		cellStyle.setFont(font);  
		HSSFRow lastRow = sheet.createRow((short) (sheet.getLastRowNum() + 1));  
		HSSFCell sumCell = lastRow.createCell(0);  
		sumCell.setCellValue(new HSSFRichTextString("合计"));  
		sumCell.setCellStyle(cellStyle);  
		sheet.addMergedRegion(new Region(sheet.getLastRowNum(), (short) 0,  
			 sheet.getLastRowNum(), (short) colSum));// 指定合并区域  
		for (int i = 2; i < (cellValue.length ); i++) {  
			 sumCell = lastRow.createCell(i);  
			 sumCell.setCellStyle(cellStyle);  
			 sumCell.setCellValue(new HSSFRichTextString(cellValue[i - 2])); 
		}
	}
	public void outputExcel(String fileName){
		 FileOutputStream fos = null;  
		 try {
			fos = new FileOutputStream(new File(fileName));
			wb.write(fos);  
			fos.close(); 
		 } catch (FileNotFoundException e) {
			e.printStackTrace();
		 } catch (IOException e) {  
			e.printStackTrace();  
		 }
	}
	
	public HSSFWorkbook getWb() {
		return wb;
	}
	public void setWb(HSSFWorkbook wb) {
		this.wb = wb;
	}
	public HSSFSheet getSheet() {
		return sheet;
	}
	public void setSheet(HSSFSheet sheet) {
		this.sheet = sheet;
	}
}
