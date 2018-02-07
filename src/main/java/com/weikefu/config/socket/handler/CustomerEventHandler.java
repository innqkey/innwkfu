package com.weikefu.config.socket.handler;



import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.weikefu.cache.RedisShopCustOnlineCache;
import com.weikefu.config.socket.client.NettyClients;
import com.weikefu.config.socket.client.NettyCustCurrentDialogUserMap;
import com.weikefu.po.Message;
import com.weikefu.po.UserInfo;
import com.weikefu.service.AllotCustService;
import com.weikefu.service.ShopCustPoStatusService;
import com.weikefu.service.UserInfoService;
import com.weikefu.util.MessageUtils;
import com.weikefu.util.WeiXinErrorCodeUtils;
import com.weikefu.vo.ParamIDS;
import com.weikefu.vo.WeiXinContent;
import com.weikefu.constant.ContextConstant;

@Component
public class CustomerEventHandler{
	
	
	@Autowired
	private AllotCustService allotSer;
	@Autowired
	private RedisShopCustOnlineCache redisShopCustOnlineCache;
	@Autowired
	private ShopCustPoStatusService shopCustPoService;
	@Autowired
	private UserInfoService userInfoService;
	
	
	@Autowired
	private RestTemplate restTemplate;
	@Value("${http.wsc.url}")
	public String serverUrl;
	
	@Value("${weixin.sendMessage}")
	public String weixinSendUrl;
	private final Logger logger = LoggerFactory.getLogger(CustomerEventHandler.class);
	
	private final  ConcurrentHashMap<String, String> tempMap = new ConcurrentHashMap<>();
	
	private Pattern pattern =  Pattern.compile(ContextConstant.URL_PATTERN);

	private final int nullValue= 1;
    /**
     * 添加userid  和socketIo端口到nettyClient中去。
     * 并将在线的客服添加到redisShopCustOnline的缓存中去
     * @param client
     */
    @OnConnect
    public void onConnect(SocketIOClient custClient){
    	logger.info("socketio connect sessionId=="+custClient.getSessionId().toString());
    //	custClient.sendEvent("sdfd", "sdfsd");
    	custClient.sendEvent("onCustJoin", "客服坐席连接成功！");
//    	String shopId = custClient.getHandshakeData().getSingleUrlParam("shopId");
//    	String custId = custClient.getHandshakeData().getSingleUrlParam("custId");
//    	String userId = custClient.getHandshakeData().getSingleUrlParam("userId");
//    	if (StringUtils.isNotBlank(custId)) {
//    		custClient.set("custNo",custId);
//			
//			//同时放入对应的缓存中
//			allotSer.custJoin(custId, shopId, custClient);
//			custClient.sendEvent("connect", "");
//		}
//    	currentDialog(custClient, shopId, custId);
    }  
      
    //消息接收入口，当接收到消息后，查找发送目标客户端，并且向该客户端发送消息，且给自己发送消息  
    @OnEvent(value = "custJoin")  
    public void custJoin(SocketIOClient custClient, AckRequest request, ParamIDS data) {
    	logger.info("custJoin socketio connect sessionId=="+custClient.getSessionId().toString());
    	String shopId = data.getShopId();
    	String custId = data.getCustId();
    	String custStatus = data.getCustStatus();
    	//如果参数为空的话，那么久断开连接。
    	if (StringUtils.isBlank(shopId) || StringUtils.isBlank(custId)) {
			custClient.sendEvent("errPara", "102");
			custClient.disconnect();
			return;
		}
    	
    	if (StringUtils.isNotBlank(custId)) {
    		custClient.set("custNo",custId);
			//同时放入对应的缓存中
    		if(StringUtils.isNotBlank(custStatus)){
    			shopCustPoService.changeCustStatus(shopId, custId, custStatus, 0);
    		}
			allotSer.custJoin(custId, shopId, custClient, null);
			tempMap.putIfAbsent(custClient.getSessionId().toString(), new StringBuilder().append(shopId).append("-").append(custId).toString());
		}
    }
    /**
     * 当断开连接的时候，删除对应的socketClient
     * @param client
     */
    @OnDisconnect  
    public void onDisconnect(SocketIOClient client){
    	logger.info("退出socketio onDisconnect sessionId=="+client.getSessionId().toString());
    	String shopAndCust = tempMap.get(client.getSessionId().toString());
    	if (StringUtils.isNotBlank(shopAndCust)) {
    		String[] split = shopAndCust.split("-");
        	//customer断开连接的时候，我们需要从redis中退出对应的内容，以及nettyClient的退出相关的内容
        	String custId = split[1];
        	String shopId =split[0];
        	SocketIOClient socketIOClient = NettyClients.getInstance().findCustClient(shopId, custId);
        	
        	if (socketIOClient != null && socketIOClient.getSessionId().toString().equals(client.getSessionId().toString())){
        		NettyClients.getInstance().removeCustomerEventClient(shopId,custId);
            	redisShopCustOnlineCache.removeOnlineCust(shopId, custId);
            	
            	logger.info("退出的session的id===" + client.getSessionId().toString());

            	shopCustPoService.changeCustStatus(shopId,custId, ContextConstant.CustomerStatus.OFFLINE.toString(),nullValue);
        	}
        	
        	tempMap.remove(client.getSessionId().toString());
        	//改变客服的状态和当前服务的Serverid
     
        	logger.info("客服退出退出了,custId==" + custId+";shopId=="+shopId);
		}
    }  
      
    //消息接收入口，当接收到消息后，查找发送目标客户端，并且向该客户端发送消息，且给自己发送消息  
    @OnEvent(value = "service")  
    public void onEvent1(SocketIOClient client, AckRequest request, Message data) {
    	logger.info("service---的");
    }  
    
	//消息接收入口，当接收到消息后，查找发送目标客户端，并且向该客户端发送消息，且给自己发送消息  
    @OnEvent(value = "status")  
    public void onEvent2(SocketIOClient client, AckRequest request, Message data)   
    {
    	logger.info("status=====");
    }
    
    /**
     *  消息接收入口，当接收到消息后，查找发送目标客户端，并且向该客户端发送消息，且给自己发送消息  
     * @param client
     * @param request
     * @param data
     */
    @OnEvent(value = "message")  
    public void onMessageEvent(SocketIOClient client, AckRequest request, Message data){
    	//currentDialog(client, request, String.valueOf(data.getShopid()), String.valueOf(data.getCustid()));
    	if (StringUtils.isNotBlank(data.getMessage())) {
    		
    		logger.info("客服发送消息--userId=="+data.getUserid()+";custId=="+data.getCustid()+";message=="+data.getMessage());
    		
    		data.setMessage(data.getMessage().trim());

    		String shopId = String.valueOf(data.getShopid());
    		String custId = String.valueOf(data.getCustid());
    		if (StringUtils.isBlank(data.getMsgtype())) {
    			data.setMsgtype(ContextConstant.MES_TEXT);
    		}
    		if(!StringUtils.isBlank(data.getMessage()) && data.getMessage().length() > 500){
        		data.setMessage(data.getMessage().trim().substring(0 , 500));
        	}
    		//如果消息发送含有http或者https话
          	if(ContextConstant.MES_TEXT.equals(data.getMsgtype()) && pattern.matcher(data.getMessage()).matches()){

        		data.setMessage("<a style='color:blue'  href='" + data.getMessage() + "' target='view_window'>" + data.getMessage() + "</a>");
        	}
    		MessageUtils.custSendMessage(client, data, shopId, custId);
		}
    }
    
    //消息接收入口，当接收到消息后，查找发送目标客户端，并且向该客户端发送消息，且给自己发送消息  
    @OnEvent(value = "custBusy")  
    public void custBusy(SocketIOClient client, AckRequest request, Message data) {
    	System.out.println("service---的");
    }
    
    //消息接收入口，当接收到消息后，查找发送目标客户端，并且向该客户端发送消息，且给自己发送消息  
//    @OnEvent(value = "currentDialog")  
//    public void currentDialog(SocketIOClient client, String shopId, String custId) {
//    	String dialogUserJson = dialogServie.dialogList(shopId, custId);
//    	client.sendEvent("currentDialog", dialogUserJson);
//    }

    //消息接收入口，当接收到消息后，查找发送目标客户端，并且向该客户端发送消息，且给自己发送消息  
    @OnEvent(value = "custCurrentUser")  
    public void custCurrentUser(SocketIOClient client, AckRequest request, String userId) {
    	String shopId = client.getHandshakeData().getSingleUrlParam("shopId");
    	String custId = client.getHandshakeData().getSingleUrlParam("custId");
//    	String userId = client.getHandshakeData().getSingleUrlParam("userId");
    	NettyCustCurrentDialogUserMap.putCurrentUserId(shopId, custId, userId);
    }

    //排队用户通知  
    @OnEvent(value = "waitQueue")  
    public void waitQueue(SocketIOClient custClient, String resData) {
    	custClient.sendEvent("waitQueue", resData);
    }
    
    
    /**
     * 用来给小程序发送消息
     * @param client
     * @param request
     * @param data
     * @throws JsonProcessingException 
     */
    @OnEvent(value = "routinemessage")
    public  void routinemessage(SocketIOClient client, AckRequest request, Message data) {
    	//不适用对象使用map
    	if (StringUtils.isBlank(data.getMsgtype())) {
    		data.setMsgtype(ContextConstant.MES_TEXT);
    	}
    	String msgtype = data.getMsgtype();
    	LinkedHashMap<String,Object> map = new LinkedHashMap<String,Object>();
    	//拼接url
    	UserInfo userInfo = userInfoService.findByUserId(data.getUserid());
    	if (userInfo == null) {
    		client.sendEvent("errPara", "发送失败");
    		return;
    	}
    	String accesstoken = getAccessToken(data, userInfo);
    	if (StringUtils.isBlank(accesstoken)) {
    		client.sendEvent("errPara", "发送失败");
    		return ;
    	}
    	
    	StringBuilder builder = new StringBuilder();
    	builder.append(weixinSendUrl);
    	builder.append("?access_token=").append(accesstoken);
    	//拼接参数
    	map.put("touser", data.getOpenid());
    	WeiXinContent weiXinContent = new WeiXinContent();
    	if (ContextConstant.MES_TEXT.equals(msgtype)) {
    		map.put("msgtype", msgtype);
    		if( pattern.matcher(data.getMessage().trim()).matches()){
    			data.setMessage("<a style='color:blue'  href='" + data.getMessage() + "' target='view_window'>" + data.getMessage() + "</a>");
    		}
    		weiXinContent.setContent(data.getMessage());
    		map.put("text", weiXinContent);
    	}else if (ContextConstant.MES_IMAGE.equals(msgtype)) {
    		map.put("msgtype", msgtype);
    		//这个是用户是微信上传的图片地址
    		weiXinContent.setMedia_id(data.getMediaId());
    		map.put("image", weiXinContent);
    	}
    	
    	//调用微信接口，给用户发送的信息
    	int  retryTime = 0;
    	do {
    		try {
    			HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(map);
    			String result = restTemplate.postForObject(builder.toString(),httpEntity,String.class);
    			if (result != null) {
    				JSONObject parseObject = JSONObject.parseObject(result);
    				Integer errcode = parseObject.getInteger("errcode");
    				if (null != errcode) {
    					if (errcode.equals(ContextConstant.WEIXIN_OK)) {
    						//保存完成结束循环
    						MessageUtils.custSendMessage(client, data, String.valueOf(data.getShopid()), String.valueOf(data.getCustid()));
    						client.sendEvent("errPara", "100");
    						return ;
    					} else if (errcode == ContextConstant.WEIXIN_BUSY ) {
							//当服务器繁忙的时候重新尝试
    						Thread.sleep(500);
						} else if (errcode == 45074) {
							client.sendEvent("errPara",WeiXinErrorCodeUtils.ErrorMsg(errcode));
							return ;
						}else {
    						logger.error("openid + " + data.getOpenid() + "发送信息失败,错误原因" + WeiXinErrorCodeUtils.ErrorMsg(errcode));
    						client.sendEvent("errPara",WeiXinErrorCodeUtils.ErrorMsg(errcode));
    						return;
    					}
    				}
    			}		
    			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		
		} while (++ retryTime  < 3);
    	client.sendEvent("errPara", "发送失败");
    }
    
    /***
     * 获取accessToken
     * @param data
     * @param userInfo
     * @return
     */
	private String getAccessToken(Message data, UserInfo userInfo) {
		String accesstoken="";
    	//如果数据库中已经在该access_token，并且没有过期的话，那么直接从数据库中直接获取就ok
    	if (userInfo.getExpireTime() != null && userInfo.getExpireTime() - (new Date().getTime()/1000) > 60
    			&& StringUtils.isNotBlank(userInfo.getAccess_token())) {
    			accesstoken = userInfo.getAccess_token();
    	}else {
    		String url = new StringBuilder().append(serverUrl).append(ContextConstant.GET_ACCESS_TOKEN).append("?shopId=").append(data.getShopid()).toString();
        	String response = restTemplate.getForObject(url, String.class);
        	if (StringUtils.isNotBlank(response)) {
        		JSONObject parseObject = JSON.parseObject(response);
        		JSONObject jsonObject = parseObject.getJSONObject("data");
        		if (jsonObject != null && StringUtils.isNotBlank(jsonObject.getString("token"))
        				&& jsonObject.getLong("expireTime") != null) {
        			userInfo.setAccess_token(jsonObject.getString("token"));
        			accesstoken=jsonObject.getString("token");
        			userInfo.setExpireTime(jsonObject.getLong("expireTime"));
        		}
        	}
    	}
		return accesstoken;
	}
}  