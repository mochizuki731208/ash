package com.zp.controller.rest;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jfinal.core.Controller;

public abstract class AbstractRestController extends Controller {

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
		boolean isValid = true;
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				if (params[i] == null || StringUtils.isEmpty(params[i].toString())) {// 如果参数为空，那么校验不通过，中断循环返回
					isValid = false;
					break;
				}
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

}
