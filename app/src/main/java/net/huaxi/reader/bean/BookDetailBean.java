package net.huaxi.reader.bean;

import com.google.gson.annotations.SerializedName;
import net.huaxi.reader.db.model.BookTable;

/**
 * Created by ZMW on 2016/1/13.
 */
public class BookDetailBean extends BookTable {


    private static final long serialVersionUID = 3066897704398116381L;

    @SerializedName("bk_keywords")
    private String keyword;
    @SerializedName("bk_seo_title")
    private String bookTitleSeo;//title修饰
    @SerializedName("bk_seo_keywords")
    private String bookKeyWordSeo;//keyword修饰
    @SerializedName("bk_seo_description")
    private String bookDescSeo;//详情修饰
    @SerializedName("bp_is_vip")
    private int isVip;//是否收费
    @SerializedName("bk_finish_flag")
    private int isFinish;//是否完结  1 完结  0 连载
    @SerializedName("bk_total_words")
    private int totalwords;
    @SerializedName("bk_total_cpts")//书籍总章节数（不包括隐藏）
    private String totalCpts;
    @SerializedName("cat_mid")
    private String catMid;//分类 mid
    @SerializedName("cat_name")
    private String catName;//分类名称
    @SerializedName("bk_total_score")
    private String totalScore;//分数
    @SerializedName("bk_total_reads")
    private long totalReaders;//多少人读 单位个
    @SerializedName("bp_price")
    private String price;//千字价格

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getIsVip() {
        return isVip;
    }

    public void setIsVip(int isVip) {
        this.isVip = isVip;
    }

    public int getTotalwords() {
        return totalwords;
    }

    public void setTotalwords(int totalwords) {
        this.totalwords = totalwords;
    }

    public long getTotalReaders() {
        return totalReaders;
    }

    public void setTotalReaders(long totalReaders) {
        this.totalReaders = totalReaders;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }


    public String getBookTitleSeo() {
        return bookTitleSeo;
    }

    public void setBookTitleSeo(String bookTitleSeo) {
        this.bookTitleSeo = bookTitleSeo;
    }

    public String getBookKeyWordSeo() {
        return bookKeyWordSeo;
    }

    public void setBookKeyWordSeo(String bookKeyWordSeo) {
        this.bookKeyWordSeo = bookKeyWordSeo;
    }

    public String getBookDescSeo() {
        return bookDescSeo;
    }

    public void setBookDescSeo(String bookDescSeo) {
        this.bookDescSeo = bookDescSeo;
    }


    public int getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(int isFinish) {
        this.isFinish = isFinish;
    }


    public String getTotalCpts() {
        return totalCpts;
    }

    public void setTotalCpts(String totalCpts) {
        this.totalCpts = totalCpts;
    }

    public String getCatMid() {
        return catMid;
    }

    public void setCatMid(String catMid) {
        this.catMid = catMid;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }
}
