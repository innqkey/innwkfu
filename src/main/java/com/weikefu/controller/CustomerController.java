package com.weikefu.controller;

import static org.hamcrest.CoreMatchers.nullValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.corundumstudio.socketio.SocketIOClient;
import com.weikefu.cache.RedisDialogCache;
import com.weikefu.cache.RedisMessageCountCache;
import com.weikefu.cache.RedisUserShopLastCustCache;
import com.weikefu.cache.RedisUserShopLastMessageCache;
import com.weikefu.cache.RedisUserShopTalkCache;
import com.weikefu.config.socket.client.NettyClients;
import com.weikefu.config.socket.client.NettyCustCurrentDialogUserMap;
import com.weikefu.config.socket.client.NettyOnlineCustClient;
import com.weikefu.constant.ContextConstant;
import com.weikefu.po.ShopCustPo;
import com.weikefu.service.ShopCustPoStatusService;
import com.weikefu.service.ShopUserHistorySerivce;
import com.weikefu.service.UserInfoService;
import com.weikefu.po.Message;
import com.weikefu.po.UserInfo;
import com.weikefu.service.AllotCustService;
import com.weikefu.service.CustUserHistoryService;
import com.weikefu.service.MessageService;
import com.weikefu.util.ConvertUtils;
import com.weikefu.util.ResUtils;
import com.weikefu.vo.CustomerBaseInfo;
import com.weikefu.vo.CustomerInfoVo;
import com.weikefu.vo.PageTemp;
import com.weikefu.vo.UserInfoVo;
/**
 * 用户提供接口
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/customer")
public class CustomerController {


	@Autowired
	private RedisUserShopTalkCache talkCache;// 聊天室队列

	@Autowired
	private RedisDialogCache dialogCache;

	@Autowired
	private CustUserHistoryService custHistCache;

	@Autowired
	private ShopUserHistorySerivce shopUserHistorySerivce;


	@Autowired
	private RedisUserShopLastMessageCache lastMessage;

	@Autowired
	private ShopCustPoStatusService custStatus;

	@Autowired
	private RedisMessageCountCache messageCountCache;

	@Autowired
	private ShopCustPoStatusService shopCustService;
	@Autowired
	private MessageService messageService;
	@Autowired
	private UserInfoService userInfoService;

	@Autowired
	private AllotCustService allotSer;
	
	@Autowired
	private RedisUserShopLastCustCache lastCustCache;
	private Pattern pattern =  Pattern.compile("<\\s*a.*?/a\\s*>");

	private final Logger logger = LoggerFactory.getLogger(CustomerController.class);
	/*
	 * 客服接入-放入在线客服队列-聊天室队列取用户-排队队列取用户
	 */
//
//	@RequestMapping("/custLogin")  
//    public String custlogin(String custId, String shopId){  
//
//		long begin = System.currentTimeMillis();
//		List<String> userIdList = dialogCache.getDialogAllList(shopId, custId);
//
//		// 客服接入，更新客服状态为在线online
//		ShopCustPo CustPo = new ShopCustPo();
//		CustPo.setShopid(Integer.valueOf(shopId));
//		CustPo.setCustid(Integer.valueOf(custId));
//		CustPo.setCustserverstatus("online");
//		CustPo.setCreatetime(new Date());
//		CustPo.setMaxusernum(5);
//		custStatus.loginShopCustPo(CustPo);
//
////		int queueNum = 0;
////		if (null != userIdList && userIdList.size() > 0) {
////			queueNum = userIdList.size();
////		}
////
////		int dialogMaxNum = 5;
////		// 判断对话数,是否最大接入数量
////		if (queueNum < 5) {
////			List<String> queueUserIdList = queue.getQuequListBySize(shopId, dialogMaxNum - userIdList.size());
////			if (null != queueUserIdList && queueUserIdList.size() > 0) {
////				userIdList.addAll(queueUserIdList);
////				custHistCache.addBatchCustHist(shopId, custId, queueUserIdList);
////				shopHistCache.addBatchShopHist(shopId, queueUserIdList);
////				dialogCache.addBatchDialog(shopId, custId, queueUserIdList);
////			}
////		}
//		onlineCache.addOnlineCust(shopId, custId);
//		long end = System.currentTimeMillis();
//
//		System.out.println("客服登录上线时间==="+(end - begin));
//		return ResUtils.okRes();
//    }


	/**
	 * 当客服选中一个窗口的时候,改变该custid服务的Serverid，并删除缓存消息
	 * @param request
	 * @param roomId
	 * @param custId
	 * @return 
	 */
	@RequestMapping(value = "/selectedUser")
	public String clearMessageCount(HttpServletRequest request, String userId, String custId, String shopId,
			PageTemp pageTemp) {
		long begin = System.currentTimeMillis();
		try {
			if (StringUtils.isBlank(userId) || StringUtils.isBlank(custId) || StringUtils.isBlank(shopId)) {
				return ResUtils.execRes( "参数不能为空");
			}

			// 改变当前服务的用户的id
			List<ShopCustPo> list = shopCustService.findListByCustIdAndShopId(custId, shopId);
			if (list != null && list.size() > 0) {
				ShopCustPo shopCustPo = list.get(0);
				shopCustPo.setServeruserid(userId);
				shopCustService.changeCustServiceUser(shopCustPo.getId(), shopCustPo.getServeruserid());
			}
			messageCountCache.clearMessageCount(shopId,userId,shopId);
			//客服坐席当前服务的用户userid，放入内存
			NettyCustCurrentDialogUserMap.putCurrentUserId(shopId, custId, userId);

			messageCountCache.clearMessageCount(shopId,userId,custId);
			long end = System.currentTimeMillis()-begin;
			System.out.println("selectedUser接口消耗时间==" + end);
			return ResUtils.okRes();
		} catch (Exception e) {
			e.printStackTrace();
			return ResUtils.execRes();
		}
	}

	/**
	 * 用来进行客服的转接工作 转接
	 * 
	 * @param request
	 * @param custId
	 *            需要转移到的custid
	 * @param shopId
	 *            当前商店的id
	 * @param userId
	 *            用户的对应的id
	 * @return 1
	 */
	@RequestMapping("/custTransfer")
	public String custTransfer(HttpServletRequest request, String custId, String shopId, String userId) {
		if (StringUtils.isNotBlank(custId) && StringUtils.isNotBlank(shopId) && StringUtils.isNotBlank(userId)) {
			try {
				List<String> custList = talkCache.getShopTalkList(userId, shopId);
				if (custList == null || custList.size() > 1) {
					return ResUtils.errRes("404", "该服务客服大于2的时候不能转接");
				}
				String orginCust = custList.get(0);
				//进行转接
				talkCache.removeTalkCust(userId, shopId, orginCust);
				
				talkCache.addShopTalk(userId, shopId, custId);
				
				//找到custid的当前会话，删除
				List<String> userList = dialogCache.getDialogAllList(shopId, orginCust);
				if (userList.contains(userId)) {
					logger.info("userId已经删除了");
					int resInt = dialogCache.removeDialog(shopId, orginCust, userId);
					//关闭对话，放入用户最后一个接入客服坐席缓存
					lastCustCache.addUserShopLastCust(userId, shopId, orginCust);
					//移除会话队列，同时更新数据库当前接入人数，会话队列数量与数据库当前接入人数保持一致
					if(resInt>0){
						shopCustService.joinUserNum(shopId, orginCust, 0-resInt);
					}
				}
				dialogCache.addDialog(shopId, custId, userId);
				shopCustService.joinUserNum(shopId, custId, 1);
				
				//同时给转接的人发送消息,先给最新的一条记录
				Message message = new Message();
				//消息提示
				message.setSendway(ContextConstant.SEND_PROMPT);
				List<ShopCustPo> list = shopCustService.findListByCustIdAndShopId(orginCust, shopId);
				if (list != null && list.size() > 0) {
					ShopCustPo shopCustPo = list.get(0);
					UserInfo userInfo = userInfoService.findByUserId(userId);
					if (userInfo != null) {
						UserInfoVo userInfoVo = (UserInfoVo) ConvertUtils.convertDtoAndVo(userInfo,UserInfoVo.class);
						if (userInfoVo != null) {
							userInfoVo.setMessage(new StringBuilder().append("来自客服").append(shopCustPo.getCustname()).append("的转接").toString());
							userInfoVo.setMsgcount(1);
							userInfoVo.setShopId(shopId);
							userInfoVo.setTimeTemp(new Date());
							NettyClients.getInstance().sendCustomerEventMessage(shopId,custId, "userTranfer", userInfoVo);
						}
					}
					message.setCreatetime(new Date());
					message.setUserid(userId);
					message.setShopid(Integer.valueOf(shopId));
					message.setCreatetime(new Date());
//					message.setRoomid(new StringBuilder().append(shopId).append("-").append(userId).toString());
					String custname = shopCustPo.getCustname();
					message.setMessage(new StringBuilder().append("来自客服").append(shopCustPo.getCustname()).toString());
					//lastMessageCache.addLastMessage(userId, shopId, message);
					StringBuilder builder = new StringBuilder();
					
					builder.append("以上对话此前由客服").append(custname).append("服务");
					message.setMessage(builder.toString());
					messageService.saveMessage(message);
					//添加对应的消息的信息
					messageCountCache.increaseMessageCount(shopId, userId, custId);
				}else {
					return ResUtils.execRes("custId" + orginCust + "不存在");
				}
			} catch (Exception e) {
			
				e.printStackTrace();
				return ResUtils.execRes("异常");
			}
		}else{
			return ResUtils.execRes( "参数不为空");
		}
		return ResUtils.okRes();
	}

	/**
	 * 用户转接的时候展现所有的用户，包括状态
	 * 
	 * @param request
	 * @param shopId
	 * @return 1
	 */
	@RequestMapping("/shopAllCust")
	public String shopAllCustStatus(HttpServletRequest request, Integer shopId) {
		if (shopId == null) {
			return ResUtils.execRes( "参数不能为空");
		}
		List<ShopCustPo> custList = shopCustService.findAllCust(shopId);
		if (custList != null && custList.size() > 0) {
			return ResUtils.okRes(custList);
		}
		return ResUtils.execRes("结果为空");
	}

	/***
	 * 在转接的时候，搜索对应的客服名称
	 * 
	 * @param request
	 * @param shopId
	 * @param keyWord
	 * @return 1
	 */
//	@RequestMapping("/searchCustomer")
//	public String searchShopCustBykeyword(HttpServletRequest request, String shopId, String keyWord) {
//		// 对非法字符进行过滤
//		String matcheString = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|"  
//            + "(\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";
//
//		if (StringUtils.isNotBlank(keyWord) && !Pattern.compile(matcheString).matcher(keyWord).matches()) {
//			List<ShopCustPo> custList = shopCustService.searchShopCustBykeyword(shopId, keyWord);
//			if (custList != null && custList.size() > 0) {
//				return ResUtils.okRes(custList);
//			}
//			return ResUtils.okRes(null);
//		} else {
//			return ResUtils.execRes("非法参数");
//		}
//	}

	/**
	 * 用于登录的时候加载客户先关的 信息
	 * 
	 * @param shopId
	 * @param custId
	 * @param request
	 * @return 1
	 */
	@RequestMapping("/init")
	public String initCustomer(String shopId, String custId, String status, HttpServletRequest request, HttpServletResponse response) {
		logger.info("init-------------------------");
		if (StringUtils.isBlank(shopId) && StringUtils.isBlank(custId)) {
			return ResUtils.execRes( "参数异常");
		}
		
		if(StringUtils.isNotBlank(status)){
			logger.info("init----更新客服状态-----shopId,custId,status=="+shopId+";"+custId+";"+status);
			shopCustService.changeCustStatus(shopId, custId, status,0);
		}
		
		CustomerInfoVo customerInfoVo = new CustomerInfoVo();
		try {
			List<ShopCustPo> list = custStatus.findListByCustIdAndShopId(custId, shopId);
			if (list != null && list.size() > 0 && list.size() < 2) {
				ShopCustPo shopCustPo = list.get(0);
				ConvertUtils.convertDtoAndVo(shopCustPo, customerInfoVo);
				List<String> daList = dialogCache.getDialogAllList(shopId, custId);
				List<UserInfoVo> userInfoVos = new ArrayList<UserInfoVo>();
//				UserInfoVo info = new UserInfoVo();
//				
//				//查询订单的消息，已经订单的数量？？？？？
//				info.setNickname("订单消息");
//				info.setMessage("暂无新订单");
//				info.setType(ContextConstant.WINDOW_ORDER);
//				info.setMsgtype(ContextConstant.MES_TEXT);
//				userInfoVos.add(info);
//				info.setHeadimgurl("https://img.yzcdn.cn//im_zero/image/ring_bk@2x.png");
				//查询出正在服务的客服，然后加载对应的信息
				if (daList != null && daList.size() > 0) {
					

					int waitPerson = 0;

					for (String userId : daList) {
						UserInfo userInfo = userInfoService.findByUserId(userId);
						long messageCount = messageCountCache.getMessageCount(shopId,userId, custId);
						Message message = lastMessage.getLastMessage(userId, shopId);
						if (null != userInfo) {
							UserInfoVo userInfoVo = (UserInfoVo) ConvertUtils.convertDtoAndVo(userInfo,UserInfoVo.class);
							if (null != userInfoVo) {
								userInfoVo.setMsgcount(messageCount);

								if(null!=message){
									userInfoVo.setTimeTemp(message.getCreatetime());
									if (StringUtils.isNotBlank(message.getMessage())){
										boolean matches = this.pattern.matcher(message.getMessage()).matches();
										if (matches){
											userInfoVo.setMessage("[链接]");
										}else{
											userInfoVo.setMessage(message.getMessage());
										}
									}
									userInfoVo.setMsgtype(message.getMsgtype());
								}
								//获取当前客服正在服务的用户
								String curUser = NettyCustCurrentDialogUserMap.getCurrentUserId(shopId, custId);
								if(StringUtils.isNotBlank(curUser)&&curUser.equals(userId)){
									userInfoVo.setActive(true);
								}

								userInfoVo.setShopId(shopId);

								waitPerson++;

								userInfoVos.add(userInfoVo);
							}
						}
					}
					insertSort(userInfoVos);
					customerInfoVo.setJoinusernum(waitPerson);
				}
				customerInfoVo.setUserInfoVos(userInfoVos);
				return ResUtils.okRes(customerInfoVo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return ResUtils.execRes("custid =" + custId + "不存在或异常");

	}

	// 根据时间进行排序算法
	public void insertSort(List<UserInfoVo> a) {
		int i, j;// 要插入的数据
		UserInfoVo date;
		for (i = 1; i < a.size(); i++) {// 从数组的第二个元素开始循环将数组中的元素插入
			date = a.get(i);// 设置数组中的第2个元素为第一次循环要插入的数据
			j = i - 1;

			while (j >= 0 && date.getTimeTemp().getTime() > a.get(j).getTimeTemp().getTime()) {
				a.set(j + 1, a.get(j));// 如果要插入的元素小于第j个元素,就将第j个元素向后移动
				j--;
			}
			a.set(j + 1, date);// 直到要插入的元素不小于第j个元素,将insertNote插入到数组中
		}
	}

	/**
	 * 客服忙碌/离线
	 * 
	 * @param shopId
	 * @param custId
	 * @param custServerStatus
	 * @return
	 */
	@RequestMapping(value = "/updateCustStatus")
	public String updateCustStatus(String shopId, String custId, String custServerStatus) {
		try {
			if (StringUtils.isBlank(shopId) || StringUtils.isBlank(custId) || StringUtils.isBlank(custId)
					|| StringUtils.isBlank(custServerStatus)) {
				return ResUtils.execRes("请求参数不能为空");
			}
			
			boolean status = false;
			for (ContextConstant.CustomerStatus e : ContextConstant.CustomerStatus.values()) {
				if (e.toString().equals(custServerStatus)) {
					status = true;
				}
			}
			if (!status) {
				return ResUtils.execRes("客服状态不匹配");
			}
			int shopid = Integer.valueOf(shopId);
			int custid = Integer.valueOf(custId);
			custStatus.updateCustOnlineTime(shopid, custid, custServerStatus);
			//离线的时候，如何用户发送消息发现该客服部是这个状态，自动放入的排序中，然后进行分配
			
			//如果更新客服在线，重新分配用户
//			if(ContextConstant.CustomerStatus.ONLINE.toString().equals(custServerStatus)){
				//重新分配用户，分配用户触发推送当前会话列表事件
				allotSer.custJoin(custId, shopId, NettyOnlineCustClient.getOnlineCustClient(shopId, custId), custServerStatus);
			//如果不是在线则发送当前会话列表事件
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return ResUtils.execRes("异常");
		}
		return ResUtils.okRes();
	}

	/**
	 * 关闭结束对话(批量删除对话也调用这个接口)
	 * 
	 * @param request
	 * @param shopId
	 * @param custId
	 * @param userIds，批量关闭userids，userid用都好分隔',';userIds = 1,2,3
	 * @return
	 */
	@RequestMapping("/closeDialog")
	public String closeDialog(HttpServletRequest request, String shopId, String custId, String userIds){
		try {
			if(StringUtils.isEmpty(custId) || StringUtils.isEmpty(shopId) || StringUtils.isEmpty(userIds) ){
				return ResUtils.execRes("请求参数错误");
			}
			//包含英文逗号，表示批量关闭会话
			String[] splitUserIds = userIds.split(",");
			
			List<String> userIdList = new ArrayList<String>();
			for (String userId : splitUserIds) {
				userIdList.add(userId);
				int resInt = dialogCache.removeDialog(shopId, custId, userId);
				//关闭对话，放入用户最后一个接入客服坐席缓存
				lastCustCache.addUserShopLastCust(userId, shopId, custId);
				//移除会话队列，同时更新数据库当前接入人数，会话队列数量与数据库当前接入人数保持一致
				if(resInt>0){
					shopCustService.joinUserNum(shopId, custId, 0-resInt);
				}
				talkCache.removeTalkCust(userId, shopId, custId);
				//发送客服变化通知事件
				allotSer.sendTalkCustEvent(shopId,userId);
			}
			custHistCache.addBatchCustHist(shopId, custId, userIdList);
			shopUserHistorySerivce.addBatchShopHist(shopId, userIdList);
			
			ShopCustPo custPo = shopCustService.selectShopCustPo(shopId, custId);
			//判断客服是否在线，如果在线重新分配用户
//			if(null!=custPo&& ContextConstant.CustomerStatus.ONLINE.toString().equals(custPo.getCustserverstatus())){
				//重新分配用户，分配用户触发推送当前会话列表事件
			allotSer.custJoin(custId, shopId, NettyOnlineCustClient.getOnlineCustClient(shopId, custId),custPo.getCustserverstatus());
			//关闭对话的同时将后台的服务的serviceUserid置空
			shopCustService.changeCustStatus(shopId, custId, null,1);	
				
			//如果不是在线则发送当前会话列表事件
//			}else{
//				allotSer.sendCurrentDialogEvent(custId, shopId, NettyOnlineCustClient.getOnlineCustClient(shopId, custId));
//			}
			return ResUtils.okRes();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ResUtils.execRes();
		}
	}

	/**
	 * 选中多个客服坐席加入聊天室会话
	 * @param request
	 * @param shopId
	 * @param custId
	 */
	@RequestMapping("/custListJoinTalk")
	public String custListJoinTalk(HttpServletRequest request, String shopId, String userId,
			@RequestParam("custIds[]") List<String> custIds) {
		try {
			if (StringUtils.isBlank(shopId) || StringUtils.isBlank(userId)){
				return ResUtils.errRes("404", "参数为空");
			}
			if (custIds == null || custIds.size() < 1) {
				return ResUtils.okRes();
			}
//			//重新分配用户，分配用户触发推送当前会话列表事件
			allotSer.custListJoinTalk(shopId, userId, custIds);
			return ResUtils.okRes();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ResUtils.execRes();
		}
	}

	/**
	 * 为该客服服务的所有的Customer的数量
	 * 
	 * @param request
	 * @param shopId
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/countJoinCust")
	public String countJoinCust(HttpServletRequest request, String shopId, String userId) {
		if (StringUtils.isBlank(shopId) || StringUtils.isBlank(userId)) {
			return ResUtils.errRes("404", "参数为空");
		}
		List<String> custIds = talkCache.getShopTalkList(userId, shopId);
		if (null != custIds && custIds.size() > 0) {
			return ResUtils.okRes(custIds.size());
		} else {
			return ResUtils.okRes("0");
		}
	}

	/**
	 * 在同以个room中所有的Customer的详细情况
	 * 
	 * @param request
	 * @param shopId
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/listJoinCust")
	public String listJoinCust(HttpServletRequest request, String shopId, String userId) {
		try {
			if (StringUtils.isBlank(shopId) || StringUtils.isBlank(userId)) {
				return ResUtils.errRes("404", "参数为空");
			}
			List<CustomerBaseInfo> infos;
			infos = allotSer.talkCustBaseInfoList(shopId, userId);
			return ResUtils.okRes(infos);
		} catch (Exception e) {
			e.printStackTrace();
			return ResUtils.exceCode;
		}
	}

	/**
	 * 获取客服服务用户历史记录
	 * 
	 * @param shopid
	 * @param custid
	 * @return
	 */
	@RequestMapping(value = "/userHistoryList")
	public String userHistoryList(String shopId, String custId, PageTemp page) {
		if (StringUtils.isEmpty(shopId) || StringUtils.isEmpty(custId)) {
			return ResUtils.errRes("404", "请求参数有误");
		}
		List<String> userList = custHistCache.getCustHistListBySize(shopId, custId, page);
		List<UserInfoVo> userHistoryList = new ArrayList<UserInfoVo>();
		
		if (userList != null && userList.size() > 0) {
			for (String user : userList) {
				UserInfo userInfo = userInfoService.findByUserId(user);
				if (userInfo != null) {
					UserInfoVo userInfoVo = new UserInfoVo();
					ConvertUtils.convertDtoAndVo(userInfo, userInfoVo);
					Message userLastMessage = lastMessage.getLastMessage(user, shopId);
					userInfoVo.setUserId(userInfo.getUserId());
					userInfoVo.setShopId(shopId);
					userInfoVo.setHeadimgurl(userInfo.getHeadimgurl());
					if(null!=userLastMessage){
						userInfoVo.setTimeTemp(userLastMessage.getCreatetime());
						if (this.pattern.matcher(userLastMessage.getMessage()).matches()){
							userInfoVo.setMessage("[链接]");
						}else{
							userInfoVo.setMessage(userLastMessage.getMessage());
						}
						userInfoVo.setMsgtype(userLastMessage.getMsgtype());
					}
					userInfoVo.setNickname(userInfo.getNickname());
					userHistoryList.add(userInfoVo);
				}
			}
			insertSort(userHistoryList);
			
		}
		
		return ResUtils.okRes(userHistoryList);
	}
	
	/*
	 * 获取当前会话用户信息
	 */
	@RequestMapping(value = "/getUserInfo")
	public String getUserInfo(String userId){
		UserInfo userInfo = userInfoService.findByUserId(userId);
		return ResUtils.okRes(userInfo);
	}

	
	/**
	 * 改变客服的最大接入人数
	 * @param request
	 * @param shopId
	 * @param custId
	 * @param maxusernum
	 * @return
	 */
	@RequestMapping(value = "/changeCustMaxusernum")
	public String changeCustMaxusernum(HttpServletRequest request, String shopId, String custId, Integer maxusernum){
		try {
			if(StringUtils.isBlank(custId) || StringUtils.isBlank(shopId) ){
				return ResUtils.errRes("102", "请求参数错误");
			}if( maxusernum==0 || maxusernum==null){
				return ResUtils.errRes("102", "最大接入人数不能为空");
			}
			//修改客服的最大接入人数
			shopCustService.updateMaxusernum(shopId,custId,maxusernum);
			SocketIOClient custClient = NettyOnlineCustClient.getOnlineCustClient(shopId, custId);
			ShopCustPo custPo = shopCustService.selectShopCustPo(shopId, custId);
			//判断客服是否在线，如果在线重新分配用户
			if(null!=custPo&& ContextConstant.CustomerStatus.ONLINE.toString().equals(custPo.getCustserverstatus())){
				//重新分配客服
				allotSer.custJoin(custId, shopId, custClient,custPo.getCustserverstatus());
			}
			return ResUtils.okRes();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ResUtils.execRes();
		}
	}
	
	/**
	 * 获取客服在线人数集合
	 * @param request
	 * @param shopId
	 * @param custId
	 * @return
	 */
	@RequestMapping(value = "/getOnlineList")
	public String getOnlineList(HttpServletRequest request, String shopId){
		try {
			if(StringUtils.isBlank(shopId) ){
				return ResUtils.errRes("102", "请求参数错误");
			}
			List<ShopCustPo> onlineList = shopCustService.findBycustserverstatus(shopId,ContextConstant.CustomerStatus.ONLINE.toString());
			return ResUtils.okRes(onlineList);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ResUtils.execRes();
		}
	}
	
	/**
	 * 获取客服忙碌人数集合
	 * @param request
	 * @param shopId
	 * @param custId
	 * @return
	 */
	@RequestMapping(value = "/getBusyList")
	public String getBusyList(HttpServletRequest request, String shopId){
		try {
			if(StringUtils.isBlank(shopId)){
				return ResUtils.errRes("102", "请求参数错误");
			}
			List<ShopCustPo> busyList = shopCustService.findBycustserverstatus(shopId,ContextConstant.CustomerStatus.BUSY.toString());
			return ResUtils.okRes(busyList);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ResUtils.execRes();
		}
	}
	
	/**
	 * 获取客服离线人数集合
	 * @param request
	 * @param shopId
	 * @param custId
	 * @return
	 */
	@RequestMapping(value = "/getOfflineList")
	public String getOfflineList(HttpServletRequest request, String shopId){
		try {
			if(StringUtils.isBlank(shopId) ){
				return ResUtils.errRes("102", "请求参数错误");
			}
			List<ShopCustPo> offlineList = shopCustService.findBycustserverstatus(shopId,ContextConstant.CustomerStatus.OFFLINE.toString());
			return ResUtils.okRes(offlineList);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ResUtils.execRes();
		}
	}
	
	/**
	 * 获取该客服的状态信息
	 * @param request
	 * @param shopId
	 * @param custId
	 * @return
	 */
	@RequestMapping(value = "/getShopCustPo")
	public String getShopCustPo(HttpServletRequest request, String shopId, String custId){
		try {
			if(StringUtils.isBlank(shopId) || StringUtils.isBlank(custId)){
				return ResUtils.errRes("102", "请求参数错误");
			}
			List<ShopCustPo> list = shopCustService.findListByCustIdAndShopId(custId, shopId);
			if(null != list && list.size() > 0){
				return ResUtils.okRes(list.get(0));
			}else{
				return ResUtils.errRes("102", "客服不存在");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ResUtils.execRes();
		}
	}


		/**
	 * 用户返回用户的额信息,搜索先不做
	 * @parm Keywod是关键字搜索
	 *
	 */
	//@RequestMapping(value="searchUser",method=RequestMethod.POST)
	public String searchUser(String keyWord,String shopId){
		// 对非法字符进行过滤
		String matcheString = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|"  
            + "(\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";

		if (StringUtils.isNotBlank(keyWord) && !Pattern.compile(matcheString).matcher(keyWord).matches()) {
			List<UserInfo> custList = shopCustService.searchUser(shopId, keyWord);
			if (custList != null && custList.size() > 0) {
				return ResUtils.okRes(custList);
			}
			return ResUtils.okRes(null);
		} else {
			return ResUtils.execRes("非法参数");
		}
	}
	

}
