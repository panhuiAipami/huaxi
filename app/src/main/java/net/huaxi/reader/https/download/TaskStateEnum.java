package net.huaxi.reader.https.download;

public enum TaskStateEnum {
	PREPARED(1),
	STARTED(2),
	LOADING(3),
	FINISHED(4),
	FAILED(5),
	PAUSED(6),
	CANCELED(7);
	
	
	private int index;
	
	private TaskStateEnum(int index){
		this.index = index;
	}

	@Override
	public String toString() {
		return "state code = "+this.index;
	}
	
	
}
