package com.spriteapp.booklibrary.model;

import com.google.gson.annotations.SerializedName;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/1/30.
 */

public class NewBookStoreResponse implements Serializable {

    /**
     * status : success
     * top_banner_lists : [{"id":"123","widget_id":"1","status":"1","sort":"3","name":"鬼眼龙樱","images":"https://img.hxdrive.net/uploads/2018/01/29/a38834d98058027fbd8a10d33157f20d.jpg","tag":null,"link":"287","intro":"人人都怕他，唯有我，明知前路不可行，却因为爱不得不向他靠近。","extend":"0","create_time":"1515743227","edit_time":"1517195710","book_store_id":"0","url":"huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_detail%3Fformat%3Dhtml%26book_id%3D287"},{"id":"14","widget_id":"1","status":"1","sort":"2","name":"婚过有晴天","images":"https://img.hxdrive.net/uploads/2018/01/29/df4c73ec1f3e06244d2f3b9e647bf115.jpg","tag":null,"link":"1208","intro":"\u201c唐皓，我不嫁了!\u201d  \u201c晨晨，你又在调皮了。\u201d 不嫁？不存在的！各种神助攻的出现，各种桃花的挡路，都成了唐皓追妻路上的风景。看总裁唐皓，如何霸宠他的野蛮小娇妻。","extend":"0","create_time":"1512968100","edit_time":"1517195820","book_store_id":"0","url":"huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_detail%3Fformat%3Dhtml%26book_id%3D1208"},{"id":"124","widget_id":"1","status":"1","sort":"1","name":"盛世云锦","images":"https://img.hxdrive.net/uploads/2018/01/29/cad284ea10a6c53a2be98ad84b979d49.jpg","tag":null,"link":"1378","intro":"安绵重生而来，由弱女变女王。  教训生父继母继女，虐待坏人恶霸，狂踩白莲花小表砸，制霸校园小渣渣。凭借一双黄金眼，赌石无数金山起，白手起家奔大道，引得各界男神尽折腰。","extend":"0","create_time":"1515743373","edit_time":"1517195900","book_store_id":"0","url":"huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_detail%3Fformat%3Dhtml%26book_id%3D1378"}]
     * recommandBookList : [{"book_id":1464,"book_name":"重生之恕罪凤妃","book_image":"https://img.huaxi.net/upload/bookimage/2018/1/20180122160406114.jpg","book_intro":"前一世，她是他身边的细作，  　　却不想杀了他的骨肉，灭了他的天下，她才发现自己真正爱的人竟然一直都是他！再次重生，她愿坠入阿鼻地狱，只为了\u2014\u2014  \u201c阿谨，这一世，换我来守护你！\u201d 可是\u2026\u2026","book_share_url":"http://w.huaxi.net/1464","book_url":"huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_detail%3Fformat%3Dhtml%26book_id%3D1464","book_total_score":9,"book_total_reads":1,"book_finish_flag":0,"book_is_vip":3,"book_content_byte":79320,"book_chapter_total":39,"book_keywords":["虐恋","皇后","复合","重生"],"book_price":0,"book_updatetime":0,"book_category":[{"class_id":"3","class_name":"穿越架空"}],"author_id":"1385476","author_name":"伃我","author_avatar":"https://img.huaxi.net/userhead/1385476.jpg","last_update_chapter_id":630079,"last_update_chapter_datetime":1517191917,"last_update_chapter_title":"第039章 故人相见","last_update_chapter_intro":"第039章 故人相见","create_time":1515572812},{"book_id":1379,"book_name":"情深见鹿","book_image":"https://img.huaxi.net/upload/bookimage/2018/1/20180119115311655.jpg","book_intro":"只一眼，单余峰便对这个女人涌起无尽兴趣。  　　\u201c单先生，你这是亏本生意。\u201d车晓凤眸微勾，魅惑淋漓尽致。  　　\u201c为了老婆，甘之如饴。\u201d单余峰俯身轻啄一口，\u201c真甜！\u201d","book_share_url":"http://w.huaxi.net/1379","book_url":"huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_detail%3Fformat%3Dhtml%26book_id%3D1379","book_total_score":9,"book_total_reads":1,"book_finish_flag":0,"book_is_vip":3,"book_content_byte":161662,"book_chapter_total":78,"book_keywords":["虐恋","宠文","轻松"],"book_price":0,"book_updatetime":0,"book_category":[{"class_id":"2","class_name":"总裁豪门"}],"author_id":"1328726","author_name":"小佳佳","author_avatar":"https://img.huaxi.net/userhead/1328726.jpg","last_update_chapter_id":566413,"last_update_chapter_datetime":1517187600,"last_update_chapter_title":"78  有缘无分","last_update_chapter_intro":"78  有缘无分","create_time":1513664206},{"book_id":827,"book_name":"谋一场离婚","book_image":"https://img.huaxi.net/upload/bookimage/2017/10/20171020101523836.jpg","book_intro":"结婚三年，却被最爱的丈夫玩弄\u201c离婚\u201d。一无所有扫地出门，她却碰到了大学一直仰望的男人。他依然对她嗤之以鼻，只是这次，他决定将她收归在床，慢慢调教。可是，他怎么会有为她不顾一切的时候？可能她是被猪油蒙了眼\u2026\u2026","book_share_url":"http://w.huaxi.net/827","book_url":"huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_detail%3Fformat%3Dhtml%26book_id%3D827","book_total_score":9,"book_total_reads":1,"book_finish_flag":0,"book_is_vip":3,"book_content_byte":144761,"book_chapter_total":75,"book_keywords":["婚恋，总裁，都市虐恋","首席","豪门","家长里短"],"book_price":0,"book_updatetime":0,"book_category":[{"class_id":"2","class_name":"总裁豪门"}],"author_id":"204","author_name":"秋洺","author_avatar":"https://img.huaxi.net/userhead/204.jpg","last_update_chapter_id":646499,"last_update_chapter_datetime":1517230523,"last_update_chapter_title":"　第七十五章 你很好！","last_update_chapter_intro":"　第七十五章 你很好！","create_time":1507860577}]
     * freelimitBookList : []
     * classicalBookList : [{"book_id":1108,"book_name":"司徒先生喜当爹","book_image":"https://img.huaxi.net/upload/bookimage/2017/11/20171108114258344.jpg","book_intro":"\"这辈子你是逃不掉的！\u201d\n夜星儿嘴角一撇。\n\u201c司徒先生，我只是来告诉你一声，你快当爹了。\u201d","book_share_url":"http://w.huaxi.net/1108","book_url":"huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_detail%3Fformat%3Dhtml%26book_id%3D1108","book_total_score":9,"book_total_reads":1,"book_finish_flag":1,"book_is_vip":3,"book_content_byte":586911,"book_chapter_total":290,"book_keywords":["虐恋","总裁","豪门"],"book_price":0,"book_updatetime":0,"book_category":[{"class_id":"2","class_name":"总裁豪门"}],"author_id":"995649","author_name":"钱串子","author_avatar":"https://img.huaxi.net/userhead/995649.jpg","last_update_chapter_id":563686,"last_update_chapter_datetime":1516371657,"last_update_chapter_title":"　　第290章 盛世婚礼","last_update_chapter_intro":"　　第290章 盛世婚礼","create_time":1509547911},{"book_id":282,"book_name":"爱可摘星辰","book_image":"https://img.huaxi.net/upload/bookimage/2017/7/20170707180559196.jpg","book_intro":"【已完结。】\n很久以前，高瞰我俩相爱时，他说过要捧我做大明星，后来他兑现了诺言，却不再爱我。\n我们曾相爱，想想就心酸。\n可我从来不是那种认命的人，想要抓住爱情再也不放开的那双手，既然曾攫取过那个男人的心，这次，我相信一定能够扭转命运，重新赢回他的爱。","book_share_url":"http://w.huaxi.net/282","book_url":"huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_detail%3Fformat%3Dhtml%26book_id%3D282","book_total_score":9,"book_total_reads":1,"book_finish_flag":1,"book_is_vip":3,"book_content_byte":1184973,"book_chapter_total":511,"book_keywords":["正剧","明星","别后重逢","虐恋"],"book_price":0,"book_updatetime":0,"book_category":[{"class_id":"2","class_name":"总裁豪门"}],"author_id":"465779","author_name":"安屠生","author_avatar":"https://img.huaxi.net/userhead/465779.jpg","last_update_chapter_id":549338,"last_update_chapter_datetime":1516324055,"last_update_chapter_title":"第012章：小星星（4）","last_update_chapter_intro":"第012章：小星星（4）","create_time":1499421193},{"book_id":703,"book_name":"死亡陪游","book_image":"https://img.huaxi.net/upload/bookimage/2017/7/20170728151101525.jpg","book_intro":"你喜欢旅游吗？如果陪你旅游的人是鬼你会怕吗？\n我现在的职业就是与鬼同游。\n每一次旅游，都注定有大事情将要发生\u2026\u2026","book_share_url":"http://w.huaxi.net/703","book_url":"huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_detail%3Fformat%3Dhtml%26book_id%3D703","book_total_score":9,"book_total_reads":1,"book_finish_flag":1,"book_is_vip":3,"book_content_byte":929926,"book_chapter_total":453,"book_keywords":["校花","陪游","灵异"],"book_price":0,"book_updatetime":0,"book_category":[{"class_id":"7","class_name":"恐怖灵异"}],"author_id":"506833","author_name":"玩鬼的女汉子","author_avatar":"https://img.huaxi.net/userhead/506833.jpg","last_update_chapter_id":538818,"last_update_chapter_datetime":1516204233,"last_update_chapter_title":"第453章   大结局","last_update_chapter_intro":"第453章   大结局","create_time":1501076526},{"book_id":27,"book_name":"爹地放开我妈咪","book_image":"https://img.huaxi.net/upload/bookimage/2017/2/20170224140730847.jpg","book_intro":"他，是人人惧怕的笑面虎，可唯独对她温柔体贴！\n她，是人人夸赞的淑女，可唯独对他总是暴跳如雷！\n重逢之际她冷言道出\u201c你我早已经结束了。\u201d\n他却笑着回应！\u201c你会回来求我要你的。\u201d\n她转身离去，留下的却是一段高傲的话语，\u201c你永远都不会等到那一天！\u201d\n一纸契约将两人再次纠缠到一起，\u201c求我，求我要你啊！\u201d\n\u201c怎么可能！\u201d\n以为是一次报复，却得知，欠她最多的，恰好是自己，当他知道真相以后，她却恨上了他，\u201c如何你才会开心？\u201d\n\u201c你死就是我最大的心愿。\u201d\n\u201c好，我为你如愿，只要你开心..........\u201d","book_share_url":"http://w.huaxi.net/27","book_url":"huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_detail%3Fformat%3Dhtml%26book_id%3D27","book_total_score":9,"book_total_reads":1,"book_finish_flag":1,"book_is_vip":3,"book_content_byte":174804,"book_chapter_total":79,"book_keywords":["宠文","虐恋","别后重逢","女强"],"book_price":0,"book_updatetime":0,"book_category":[{"class_id":"2","class_name":"总裁豪门"}],"author_id":"206","author_name":"糖豆姐","author_avatar":"https://img.huaxi.net/userhead/206.jpg","last_update_chapter_id":510417,"last_update_chapter_datetime":1515850460,"last_update_chapter_title":"第079章：大结局","last_update_chapter_intro":"第079章：大结局","create_time":1487244105}]
     * freeNewBookList : [{"book_id":1391,"book_name":"重生之一笑倾城","book_image":"https://img.huaxi.net/upload/bookimage/2018/1/20180116164141387.jpg","book_intro":"一场权谋，佳人为棋，三年苦楚，一朝丧命！\n一梦回溯，重生三年前，花烛红妆，今生掌控不屈的命运；\n弱女归，长风破浪会有时，一笑倾城，双宿双飞。\n","book_share_url":"http://w.huaxi.net/1391","book_url":"huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_detail%3Fformat%3Dhtml%26book_id%3D1391","book_total_score":9,"book_total_reads":1,"book_finish_flag":0,"book_is_vip":2,"book_content_byte":88003,"book_chapter_total":39,"book_keywords":["重生","王妃","权谋","唯美"],"book_price":0,"book_updatetime":0,"book_category":[{"class_id":"11","class_name":""}],"author_id":"1330954","author_name":"三顾佳人","author_avatar":"https://img.huaxi.net/userhead/1330954.jpg","last_update_chapter_id":646382,"last_update_chapter_datetime":1517211376,"last_update_chapter_title":"第39章 碧螺之死","last_update_chapter_intro":"第39章 碧螺之死","create_time":1513835636},{"book_id":739,"book_name":"被渣男背叛的那些日子","book_image":"https://img.huaxi.net/upload/bookimage/2017/9/20170912110318748.jpg","book_intro":"","book_share_url":"http://w.huaxi.net/739","book_url":"huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_detail%3Fformat%3Dhtml%26book_id%3D739","book_total_score":9,"book_total_reads":1,"book_finish_flag":0,"book_is_vip":2,"book_content_byte":61142,"book_chapter_total":33,"book_keywords":["爆笑","欢喜冤家","卧底","契约"],"book_price":0,"book_updatetime":0,"book_category":[{"class_id":"14","class_name":""}],"author_id":"662188","author_name":"苏小盒","author_avatar":"https://img.huaxi.net/userhead/662188.jpg","last_update_chapter_id":646374,"last_update_chapter_datetime":1517208835,"last_update_chapter_title":"第33章 出差","last_update_chapter_intro":"第33章 出差","create_time":1504530072},{"book_id":1402,"book_name":"妻不二嫁","book_image":"https://img.huaxi.net/upload/bookimage/2018/1/20180102111753614.jpg","book_intro":"我会时刻记住我是顾家的少奶奶，不会给你再丢一次人。我保证","book_share_url":"http://w.huaxi.net/1402","book_url":"huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_detail%3Fformat%3Dhtml%26book_id%3D1402","book_total_score":9,"book_total_reads":1,"book_finish_flag":0,"book_is_vip":2,"book_content_byte":57692,"book_chapter_total":27,"book_keywords":["正剧","替身","日久生情"],"book_price":0,"book_updatetime":0,"book_category":[{"class_id":"2","class_name":"总裁豪门"}],"author_id":"1335351","author_name":"爱吃糖的猫","author_avatar":"https://img.huaxi.net/userhead/1335351.jpg","last_update_chapter_id":617308,"last_update_chapter_datetime":1517187600,"last_update_chapter_title":"第27章  慌了起来","last_update_chapter_intro":"第27章  慌了起来","create_time":1513929422},{"book_id":1198,"book_name":"唯有美食不可辜负","book_image":"https://img.huaxi.net/upload/bookimage/2017/12/20171208115156747.jpg","book_intro":"安千千的父母在一场意外的车祸去世，醒来的安千千要承受父母双亡和自己面部创伤导致的面神经瘫痪的痛苦，还要面对四个亡者家属的巨额赔偿追责，四个月的官司拖垮了她父母的所有积蓄，包括那凝聚了一家人心血的小店。\n\n无奈回到乡下的她，却意外得到了父亲曾经拥有的美食系统。\n\n旁人的恶意，种植的艰难，系统的任务，对厨艺一窍不通的安千千，是否能够依靠系统完成这一场人生的历练。\n\n美食对于人们而言，是什么？\n\n安千千：唯有美食不可辜负，美食不是食物，食物仅仅饱腹，而美食则是让人欢喜。\n\n","book_share_url":"http://w.huaxi.net/1198","book_url":"huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_detail%3Fformat%3Dhtml%26book_id%3D1198","book_total_score":9,"book_total_reads":1,"book_finish_flag":0,"book_is_vip":2,"book_content_byte":30437,"book_chapter_total":12,"book_keywords":["美食","系统流","升级","爽文"],"book_price":0,"book_updatetime":0,"book_category":[{"class_id":"4","class_name":"休闲种田"}],"author_id":"23236","author_name":"咸鱼人参","author_avatar":"https://img.huaxi.net/userhead/23236.jpg","last_update_chapter_id":616777,"last_update_chapter_datetime":1517014753,"last_update_chapter_title":"第十二章 比赛是不是有奖金","last_update_chapter_intro":"第十二章 比赛是不是有奖金","create_time":1511856292}]
     * recentBookList : [{"book_id":732,"book_name":"撕婚十年","book_image":"https://img.huaxi.net/upload/bookimage/2017/9/20170905164726796.jpg","book_intro":"我大学毕业就结婚了，老公是闺蜜的前任！\n关系是有些复杂，但是别误会，我不是趁机上位的绿茶。\n我和老公周翊辰是在他们分手一年以后才结婚的，准确的说我们是契约婚姻。\n真正的豪门婚姻与生活是什么模样，由我娓娓道来\u2026\u2026","book_share_url":"http://w.huaxi.net/732","book_url":"huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_detail%3Fformat%3Dhtml%26book_id%3D732","book_total_score":9,"book_total_reads":1,"book_finish_flag":0,"book_is_vip":3,"book_content_byte":452991,"book_chapter_total":205,"book_keywords":["虐恋","总裁","豪门","励志"],"book_price":0,"book_updatetime":0,"book_category":[{"class_id":"2","class_name":"总裁豪门"}],"author_id":"655035","author_name":"乔梓宸","author_avatar":"https://img.huaxi.net/userhead/655035.jpg","last_update_chapter_id":646674,"last_update_chapter_datetime":1517249172,"last_update_chapter_title":"第205章 击败如梦","last_update_chapter_intro":"第205章 击败如梦","create_time":1504244903},{"book_id":1103,"book_name":"实力护妻","book_image":"https://img.huaxi.net/upload/bookimage/2017/11/2017111314543723.jpg","book_intro":"为了利益他选择和她结婚，但是却和自己公司下属有些暧昧关系，\n为了报复未婚夫的背叛而签下了一张假结婚协议，\n卖了自己，上了贼船，被提出了同居的要求，\n一次次的掉进他设计的陷阱，可是上了贼船想回头却很难。\n终究被伤的片体鳞伤，怀着孩子消失\u2026\u2026\n五年后，她再次出现，\n这次他放话\u201c这一次，我再也不要你离开我身边，做我老婆，好不好？\u201d\n\n","book_share_url":"http://w.huaxi.net/1103","book_url":"huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_detail%3Fformat%3Dhtml%26book_id%3D1103","book_total_score":9,"book_total_reads":1,"book_finish_flag":0,"book_is_vip":3,"book_content_byte":437201,"book_chapter_total":257,"book_keywords":["虐恋","暧昧","总裁","弃妇","豪门","别后重逢","别后重逢","女强"],"book_price":0,"book_updatetime":0,"book_category":[{"class_id":"2","class_name":"总裁豪门"}],"author_id":"1027658","author_name":"玥笼纱","author_avatar":"https://img.huaxi.net/userhead/1027658.jpg","last_update_chapter_id":646673,"last_update_chapter_datetime":1517246159,"last_update_chapter_title":"第257章  夏菡害怕","last_update_chapter_intro":"第257章  夏菡害怕","create_time":1509532420},{"book_id":843,"book_name":"爱你在雷霆之间","book_image":"https://img.huaxi.net/upload/bookimage/2017/10/20171021211618962.jpg","book_intro":"新婚夜，我的老公在隔壁房间搞基，我被小叔带到房间度过新婚夜，第二天被婆婆老公抓奸在床，看着倚靠在门口的小叔，这才明白，我的小叔竟然是我的前男友\u2026\u2026","book_share_url":"http://w.huaxi.net/843","book_url":"huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_detail%3Fformat%3Dhtml%26book_id%3D843","book_total_score":9,"book_total_reads":1,"book_finish_flag":0,"book_is_vip":3,"book_content_byte":442017,"book_chapter_total":218,"book_keywords":["轻松","总裁","日久生情","复仇"],"book_price":0,"book_updatetime":0,"book_category":[{"class_id":"16","class_name":""}],"author_id":"130","author_name":"梦回千年","author_avatar":"https://img.huaxi.net/userhead/130.jpg","last_update_chapter_id":646666,"last_update_chapter_datetime":1517245277,"last_update_chapter_title":"第100章伪装，报复100","last_update_chapter_intro":"第100章伪装，报复100","create_time":1508311219},{"book_id":29,"book_name":"狼性总裁请自重","book_image":"https://img.huaxi.net/upload/bookimage/2017/4/20170417194817792.jpg","book_intro":"她爱他，倾尽所有。\n他娶她，迫不得已。\n婚后，他对她百般折磨，她低头忍受，直到将爱耗尽。\n\u201c离婚！\u201d米雪说的决绝。\n\u201c想离婚，除非我对你这具躯壳厌烦！\u201d林慕寒捏着她的下巴。\n小女人能屈能伸，不离就不离，她不再将他当成她的天，不再迁就他的喜好，开心做自己。\n\u201c林太太，你这样将我推给别人，我很伤心！\u201d\n\u201c林太太，随便花，别省着。\u201d\n\u201c林太太，不如生个娃吧！\u201d\n谁来告诉她，这是怎么回事？\n\n每天更新两章，时间为上午九点。","book_share_url":"http://w.huaxi.net/29","book_url":"huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_detail%3Fformat%3Dhtml%26book_id%3D29","book_total_score":9,"book_total_reads":1,"book_finish_flag":0,"book_is_vip":3,"book_content_byte":1344024,"book_chapter_total":430,"book_keywords":["宠文","总裁","世家","爽文"],"book_price":0,"book_updatetime":0,"book_category":[{"class_id":"2","class_name":"总裁豪门"}],"author_id":"212","author_name":"我就是静静","author_avatar":"https://img.huaxi.net/userhead/212.jpg","last_update_chapter_id":646654,"last_update_chapter_datetime":1517241591,"last_update_chapter_title":"第430章  突发事件","last_update_chapter_intro":"第430章  突发事件","create_time":1487317635}]
     * hashExtrainfo : {"27":{"islianzai":"0","noveltag":"宠文"},"29":{"islianzai":"1","noveltag":"宠文"},"282":{"islianzai":"0","noveltag":"正剧"},"703":{"islianzai":"0","noveltag":"校花"},"732":{"islianzai":"1","noveltag":"虐恋"},"739":{"islianzai":"1","noveltag":"爆笑"},"827":{"islianzai":"1","noveltag":"婚恋，总裁，都市虐恋"},"843":{"islianzai":"1","noveltag":"轻松"},"1103":{"islianzai":"1","noveltag":"虐恋"},"1108":{"islianzai":"0","noveltag":"虐恋"},"1198":{"islianzai":"1","noveltag":"美食"},"1379":{"islianzai":"1","noveltag":"虐恋"},"1391":{"islianzai":"1","noveltag":"重生"},"1402":{"islianzai":"1","noveltag":"正剧"},"1464":{"islianzai":"1","noveltag":"虐恋"}}
     * classicalBookUrl : huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_finish%3Fformat%3Dhtml%26is_new%3D1
     * recentBookUrl : huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_news%3Fformat%3Dhtml%26is_new%3D1
     * freeNewBookUrl : huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_freenew%3Fformat%3Dhtml
     * recommandBookUrl : huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_recommand%3Fformat%3Dhtml
     * code : 10000
     */

    private String status;
    private String classicalBookUrl;
    private String recentBookUrl;
    private String freeNewBookUrl;
    private String recommandBookUrl;
    private int code;
    private List<TopBannerListsBean> top_banner_lists;//轮播
    private List<BookDetailResponse> recommandBookList;//重磅推荐
    private List<BookDetailResponse> freelimitBookList;//限时免费
    private List<BookDetailResponse> classicalBookList;//经典完本
    private List<BookDetailResponse> freeNewBookList;//免费新书
    private List<BookDetailResponse> recentBookList;//最近更新
    private List<BookDetailResponse> discountlimitBookList;//限时折扣
    //男女生频道
    @SerializedName(value = "man_woman_one", alternate = {"location_10", "location_12"})
    private List<BookDetailResponse> man_woman_one;//女生:现言总裁;男生:玄幻仙侠
    @SerializedName(value = "man_woman_two", alternate = {"location_11", "location_13"})
    private List<BookDetailResponse> man_woman_two;//女生:古言穿越;男生:都市传说

    //男女生频道
    @SerializedName(value = "man_woman_one_url", alternate = {"location_10_url", "location_12_url"})
    private String man_woman_one_url;
    //男女生频道
    @SerializedName(value = "man_woman_two_url", alternate = {"location_11_url", "location_13_url"})
    private String man_woman_two_url;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClassicalBookUrl() {
        return classicalBookUrl;
    }

    public void setClassicalBookUrl(String classicalBookUrl) {
        this.classicalBookUrl = classicalBookUrl;
    }

    public String getMan_woman_one_url() {
        return man_woman_one_url;
    }

    public void setMan_woman_one_url(String man_woman_one_url) {
        this.man_woman_one_url = man_woman_one_url;
    }

    public String getMan_woman_two_url() {
        return man_woman_two_url;
    }

    public void setMan_woman_two_url(String man_woman_two_url) {
        this.man_woman_two_url = man_woman_two_url;
    }

    public String getRecentBookUrl() {
        return recentBookUrl;
    }

    public void setRecentBookUrl(String recentBookUrl) {
        this.recentBookUrl = recentBookUrl;
    }

    public String getFreeNewBookUrl() {
        return freeNewBookUrl;
    }

    public void setFreeNewBookUrl(String freeNewBookUrl) {
        this.freeNewBookUrl = freeNewBookUrl;
    }

    public String getRecommandBookUrl() {
        return recommandBookUrl;
    }

    public void setRecommandBookUrl(String recommandBookUrl) {
        this.recommandBookUrl = recommandBookUrl;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<TopBannerListsBean> getTop_banner_lists() {
        return top_banner_lists;
    }

    public void setTop_banner_lists(List<TopBannerListsBean> top_banner_lists) {
        this.top_banner_lists = top_banner_lists;
    }

    public List<BookDetailResponse> getRecommandBookList() {
        return recommandBookList;
    }

    public void setRecommandBookList(List<BookDetailResponse> recommandBookList) {
        this.recommandBookList = recommandBookList;
    }

    public List<BookDetailResponse> getFreelimitBookList() {
        return freelimitBookList;
    }

    public void setFreelimitBookList(List<BookDetailResponse> freelimitBookList) {
        this.freelimitBookList = freelimitBookList;
    }

    public List<BookDetailResponse> getClassicalBookList() {
        return classicalBookList;
    }

    public void setClassicalBookList(List<BookDetailResponse> classicalBookList) {
        this.classicalBookList = classicalBookList;
    }

    public List<BookDetailResponse> getFreeNewBookList() {
        return freeNewBookList;
    }

    public void setFreeNewBookList(List<BookDetailResponse> freeNewBookList) {
        this.freeNewBookList = freeNewBookList;
    }

    public List<BookDetailResponse> getRecentBookList() {
        return recentBookList;
    }

    public void setRecentBookList(List<BookDetailResponse> recentBookList) {
        this.recentBookList = recentBookList;
    }

    public List<BookDetailResponse> getDiscountlimitBookList() {
        return discountlimitBookList;
    }

    public void setDiscountlimitBookList(List<BookDetailResponse> discountlimitBookList) {
        this.discountlimitBookList = discountlimitBookList;
    }

    public List<BookDetailResponse> getMan_woman_one() {
        return man_woman_one;
    }

    public void setMan_woman_one(List<BookDetailResponse> man_woman_one) {
        this.man_woman_one = man_woman_one;
    }

    public List<BookDetailResponse> getMan_woman_two() {
        return man_woman_two;
    }

    public void setMan_woman_two(List<BookDetailResponse> man_woman_two) {
        this.man_woman_two = man_woman_two;
    }

    public static class HashExtrainfoBean {
        /**
         * 27 : {"islianzai":"0","noveltag":"宠文"}
         * 29 : {"islianzai":"1","noveltag":"宠文"}
         * 282 : {"islianzai":"0","noveltag":"正剧"}
         * 703 : {"islianzai":"0","noveltag":"校花"}
         * 732 : {"islianzai":"1","noveltag":"虐恋"}
         * 739 : {"islianzai":"1","noveltag":"爆笑"}
         * 827 : {"islianzai":"1","noveltag":"婚恋，总裁，都市虐恋"}
         * 843 : {"islianzai":"1","noveltag":"轻松"}
         * 1103 : {"islianzai":"1","noveltag":"虐恋"}
         * 1108 : {"islianzai":"0","noveltag":"虐恋"}
         * 1198 : {"islianzai":"1","noveltag":"美食"}
         * 1379 : {"islianzai":"1","noveltag":"虐恋"}
         * 1391 : {"islianzai":"1","noveltag":"重生"}
         * 1402 : {"islianzai":"1","noveltag":"正剧"}
         * 1464 : {"islianzai":"1","noveltag":"虐恋"}
         */

        @SerializedName("27")
        private _$27Bean _$27;
        @SerializedName("29")
        private _$29Bean _$29;
        @SerializedName("282")
        private _$282Bean _$282;
        @SerializedName("703")
        private _$703Bean _$703;
        @SerializedName("732")
        private _$732Bean _$732;
        @SerializedName("739")
        private _$739Bean _$739;
        @SerializedName("827")
        private _$827Bean _$827;
        @SerializedName("843")
        private _$843Bean _$843;
        @SerializedName("1103")
        private _$1103Bean _$1103;
        @SerializedName("1108")
        private _$1108Bean _$1108;
        @SerializedName("1198")
        private _$1198Bean _$1198;
        @SerializedName("1379")
        private _$1379Bean _$1379;
        @SerializedName("1391")
        private _$1391Bean _$1391;
        @SerializedName("1402")
        private _$1402Bean _$1402;
        @SerializedName("1464")
        private _$1464Bean _$1464;

        public _$27Bean get_$27() {
            return _$27;
        }

        public void set_$27(_$27Bean _$27) {
            this._$27 = _$27;
        }

        public _$29Bean get_$29() {
            return _$29;
        }

        public void set_$29(_$29Bean _$29) {
            this._$29 = _$29;
        }

        public _$282Bean get_$282() {
            return _$282;
        }

        public void set_$282(_$282Bean _$282) {
            this._$282 = _$282;
        }

        public _$703Bean get_$703() {
            return _$703;
        }

        public void set_$703(_$703Bean _$703) {
            this._$703 = _$703;
        }

        public _$732Bean get_$732() {
            return _$732;
        }

        public void set_$732(_$732Bean _$732) {
            this._$732 = _$732;
        }

        public _$739Bean get_$739() {
            return _$739;
        }

        public void set_$739(_$739Bean _$739) {
            this._$739 = _$739;
        }

        public _$827Bean get_$827() {
            return _$827;
        }

        public void set_$827(_$827Bean _$827) {
            this._$827 = _$827;
        }

        public _$843Bean get_$843() {
            return _$843;
        }

        public void set_$843(_$843Bean _$843) {
            this._$843 = _$843;
        }

        public _$1103Bean get_$1103() {
            return _$1103;
        }

        public void set_$1103(_$1103Bean _$1103) {
            this._$1103 = _$1103;
        }

        public _$1108Bean get_$1108() {
            return _$1108;
        }

        public void set_$1108(_$1108Bean _$1108) {
            this._$1108 = _$1108;
        }

        public _$1198Bean get_$1198() {
            return _$1198;
        }

        public void set_$1198(_$1198Bean _$1198) {
            this._$1198 = _$1198;
        }

        public _$1379Bean get_$1379() {
            return _$1379;
        }

        public void set_$1379(_$1379Bean _$1379) {
            this._$1379 = _$1379;
        }

        public _$1391Bean get_$1391() {
            return _$1391;
        }

        public void set_$1391(_$1391Bean _$1391) {
            this._$1391 = _$1391;
        }

        public _$1402Bean get_$1402() {
            return _$1402;
        }

        public void set_$1402(_$1402Bean _$1402) {
            this._$1402 = _$1402;
        }

        public _$1464Bean get_$1464() {
            return _$1464;
        }

        public void set_$1464(_$1464Bean _$1464) {
            this._$1464 = _$1464;
        }

        public static class _$27Bean {
            /**
             * islianzai : 0
             * noveltag : 宠文
             */

            private String islianzai;
            private String noveltag;

            public String getIslianzai() {
                return islianzai;
            }

            public void setIslianzai(String islianzai) {
                this.islianzai = islianzai;
            }

            public String getNoveltag() {
                return noveltag;
            }

            public void setNoveltag(String noveltag) {
                this.noveltag = noveltag;
            }
        }

        public static class _$29Bean {
            /**
             * islianzai : 1
             * noveltag : 宠文
             */

            private String islianzai;
            private String noveltag;

            public String getIslianzai() {
                return islianzai;
            }

            public void setIslianzai(String islianzai) {
                this.islianzai = islianzai;
            }

            public String getNoveltag() {
                return noveltag;
            }

            public void setNoveltag(String noveltag) {
                this.noveltag = noveltag;
            }
        }

        public static class _$282Bean {
            /**
             * islianzai : 0
             * noveltag : 正剧
             */

            private String islianzai;
            private String noveltag;

            public String getIslianzai() {
                return islianzai;
            }

            public void setIslianzai(String islianzai) {
                this.islianzai = islianzai;
            }

            public String getNoveltag() {
                return noveltag;
            }

            public void setNoveltag(String noveltag) {
                this.noveltag = noveltag;
            }
        }

        public static class _$703Bean {
            /**
             * islianzai : 0
             * noveltag : 校花
             */

            private String islianzai;
            private String noveltag;

            public String getIslianzai() {
                return islianzai;
            }

            public void setIslianzai(String islianzai) {
                this.islianzai = islianzai;
            }

            public String getNoveltag() {
                return noveltag;
            }

            public void setNoveltag(String noveltag) {
                this.noveltag = noveltag;
            }
        }

        public static class _$732Bean {
            /**
             * islianzai : 1
             * noveltag : 虐恋
             */

            private String islianzai;
            private String noveltag;

            public String getIslianzai() {
                return islianzai;
            }

            public void setIslianzai(String islianzai) {
                this.islianzai = islianzai;
            }

            public String getNoveltag() {
                return noveltag;
            }

            public void setNoveltag(String noveltag) {
                this.noveltag = noveltag;
            }
        }

        public static class _$739Bean {
            /**
             * islianzai : 1
             * noveltag : 爆笑
             */

            private String islianzai;
            private String noveltag;

            public String getIslianzai() {
                return islianzai;
            }

            public void setIslianzai(String islianzai) {
                this.islianzai = islianzai;
            }

            public String getNoveltag() {
                return noveltag;
            }

            public void setNoveltag(String noveltag) {
                this.noveltag = noveltag;
            }
        }

        public static class _$827Bean {
            /**
             * islianzai : 1
             * noveltag : 婚恋，总裁，都市虐恋
             */

            private String islianzai;
            private String noveltag;

            public String getIslianzai() {
                return islianzai;
            }

            public void setIslianzai(String islianzai) {
                this.islianzai = islianzai;
            }

            public String getNoveltag() {
                return noveltag;
            }

            public void setNoveltag(String noveltag) {
                this.noveltag = noveltag;
            }
        }

        public static class _$843Bean {
            /**
             * islianzai : 1
             * noveltag : 轻松
             */

            private String islianzai;
            private String noveltag;

            public String getIslianzai() {
                return islianzai;
            }

            public void setIslianzai(String islianzai) {
                this.islianzai = islianzai;
            }

            public String getNoveltag() {
                return noveltag;
            }

            public void setNoveltag(String noveltag) {
                this.noveltag = noveltag;
            }
        }

        public static class _$1103Bean {
            /**
             * islianzai : 1
             * noveltag : 虐恋
             */

            private String islianzai;
            private String noveltag;

            public String getIslianzai() {
                return islianzai;
            }

            public void setIslianzai(String islianzai) {
                this.islianzai = islianzai;
            }

            public String getNoveltag() {
                return noveltag;
            }

            public void setNoveltag(String noveltag) {
                this.noveltag = noveltag;
            }
        }

        public static class _$1108Bean {
            /**
             * islianzai : 0
             * noveltag : 虐恋
             */

            private String islianzai;
            private String noveltag;

            public String getIslianzai() {
                return islianzai;
            }

            public void setIslianzai(String islianzai) {
                this.islianzai = islianzai;
            }

            public String getNoveltag() {
                return noveltag;
            }

            public void setNoveltag(String noveltag) {
                this.noveltag = noveltag;
            }
        }

        public static class _$1198Bean {
            /**
             * islianzai : 1
             * noveltag : 美食
             */

            private String islianzai;
            private String noveltag;

            public String getIslianzai() {
                return islianzai;
            }

            public void setIslianzai(String islianzai) {
                this.islianzai = islianzai;
            }

            public String getNoveltag() {
                return noveltag;
            }

            public void setNoveltag(String noveltag) {
                this.noveltag = noveltag;
            }
        }

        public static class _$1379Bean {
            /**
             * islianzai : 1
             * noveltag : 虐恋
             */

            private String islianzai;
            private String noveltag;

            public String getIslianzai() {
                return islianzai;
            }

            public void setIslianzai(String islianzai) {
                this.islianzai = islianzai;
            }

            public String getNoveltag() {
                return noveltag;
            }

            public void setNoveltag(String noveltag) {
                this.noveltag = noveltag;
            }
        }

        public static class _$1391Bean {
            /**
             * islianzai : 1
             * noveltag : 重生
             */

            private String islianzai;
            private String noveltag;

            public String getIslianzai() {
                return islianzai;
            }

            public void setIslianzai(String islianzai) {
                this.islianzai = islianzai;
            }

            public String getNoveltag() {
                return noveltag;
            }

            public void setNoveltag(String noveltag) {
                this.noveltag = noveltag;
            }
        }

        public static class _$1402Bean {
            /**
             * islianzai : 1
             * noveltag : 正剧
             */

            private String islianzai;
            private String noveltag;

            public String getIslianzai() {
                return islianzai;
            }

            public void setIslianzai(String islianzai) {
                this.islianzai = islianzai;
            }

            public String getNoveltag() {
                return noveltag;
            }

            public void setNoveltag(String noveltag) {
                this.noveltag = noveltag;
            }
        }

        public static class _$1464Bean {
            /**
             * islianzai : 1
             * noveltag : 虐恋
             */

            private String islianzai;
            private String noveltag;

            public String getIslianzai() {
                return islianzai;
            }

            public void setIslianzai(String islianzai) {
                this.islianzai = islianzai;
            }

            public String getNoveltag() {
                return noveltag;
            }

            public void setNoveltag(String noveltag) {
                this.noveltag = noveltag;
            }
        }
    }

    public static class TopBannerListsBean {

        private String images;
        private String url;
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getImages() {
            return images;
        }

        public void setImages(String images) {
            this.images = images;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class RecommandBookListBean {
        /**
         * book_id : 1464
         * book_name : 重生之恕罪凤妃
         * book_image : https://img.huaxi.net/upload/bookimage/2018/1/20180122160406114.jpg
         * book_intro : 前一世，她是他身边的细作，  　　却不想杀了他的骨肉，灭了他的天下，她才发现自己真正爱的人竟然一直都是他！再次重生，她愿坠入阿鼻地狱，只为了——  “阿谨，这一世，换我来守护你！” 可是……
         * book_share_url : http://w.huaxi.net/1464
         * book_url : huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_detail%3Fformat%3Dhtml%26book_id%3D1464
         * book_total_score : 9
         * book_total_reads : 1
         * book_finish_flag : 0
         * book_is_vip : 3
         * book_content_byte : 79320
         * book_chapter_total : 39
         * book_keywords : ["虐恋","皇后","复合","重生"]
         * book_price : 0
         * book_updatetime : 0
         * book_category : [{"class_id":"3","class_name":"穿越架空"}]
         * author_id : 1385476
         * author_name : 伃我
         * author_avatar : https://img.huaxi.net/userhead/1385476.jpg
         * last_update_chapter_id : 630079
         * last_update_chapter_datetime : 1517191917
         * last_update_chapter_title : 第039章 故人相见
         * last_update_chapter_intro : 第039章 故人相见
         * create_time : 1515572812
         */

        private int book_id;
        private String book_name;
        private String book_image;
        private String book_intro;
        private String book_share_url;
        private String book_url;
        private int book_total_score;
        private int book_total_reads;
        private int book_finish_flag;
        private int book_is_vip;
        private int book_content_byte;
        private int book_chapter_total;
        private int book_price;
        private int book_updatetime;
        private String author_id;
        private String author_name;
        private String author_avatar;
        private int last_update_chapter_id;
        private int last_update_chapter_datetime;
        private String last_update_chapter_title;
        private String last_update_chapter_intro;
        private int create_time;
        private List<String> book_keywords;
        private List<BookCategoryBean> book_category;

        public int getBook_id() {
            return book_id;
        }

        public void setBook_id(int book_id) {
            this.book_id = book_id;
        }

        public String getBook_name() {
            return book_name;
        }

        public void setBook_name(String book_name) {
            this.book_name = book_name;
        }

        public String getBook_image() {
            return book_image;
        }

        public void setBook_image(String book_image) {
            this.book_image = book_image;
        }

        public String getBook_intro() {
            return book_intro;
        }

        public void setBook_intro(String book_intro) {
            this.book_intro = book_intro;
        }

        public String getBook_share_url() {
            return book_share_url;
        }

        public void setBook_share_url(String book_share_url) {
            this.book_share_url = book_share_url;
        }

        public String getBook_url() {
            return book_url;
        }

        public void setBook_url(String book_url) {
            this.book_url = book_url;
        }

        public int getBook_total_score() {
            return book_total_score;
        }

        public void setBook_total_score(int book_total_score) {
            this.book_total_score = book_total_score;
        }

        public int getBook_total_reads() {
            return book_total_reads;
        }

        public void setBook_total_reads(int book_total_reads) {
            this.book_total_reads = book_total_reads;
        }

        public int getBook_finish_flag() {
            return book_finish_flag;
        }

        public void setBook_finish_flag(int book_finish_flag) {
            this.book_finish_flag = book_finish_flag;
        }

        public int getBook_is_vip() {
            return book_is_vip;
        }

        public void setBook_is_vip(int book_is_vip) {
            this.book_is_vip = book_is_vip;
        }

        public int getBook_content_byte() {
            return book_content_byte;
        }

        public void setBook_content_byte(int book_content_byte) {
            this.book_content_byte = book_content_byte;
        }

        public int getBook_chapter_total() {
            return book_chapter_total;
        }

        public void setBook_chapter_total(int book_chapter_total) {
            this.book_chapter_total = book_chapter_total;
        }

        public int getBook_price() {
            return book_price;
        }

        public void setBook_price(int book_price) {
            this.book_price = book_price;
        }

        public int getBook_updatetime() {
            return book_updatetime;
        }

        public void setBook_updatetime(int book_updatetime) {
            this.book_updatetime = book_updatetime;
        }

        public String getAuthor_id() {
            return author_id;
        }

        public void setAuthor_id(String author_id) {
            this.author_id = author_id;
        }

        public String getAuthor_name() {
            return author_name;
        }

        public void setAuthor_name(String author_name) {
            this.author_name = author_name;
        }

        public String getAuthor_avatar() {
            return author_avatar;
        }

        public void setAuthor_avatar(String author_avatar) {
            this.author_avatar = author_avatar;
        }

        public int getLast_update_chapter_id() {
            return last_update_chapter_id;
        }

        public void setLast_update_chapter_id(int last_update_chapter_id) {
            this.last_update_chapter_id = last_update_chapter_id;
        }

        public int getLast_update_chapter_datetime() {
            return last_update_chapter_datetime;
        }

        public void setLast_update_chapter_datetime(int last_update_chapter_datetime) {
            this.last_update_chapter_datetime = last_update_chapter_datetime;
        }

        public String getLast_update_chapter_title() {
            return last_update_chapter_title;
        }

        public void setLast_update_chapter_title(String last_update_chapter_title) {
            this.last_update_chapter_title = last_update_chapter_title;
        }

        public String getLast_update_chapter_intro() {
            return last_update_chapter_intro;
        }

        public void setLast_update_chapter_intro(String last_update_chapter_intro) {
            this.last_update_chapter_intro = last_update_chapter_intro;
        }

        public int getCreate_time() {
            return create_time;
        }

        public void setCreate_time(int create_time) {
            this.create_time = create_time;
        }

        public List<String> getBook_keywords() {
            return book_keywords;
        }

        public void setBook_keywords(List<String> book_keywords) {
            this.book_keywords = book_keywords;
        }

        public List<BookCategoryBean> getBook_category() {
            return book_category;
        }

        public void setBook_category(List<BookCategoryBean> book_category) {
            this.book_category = book_category;
        }

        public static class BookCategoryBean {
            /**
             * class_id : 3
             * class_name : 穿越架空
             */

            private String class_id;
            private String class_name;

            public String getClass_id() {
                return class_id;
            }

            public void setClass_id(String class_id) {
                this.class_id = class_id;
            }

            public String getClass_name() {
                return class_name;
            }

            public void setClass_name(String class_name) {
                this.class_name = class_name;
            }
        }
    }

    public static class ClassicalBookListBean {
        /**
         * book_id : 1108
         * book_name : 司徒先生喜当爹
         * book_image : https://img.huaxi.net/upload/bookimage/2017/11/20171108114258344.jpg
         * book_intro : "这辈子你是逃不掉的！”
         * 夜星儿嘴角一撇。
         * “司徒先生，我只是来告诉你一声，你快当爹了。”
         * book_share_url : http://w.huaxi.net/1108
         * book_url : huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_detail%3Fformat%3Dhtml%26book_id%3D1108
         * book_total_score : 9
         * book_total_reads : 1
         * book_finish_flag : 1
         * book_is_vip : 3
         * book_content_byte : 586911
         * book_chapter_total : 290
         * book_keywords : ["虐恋","总裁","豪门"]
         * book_price : 0
         * book_updatetime : 0
         * book_category : [{"class_id":"2","class_name":"总裁豪门"}]
         * author_id : 995649
         * author_name : 钱串子
         * author_avatar : https://img.huaxi.net/userhead/995649.jpg
         * last_update_chapter_id : 563686
         * last_update_chapter_datetime : 1516371657
         * last_update_chapter_title : 　　第290章 盛世婚礼
         * last_update_chapter_intro : 　　第290章 盛世婚礼
         * create_time : 1509547911
         */

        private int book_id;
        private String book_name;
        private String book_image;
        private String book_intro;
        private String book_share_url;
        private String book_url;
        private int book_total_score;
        private int book_total_reads;
        private int book_finish_flag;
        private int book_is_vip;
        private int book_content_byte;
        private int book_chapter_total;
        private int book_price;
        private int book_updatetime;
        private String author_id;
        private String author_name;
        private String author_avatar;
        private int last_update_chapter_id;
        private int last_update_chapter_datetime;
        private String last_update_chapter_title;
        private String last_update_chapter_intro;
        private int create_time;
        private List<String> book_keywords;
        private List<BookCategoryBeanX> book_category;

        public int getBook_id() {
            return book_id;
        }

        public void setBook_id(int book_id) {
            this.book_id = book_id;
        }

        public String getBook_name() {
            return book_name;
        }

        public void setBook_name(String book_name) {
            this.book_name = book_name;
        }

        public String getBook_image() {
            return book_image;
        }

        public void setBook_image(String book_image) {
            this.book_image = book_image;
        }

        public String getBook_intro() {
            return book_intro;
        }

        public void setBook_intro(String book_intro) {
            this.book_intro = book_intro;
        }

        public String getBook_share_url() {
            return book_share_url;
        }

        public void setBook_share_url(String book_share_url) {
            this.book_share_url = book_share_url;
        }

        public String getBook_url() {
            return book_url;
        }

        public void setBook_url(String book_url) {
            this.book_url = book_url;
        }

        public int getBook_total_score() {
            return book_total_score;
        }

        public void setBook_total_score(int book_total_score) {
            this.book_total_score = book_total_score;
        }

        public int getBook_total_reads() {
            return book_total_reads;
        }

        public void setBook_total_reads(int book_total_reads) {
            this.book_total_reads = book_total_reads;
        }

        public int getBook_finish_flag() {
            return book_finish_flag;
        }

        public void setBook_finish_flag(int book_finish_flag) {
            this.book_finish_flag = book_finish_flag;
        }

        public int getBook_is_vip() {
            return book_is_vip;
        }

        public void setBook_is_vip(int book_is_vip) {
            this.book_is_vip = book_is_vip;
        }

        public int getBook_content_byte() {
            return book_content_byte;
        }

        public void setBook_content_byte(int book_content_byte) {
            this.book_content_byte = book_content_byte;
        }

        public int getBook_chapter_total() {
            return book_chapter_total;
        }

        public void setBook_chapter_total(int book_chapter_total) {
            this.book_chapter_total = book_chapter_total;
        }

        public int getBook_price() {
            return book_price;
        }

        public void setBook_price(int book_price) {
            this.book_price = book_price;
        }

        public int getBook_updatetime() {
            return book_updatetime;
        }

        public void setBook_updatetime(int book_updatetime) {
            this.book_updatetime = book_updatetime;
        }

        public String getAuthor_id() {
            return author_id;
        }

        public void setAuthor_id(String author_id) {
            this.author_id = author_id;
        }

        public String getAuthor_name() {
            return author_name;
        }

        public void setAuthor_name(String author_name) {
            this.author_name = author_name;
        }

        public String getAuthor_avatar() {
            return author_avatar;
        }

        public void setAuthor_avatar(String author_avatar) {
            this.author_avatar = author_avatar;
        }

        public int getLast_update_chapter_id() {
            return last_update_chapter_id;
        }

        public void setLast_update_chapter_id(int last_update_chapter_id) {
            this.last_update_chapter_id = last_update_chapter_id;
        }

        public int getLast_update_chapter_datetime() {
            return last_update_chapter_datetime;
        }

        public void setLast_update_chapter_datetime(int last_update_chapter_datetime) {
            this.last_update_chapter_datetime = last_update_chapter_datetime;
        }

        public String getLast_update_chapter_title() {
            return last_update_chapter_title;
        }

        public void setLast_update_chapter_title(String last_update_chapter_title) {
            this.last_update_chapter_title = last_update_chapter_title;
        }

        public String getLast_update_chapter_intro() {
            return last_update_chapter_intro;
        }

        public void setLast_update_chapter_intro(String last_update_chapter_intro) {
            this.last_update_chapter_intro = last_update_chapter_intro;
        }

        public int getCreate_time() {
            return create_time;
        }

        public void setCreate_time(int create_time) {
            this.create_time = create_time;
        }

        public List<String> getBook_keywords() {
            return book_keywords;
        }

        public void setBook_keywords(List<String> book_keywords) {
            this.book_keywords = book_keywords;
        }

        public List<BookCategoryBeanX> getBook_category() {
            return book_category;
        }

        public void setBook_category(List<BookCategoryBeanX> book_category) {
            this.book_category = book_category;
        }

        public static class BookCategoryBeanX {
            /**
             * class_id : 2
             * class_name : 总裁豪门
             */

            private String class_id;
            private String class_name;

            public String getClass_id() {
                return class_id;
            }

            public void setClass_id(String class_id) {
                this.class_id = class_id;
            }

            public String getClass_name() {
                return class_name;
            }

            public void setClass_name(String class_name) {
                this.class_name = class_name;
            }
        }
    }

    public static class FreeNewBookListBean {
        /**
         * book_id : 1391
         * book_name : 重生之一笑倾城
         * book_image : https://img.huaxi.net/upload/bookimage/2018/1/20180116164141387.jpg
         * book_intro : 一场权谋，佳人为棋，三年苦楚，一朝丧命！
         * 一梦回溯，重生三年前，花烛红妆，今生掌控不屈的命运；
         * 弱女归，长风破浪会有时，一笑倾城，双宿双飞。
         * <p>
         * book_share_url : http://w.huaxi.net/1391
         * book_url : huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_detail%3Fformat%3Dhtml%26book_id%3D1391
         * book_total_score : 9
         * book_total_reads : 1
         * book_finish_flag : 0
         * book_is_vip : 2
         * book_content_byte : 88003
         * book_chapter_total : 39
         * book_keywords : ["重生","王妃","权谋","唯美"]
         * book_price : 0
         * book_updatetime : 0
         * book_category : [{"class_id":"11","class_name":""}]
         * author_id : 1330954
         * author_name : 三顾佳人
         * author_avatar : https://img.huaxi.net/userhead/1330954.jpg
         * last_update_chapter_id : 646382
         * last_update_chapter_datetime : 1517211376
         * last_update_chapter_title : 第39章 碧螺之死
         * last_update_chapter_intro : 第39章 碧螺之死
         * create_time : 1513835636
         */

        private int book_id;
        private String book_name;
        private String book_image;
        private String book_intro;
        private String book_share_url;
        private String book_url;
        private int book_total_score;
        private int book_total_reads;
        private int book_finish_flag;
        private int book_is_vip;
        private int book_content_byte;
        private int book_chapter_total;
        private int book_price;
        private int book_updatetime;
        private String author_id;
        private String author_name;
        private String author_avatar;
        private int last_update_chapter_id;
        private int last_update_chapter_datetime;
        private String last_update_chapter_title;
        private String last_update_chapter_intro;
        private int create_time;
        private List<String> book_keywords;
        private List<BookCategoryBeanXX> book_category;

        public int getBook_id() {
            return book_id;
        }

        public void setBook_id(int book_id) {
            this.book_id = book_id;
        }

        public String getBook_name() {
            return book_name;
        }

        public void setBook_name(String book_name) {
            this.book_name = book_name;
        }

        public String getBook_image() {
            return book_image;
        }

        public void setBook_image(String book_image) {
            this.book_image = book_image;
        }

        public String getBook_intro() {
            return book_intro;
        }

        public void setBook_intro(String book_intro) {
            this.book_intro = book_intro;
        }

        public String getBook_share_url() {
            return book_share_url;
        }

        public void setBook_share_url(String book_share_url) {
            this.book_share_url = book_share_url;
        }

        public String getBook_url() {
            return book_url;
        }

        public void setBook_url(String book_url) {
            this.book_url = book_url;
        }

        public int getBook_total_score() {
            return book_total_score;
        }

        public void setBook_total_score(int book_total_score) {
            this.book_total_score = book_total_score;
        }

        public int getBook_total_reads() {
            return book_total_reads;
        }

        public void setBook_total_reads(int book_total_reads) {
            this.book_total_reads = book_total_reads;
        }

        public int getBook_finish_flag() {
            return book_finish_flag;
        }

        public void setBook_finish_flag(int book_finish_flag) {
            this.book_finish_flag = book_finish_flag;
        }

        public int getBook_is_vip() {
            return book_is_vip;
        }

        public void setBook_is_vip(int book_is_vip) {
            this.book_is_vip = book_is_vip;
        }

        public int getBook_content_byte() {
            return book_content_byte;
        }

        public void setBook_content_byte(int book_content_byte) {
            this.book_content_byte = book_content_byte;
        }

        public int getBook_chapter_total() {
            return book_chapter_total;
        }

        public void setBook_chapter_total(int book_chapter_total) {
            this.book_chapter_total = book_chapter_total;
        }

        public int getBook_price() {
            return book_price;
        }

        public void setBook_price(int book_price) {
            this.book_price = book_price;
        }

        public int getBook_updatetime() {
            return book_updatetime;
        }

        public void setBook_updatetime(int book_updatetime) {
            this.book_updatetime = book_updatetime;
        }

        public String getAuthor_id() {
            return author_id;
        }

        public void setAuthor_id(String author_id) {
            this.author_id = author_id;
        }

        public String getAuthor_name() {
            return author_name;
        }

        public void setAuthor_name(String author_name) {
            this.author_name = author_name;
        }

        public String getAuthor_avatar() {
            return author_avatar;
        }

        public void setAuthor_avatar(String author_avatar) {
            this.author_avatar = author_avatar;
        }

        public int getLast_update_chapter_id() {
            return last_update_chapter_id;
        }

        public void setLast_update_chapter_id(int last_update_chapter_id) {
            this.last_update_chapter_id = last_update_chapter_id;
        }

        public int getLast_update_chapter_datetime() {
            return last_update_chapter_datetime;
        }

        public void setLast_update_chapter_datetime(int last_update_chapter_datetime) {
            this.last_update_chapter_datetime = last_update_chapter_datetime;
        }

        public String getLast_update_chapter_title() {
            return last_update_chapter_title;
        }

        public void setLast_update_chapter_title(String last_update_chapter_title) {
            this.last_update_chapter_title = last_update_chapter_title;
        }

        public String getLast_update_chapter_intro() {
            return last_update_chapter_intro;
        }

        public void setLast_update_chapter_intro(String last_update_chapter_intro) {
            this.last_update_chapter_intro = last_update_chapter_intro;
        }

        public int getCreate_time() {
            return create_time;
        }

        public void setCreate_time(int create_time) {
            this.create_time = create_time;
        }

        public List<String> getBook_keywords() {
            return book_keywords;
        }

        public void setBook_keywords(List<String> book_keywords) {
            this.book_keywords = book_keywords;
        }

        public List<BookCategoryBeanXX> getBook_category() {
            return book_category;
        }

        public void setBook_category(List<BookCategoryBeanXX> book_category) {
            this.book_category = book_category;
        }

        public static class BookCategoryBeanXX {
            /**
             * class_id : 11
             * class_name :
             */

            private String class_id;
            private String class_name;

            public String getClass_id() {
                return class_id;
            }

            public void setClass_id(String class_id) {
                this.class_id = class_id;
            }

            public String getClass_name() {
                return class_name;
            }

            public void setClass_name(String class_name) {
                this.class_name = class_name;
            }
        }
    }

    public static class RecentBookListBean {
        /**
         * book_id : 732
         * book_name : 撕婚十年
         * book_image : https://img.huaxi.net/upload/bookimage/2017/9/20170905164726796.jpg
         * book_intro : 我大学毕业就结婚了，老公是闺蜜的前任！
         * 关系是有些复杂，但是别误会，我不是趁机上位的绿茶。
         * 我和老公周翊辰是在他们分手一年以后才结婚的，准确的说我们是契约婚姻。
         * 真正的豪门婚姻与生活是什么模样，由我娓娓道来……
         * book_share_url : http://w.huaxi.net/732
         * book_url : huaxi://app?action=openpage&url=https%3A%2F%2Fs.hxdrive.net%2Fbook_detail%3Fformat%3Dhtml%26book_id%3D732
         * book_total_score : 9
         * book_total_reads : 1
         * book_finish_flag : 0
         * book_is_vip : 3
         * book_content_byte : 452991
         * book_chapter_total : 205
         * book_keywords : ["虐恋","总裁","豪门","励志"]
         * book_price : 0
         * book_updatetime : 0
         * book_category : [{"class_id":"2","class_name":"总裁豪门"}]
         * author_id : 655035
         * author_name : 乔梓宸
         * author_avatar : https://img.huaxi.net/userhead/655035.jpg
         * last_update_chapter_id : 646674
         * last_update_chapter_datetime : 1517249172
         * last_update_chapter_title : 第205章 击败如梦
         * last_update_chapter_intro : 第205章 击败如梦
         * create_time : 1504244903
         */

        private int book_id;
        private String book_name;
        private String book_image;
        private String book_intro;
        private String book_share_url;
        private String book_url;
        private int book_total_score;
        private int book_total_reads;
        private int book_finish_flag;
        private int book_is_vip;
        private int book_content_byte;
        private int book_chapter_total;
        private int book_price;
        private int book_updatetime;
        private String author_id;
        private String author_name;
        private String author_avatar;
        private int last_update_chapter_id;
        private int last_update_chapter_datetime;
        private String last_update_chapter_title;
        private String last_update_chapter_intro;
        private int create_time;
        private List<String> book_keywords;
        private List<BookCategoryBeanXXX> book_category;

        public int getBook_id() {
            return book_id;
        }

        public void setBook_id(int book_id) {
            this.book_id = book_id;
        }

        public String getBook_name() {
            return book_name;
        }

        public void setBook_name(String book_name) {
            this.book_name = book_name;
        }

        public String getBook_image() {
            return book_image;
        }

        public void setBook_image(String book_image) {
            this.book_image = book_image;
        }

        public String getBook_intro() {
            return book_intro;
        }

        public void setBook_intro(String book_intro) {
            this.book_intro = book_intro;
        }

        public String getBook_share_url() {
            return book_share_url;
        }

        public void setBook_share_url(String book_share_url) {
            this.book_share_url = book_share_url;
        }

        public String getBook_url() {
            return book_url;
        }

        public void setBook_url(String book_url) {
            this.book_url = book_url;
        }

        public int getBook_total_score() {
            return book_total_score;
        }

        public void setBook_total_score(int book_total_score) {
            this.book_total_score = book_total_score;
        }

        public int getBook_total_reads() {
            return book_total_reads;
        }

        public void setBook_total_reads(int book_total_reads) {
            this.book_total_reads = book_total_reads;
        }

        public int getBook_finish_flag() {
            return book_finish_flag;
        }

        public void setBook_finish_flag(int book_finish_flag) {
            this.book_finish_flag = book_finish_flag;
        }

        public int getBook_is_vip() {
            return book_is_vip;
        }

        public void setBook_is_vip(int book_is_vip) {
            this.book_is_vip = book_is_vip;
        }

        public int getBook_content_byte() {
            return book_content_byte;
        }

        public void setBook_content_byte(int book_content_byte) {
            this.book_content_byte = book_content_byte;
        }

        public int getBook_chapter_total() {
            return book_chapter_total;
        }

        public void setBook_chapter_total(int book_chapter_total) {
            this.book_chapter_total = book_chapter_total;
        }

        public int getBook_price() {
            return book_price;
        }

        public void setBook_price(int book_price) {
            this.book_price = book_price;
        }

        public int getBook_updatetime() {
            return book_updatetime;
        }

        public void setBook_updatetime(int book_updatetime) {
            this.book_updatetime = book_updatetime;
        }

        public String getAuthor_id() {
            return author_id;
        }

        public void setAuthor_id(String author_id) {
            this.author_id = author_id;
        }

        public String getAuthor_name() {
            return author_name;
        }

        public void setAuthor_name(String author_name) {
            this.author_name = author_name;
        }

        public String getAuthor_avatar() {
            return author_avatar;
        }

        public void setAuthor_avatar(String author_avatar) {
            this.author_avatar = author_avatar;
        }

        public int getLast_update_chapter_id() {
            return last_update_chapter_id;
        }

        public void setLast_update_chapter_id(int last_update_chapter_id) {
            this.last_update_chapter_id = last_update_chapter_id;
        }

        public int getLast_update_chapter_datetime() {
            return last_update_chapter_datetime;
        }

        public void setLast_update_chapter_datetime(int last_update_chapter_datetime) {
            this.last_update_chapter_datetime = last_update_chapter_datetime;
        }

        public String getLast_update_chapter_title() {
            return last_update_chapter_title;
        }

        public void setLast_update_chapter_title(String last_update_chapter_title) {
            this.last_update_chapter_title = last_update_chapter_title;
        }

        public String getLast_update_chapter_intro() {
            return last_update_chapter_intro;
        }

        public void setLast_update_chapter_intro(String last_update_chapter_intro) {
            this.last_update_chapter_intro = last_update_chapter_intro;
        }

        public int getCreate_time() {
            return create_time;
        }

        public void setCreate_time(int create_time) {
            this.create_time = create_time;
        }

        public List<String> getBook_keywords() {
            return book_keywords;
        }

        public void setBook_keywords(List<String> book_keywords) {
            this.book_keywords = book_keywords;
        }

        public List<BookCategoryBeanXXX> getBook_category() {
            return book_category;
        }

        public void setBook_category(List<BookCategoryBeanXXX> book_category) {
            this.book_category = book_category;
        }

        public static class BookCategoryBeanXXX {
            /**
             * class_id : 2
             * class_name : 总裁豪门
             */

            private String class_id;
            private String class_name;

            public String getClass_id() {
                return class_id;
            }

            public void setClass_id(String class_id) {
                this.class_id = class_id;
            }

            public String getClass_name() {
                return class_name;
            }

            public void setClass_name(String class_name) {
                this.class_name = class_name;
            }
        }
    }
}
