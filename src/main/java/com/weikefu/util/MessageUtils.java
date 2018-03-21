package com.weikefu.util;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;


import com.common.WKFDataContext;
import com.corundumstudio.socketio.SocketIOClient;
import com.weikefu.cache.RedisDialogCache;
import com.weikefu.cache.RedisMessageCountCache;
import com.weikefu.cache.RedisQueueCache;
import com.weikefu.cache.RedisShopCustOnlineCache;
import com.weikefu.cache.RedisUserShopLastMessageCache;
import com.weikefu.cache.RedisUserShopTalkCache;
import com.weikefu.config.socket.client.NettyClients;
import com.weikefu.config.socket.handler.CustomerEventHandler;
import com.weikefu.constant.ContextConstant;
import com.weikefu.dao.mangodb.MessageDao;
import com.weikefu.dao.mangodb.ShopCustDao;
import com.weikefu.po.CustConfigInfo;
import com.weikefu.po.Message;
import com.weikefu.po.ShopCustPo;
import com.weikefu.service.AllotCustService;

/**
 * 用于发送消息
 * @author Administrator
 *
 */
public class MessageUtils {
	private final static Logger logger = LoggerFactory.getLogger(MessageUtils.class);
	
	/**
	 * 用于发送消息
	 * @param client
	 * @param data
	 */
	public static void userSendMesage(SocketIOClient client, Message data) {
		data.setCreatetime(new Date());
		
		RedisUserShopLastMessageCache lastMesage = WKFDataContext.getApplicationContext().getBean(RedisUserShopLastMessageCache.class);
		//对存入的消息进行判断
		String temp=data.getMessage();
		if (ContextConstant.MES_IMAGE.equals(data.getMsgtype())) {
			data.setMessage("[图片]");
		}else if (ContextConstant.MES_GOODS.equals(data.getMsgtype())) {
			data.setMessage("[商品]");
		}else if(ContextConstant.MES_VOICE.equals(data.getMsgtype())){
			data.setMessage("[音频]");
		}else if(ContextConstant.MES_CARD.equals(data.getMsgtype())){
			data.setMessage("[小程序卡片]");
		}
		//存放到缓存中去
		lastMesage.addLastMessage(data.getUserid(), String.valueOf(data.getShopid()), data);
		data.setMessage(temp);
//		if (StringUtils.isBlank(data.getRoomid())) {
//			data.setRoomid(data.getShopid()+ "-" + data.getUserid());
//		}
		
		MessageDao messageDao = WKFDataContext.getApplicationContext().getBean(MessageDao.class);
		//查找房间
		RedisUserShopTalkCache shopUserTalk = WKFDataContext.getApplicationContext().getBean(RedisUserShopTalkCache.class);
		//如果房间为空，说明没有被分配客服
		List<String> custIdList = shopUserTalk.getShopTalkList(data.getUserid(), String.valueOf(data.getShopid()));
		
		//找到目前房间在的客服
	
//		NettyClients.getInstance().sendUserEventMessage(userid, "message", data);
		
		data.setSendway(ContextConstant.SEND_USERWAY);
		//给自己发送消息
	
		messageDao.insert(data);
	//	client.sendEvent("message", data);
		
		//没有分配客服
		if (custIdList == null || custIdList.size() < 1) {
			//目前不需要
//			RedisShopCustOnlineCache redisShopCustOnlineCache = WKFDataContext.getApplicationContext().getBean(RedisShopCustOnlineCache.class);
//			List list = redisShopCustOnlineCache.getOnlineCustAllList(String.valueOf(data.getShopid()));
//			//但是有在线客服，这个时候暂时不用分配，等待客服手动接入
//			if (list != null && list.size() > 0) {
//				RedisQueueCache redisQueue = WKFDataContext.getApplicationContext().getBean(RedisQueueCache.class);
//				int size = redisQueue.getQuequSize(String.valueOf(data.getShopid()));
//				Message message = new Message();
//				message.setSendway(ContextConstant.SEND_SHOPWAY);
//				message.setMessage("您好，您前面还有" + size + "人正在排队，请耐心等待");
//				message.setShopid(data.getShopid());
//				message.setUserid(data.getUserid());
//				messagePoMapper.insert(message);
//				client.sendEvent("message", message);
//			}
		}else {
			int num = 0;
			//给该房间在线的发送消息
			for (int i = 0; i < custIdList.size(); i++) {
				String custId = custIdList.get(i);
				if (StringUtils.isNotBlank(custId)) {
					//data.setCustid(Integer.valueOf(custId));
					RedisMessageCountCache messageCount = WKFDataContext.getApplicationContext().getBean(RedisMessageCountCache.class);
					long count = messageCount.getMessageCount(String.valueOf(data.getShopid()),data.getUserid(), custId);
					//查询该用户是否被客服选中了，选中的话那么不允许增加,在接入的时候，同时清空对应的数据
					ShopCustDao shopCustDao = WKFDataContext.getApplicationContext().getBean(ShopCustDao.class);
					List<ShopCustPo> shopCustPos =shopCustDao.findByShopidAndCustid(data.getShopid(), Integer.valueOf(custId));
					if (shopCustPos != null && shopCustPos.size() >0) {
						ShopCustPo shopCustPo = shopCustPos.get(0);
						
						//如果正在聊天中，服务的所有的人都下线了，那么久放到队列中去
						if (shopCustPo.getCustserverstatus().equals(ContextConstant.CustomerStatus.OFFLINE.toString())){
							num += 1;
						}
						String serveruserid = shopCustPo.getServeruserid();
						//没被选中增加+1,选中的缓存清零
						if (StringUtils.isBlank(serveruserid) || !serveruserid.equals(data.getUserid())) {
							if (count < 99) {
								messageCount.increaseMessageCount(String.valueOf(data.getShopid()),data.getUserid(), custId);
								data.setMsgcount(count + 1);
							}else{
								data.setMsgcount(count);
							}
						}
					}
					
					NettyClients.getInstance().sendCustomerEventMessage(String.valueOf(data.getShopid()),custId, "message", data);
				}
			}
			//如果所有的都为空那么久放入队列中，并且将消息缓存起来
			if (num == custIdList.size()){
				AllotCustService allotSer = WKFDataContext.getApplicationContext().getBean(AllotCustService.class);
				allotSer.userJoin(data.getUserid(), String.valueOf(data.getShopid()), client);
			}
			
		}
	}
	
	

	public static void custSendMessage(SocketIOClient client, Message data,
			String shopId, String custId) {
		//发送给用户
    	if (StringUtils.isBlank(data.getMsgtype())) {
			data.setMsgtype(ContextConstant.MES_TEXT);
		}
    	data.setCreatetime(new Date());
    	//如果消息的用户不为空的话
    	if (StringUtils.isNotBlank(data.getUserid()) && data.getCustid() != null) {
    		//保存客服的聊天的消息，并同时发送非对应的用户
    		String userid = data.getUserid();
    		Integer custid = data.getCustid();
//    		data.setRoomid(shopId + "-" + userid);
    		ShopCustDao shopCustDao = WKFDataContext.getApplicationContext().getBean(ShopCustDao.class);
			List<ShopCustPo> shopCustPoList =shopCustDao.findByShopidAndCustid(data.getShopid(), Integer.valueOf(custId));
    		if (shopCustPoList != null && shopCustPoList.size() > 0) {
    			//如果获取的该客服的数量大于1的话
    			if (shopCustPoList.size() > 1) {
    				logger.error("Customer of i=" + custid + "  size  execde  1");
				}
    			ShopCustPo shopCustPo = shopCustPoList.get(0);
    			//用户的状态不是离线或者离开的时候，可以发送消息
    			if (!(shopCustPo.getCustserverstatus().equals(ContextConstant.CustomerStatus.LEAVE.toString()) ||shopCustPo.getCustserverstatus().equals(ContextConstant.CustomerStatus.OFFLINE.toString()))) {
    				
    				RedisUserShopTalkCache userShopTalkService = WKFDataContext.getApplicationContext().getBean(RedisUserShopTalkCache.class);
    	    		//对userid所在房间的客服进行判断
    	    		List<String> list = userShopTalkService.getShopTalkList(userid, String.valueOf(data.getShopid()));
    	    		RedisDialogCache redisDialogCache = WKFDataContext.getApplicationContext().getBean(RedisDialogCache.class);
    	    		//如果是服务队列
    	    		List<String> dialogList = redisDialogCache.getDialogAllList(String.valueOf(data.getShopid()),String.valueOf(custid));
    	    		
    	    		//如果该用户是服务范围中的
    	    		if (list != null && list.contains(String.valueOf(custid)) && dialogList.contains(String.valueOf(userid))){
    	    			data.setSendway(ContextConstant.SEND_CUSTWAY);
    	    			if (client != null) {
    	    				client.sendEvent("message", data);
    	    			}
    	    			//保存记录
    	    			MessageDao messageDao = WKFDataContext.getApplicationContext().getBean(MessageDao.class);
    	    			messageDao.insert(data);
    	    			RedisUserShopLastMessageCache bean = WKFDataContext.getApplicationContext().getBean(RedisUserShopLastMessageCache.class);
    	    			String tempMessage = data.getMessage();
    	    			if (ContextConstant.MES_IMAGE.equals(data.getMsgtype())) {
							data.setMessage("[图片]");
						}else if (ContextConstant.MES_GOODS.equals(data.getMsgtype())) {
							data.setMessage("[商品]");
						}else if(ContextConstant.MES_VOICE.equals(data.getMsgtype())){
							data.setMessage("[音频]");
						}
    	    			bean.addLastMessage(userid,String.valueOf(data.getShopid()) , data);
    	    			
    	    			if (list.size() > 1) {
    	    				for (String custiString : list) {
    	    					if (!custiString.equals(custId)) {
    	    						//判断当前的房间中的其他的custid是否为该userid服务，如果不是，那么增加消息的条数
    	    						List<ShopCustPo> shopCustPos = shopCustDao.findByShopidAndCustid(data.getShopid(),Integer.valueOf(custiString));
    	    						if (shopCustPos != null && shopCustPos.size() >0) {
    	    							 shopCustPo = shopCustPos.get(0);
    	    							String serveruserid = shopCustPo.getServeruserid();
    	    							RedisMessageCountCache messageCount = WKFDataContext.getApplicationContext().getBean(RedisMessageCountCache.class);
    	    							long count = messageCount.getMessageCount(String.valueOf(data.getShopid()),data.getUserid(), custiString);
    	    							//没被选中增加+1,选中的缓存清零
    	    							if (StringUtils.isBlank(serveruserid) || !serveruserid.equals(data.getUserid())) {
    	    								if (count < 99) {
    	    									messageCount.increaseMessageCount(String.valueOf(data.getShopid()),data.getUserid(), custiString);
    	    									data.setMsgcount(count + 1);
    	    								}else{
    	    									data.setMsgcount(count);
    	    								}
    	    							}
    	    						}
    	    						
    	    						RedisUserShopLastMessageCache lastMesage = WKFDataContext.getApplicationContext().getBean(RedisUserShopLastMessageCache.class);
    	    						
    	    						//存放到缓存中去
    	    						lastMesage.addLastMessage(data.getUserid(), String.valueOf(data.getShopid()), data);
    	    						data.setMessage(tempMessage);
    	    						NettyClients.getInstance().sendCustomerEventMessage(shopId, custiString, "message", data);
								}
    	    				}
						}
    	    			data.setMessage(tempMessage);
    	    			NettyClients.getInstance().sendUserEventMessage(shopId,userid, "message", data);
    	    		}else {
    					//该用户不在客服的范围？？
    	    			
    				}
				}
			}
		}else {
			//用户的userid和custid 为空的话？？？
			
		}
	}  

}
