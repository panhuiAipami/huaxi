package com.spriteapp.booklibrary.api;

import com.google.gson.JsonObject;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseTwo;
import com.spriteapp.booklibrary.model.BookCommentBean;
import com.spriteapp.booklibrary.model.BookCommentReplyBean;
import com.spriteapp.booklibrary.model.CateBean;
import com.spriteapp.booklibrary.model.ChoiceBean;
import com.spriteapp.booklibrary.model.CommentDetailsBean;
import com.spriteapp.booklibrary.model.MyApprenticeBean;
import com.spriteapp.booklibrary.model.NewBookStoreResponse;
import com.spriteapp.booklibrary.model.SquareBean;
import com.spriteapp.booklibrary.model.TaskBean;
import com.spriteapp.booklibrary.model.TaskRewardBean;
import com.spriteapp.booklibrary.model.UpLoadImgBean;
import com.spriteapp.booklibrary.model.UserBean;
import com.spriteapp.booklibrary.model.UserModel;
import com.spriteapp.booklibrary.model.WeChatBean;
import com.spriteapp.booklibrary.model.XiaoMiBean;
import com.spriteapp.booklibrary.model.response.BookChapterResponse;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.model.response.BookStoreResponse;
import com.spriteapp.booklibrary.model.response.CommentResponse;
import com.spriteapp.booklibrary.model.response.HuaWeiResponse;
import com.spriteapp.booklibrary.model.response.LoginResponse;
import com.spriteapp.booklibrary.model.response.PayResponse;
import com.spriteapp.booklibrary.model.response.SubscriberContent;
import com.spriteapp.booklibrary.model.store.AppUpDateModel;
import com.spriteapp.booklibrary.ui.adapter.CommentReplyBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
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
    Observable<Base<List<BookDetailResponse>>> getBookShelf(@Query("row_count") String row_count);

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
    Observable<Base<Void>> deleteBook(@Field("book_ids") String bookIds,
                                      @Field("u_action") String action);

    @FormUrlEncoded
    @POST("book_shelf")
    Observable<Base<Void>> addBook(@Field("u_action") String action,
                                   @Field("data") String bookJson);

    @FormUrlEncoded
    @POST("pay_appalipay")
    Observable<Base<PayResponse>> getAliPayRequest(@Field("product_id") String productId);

    //原生微信支付
    @FormUrlEncoded
    @POST("pay_appwechat")
    Observable<Base<WeChatBean>> getWeChatRequest(@Field("product_id") String productId);


    //威富通pay_appswiftpassg
    @FormUrlEncoded
    @POST("pay_wapswiftpassg")
    Observable<Base<WeChatBean>> pay_wapswiftpassg(@Field("product_id") String productId);

    @FormUrlEncoded
    @POST("pay_appswiftpassg")
    Observable<Base<WeChatBean>> pay_appswiftpassg(@Field("product_id") String productId);

    //原生微信支付
    @FormUrlEncoded
    @POST("pay_apphuawei")
    Observable<Base<HuaWeiResponse>> pay_apphuawei(@Field("product_id") String productId);

    @FormUrlEncoded
    @POST("book_comment")
    Observable<Base<Void>> addComment(@Field("book_id") int bookId,
                                      @Field("title") String title,
                                      @Field("content") String content,
                                      @Field("star") float score);

    @GET("book_store")
    Call<BookStoreResponse> getBookStore(@Query("format") String type);

    @GET("user_info")
    Observable<Base<UserModel>> getUserInfo(@Query("format") String format);

    @GET("user_info")
    Observable<Base<UserBean>> getUserBean(@Query("format") String format);

    @GET("login_logout")
    Observable<Base<Void>> loginOut(@Query("sn") String sn,
                                    @Query("token") String token);

    ///////////////////////////////////////////////////////////////////////////////
    @GET("server_version")
    Observable<Base<AppUpDateModel>> app_Upate(@Query("type") String type);

    //检查更新
    @GET("server_check")
    Observable<Base<CateBean>> app_cate();
//    @Query("system") String system,
//    @Query("system_version") String system_version,
//    @Query("type") String type,
//    @Query("type") String type,
//    @Query("type") String type,
//    @Query("type") String type

    //广场列表
    @GET("square_index")
    Observable<Base<List<SquareBean>>> square_index(@Query("page_index") int page,
                                                    @Query("platform_id") int platform_id,
                                                    @Query("follow") int follow);

    //广场列表详情
    @GET("square_detail")
    Observable<Base<SquareBean>> square_detail(@Query("page_index") int page,
                                               @Query("squareid") int squareid,
                                               @Query("platform_id") int platform_id);

    //广场详情翻页
    @GET("square_detailpage")
    Observable<Base<CommentDetailsBean>> square_detailpage(@Query("page_index") int page,
                                                           @Query("squareid") int squareid,
                                                           @Query("act") String act,
                                                           @Query("platform_id") int platform_id);

    //广场帖子评论回复详情页翻页
    @GET("square_detail")
    Observable<Base<SquareBean>> square_detail(@Query("page_index") int page,
                                               @Query("squareid") int squareid,
                                               @Query("id") int id,
                                               @Query("platform_id") int platform_id);

    //添加广场帖子
    @FormUrlEncoded
    @POST("square_add")
    Observable<Base<SquareBean>> square_add(@Field("subject") String content,
                                            @Field("pic_url") String pic_url,
                                            @Field("platform_id") int platform_id);

    //上传图片
    @Multipart
    @POST("uploadfile_multi")
    Observable<Base<List<UpLoadImgBean>>> uploadfile_multi(@Query("field_name") String field_name,
                                                           @PartMap Map<String, RequestBody> image,
                                                           @Query("platform_id") int platform_id);

    //广场添加评论
    @FormUrlEncoded
    @POST("square_addcomment")
    Observable<Base> square_addcomment(@Field("content") String content,
                                       @Field("squareid") int squareid,
                                       @Field("platform_id") int platform_id);

    //广场回复评论
    @FormUrlEncoded
    @POST("square_addcomment")
    Observable<Base<SquareBean>> square_addcomment(@Field("content") String content,
                                                   @Field("squareid") int squareid,
                                                   @Field("pid") int pid,
                                                   @Field("replyto") int replyto,
                                                   @Field("platform_id") int platform_id);


    //广场帖子阅读数与点赞 act:readnum代表阅读,supportnum代表点赞
    @GET("square_actcmt")
    Observable<Base> square_actcmt(@Query("squareid") int squareid,
                                   @Query("act") String act,
                                   @Query("platform_id") int platform_id);

    //评论点赞
    @GET("square_addcontentsupport")
    Observable<Base> square_addcontentsupport(@Query("id") int comment_id,
                                              @Query("platform_id") int platform_id);


    //帖子评论回复详情页翻页
    @GET("square_replypage")
    Observable<Base<CommentReplyBean>> square_replypage(@Query("id") int comment_id,
                                                        @Query("squareid") int squareid,
                                                        @Query("page_index") int page_index,
                                                        @Query("page_size") int page_size,
                                                        @Query("platform_id") int platform_id);


    //书城
    @GET("book_store")
    Observable<NewBookStoreResponse> book_store(@Query("format") String type);

    //搜索列表
    @GET("book_search")
    Observable<BaseTwo<List<String>>> book_search(@Query("format") String type);

    //搜索
    @GET("book_search")
    Observable<Base<List<BookDetailResponse>>> book_search(@Query("format") String type,
                                                           @Query("q") String content);

    //精选页
    @GET("book_weekly")
    Observable<Base<List<ChoiceBean>>> book_weekly(
            @Query("format") String format,
            @Query("page") int page);

    //排行页
    @GET("book_ranklist")
    Observable<Base<List<BookDetailResponse>>> book_ranklist(@Query("format") String format,
                                                             @Query("type") int type,
                                                             @Query("interval") int interval,
                                                             @Query("pagesize") int pagesize);

    //获取口令书籍
    @GET("book_command")
    Observable<Base<List<BookDetailResponse>>> book_command(@Query("q") String q);

    //领取小米卡券花瓣
    @GET("card_get")
    Observable<BaseTwo<XiaoMiBean>> card_get(@Query("id") String id,
                                             @Query("format") String format);

    //阅读时长判断
    @GET("book_readhistroy")
    Observable<Base> book_readhistroy(@Query("book_id") String book_id,
                                      @Query("chapter_id") String chapter_id);

    //获取用户邀请码
    @GET("invite_getcode")
    Observable<BaseTwo> invite_getcode();

    //获取用户邀请码
    @GET("user_task")
    Observable<TaskBean> user_task(@Query("format") String type);

    //填写邀请码
    @GET("invite_activate")
    Observable<BaseTwo> invite_activate(@Query("code") String code);

    //提现到支付宝
    @GET("user_exchange")
    Observable<Base> user_exchange(@Query("format") String format,
                                   @Query("amount") String amount,
                                   @Query("amount") String account,
                                   @Query("real_name") String real_name);

    //获取短信验证码
    @GET("user_mobile")
    Observable<Base> user_mobile(@Query("mobile") String mobile,
                                 @Query("captcha") String captcha,
                                 @Query("u_action") String u_action);

    //我的徒弟列表
    //@Query("userid") int userid
    @GET("user_mypupillist")
    Observable<Base<MyApprenticeBean>> user_mypupillist(@Query("format") String format,
                                                        @Query("page") int page);

    //待激活徒弟列表
    //@Query("userid") int userid
    @GET("user_myawakepupillist")
    Observable<Base<List<MyApprenticeBean.PupilDataBean>>> user_myawakepupillist(@Query("format") String format,
                                                                                 @Query("page") int page);

    //激活徒弟
    @GET("user_awakepupil")
    Observable<Base> user_awakepupil(@Query("awake_user_id") int page);

    //用户收益明细
    //@Query("userid") int userid
    @GET("user_coninslog")
    Observable<Base> user_coninslog(@Query("page") int page);


    //用户收徒明细
    @GET("user_coninspupillog")
    Observable<Base<List<TaskRewardBean>>> user_coninspupillog(@Query("format") String format,
                                                               @Query("page") int page);

    //用户任务明细
    @GET("user_coninsselflog")
    Observable<Base<List<TaskRewardBean>>> user_coninsselflog(@Query("format") String format,
                                                              @Query("page") int page);

    @GET("user_novelpackage")
    Observable<Base<List<BookDetailResponse>>> user_novelpackage(@Query("format") String format,
                                                                 @Query("page") int page);

    //猜你喜欢
    @GET("book_searchrecommend")
    Observable<Base<List<BookDetailResponse>>> book_searchrecommend(@Query("format") String format);


    //历史记录
    @GET("user_readhistory")
    Observable<Base<List<BookDetailResponse>>> user_readhistory(@Query("format") String format);

    //获取书的评论
    @GET("book_commentreply")
    Observable<Base<List<BookCommentBean>>> getBookComment(@Query("book_id") int book_id,
                                                           @Query("chapter_id") int chapter_id,
                                                           @Query("start_time") int start_time,
                                                           @Query("stop_time") int stop_time,
                                                           @Query("row_count") int row_count);

    //获取章节评论数
    @GET("book_chaptercomment")
    Observable<JsonObject> get_book_chaptercomment(@Query("book_id") int book_id,
                                                   @Query("chapter_id") int chapter_id,
                                                   @Query("u_action") String action

    );

    //获取章节评论内容
    @GET("book_chaptercomment")
    Observable<Base<BookCommentReplyBean>> get_chapter_comment_content(@Query("book_id") int book_id,
                                                                       @Query("chapter_id") int chapter_id,
                                                                       @Query("pid") int pid,
                                                                       @Query("u_action") String action,
                                                                       @Query("start_time") int start_time

    );

    //添加书的评论
    @GET("book_comment")
    Observable<Base> send_book_comment(@Query("book_id") int book_id,
                                               @Query("chapter_id") int chapter_id,
                                               @Query("parent_id") int parent_id,
                                               @Query("content") String content

    );
}
