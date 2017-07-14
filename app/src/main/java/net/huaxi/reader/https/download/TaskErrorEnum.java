package net.huaxi.reader.https.download;

/**
 * 客户端提示的错误码
 * @author taoyingfeng
 *
 */
public enum TaskErrorEnum {
	/**
	 * 错误编码格式:错误编码有4位长度的整数组成，前两位是模块，后2位是具体的错误定位;
	 * 错误值：code为负值代表错误，code为正值代表正常操作.
	 */
	//网络错误以-30***为例；
	TASK_NETWORK_UNAVAILABLE(-3001,"网络不可连接"),
	TASK_NETWORK_CONNECTIONTIMEOUT(-3002,"网络连接超时"),
	TASK_NETWORK_PARSE_URL_FAILED(-3003,"解析URL失败"),
	TASK_NETWORK_NUKONWN(-3004,"未知错误"),
	TASK_NETWORK_FILE_NOT_FOUNDED(-3005,"网络文件找不到"),
	TASK_NETWORK_LOCAL_FILE_DELETED(-3006,"本地文件已被删除"),

	TASK_HAS_ADDED(2,"任务已经存在"),
	TASK_USER_CANCEL(1,"用户取消"),
	TASK_SUCCESS(0,"操作成功");
	
	private int code;
	private String msg;
	
	private TaskErrorEnum(int code,String msg){
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	

	@Override
	public String toString() {
		return "XsError code = "+this.code +" msg ="+this.msg;
	}
	
	
}
