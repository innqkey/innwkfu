package com.weikefu.constant;

/**
 * 用来定义全局变量的
 *
 * @author Administrator
 */
public class ContextConstant {

	//排队队列
	public static final String REDIS_QUEUE = "redis_queue";
	//会话队列
	public static final String REDIS_DIALOG = "redis_dialog";
	//商户在线客服队列
	public static final String REDIS_SHOP_CUSTONLINE = "redis_shop_cust_online";
	//客服服务历史队列
	public static final String REDIS_CUSTUSER_HISTORY = "redis_cust_user_history";
	//商户历史服务队列
	public static final String REDIS_SHOP_USERHISTORY = "redis_shop_user_history";
	//多人会话队列（聊天室）
	public static final String REDIS_USER_SHOP_TALK = "redis_user_shop_talk";
	
	public static final String REDIS_MESSAGE_COUNT = "redis_message_count";
	//客服信息缓存
	public static final String REDIS_CUST_INFO = "redis_cust_info";
	
	
	//最后一条消息
	public static final String REDIS_LAST_MES = "redis_last_mes";
	
	//用户信息
	public static final String REDIS_USER_INFO = "redis_user_info";
	
	//用户信息
	public static final String REDIS_USER_SHOP_LAST_CUST = "redis_user_shop_last_cust";
	
	//微商城接口--获取用户、商户、客服坐席基础信息
	public static final String GET_CHAT_DATA = "api/getChatData";
	
	//微商城接口名称--获取新订单消息列表
	public static final String GET_NEW_ORDER_NOTIFICATION = "api/getNewOrderNotification";
	
	//微商城接口名称--获取用户-商户订单
	public static final String GET_USER_ORDER_DATA = "api/getUserOrderData";
	
	//微商城接口名称--获取新订单消息数量
	public static final String GET_NEW_ORDER_COUNT = "api/getNewOrderCount";
	
	//微商城接口名称--清空新订单消息
	public static final String CLEAR_ORDER_NOTIFICATION = "api/clearOrderNotification";
	
	//获取小程序的access_token
	public static final String GET_ACCESS_TOKEN = "api/getToken";
	
	//redis数据库
	public static final int REDES_DATABASE0 = 0;
	public static final int REDES_DATABASE1 = 1;
	
	//redis list remove 当count=0时，移除所有匹配的元素； 当count为负数时，移除方向是从尾到头；当count为正数时，移除方向是从头到尾；
	public static final int REDESREM0 = 0;
	
    //上传异常,701图片为空，702图片超出大小，709图片的类型错误，704 服务器异常
    public static final String IMAGE_NULL = "701";
    public static final String SIZE_EXCEEDS = "702";
    public static final String IMAGE_TYPE = "703";
    public static final String UPLOAD_FAILURE = "704";

    //手机号已存在
    public static final String EXIST_PHONE = "102";
    //用户或角色已存在
    public static final String EXIST_NAME = "102";
    //用户已经登录了
    public static final String LOGIN_REPET = "102";
    
    //登录成功
    public static final String LOGIN_OK = "100";
    //登录异常
    public static final String LOGIN_EXCEPTION = "404";
    //小程序用户发送图片保存的地址
    public static final String  SMALLROUTINUE_SAVE_NAME="smallroutinue";
    
    

    //必传参数为空
    public static final String PARAM_NULL = "401";

    //搜索条件
    public static final String SEARCH_PHONE = "phone";
    public static final String SEARCH_CUSTNAME = "contact";
    public static final String SEARCH_ITEMTYPE = "itemtype";
    public static final String SEARCH_TIEMNAME = "itemname";
    public static final String SEARCH_ALLCUST = null;
    
    //消息的类型
    public static final String MES_TEXT = "text";
    public static final String MES_IMAGE = "image";
    public static final String MES_VOICE = "voice";
    public static final String MES_GOODS= "goods";
    //小程序卡片
    public static final String MES_CARD="miniprogrampage";

    //消息的发送方式
    public static final String SEND_USERWAY = "userway";
    public static final String SEND_CUSTWAY = "custway";
    public static final String SEND_SHOPWAY = "shopway";
    //这个是对应的消息提示
    public static final String SEND_PROMPT = "promptway";

    //token
    public static final String COOKIE_TOKEN = "cookie_token";
    
    //url的
	public static final String URL_PATTERN = "^(http|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?$";
	//聊天窗口信息
	public static final String WINDOW_CHAT = "chat";
	public static final String WINDOW_ORDER = "order";
	public static final String TOKEN_SALT = "-huisou_kefu_java_4";
	public static final String TOKEN = "huisou";
	
	
	public static final String JOINWAY_WEIXIN = "weixin";
	public static final String JOINWAY_SMALLROUTINE = "small";
	public static final String JOINWAY_PHONE = "phone";
	
	public static final int WEIXIN_OK = 0;
	public static final int WEIXIN_BUSY = -1;
	
	//微信错误
	public static final int WEIXIN_ERR_40001 = 40001;
    /**
     * 42001 access_token超时
     */
    public static final int WEIXIN_ERR_42001 = 42001;

    /**
     * 40014 不合法的access_token，请开发者认真比对access_token的有效性（如是否过期）
     */
    public static final int WEIXIN_ERR_40014 = 40014;
    
    
	
	//socketio页面监听事件
	public static final String TALK_CUST_EVENT = "talkCustEvent";
	
	//socketio页面排队用户监听事件
	public static final String WAIT_QUEUE = "waitQueue";
	
	
	public enum NameSpaceEnum{
		
		IM("/im/user") ,
		AGENT("/im/agent"), 
		ENTIM("/im/ent") ;
		
		private String namespace ;
		
		public String getNamespace() {
			return namespace;
		}

		public void setNamespace(String namespace) {
			this.namespace = namespace;
		}

		NameSpaceEnum(String namespace){
			this.namespace = namespace ;
		}
		@Override
		public String toString(){
			return super.toString().toLowerCase() ;
		}
	}
	
	
	/**
	 * 客服的状态
	 * @author Administrator
	 *
	 */
    public enum CustomerStatus{
		
		ONLINE,
		BUSY, 
		LEAVE,
		OFFLINE;
		@Override
		public String toString(){
			return super.toString().toLowerCase() ;
		}
    }
    
}
