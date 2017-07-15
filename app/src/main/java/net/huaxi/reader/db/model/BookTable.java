package net.huaxi.reader.db.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "book_table")
public class BookTable implements Serializable {
    private static final long serialVersionUID = 24653157905739211L;
    /**
     *
     */

    @DatabaseField(columnName = "b_id", dataType = DataType.INTEGER, unique = true, canBeNull = false, generatedId = true)
    private int id;
    /**
     * 书id
     */
    @DatabaseField(columnName = "b_sid", dataType = DataType.STRING, canBeNull = false)
    @SerializedName("bk_mid")
    private String bookId;
    /**
     * 书名
     */
    @DatabaseField(columnName = "b_name", dataType = DataType.STRING)
    @SerializedName("bk_title")
    private String name;
    /**
     * 封面图
     */
    @DatabaseField(columnName = "b_cover_img_id", dataType = DataType.STRING)
    @SerializedName("bk_cover_imgid")
    private String coverImageId;

    @DatabaseField(columnName = "b_author_id", dataType = DataType.STRING)
    private String authorId;

    @DatabaseField(columnName = "b_author_name", dataType = DataType.STRING)
    @SerializedName("bp_au_pname")
    private String authorName;
    /**
     * 书籍是否包月作品(书架VIP标识)
     */
    @DatabaseField(columnName = "b_is_monthly", dataType = DataType.INTEGER)
    @SerializedName("bp_hire_flag")
    private int isMonthly;
    /**
     * 是否有新章节
     */
    @DatabaseField(columnName = "b_has_unclicked_new_chapter", dataType = DataType.INTEGER)
    private int hasNewChapter;//比较catalogupdatetime值确定这个参数

    @DatabaseField(columnName = "b_last_read_date", dataType = DataType.LONG)
    private long lastReadDate;//请求书架时返回没有数据就用默认数据0

    @DatabaseField(columnName = "b_last_read_chapter", dataType = DataType.STRING)
    private String lastReadChapter;

    @DatabaseField(columnName = "b_last_read_location", dataType = DataType.INTEGER)
    private int lastReadLocation = 0;

    @DatabaseField(columnName = "b_read_percentage", dataType = DataType.FLOAT)
    private float readPercentage;//读书的时候修改

    @DatabaseField(columnName = "b_catalog_update_time", dataType = DataType.LONG)
    @SerializedName("bp_last_cpt_time")
    private long catalogUpdateTime;//最后修改时间

    @DatabaseField(columnName = "b_is_on_shelf", dataType = DataType.INTEGER)
    private int IsOnShelf;//是否在书架0为假,1为真

    @DatabaseField(columnName = "b_add_to_shelf_time", dataType = DataType.LONG)
    @SerializedName("operate_time")
    private long addToShelfTime;//加入书架之后生成时间  //移除书架的时候设置为0

    @SerializedName("bp_mdate")
    @DatabaseField(columnName = "book_total_stamp", dataType = DataType.LONG)
    private long bookTotalStamp;//用于更新目录

    @SerializedName("bk_description")
    @DatabaseField(columnName = "b_description", dataType = DataType.STRING)
    private String bookDesc;//详情

    @SerializedName("bk_share_url")
    @DatabaseField(columnName = "b_website", dataType = DataType.STRING)
    private String website;//分享地址

    @SerializedName("last_update_chapter")
    @DatabaseField(columnName = "b_last_update_chapter", dataType = DataType.INTEGER)
    private int chapterCount;


    @SerializedName("last_date")
    private int lastDate;   //最后订阅时间。

    public String getBookDesc() {
        return bookDesc;
    }

    public void setBookDesc(String bookDesc) {
        this.bookDesc = bookDesc;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public long getBookTotalStamp() {
        return bookTotalStamp;
    }

    public void setBookTotalStamp(long bookTotalStamp) {
        this.bookTotalStamp = bookTotalStamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverImageId() {
        return coverImageId;
    }

    public void setCoverImageId(String coverImageId) {
        this.coverImageId = coverImageId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getIsMonthly() {
        return isMonthly;
    }

    public void setIsMonthly(int isMonthly) {
        this.isMonthly = isMonthly;
    }

    public int getHasNewChapter() {
        return hasNewChapter;
    }

    public void setHasNewChapter(int hasNewChapter) {
        this.hasNewChapter = hasNewChapter;
    }

    public long getLastReadDate() {
        return lastReadDate;
    }

    public void setLastReadDate(long lastReadDate) {
        this.lastReadDate = lastReadDate;
    }

    public String getLastReadChapter() {
        return lastReadChapter;
    }

    public void setLastReadChapter(String lastReadChapter) {
        this.lastReadChapter = lastReadChapter;
    }

    public int getLastReadLocation() {
        return lastReadLocation;
    }

    public void setLastReadLocation(int lastReadLocation) {
        this.lastReadLocation = lastReadLocation;
    }

    public long getCatalogUpdateTime() {
        return catalogUpdateTime;
    }

    public void setCatalogUpdateTime(long catalogUpdateTime) {
        this.catalogUpdateTime = catalogUpdateTime;
    }

    public int getIsOnShelf() {
        return IsOnShelf;
    }

    public void setIsOnShelf(int isOnShelf) {
        IsOnShelf = isOnShelf;
    }

    public long getAddToShelfTime() {
        return addToShelfTime;
    }

    public void setAddToShelfTime(long addToShelfTime) {
        this.addToShelfTime = addToShelfTime;
    }

    public float getReadPercentage() {
        return readPercentage;
    }

    public void setReadPercentage(float readPercentage) {
        this.readPercentage = readPercentage;
    }


    public int getChapterCount() {
        return chapterCount;
    }

    public void setChapterCount(int chapterCount) {
        this.chapterCount = chapterCount;
    }

    public int getLastDate() {
        return lastDate;
    }

    public void setLastDate(int lastDate) {
        this.lastDate = lastDate;
    }

    @Override
    public String toString() {
        return "BookTable{" +
                "id=" + id +
                ", bookid='" + bookId + '\'' +
                ", name='" + name + '\'' +
                ", coverImageId='" + coverImageId + '\'' +
                ", authorName='" + authorName + '\'' +
                ", isMonthly=" + isMonthly +
                ", hasNewChapter=" + hasNewChapter +
                ", lastReadDate=" + lastReadDate +
                ", lastReadChapter=" + lastReadChapter +
                ", lastReadLocation=" + lastReadLocation +
                ", readPercentage=" + readPercentage +
                ", catalogUpdateTime=" + catalogUpdateTime +
                ", IsOnShelf=" + IsOnShelf +
                ", addToShelfTime=" + addToShelfTime +
                '}';
    }


}
