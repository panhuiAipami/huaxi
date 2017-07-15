package net.huaxi.reader.db.model;

import java.io.Serializable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="chapter_content_table")
public class ChapterContentTable implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1258388109784133146L;
	@DatabaseField(columnName ="cc_id", dataType = DataType.INTEGER,unique=true,canBeNull=false,generatedId=true)
	private int id;
	@DatabaseField(columnName="c_sid",dataType=DataType.STRING,canBeNull=false,uniqueCombo=true)
	private String chapterId;
	@DatabaseField(columnName="b_sid",dataType=DataType.STRING,canBeNull=false,uniqueCombo=true)
	private String bookId;
	@DatabaseField(columnName="cc_is_readed",dataType=DataType.INTEGER)
	private int isReaded;
	@DatabaseField(columnName="cc_is_cached",dataType=DataType.INTEGER)
	private int isCached;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getChapterId() {
		return chapterId;
	}

	public void setChapterId(String chapterId) {
		this.chapterId = chapterId;
	}

	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public int getIsReaded() {
		return isReaded;
	}
	public void setIsReaded(int isReaded) {
		this.isReaded = isReaded;
	}
	public int getIsCached() {
		return isCached;
	}
	public void setIsCached(int isCached) {
		this.isCached = isCached;
	}
	
	
}
