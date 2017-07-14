package com.tools.commonlibs.common;

/**
 * 当前网络类型
 * 
 */
public enum NetType {
	TYPE_WIFI("WIFI网络"), //
	TYPE_2G("2G手机网络"), //
	TYPE_3G_OR_OTHERS("3G或其它手机网络"), //
	TYPE_UNKNOWN("未知网络"), //
	TYPE_NONE("无可用网络"), //
	;

	private String desc;// 网络连接描述

	private NetType(String desc) {
		this.desc = desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

}
