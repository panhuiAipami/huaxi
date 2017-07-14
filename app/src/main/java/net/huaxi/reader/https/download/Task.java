package net.huaxi.reader.https.download;

import net.huaxi.reader.https.download.listener.ITaskStateChangeListener;


public class Task {
	/**
	 * 任务id
	 */
	private String id;
	/**
	 * 文件名
	 */
	private String name;
	/**
	 * 下载地址
	 */
	private String url;
	/**
	 * 文件大小
	 */
	private long size;
	/**
	 * 文件后缀名.
	 */
	private String suffix;
	/**
	 * 任务状态
	 */
	private TaskStateEnum state;
	/**
	 * 下载进度
	 */
	private int progress;
	/**
	 * 错误码
	 */
	private int errorCode;
	
	/**
	 * 联网超时时间
	 */
	private int timeout = 30 * 1000;
	/**
	 * 下载目录
	 */
	private String downloadDirectory;
	/**
	 * 任务监听回调接口;
	 */
	ITaskStateChangeListener listener;

	/**
	 * 构造函数;
	 * @param id	
	 * @param name	文件名
	 * @param url	下载地址
	 * @param suffix  文件后缀名
	 * @param downloadPath 下载目录
	 * @param listener 回调接口
	 * @author taoyingfeng
	 */
	public Task(String id, String name, String url, String suffix, String downloadPath,ITaskStateChangeListener listener) {
		this.id = id;
		this.name = name;
		this.url = url;
		this.suffix = suffix;
		this.downloadDirectory = downloadPath;
		this.listener = listener;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getSize() {
		return size;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public TaskStateEnum getState() {
		return state;
	}

	public void setState(TaskStateEnum state) {
		this.state = state;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getDownloadDirectory() {
		return downloadDirectory;
	}

	public ITaskStateChangeListener getListener() {
		return listener;
	}

	public void setListener(ITaskStateChangeListener listener) {
		this.listener = listener;
	}

	public void setDownloadDirectory(String downloadDirectory) {
		this.downloadDirectory = downloadDirectory;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (getClass() != o.getClass()) {
			return false;
		}
		if (o instanceof Task) {
			Task t = (Task) o;
			if (t != null) {
				return this.url.equals(t.getUrl());
			}
		}
		return false;
	}

}
