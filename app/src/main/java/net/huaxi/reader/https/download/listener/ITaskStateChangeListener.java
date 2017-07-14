package net.huaxi.reader.https.download.listener;

import net.huaxi.reader.https.download.Task;

/**
 * 任务中心回调接口.
 * @author taoyingfeng
 *
 */
public interface ITaskStateChangeListener {
	
	public void onStateChanged(Task task);
	
}
