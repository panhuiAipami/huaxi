package net.huaxi.reader.https.download;

import net.huaxi.reader.https.download.listener.ITaskStateChangeListener;


public class TaskManagerDelegate {
	
	
	/**
	 * 启动下载任务
	 */
	public static int startTask(Task task){
		try {
			return TaskManager.getInstance().startTask(task);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	
	/**
	 * 停止下载任务
	 * @param task
	 * @return
	 */
	public static int stopTask(Task task){
		return TaskManager.getInstance().removeTask(task);
	}
	
	/**
	 * 注册任务的监听回调接口
	 * @param task
	 * @param listener
	 */
	public static void registerStateChangeListener(Task task,ITaskStateChangeListener listener){
		TaskManager.getInstance().registerStateChangeListener(task, listener);
	}
	
	/**
	 * 查找任务
	 * @param Url
	 * @return
	 */
	public static Task findTask(String url){
		return TaskManager.getInstance().findTask(url);
	}
	
	/**
	 * 设置线程池最大数;
	 * @param max
	 */
	public static void setConcurrentTask(int max){
		TaskManager.getInstance().setConcurrentTasks(max);
	}
	
}
