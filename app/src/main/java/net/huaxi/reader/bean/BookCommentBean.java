package net.huaxi.reader.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ZMW on 2016/1/13.
 */
public class BookCommentBean implements Serializable {


    private static final long serialVersionUID = -2477459268525225016L;
    @SerializedName("cmt_id")
    private String commentId;//评论ID

    @SerializedName("bk_mid")
    private String bookId;//书籍ID

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCommentStar() {
        return commentStar;
    }

    public void setCommentStar(int commentStar) {
        this.commentStar = commentStar;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getIsVip() {
        return isVip;
    }

    public void setIsVip(int isVip) {
        this.isVip = isVip;
    }

    @SerializedName("cat_mid")
    private String chapterId;//章节ID

    @SerializedName("u_img_id")
    private String imageId;//图片ID

    @SerializedName("u_mid")
    private String userId;//用户ID

    @SerializedName("cmt_content")
    private String content;//评论内容

    @SerializedName("cmt_star")
    private int commentStar;//评论星级，0~5星

    @SerializedName("cmt_ctime")
    private String commentTime;//发布时间

    @SerializedName("u_img_url")
    private String avatar;//用户头像Url

    @SerializedName("u_nickname")
    private String nickname;//昵称

    @SerializedName("u_vip")
    private int isVip;//是否为会员   1会员，0非会员


    @Override
    public String toString() {
        return "BookCommentBean{" +
                "commentId='" + commentId + '\'' +
                ", bookId='" + bookId + '\'' +
                ", chapterId='" + chapterId + '\'' +
                ", imageId='" + imageId + '\'' +
                ", userId='" + userId + '\'' +
                ", content='" + content + '\'' +
                ", commentStar=" + commentStar +
                ", commentTime='" + commentTime + '\'' +
                ", avatar='" + avatar + '\'' +
                ", nickname='" + nickname + '\'' +
                ", isVip=" + isVip +
                '}';
    }
}
