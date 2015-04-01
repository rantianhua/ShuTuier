package weike.util;

/**
 * Created by Rth on 2015/2/23.
 */
public class Constants {

    public static final String OLink = "http://www.tengshufang.com/API/Index/index/name/";
    public static final String DetailLink = "http://www.tengshufang.com/API/Index/item/id/";
    public static String CommentsLink = "http://www.tengshufang.com/API/Index/mark.html";
    public static String COMMITLINK = "http://www.tengshufang.com/API/Index/publish.html";
    public static String LOGINLINK = "http://www.tengshufang.com/API/Index/login.html";
    public static String PICLINK = "http://7xi56i.com1.z0.glb.clouddn.com/";
    public static final String TYPE_1 = "textbook";     //表示来自TextbookFragment中的请求
    public static final String TYPE_2 = "program";      //表示来自ProgramFragment中的请求
    public static final String TYPE_3 = "kaoyan";       //表示来自KaoyanFragment中的请求
    public static final String TYPE_4 = "give";     //表示来自GiveFragment中的请求
    public static final String TYPE_5= "latest";
    public static  String EXTRA_ITEM_ID = "itemID";
    public static final String REQUEST_FROM_FRAGMENT = "whichFragment";
    public static final int BookDetailFragme_RequestCode = 1;
    public static final String EXTRA_COMMENT = "comment";
    public static final String EXTRA_SEND_TIME = "send_time";
    public static final String[] LITERATURE = {"小说","随笔","散文","诗歌","随笔","杂文"};
    public static final String[] POPULAR = {"漫画","青春","推理","悬疑","科幻","言情","武侠"};
    public static final String[] CULTURE = {"历史","心理学","哲学","传记","社会学","设计","艺术","政治","建筑","佛教","绘画","戏剧","人文"
        ,"宗教","军事","美术","考古"};
    public static final String[] LIFE = {"爱情","旅行","励志","女性","摄影","美食","职场","教育","情感","健康","手工","人际关系","养生"
            ,"两性","家居"};
    public static final String[] MANAGEMENT = {"经济学","管理","经融","商业","投资","营销","创业","广告","股票","策划"};
    public static final String[] SCIENCE = {"科普","互联网","科学","交互设计","用户体验","通信"};
    public static final int REQUEST_IMAGE_CAPTURE = 2;
    public static final String BASEDOUBANURL = "https://api.douban.com/v2/book/isbn/:";
    public static final String DOUBANURLFEILS = "?apikey=0a719bbb2be836be1f54d33183b8a43a&fields=title,image,author,publisher,price,summary";
    public static final String EXTRA_ISBN = "isbn";
    public static final String EXTRA_PIC = "获取封面方式";
    public static final String SP_USER = "user";
    public static final String USER_ONLINE_KEY = "online";
    public static final String QQNICNAME = "nickname";
    public static final String QQICONURL = "figureurl_qq_1";
    public static final String SEX = "gender";

}