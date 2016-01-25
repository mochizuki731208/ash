package com.zp.tools;

/**
 * @author Lijinwei
 * @desc 异常处理
 * @version 1.0
 */
@SuppressWarnings("serial")
public class MisException extends RuntimeException {

	public MisException() {
		super();
	}

	public MisException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public MisException(String arg0) {
		super(arg0);
	}

	public MisException(Throwable arg0) {
		super(arg0);
	}

}
