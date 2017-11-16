package net.huaxi.reader.callback;

public interface HttpActionHandle {
	//请求动作开始调用
//	public void handleActionStart(String actionName, Object object);
	//请求动作结束调用
//	public void handleActionFinish(String actionName, Object object);
	//请求动作错误调用
	public void handleActionError(String actionName, Object object);
	//请求动作成功时调用
	public void handleActionSuccess(String actionName, Object object);

}
