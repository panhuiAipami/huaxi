package com.spriteapp.booklibrary.api;

import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.model.CateBean;
import com.spriteapp.booklibrary.model.UserModel;
import com.spriteapp.booklibrary.model.WeChatBean;
import com.spriteapp.booklibrary.model.response.BookChapterResponse;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.model.response.BookStoreResponse;
import com.spriteapp.booklibrary.model.response.CommentResponse;
import com.spriteapp.booklibrary.model.response.LoginResponse;
import com.spriteapp.booklibrary.model.response.PayResponse;
import com.spriteapp.booklibrary.model.response.SubscriberContent;
import com.spriteapp.booklibrary.model.store.AppUpDateModel;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by kuangxiaoguo on 2017/7/7.
 */

public interface BookApiService {

    /**
     * 添加评论
     *
     * @param bookId    书籍id
     * @param volumeId  圈id
     * @param chapterId 章节id
     * @param parentId  段落id
     * @param title     标题
     * @param content   评论内容
     */
    @POST("book_comment")
    Observable<Base<Void>> addComment(@Query("book_id") int bookId,
                                      @Query("volume_id") int volumeId,
                                      @Query("chapter_id") int chapterId,
                                      @Query("parent_id") int parentId,
                                      @Query("title") String title,
                                      @Query("content") String content);

    /**
     * 获取评论
     *
     * @param bookId    书籍id
     * @param chapterId 章节id
     * @param offset    页数
     * @param rowCount  每页返回数据(default 20)
     * @param startTime 开始时间戳
     * @param stopTime  结束时间戳
     */
    @GET("book_comment?")
    Observable<Base<List<CommentResponse>>> getComment(@Query("book_id") int bookId,
                                                       @Query("chapter_id") int chapterId,
                                                       @Query("offset") int offset,
                                                       @Query("row_count") int rowCount,
                                                       @Query("start_time") long startTime,
                                                       @Query("stop_time") long stopTime);

    /**
     * 获取书籍详情
     *
     * @param bookId 书籍id
     */
    @GET("book_detail?")
    Observable<Base<BookDetailResponse>> getBookDetail(@Query("book_id") int bookId);

    /**
     * 获取章节
     */
    @GET("book_catalog?")
    Observable<Base<List<BookChapterResponse>>> getCatalog(@Query("book_id") int bookId);


    /**
     * 获取订阅内容
     *
     * @param autoSub 是否是自动订阅
     */
    @GET("book_content?")
    Observable<Base<SubscriberContent>> getSubscriberContent(@Query("book_id") int bookId,
                                                             @Query("chapter_id") int chapterId,
                                                             @Query("auto_sub") int autoSub);

    /**
     * @param code 微信code
     * @return
     */
    @GET("login_wxchat")
    Call<Base<LoginResponse>> getLogin_Wx(@Query("code") String code);

    /**
     * @param mobile   手机号
     * @param captcha  验证码
     * @param password 密码
     * @param u_action 动作
     * @return
     */
    @GET("login_register")
    Call<Base<LoginResponse>> getLogin_Phone(@Query("mobile") String mobile,
                                             @Query("captcha") String captcha,
                                             @Query("password") String password,
                                             @Query("u_action") String u_action);


    /**
     * @param account  账号
     * @param password 密码
     * @return
     */
    @GET("login_account")
    Call<Base<LoginResponse>> getLogin_Zhuce(@Query("account") String account,
                                             @Query("password") String password);


    @GET("book_shelf")
    Observable<Base<List<BookDetailResponse>>> getBookShelf();

    @FormUrlEncoded
    @POST("book_shelf")
    Observable<Base<Void>> addBookToShelf(@Field("book_id") int bookId,
                                          @Field("u_action") String action,
                                          @Field("chapter_id") int chapterId);

    @FormUrlEncoded
    @POST("book_shelf")
    Observable<Base<Void>> deleteBook(@Field("book_ids") int bookIds,
                                      @Field("u_action") String action);

    @FormUrlEncoded
    @POST("book_shelf")
    Observable<Base<Void>> addBook(@Field("u_action") String action,
                                   @Field("data") String bookJson);

    @GET("pay_appalipay")
    Observable<Base<PayResponse>> getAliPayRequest(@Query("product_id") String productId);

    //威富通
    @GET("pay_appswiftpassg")
    Observable<Base<WeChatBean>> getWeChatRequest(@Query("product_id") String productId);

    @FormUrlEncoded
    @POST("book_comment")
    Observable<Base<Void>> addComment(@Field("book_id") int bookId,
                                      @Field("content") String content);

    @GET("book_store")
    Call<BookStoreResponse> getBookStore(@Query("format") String type);

    @GET("user_info")
    Observable<Base<UserModel>> getUserInfo(@Query("format") String format);

    @GET("login_logout")
    Observable<Base<Void>> loginOut(@Query("sn") String sn,
                                    @Query("token") String token);

    ///////////////////////////////////////////////////////////////////////////////
    @GET("server_version")
    Observable<Base<AppUpDateModel>> app_Upate(@Query("type") String type);

    //检查更新
    @GET("server_check")
    Observable<Base<CateBean>> app_cate();

    //广场列表
    @GET("square_index")
    Observable<Base<CateBean>> square_index(@Query("page") String page);


}