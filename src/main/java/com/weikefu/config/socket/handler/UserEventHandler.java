package com.weikefu.config.socket.handler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.common.WKFDataContext;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.weikefu.config.socket.client.NettyClients;
import com.weikefu.constant.ContextConstant;
import com.weikefu.po.Message;
import com.weikefu.po.UserInfo;
import com.weikefu.service.AllotCustService;
import com.weikefu.service.UserInfoService;
import com.weikefu.util.FileUtils;
import com.weikefu.util.MD5Utils;
import com.weikefu.util.MessageUtils;
import com.weikefu.vo.ParamIDS;

/**
 * 用于客户连接的处理器,
 * @author Administrator
 *
 */


@Component
public class UserEventHandler {  
	
	private String httpWscUrl = FileUtils.getApplicationPro("http.wsc.url");
	

	private final Logger logger = LoggerFactory.getLogger(UserEventHandler.class);
	
	
	@Autowired
	private RestTemplate template;
	
	private final  ConcurrentHashMap<String, String> tempMap = new ConcurrentHashMap<>();
	private Pattern pattern =  Pattern.compile(ContextConstant.URL_PATTERN);
    /**
     * 当用户连接的时候保存用户的socket等
     * 相关的信息
     * @param client
     */
	@Autowired
	private AllotCustService allotSer;
	
    @OnConnect  
    public void onConnect(SocketIOClient client){
    	
    	System.out.println("user------------------------------");
    	client.sendEvent("connect", "connected");
    }  
      
    /*
     * 用户接入
     */
    @OnEvent(value = "userJoin")  
    public void userJoin(SocketIOClient client, AckRequest request, ParamIDS ids){
    	//获取对应的信息
    	String shopId = ids.getShopId();
    	String userId = ids.getUserId();
    	String joinWay = ids.getJoinWay();
    	String sign = ids.getSign();
    	logger.info("用户接入userId--"+userId);
    	
    	//判断是否开启令牌
    	String signSwitch = FileUtils.getApplicationPro("sign.switch");
		if ("1".equals(signSwitch)) {
			String token = new StringBuilder().append(shopId).append(userId).append(ContextConstant.TOKEN).toString();
			token =MD5Utils.md5Encode(token);
			//验证sign中的对应是否是发过来的信息
			if (StringUtils.isBlank(sign)||!sign.equals(token)) {
				client.sendEvent("errPara", "102");
				client.disconnect();
				return;
			}
		}
		
    	if(StringUtils.isBlank(shopId) || StringUtils.isBlank(joinWay) || StringUtils.isBlank(userId)){
			client.sendEvent("errPara", "102");
			client.disconnect();
			return;
		}
    	String localUserId =null;
    	UserInfo userInfo = null;
    	UserInfoService userInfoService = WKFDataContext.getApplicationContext().getBean(UserInfoService.class);
		//调用接口获取信息
    	JSONObject jsonObject = template.getForObject(httpWscUrl + ContextConstant.GET_CHAT_DATA + "?userId="+userId, JSONObject.class);
//    		JSONObject jsonObject = template.getForObject(httpWscUrl+"userId={userId}", JSONObject.class,wscUserDataMap);
		JSONObject data = jsonObject.getJSONObject("data");
		logger.info("调用微商城查询user--"+data);
		if (data != null) {
			JSONObject userJson = data.getJSONObject("userData");
			if(null != userJson){
				String truename = userJson.getString("truename");
				String appid = userJson.getString("appid");
				String nickname = userJson.getString("nickname");
				String headimgurl = userJson.getString("headimgurl");
				String mobile = userJson.getString("mobile");
				//String sex = userJson.getString("sex");
				String province = userJson.getString("province");
				String city = userJson.getString("city");
				String country = userJson.getString("country");
				String createdat = userJson.getString("created_at");
				
				userInfo = new UserInfo();
				userInfo.setWeiuserid(userId);
				userInfo.setOpenid(appid);
				userInfo.setTruename(truename);
				userInfo.setNickname(nickname);
				userInfo.setHeadimgurl(headimgurl);
				userInfo.setProvince(province);
				userInfo.setCity(city);
				userInfo.setCountry(country);
				userInfo.setMobile(mobile);
				userInfo.setCreated_at(createdat);
				userInfo.setJoinway(joinWay);
				//将微商城的userid 替换userId
				localUserId = userInfoService.saveUser(userInfo);
				//为了防止接口损坏
			}
			
		}else {
			userInfo = new UserInfo();
			userInfo.setWeiuserid(userId);
			userInfo.setJoinway(joinWay);
			//替换微商城的id
			localUserId = userInfoService.saveUser(userInfo);
		}

		if(null == userInfo || StringUtils.isBlank(localUserId)){
			client.sendEvent("errPara", "102");
			client.disconnect();
			return;
		}
     	//替换前面的userid
		client.sendEvent("changeId", localUserId);
		
    	tempMap.putIfAbsent(client.getSessionId().toString(), new StringBuilder().append(shopId).append("-").append(localUserId).toString());
    	//分配客服
    	allotSer.userJoin(localUserId, shopId, client);
    }
    /**
     * 
     * 什么时候离线，就是在用用户关闭窗口的时候，删除Netty中的对象
     * @param client
     */
    @OnDisconnect  
    public void onDisconnect(SocketIOClient client){
    	String shopAndCust = tempMap.get(client.getSessionId().toString());
    	if (StringUtils.isNotBlank(shopAndCust)) {
    		String[] split = shopAndCust.split("-");
        	//customer断开连接的时候，我们需要从redis中退出对应的内容，以及nettyClient的退出相关的内容
        	String userId = split[1];
        	String shopId =split[0];
	    	//删除在线的客服,根据client的sessionId 删除
	    	NettyClients.getInstance().removeUserEventClient(shopId,userId);
	    	System.out.println("退出的session的id==" + client.getSessionId().toString() );
	    	tempMap.remove(client.getSessionId().toString());
    	}
    }  
      
    /**
     * 发送消息
     * @param client
     * @param request
     * @param data
     */
    @OnEvent(value = "message")
    public void onEvent(SocketIOClient client, AckRequest request, Message data){
    	//判断消息是否为空
    	if (StringUtils.isNotBlank(data.getMessage()) && StringUtils.isNotBlank(data.getUserid())) {
    		
    		logger.info("客服发送消息--userId=="+data.getUserid()+";custId=="+data.getCustid()+";message=="+data.getMessage());
    		
    		data.setMessage(data.getMessage().trim());
    	 	if (StringUtils.isBlank(data.getMsgtype())) {
    			data.setMsgtype(ContextConstant.MES_TEXT);
    		}
        	//对消息的长度进行限制
        	if(!StringUtils.isBlank(data.getMessage()) && data.getMessage().length() > 500){
        		data.setMessage(data.getMessage().trim().substring(0 , 500));
        	}
        	//如果消息发送含有http或者https话
          	if(ContextConstant.MES_TEXT.equals(data.getMsgtype()) && pattern.matcher(data.getMessage().trim()).matches()){
        		data.setMessage("<a style='color:blue'  href='" + data.getMessage() + "' target='view_window'>" + data.getMessage() + "</a>");
        	}
          	//发送消息
          	MessageUtils.userSendMesage(client,data);
          	
		}
    }
    

    
  
   
}  