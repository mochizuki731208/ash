package com.zp.controller.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.core.JFinal;
import com.jfinal.upload.UploadFile;
import com.web.processor.AbOrderProcessor;
import com.web.processor.AbOrderProcessorImpl;
import com.web.util.DataElement;
import com.zp.tools.ImageScale;
import com.zp.tools.ImageUtils;
import com.zp.tools.StringUtil;
import com.zp.tools.Setting.WatermarkPosition;

public class RestCommonController extends AbstractRestController {

	public void uploadResource(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String filename = null;
		UploadFile f = null;
		
		try {
			f = this.getFile();
			filename = StringUtil.getUuid32()+StringUtil.getExt(f.getFileName());
			f.getFile().renameTo(new File(f.getSaveDirectory()+ filename));
			//对文件进行缩略图生成
			ImageScale is = new ImageScale();
			try {
				is.saveImageAsJpg(f.getSaveDirectory()+ filename, f.getSaveDirectory()+ StringUtil.getThumbnail(filename), 800, 600);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				File parent= new File(f.getSaveDirectory()+"sy");
				if(!parent.exists()){
					parent.mkdirs();
				}
				System.out.println(JFinal.me().getServletContext()
						.getRealPath("/")+"/qrcode/yhl.png");
				ImageUtils.addWatermark(new File(f.getSaveDirectory()+ filename), new File(f.getSaveDirectory()+"sy/"+ filename), new File(JFinal.me().getServletContext()
						.getRealPath("/")+"/qrcode/yhl.png"), WatermarkPosition.center, 50);
			} catch (Exception e) {
				e.printStackTrace();
			}
			resultMap.put("msg", "资源上传成功！");
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("filename", filename);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("result", RESULT_FAIL);
			resultMap.put("msg", "资源上传失败！");
		}finally{
			if(f != null){
				(new File(f.getSaveDirectory()+f.getOriginalFileName())).delete();
			}
		}
		renderJson(resultMap);
	}
//	http://127.0.0.1/pro/rest/common/uploadClassResource
	public void uploadClassResource(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
//		String filename = null;
		UploadFile f = null;
		FileInputStream input = null;
		FileOutputStream output = null;
		try {
			f = this.getFile();
//			filename = StringUtil.getUuid32()+StringUtil.getExt(f.getFileName());
//			f.getFile().renameTo(new File(f.getSaveDirectory()+ filename));
			//对文件进行缩略图生成
//			File fi = new File("C:/" + f.getFileName());
//			if(fi.exists()){
//				fi.delete();
//			}
//			fi.createNewFile();
//			input = new FileInputStream(f.getFile().getAbsolutePath());// 可替换为任何路径何和文件名
//			output = new FileOutputStream(fi);// 可替换为任何路径何和文件名
//			int in = input.read();
//			while (in != -1) {
//				output.write(in);
//				in = input.read();
//			}
			resultMap.put("msg", "资源上传成功！");
			resultMap.put("result", RESULT_SUCCESS);
//			resultMap.put("filename", filename);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("result", RESULT_FAIL);
			resultMap.put("msg", "资源上传失败！");
		}finally{
			try {
				if(output != null){
					output.flush();
					output.close();
					output = null;
				}
				if(input != null){
					input.close();
					input = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		}
		renderJson(resultMap);
	}
	
	
	public void getDataAddr() throws IOException, ClassNotFoundException{
		HttpServletRequest request=getRequest();
		HttpServletResponse response=getResponse();
		long start = System.currentTimeMillis();
	    String reqAddr = request.getRemoteAddr();
	    System.out.println("requst from :"+reqAddr);
	    AbOrderProcessor processor = new AbOrderProcessorImpl();
	    processor.process(reqAddr, request.getInputStream(), response.getOutputStream());
	    System.out.println("AbOrderService execution costs " + (System.currentTimeMillis() - start) + "ms");
		
		
	}
	
}
