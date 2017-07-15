package net.huaxi.reader.db.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 *
 */
@DatabaseTable(tableName="chapter_table")
public class ChapterTable implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7145723190837486940L;
	@DatabaseField(columnName ="c_id", dataType = DataType.INTEGER,unique=true,canBeNull=false,generatedId=true)
	private int id;

	@DatabaseField(columnName="c_sid",dataType=DataType.STRING,canBeNull=false,uniqueCombo=true)
	@SerializedName("cpt_mid")
	private String chapterId;

	@DatabaseField(columnName="b_sid",dataType=DataType.STRING,canBeNull=false,uniqueCombo=true)
	@SerializedName("bk_mid")
	private String bookId;

	@DatabaseField(columnName="c_name",dataType=DataType.STRING)
	@SerializedName("cpt_title")
	private String name;

	@DatabaseField(columnName="c_chapter_no",dataType=DataType.INTEGER)
	@SerializedName("cpt_order")
	private int chapterNo;

	@DatabaseField(columnName="c_is_vip",dataType=DataType.INTEGER)
	@SerializedName("cpt_is_vip")
	private int isVip;

	@DatabaseField(columnName="c_totalwords",dataType=DataType.INTEGER)
	@SerializedName("cpt_word_count")
    private int totalWords;

    @DatabaseField(columnName="c_is_subscribed",dataType=DataType.INTEGER)
	@SerializedName("cpt_is_sub")
	private int isSubscribed;

	@DatabaseField(columnName="c_price",dataType=DataType.INTEGER)
	@SerializedName("price")
	private int price;


	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

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

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getChapterNo() {
		return chapterNo;
	}
	public void setChapterNo(int chapterNo) {
		this.chapterNo = chapterNo;
	}
	public int getIsVip() {
		return isVip;
	}
	public void setIsVip(int isVip) {
		this.isVip = isVip;
	}

    public int getTotalWords() {
        return totalWords;
    }

    public void setTotalWords(int totalWords) {
        this.totalWords = totalWords;
    }
	public int getIsSubscribed() {
		return isSubscribed;
	}
	public void setIsSubscribed(int isSubscribed) {
		this.isSubscribed = isSubscribed;
	}

	@Override
	public String toString() {
		return "ChapterTable{" +
				"id=" + id +
				", chapterId='" + chapterId + '\'' +
				", bookId='" + bookId + '\'' +
				", name='" + name + '\'' +
				", chapterNo=" + chapterNo +
				", isVip=" + isVip +
				", totalWords=" + totalWords +
				", isSubscribed=" + isSubscribed +
				", price=" + price +
				'}';
	}
}

