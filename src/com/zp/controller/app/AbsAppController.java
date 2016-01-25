package com.zp.controller.app;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;

public abstract class AbsAppController extends Controller {

	public static final String BASE_URL = "";
	
	/** 调用成功 */
	protected static final int RESULT_SUCCESS = 0;
	/** 调用失败 */
	protected static final int RESULT_FAIL = -1;

	/** 参数非法 */
	protected static final String RESULT_INVALID_PARAM = "参数非法，请重新确认！";
	/** 调用失败，包含内部处理出错 */
	protected static final String RESULT_INVOKE_FAIL = "调用失败，请重新刷新！";

	/**
	 * 校验参数是否合法
	 * 
	 * @return
	 */
	protected boolean checkParam(Object... params) {
		System.out.println("checkParam    ");
		boolean isValid = true;
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				if (params[i] == null || StringUtils.isEmpty(params[i].toString())) {// 如果参数为空，那么校验不通过，中断循环返回
					isValid = false;
					break;
				}
				System.out.println("    "+params[i]);
			}
		}

		return isValid;
	}

	/**
	 * 返回调用失败信息
	 * 
	 * @param e
	 */
	protected void formatInvokeFailResponse(Exception e, Map<String, Object> resultMap) {
		e.printStackTrace();
		resultMap.put("result", RESULT_FAIL);
		resultMap.put("msg", RESULT_INVOKE_FAIL);
	}
	
	/**
	 * 返回参数校验不通过
	 * 
	 * @param paramMap
	 */
	protected void formatInvalidParamResponse(Map<String, Object> resultMap) {
		resultMap.put("result", RESULT_FAIL);
		resultMap.put("msg", RESULT_INVALID_PARAM);
	}

	/**
	 * 
	 * @param paramMap
	 */
	protected void formatSuccessResponse(Map<String, Object> resultMap) {
		resultMap.put("result", RESULT_SUCCESS);
		resultMap.put("msg", "success");
	}
	
	/**
	 * 
	 * @param paramMap
	 */
	protected void formatErrorMsgResponse(String msg,Map<String, Object> resultMap) {
		resultMap.put("result", RESULT_FAIL);
		resultMap.put("msg", msg);
	}
	
	/**
	 * 获取开始的记录下标
	 * 
	 * @param pi
	 *            页码
	 * @param ps
	 *            每页的记录数
	 * @return
	 */
	protected int getStart(int pi, int ps) {
		return (pi - 1) * ps;
	}

	
	/**
	 * 获取应用的基础url
	 * @return
	 */
	protected String getBaseUrl(){
		String url = getRequest().getRequestURL().toString();
		String path = getRequest().getServletPath().toString();
		String basePath = url.replace(path, "");
		return basePath;
		
	}
	
	/**
	 * 检查文件是否在upload里面
	 * @param filename
	 * @return
	 */
	protected boolean checkImageExists(String filename){
		System.out.println(JFinal.me().getServletContext().getRealPath("/"));
		String realPath = JFinal.me().getServletContext().getRealPath("/");
		File f = new File(realPath+"/upload/"+filename);
		if(!f.exists()){
			System.out.println("file not exists ");
			return false;
		}else{
			return true;
		}
	}
}
