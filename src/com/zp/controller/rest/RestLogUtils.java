package com.zp.controller.rest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

public class RestLogUtils {
	public static final String LOG_FILE_PATH = "C:/logs/pay.txt";
	
	public static File checkAndCreateFile(String filePath) throws IOException {
		File file = new File(filePath);
		File parentFile = file.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}

	/**
	 * 写入数据到本地文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @param fileContext
	 *            写入内容
	 * @param isAdd
	 *            是否追加
	 * @时间: 2014-4-15 上午8:59:18
	 * @版本: V1.0.0.0
	 */
	public static void writeToLocal(String fileContext, boolean isAdd) {
		if (fileContext == null) {
			return;
		}
		OutputStreamWriter osw = null;
		FileOutputStream fos = null;
		BufferedWriter bw = null;
		try {
			checkAndCreateFile(LOG_FILE_PATH);
			fos = new FileOutputStream(LOG_FILE_PATH, isAdd);
			osw = new OutputStreamWriter(fos, "utf-8");// 处理乱码
			bw = new BufferedWriter(osw);
			bw.write("\r\n");
			bw.write(new Date().toLocaleString() + "\r\n");
			bw.write(fileContext + "\r\n");
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.flush();
					bw.close();
					bw = null;
				}
				if (osw != null) {
					osw.close();
					osw = null;
				}
				if (fos != null) {
					fos.close();
					fos = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * 输出完整的错误堆栈
	 * @param e
	 * @return
	 */
	public static String getStackTrace(Throwable ex) {
		if ( ex == null) {return "";}      
		Writer writer = null;
		String result = "";
		PrintWriter printWriter = null;
		try {
			writer = new StringWriter();
			printWriter = new PrintWriter(writer);
			ex.printStackTrace(printWriter);
			Throwable cause = ex.getCause();
			while (cause != null) {
				cause.printStackTrace(printWriter);
				cause = cause.getCause();
			}
			result = writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if (printWriter != null) {
					printWriter.close();
					printWriter = null;
				}
				if (writer != null) {
					writer.close();
					writer = null;
				}
			} catch (IOException e) {
				 e.printStackTrace();
			}
		}
		return result;
	}
	
}
