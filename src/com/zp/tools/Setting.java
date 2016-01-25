package com.zp.tools;

import java.io.Serializable;

public class Setting implements Serializable {

	private static final long serialVersionUID = -1478999889661796840L;

	/**
	 * 水印位置
	 */
	public enum WatermarkPosition {

		/** 无 */
		no,

		/** 左上 */
		topLeft,

		/** 右上 */
		topRight,

		/** 居中 */
		center,

		/** 左下 */
		bottomLeft,

		/** 右下 */
		bottomRight
	}


}