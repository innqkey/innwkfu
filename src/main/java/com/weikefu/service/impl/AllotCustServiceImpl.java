package com.weikefu.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.corundumstudio.socketio.SocketIOClient;
import com.weikefu.cache.RedisDialogCache;
import com.weikefu.cache.RedisQueueCache;
import com.weikefu.cache.RedisShopCustOnlineCache;
import com.weikefu.cache.RedisUserShopLastCustCache;
import com.weikefu.cache.RedisUserShopLastMessageCache;
import com.weikefu.cache.RedisUserShopTalkCache;
import com.weikefu.config.socket.client.NettyClients;
import com.weikefu.config.socket.client.NettyCustCurrentDialogUserMap;
import com.weikefu.config.socket.client.NettyOnlineCustClient;
import com.weikefu.constant.ContextConstant;
import com.weikefu.po.Message;
import com.weikefu.po.ShopCustPo;
import com.weikefu.service.AllotCustService;
import com.weikefu.service.DialogService;
import com.weikefu.service.ShopCustPoStatusService;
import com.weikefu.vo.CustomerBaseInfo;


@Service
public class AllotCustServiceImpl implements AllotCustService {

	private  static  final Logger logger = LoggerFactory.getLogger(AllotCustServiceImpl.class);
	//用来提示已经登录，然后下线
	private  final String  STATE_LOGIN = "1";
	
	@Autowired
	private RedisQueueCache queue;//排队队列
	
	@Autowired
	private RedisUserShopTalkCache talkCache;//聊天室队列
	
	@Autowired
	private RedisDialogCache dialogCache;//会话队列
	
	@Autowired
	private RedisShopCustOnlineCache onlineCache;
	
	@Autowired
	private RedisUserShopLastMessageCache lastMesCache;

	@Autowired
	private ShopCustPoStatusService custStatus;
	
	@Autowired
	private DialogService dialogServie;
	
	@Autowired
	private RedisUserShopLastCustCache lastCustCache;
	
	/**
	 * 修改client为空的，就是小程序的加入的情况
	 */
	@Override
	public void userJoin(String userId, String shopId, SocketIOClient client) {
		// TODO Auto-generated method stub
		
		if (client != null) {
			//创建socketClient
			NettyClients.getInstance().putUserEventClient(shopId,userId, client);
		}
		long begin = System.currentTimeMillis();
		List<String> roomCustIdList = talkCache.getShopTalkList(userId, shopId);
//		List<String> custIdOnlineList = onlineCache.getOnlineCustAllList(shopId);
		//在线客服为空，则放入排队队列
//		if(null==custIdOnlineList||custIdOnlineList.size()==0){
//			queue.rPushQuequ(shopId, userId);
//			return;
//		}
			
		boolean isRoomCustEmpty = true;
		//当前聊天室中有客服，判断客服是否在线，是否最大接入数
		if(null!=roomCustIdList&&roomCustIdList.size()>0){
			for(String roomCustId : roomCustIdList){
				//查询所有在线客服，判断聊天室中客服是否在线
				//获取在线客服socket，判断客服是否在线,如果为null表示不在线，调用重新分配客服
				boolean custIsOnline = custStatus.isStatusByShopCustStatus(shopId, roomCustId, ContextConstant.CustomerStatus.ONLINE.toString(),ContextConstant.CustomerStatus.BUSY.toString());
				//判断聊天室客服是否在线，在线就建立连接
				if(custIsOnline){
					isRoomCustEmpty = false;
					continue;
				}
			}
			//聊天室客服如果不在线，按照历史优先，平均分配客服，并放入聊天室
			if(isRoomCustEmpty){
				//没有聊天室，建立聊天室，按照历史优先，平均分配客服
				allocationRules(userId, shopId, client);
			}
		}else{
			//没有聊天室，建立聊天室，按照历史优先，平均分配客服
			allocationRules(userId, shopId, client);
		}
		//聊天室custids发送当前用户接入事件
		this.currentDialogJoin(shopId, userId);
		long end = System.currentTimeMillis();
		logger.info("用户接入时间==="+(end - begin));
	}

	/*
	 * 分配原则，历史客户优先，平均分配原则
	 */
	public void allocationRules(String userId, String shopId, SocketIOClient userClient) {
		logger.info("用户接入，历史优先分配原则---shopId=="+shopId+";userId=="+userId);
		//历史优先
		String custId = lastCustCache.getLastCustId(userId, shopId);
		if(StringUtils.isNotBlank(custId)){
			logger.info("历史服务客服custId=="+custId);
			//获取在线客服socket，判断客服是否在线,如果为null表示不在线，调用重新分配客服
			boolean custIsOnline = custStatus.isStatusByShopCustStatus(shopId, custId, ContextConstant.CustomerStatus.ONLINE.toString());
			if(!custIsOnline){
				logger.info("历史服务客服custId=="+custId+";不在线，重新分配");
				//平均分配
				this.eveDistributionRules(shopId, userId, userClient);
			}else{
				//判断客服坐席是否最大接入数
				ShopCustPo custPo = custStatus.selectShopCustPo(shopId, custId);
				if(null!=custPo&&custPo.getMaxusernum()>custPo.getJoinusernum()){
					//客服坐席加入聊天室，并发送接入成功消息
					joinTalkMessage(shopId, custId, userId, userClient);
				}else{
					//平均分配
					this.eveDistributionRules(shopId, userId, userClient);
				}
			}
		}else{
			logger.info("没有历史服务记录，调用平均分配原则");
			//平均分配
			this.eveDistributionRules(shopId, userId, userClient);
		}
	}
	
	/**
	 * 平均分配原则
	 */
	public void eveDistributionRules(String shopId, String userId, SocketIOClient userClient){
		logger.info("用户接入，平均分配原则---shopId=="+shopId+";userId=="+userId);
		String custId;
		//平均分配，获取最小服务数online客服
		custId = custStatus.selectMinCust(Integer.valueOf(shopId));
		//如果为null表示客服坐席接待已满，放入排队队列
		if(StringUtils.isBlank(custId)){
			//放入排队队列后发送页面排队用户监听事件（方法里包含发送监听事件）
			queue.rPushQuequ(shopId, userId);
			logger.info("在线客服为空，放入排队队列---userId=="+userId);
		//如果不为null，则加入聊天室，加入会话队列，更新客服接入用户数量，发送接入消息
		}else{
			//客服坐席加入聊天室，并发送接入成功消息
			joinTalkMessage(shopId, custId, userId, userClient);
			//获取在线客服socket
			SocketIOClient custClient = NettyOnlineCustClient.getOnlineCustClient(shopId, custId);
		}
		
	}

	/*
	 * 客服坐席加入聊天室，并发送接入成功消息
	 */
	public void joinTalkMessage(String shopId, String custId, String userId, SocketIOClient userClient) {
		//加入聊天室，多人会话队列
		talkCache.addShopTalk(userId, shopId, custId);
		//加入会话队列
		dialogCache.addDialog(shopId, custId, userId);
		//更新客服接入用户数量
		custStatus.joinUserNum(shopId, custId, 1);

		//发送接入消息
		Message message = new Message();
		message.setCreatetime(new Date());
		message.setUserid(userId);
		message.setShopid(Integer.valueOf(shopId));
		message.setCustid(Integer.valueOf(custId));
		message.setSendway(ContextConstant.SEND_PROMPT);
		message.setMessage("接入成功");
		lastMesCache.addLastMessage(userId, shopId, message);
		if (userClient != null) {
			userClient.sendEvent("message", message);
		}
	}
	@Override
	public void custJoin(String custId, String shopId, SocketIOClient custClient, String kefuStatus) {
		// TODO Auto-generated method stub
		long begin = System.currentTimeMillis();
		
		SocketIOClient ioClient = NettyOnlineCustClient.getOnlineCustClient(shopId, custId);
		
		//发送监听事件，提示已经登录了,然后关闭对应的浏览器
		if (ioClient != null &&  !ioClient.getSessionId().toString().equals(custClient.getSessionId().toString()) ) {
			ioClient.sendEvent("status", STATE_LOGIN);
			ioClient.disconnect();
		}
		//在线客服socket全局保存，可能是忙碌和在线，忙碌也可以接收消息，所以放入内存缓存
		NettyOnlineCustClient.putClient(shopId, custId, custClient);
		//放入在线缓存队列
		onlineCache.addOnlineCust(shopId, custId);
		
		this.compareCustDialogAndQueue(custId, shopId);
		boolean isCustStatus = false;
		//如果客服状态为null,只有客服在线，分配用户接入
		if(StringUtils.isBlank(kefuStatus)){
			isCustStatus = custStatus.isStatusByShopCustStatus(shopId, custId, ContextConstant.CustomerStatus.ONLINE.toString());
			logger.info("传入kefuStatus参数null----查询数据库客服是否在线online-----"+isCustStatus);
		}
		if(ContextConstant.CustomerStatus.ONLINE.toString().equals(kefuStatus)){
			isCustStatus = true;
			logger.info("传入kefuStatus参数在线online-----"+isCustStatus);
		}
		logger.info("客服坐席是否重新分配用户-----"+isCustStatus);
		if(isCustStatus){
			logger.info("客服坐席重新分配用户-------------");
			//客服接入，更新客服状态为在线online
			ShopCustPo custPo = custStatus.selectShopCustPo(shopId, custId);
			//客服为空，则为登录，拒绝连接
			if(null==custPo){
				return ;
			}
			
			//统计会话队列客服的会话人数
			int queueNum = dialogCache.countDialogSize(shopId,custId);
			
//			custPo.setCustserverstatus("online");
//			custStatus.loginShopCustPo(custPo);
			
			int dialogMaxNum = custPo.getMaxusernum();
			//判断对话数,是否最大接入数量
			if(queueNum<dialogMaxNum){
				List<String> queueUserIdList = queue.getQuequListBySize(shopId, dialogMaxNum-queueNum);
				if(null != queueUserIdList && queueUserIdList.size() >0){
					for(String userId : queueUserIdList){
//						NettyClients.getInstance().putCustomerEventClient("room_"+shopId+"-"+userId, custClient);
						talkCache.addShopTalk(userId, shopId, custId);
					}
					
//					custHistCache.addBatchCustHist(shopId, custId, queueUserIdList);
//					shopHistCache.addBatchShopHist(shopId, queueUserIdList);
					dialogCache.addBatchDialog(shopId, custId, queueUserIdList);
					custStatus.joinUserNum(shopId, custId, queueUserIdList.size());
					//发送页面排队用户监听事件
					dialogServie.waitDialogList(shopId);
				}
			}

		}
		if(ContextConstant.CustomerStatus.ONLINE.toString().equals(kefuStatus)||ContextConstant.CustomerStatus.BUSY.toString().equals(kefuStatus)){
			sendCurrentDialogEvent(custId, shopId, custClient);
		}
		long end = System.currentTimeMillis();
		logger.info("客服接入时间==="+(end - begin));
	}

	@Override
	public void sendCurrentDialogEvent(String custId, String shopId, SocketIOClient custClient) {
		//发送当前会话列表事件
		String dialogUserJson = dialogServie.dialogList(shopId, custId);
		if (custClient != null){
			custClient.sendEvent("currentDialog", dialogUserJson);
		}
		
	}

	@Override
	public void currentDialogJoin(String shopId, String userId) {
		// TODO Auto-generated method stub
		List<String> roomCustIdList = talkCache.getShopTalkList(userId, shopId);
		if(null!=roomCustIdList&&roomCustIdList.size()>0){
			for(String custId : roomCustIdList){
				SocketIOClient custClient = NettyOnlineCustClient.getOnlineCustClient(shopId, custId);
				if (custClient != null) {
					sendCurrentDialogEvent(custId, shopId, custClient);
				}
				
			}
		}
	}

	@Override
	public void custListJoinTalk(String shopId, String userId, List<String> custIds) {
		// TODO Auto-generated method stub
		if(null!=custIds&&custIds.size()>0){
			for(String custId : custIds){
				//加入聊天室，多人会话队列
				talkCache.addShopTalk(userId, shopId, custId);
				//加入会话队列
				dialogCache.addDialog(shopId, custId, userId);
				//更新客服接入用户数量
				custStatus.joinUserNum(shopId, custId, 1);
				
				SocketIOClient custClient = NettyOnlineCustClient.getOnlineCustClient(shopId, custId);

				if(null!=custClient){
					sendCurrentDialogEvent(custId, shopId, custClient);
				}
			}
			//发送页面聊天室客服列表监听事件
			sendTalkCustEvent(shopId,userId);
		}
	}

	private void currentDialogJoinByCustId(String shopId, String userId, String custId) {
		// TODO Auto-generated method stub
		SocketIOClient custClient = NettyOnlineCustClient.getOnlineCustClient(shopId, custId);
		sendCurrentDialogEvent(custId, shopId, custClient);
	}
	
	@Override
	public void sendTalkCustEvent(String shopId, String userId){
		List<String> custIds = talkCache.getShopTalkList(userId, shopId);
		if(null!=custIds&&custIds.size()>0){
			
			List<CustomerBaseInfo> infos = new ArrayList();
			for(String custId : custIds){
				List<ShopCustPo> list = custStatus.findListByCustIdAndShopId(custId, shopId);
				if (list != null && list.size() > 0){
					ShopCustPo shopCustPo = list.get(0);
					CustomerBaseInfo info = new CustomerBaseInfo();
					info.setCustId(custId);
					info.setCustname(shopCustPo.getCustname());
					info.setHeadurl(shopCustPo.getHeadurl());
					infos.add(info);
				}
			
			}
			
			for(String custId : custIds){
				
				//当前服务的用户，如果相同则发送聊天室客服列表
				String serUserId = NettyCustCurrentDialogUserMap.getCurrentUserId(shopId, custId);
				if(StringUtils.isNotBlank(serUserId)&&userId.equals(serUserId)){
					SocketIOClient custClient = NettyOnlineCustClient.getOnlineCustClient(shopId, custId);
					if (custClient != null){
						custClient.sendEvent(ContextConstant.TALK_CUST_EVENT, infos);
					}
				}
			}
		}
	}

	@Override
	public List<CustomerBaseInfo> talkCustBaseInfoList(String shopId, String userId) {
		// TODO Auto-generated method stub
		List<CustomerBaseInfo> infos = null;
		//获取聊天室所有客服ids
		List<String> custIds = talkCache.getShopTalkList(userId, shopId);
		if (null != custIds && custIds.size() > 0) {
			infos = new ArrayList();
			for (String custId : custIds) {
				List<ShopCustPo> list = custStatus.findListByCustIdAndShopId(custId, shopId);
				if (list != null && list.size() > 0){
					ShopCustPo shopCustPo = list.get(0);
					CustomerBaseInfo info = new CustomerBaseInfo();
					info.setCustId(custId);
					info.setCustname(shopCustPo.getCustname());
					info.setHeadurl(shopCustPo.getHeadurl());
					infos.add(info);
				}
			}
		}
		return infos;
	}

	@Override
	public void compareCustDialogAndQueue(String custId, String shopId) {
		logger.info("会话状态改变，对比会话队列与排队队列，删除重复元素------------");
		// TODO Auto-generated method stub
		List<String> diaList = dialogCache.getDialogAllList(shopId, custId);
		List<String> queueList = queue.getQuequList(shopId);
		//集合交集，相同的元素
		diaList.retainAll(queueList);
		if(null!=diaList&&diaList.size()>0){
			logger.info("删除排队重复元素，并发送排队用户事件------------"+diaList);
			queue.removeQuequList(shopId, diaList);
			//发送页面排队用户监听事件
			dialogServie.waitDialogList(shopId);
		}
	}
}
