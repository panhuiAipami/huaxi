package net.huaxi.reader.https.download;


import android.util.Log;

public class TaskDispatcher implements Runnable {

	TaskManager manager;
	volatile boolean isClosed = false;
	public TaskDispatcher(TaskManager manager){
		this.manager = manager;
	}
	
	@Override
	public void run() {
		Log.i(TaskDispatcher.class.getName(), "启动TaskDispatcher线程");
		try {
			while ((!isClosed) && (!Thread.currentThread().isInterrupted())) {
				if ( manager.hasIdleTask() && manager.hasIdleWorks()) {
					Task task = manager.getTask();
					manager.doTask(task);
				} else {
					synchronized (this) {
						wait(500);
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
