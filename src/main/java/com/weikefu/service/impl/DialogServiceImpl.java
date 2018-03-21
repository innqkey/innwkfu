package com.weikefu.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.SocketIOClient;
import com.weikefu.cache.RedisDialogCache;
import com.weikefu.cache.RedisMessageCountCache;
import com.weikefu.cache.RedisQueueCache;
import com.weikefu.cache.RedisShopCustOnlineCache;
import com.weikefu.cache.RedisUserShopLastMessageCache;
import com.weikefu.config.socket.client.NettyCustCurrentDialogUserMap;
import com.weikefu.config.socket.client.NettyOnlineCustClient;
import com.weikefu.constant.ContextConstant;
import com.weikefu.dao.mangodb.UserInfoDao;
import com.weikefu.po.Message;
import com.weikefu.po.UserInfo;
import com.weikefu.service.DialogService;
import com.weikefu.util.ResUtils;
import com.weikefu.vo.UserInfoVo;

@Service
public class DialogServiceImpl implements DialogService {

	@Autowired
	private RedisDialogCache dialogCache; //排队队列
	
	@Autowired
	private UserInfoDao userInfoDao; //聊天用户信息
	
	@Autowired
	private RedisUserShopLastMessageCache lastMsgCache; //最后一条消息
	
	@Autowired
	private RedisMessageCountCache  messageCountCache;
	
	@Autowired
	private RedisQueueCache queueCache;
	
	@Autowired
	private RedisShopCustOnlineCache onlineCache;
	private Pattern pattern =  Pattern.compile("<\\s*a.*?/a\\s*>");
	
	private final Logger logger = LoggerFactory.getLogger(DialogServiceImpl.class);
	@Override
	public String dialogList(String shopId, String custId) {
		// TODO Auto-generated method stub
		if(StringUtils.isEmpty(shopId)||StringUtils.isEmpty(custId)){
			return ResUtils.errRes("102", "请求参数错误");
		}
		List<String> useridList = dialogCache.getDialogAllList(shopId, custId);
		
		List<UserInfoVo> joinUserList = getUserInfoVoList(shopId, custId, useridList);
		return ResUtils.okRes(joinUserList);
	}
	
	/*
	 * 根据userid获取用户最后一条消息记录，获取用户信息，获取未读消息数量，拼装组合成UserInfoVo
	 */
	private List<UserInfoVo> getUserInfoVoList(String shopId, String custId, List<String> useridList) {
		if(StringUtils.isBlank(shopId)||StringUtils.isBlank(custId)||null==useridList||useridList.size()==0){
			return null;
		}
		List<UserInfoVo> joinUserList = new ArrayList<>();
		for (String userId : useridList) {
			UserInfo userInfo = userInfoDao.findByUserId(userId);
			Message msgVo = lastMsgCache.getLastMessage(userId, shopId);
			if (msgVo == null){
				msgVo = new Message();
				msgVo.setCreatetime(new Date());
				msgVo.setShopid(Integer.valueOf(shopId));
				msgVo.setUserid(userId);
				msgVo.setSendway(ContextConstant.SEND_PROMPT);
				msgVo.setMsgcount(1);
				lastMsgCache.addLastMessage(userId, shopId, msgVo);
			}
			long messageCount = messageCountCache.getMessageCount(shopId,userId, custId);
			UserInfoVo joinInfo = new UserInfoVo();
			joinInfo.setUserId(userId);
			joinInfo.setShopId(shopId);
			//获取当前客服正在服务的用户
			String curUser = NettyCustCurrentDialogUserMap.getCurrentUserId(shopId, custId);
			if(StringUtils.isNotBlank(curUser)&&curUser.equals(userId)){
				joinInfo.setActive(true);
			}
			
			if(null!=userInfo){
				joinInfo.setOpenid(userInfo.getOpenid());
				joinInfo.setNickname(userInfo.getNickname());
				joinInfo.setHeadimgurl(userInfo.getHeadimgurl());
				joinInfo.setJoinway(userInfo.getJoinway());
			}
			if(null!=msgVo){
				joinInfo.setMsgcount(messageCount);
				//修改了加入的方式，之前是全部是写死为weixin的，现在从用户信息中直接获取加入的类型
				
				if (StringUtils.isNotBlank(msgVo.getMessage())){
					boolean matches = this.pattern.matcher(msgVo.getMessage()).matches();
					if (matches){
						joinInfo.setMessage("[链接]");
					}else {
						joinInfo.setMessage(msgVo.getMessage());
					}
				}
				joinInfo.setTimeTemp(msgVo.getCreatetime());
			}
			joinUserList.add(joinInfo);
		}
		if (joinUserList.size() > 1){
			insertSort(joinUserList);
		}
		
		return joinUserList;
	}
		/**
		 * 插入排序算法
		 * @param a
		 */
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

	@Override
	public void waitDialogList(String shopId) {
		// TODO Auto-generated method stub
		List<String> userIdList = queueCache.getQuequList(shopId);
		
		
		List<String> custIds = onlineCache.getOnlineCustAllList(shopId);
    	if(null!=custIds&&custIds.size()>0){
    		for(String custId : custIds){
    			List<UserInfoVo> joinUserList = getUserInfoVoList(shopId, custId, userIdList);
    			SocketIOClient custClient = NettyOnlineCustClient.getOnlineCustClient(shopId, custId);
    			logger.info("waitDialog-----shopId;custId;custclient"+shopId+";"+custId+";"+custClient);
    			if (null != custClient){
    				custClient.sendEvent("waitQueue", joinUserList);
    			}
    			
        	}
    	}
	}

	@Override
	public List<UserInfoVo> queueUserInfoVoList(String shopId, String custId) {
		// TODO Auto-generated method stub
		List<String> userIdList = queueCache.getQuequList(shopId);
    	List<UserInfoVo> joinUserList = getUserInfoVoList(shopId, custId, userIdList);
    	return joinUserList;
	}

}
