package com.weikefu.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.weikefu.cache.RedisDialogCache;
import com.weikefu.cache.RedisMessageCountCache;
import com.weikefu.cache.RedisUserShopLastMessageCache;
import com.weikefu.po.Message;
import com.weikefu.po.QuickMessagePo;
import com.weikefu.po.UserInfo;
import com.weikefu.service.MessageService;
import com.weikefu.service.QuickMessageService;
import com.weikefu.service.ShopUserHistorySerivce;
import com.weikefu.service.UserInfoService;
import com.weikefu.util.ResUtils;
import com.weikefu.vo.PageInfo;
import com.weikefu.vo.PageTemp;

/** 
* @author qinkai 
* @date 2017年12月6日
*/

@RestController
@RequestMapping(value = "/message")
public class MessageController {
	
	@Autowired
	private QuickMessageService quickMessageService;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private ShopUserHistorySerivce shopUserHistorySerivce;
	
	@Autowired
	private UserInfoService userInfoService;
	
	@Autowired
	private RedisUserShopLastMessageCache lastMessageCache;

	@Autowired
	private RedisDialogCache dialogCache;
	@Autowired
	private RedisMessageCountCache messageCountCache;
	

	/**
	 * 从redis中获取商户的所有用户的最后一条历史记录
	 * @param shopId
	 * @param page
	 * @param request
	 * @return
	 */
	@RequestMapping("/getShopLastMessage")
	public String getShopLastMessage(String shopId, PageTemp page, HttpServletRequest request){
		try {
			List<Message> shopLastMessage = new ArrayList<>();
			List<String> dialogList = shopUserHistorySerivce.getShopHistListByPage(shopId, page);
			if (dialogList == null || dialogList.size() < 0) {
				 return ResUtils.execRes(null);
			}
			
			for (String userid : dialogList) {
				UserInfo userInfo = userInfoService.findByUserId(userid);
				Message message = lastMessageCache.getLastMessage(userid, shopId);
				if (userInfo != null && message != null) {
					message.setUsername(userInfo.getNickname());
				}

				shopLastMessage.add(message);
			}
			return ResUtils.okRes(shopLastMessage);
		} catch (Exception e) {
			// TODO: handle exception
			 e.printStackTrace();
			 return ResUtils.execRes();
		}
	}
	
	/**
	 * 返回所有快捷回复
	 */
	@RequestMapping(value = "/quickMessageList")
	public String list(String shopid, HttpServletRequest request){
		if (StringUtils.isBlank(shopid) || Integer.valueOf(shopid) < 0){
			return ResUtils.execRes();
		}
		List<QuickMessagePo> poList = quickMessageService.selectAll(Integer.valueOf(shopid));
		return ResUtils.okRes(poList);
	}
	
	/**
	 * 新增一条快捷回复
	 */
	@RequestMapping(value= "/quickMessageSave",method = RequestMethod.GET)
	public String save(HttpServletRequest request){
		try {
			String shopid = request.getParameter("shopid");
			String custid = request.getParameter("custid");
			String message = request.getParameter("message");
			if (StringUtils.isBlank(shopid) || Integer.valueOf(shopid) < 0 || StringUtils.isBlank(custid) ||
					Integer.valueOf(custid) <= 0 || StringUtils.isBlank(message)){
				return ResUtils.execRes();
			}
			QuickMessagePo quickMessagePo = new QuickMessagePo();
			quickMessagePo.setShopid(Integer.valueOf(shopid));
			quickMessagePo.setCustid(Integer.valueOf(custid));
			quickMessagePo.setMessage(message);
			quickMessagePo.setStatus(1);
			quickMessagePo.setCreatetime(new Date());
			quickMessageService.saveMessage(quickMessagePo);
			return ResUtils.okRes();
		} catch (Exception e) {
			return ResUtils.execRes();
		}
	}
	
	/**
	 * 修改一条快捷回复
	 */
	@RequestMapping(value = "/quickMessageUpdate")
	public String update(HttpServletRequest request){
		try {
			String messageId = request.getParameter("messageid");
			if (StringUtils.isBlank(messageId) || Integer.valueOf(messageId) < 0){
				return ResUtils.execRes();
			}
			String shopId = request.getParameter("shopid");
			String custId = request.getParameter("custid");
			String message = request.getParameter("message");
			if (StringUtils.isBlank(shopId) || Integer.valueOf(shopId) < 0 || StringUtils.isBlank(custId) ||
					Integer.valueOf(custId) <= 0 || StringUtils.isBlank(message)){
				return ResUtils.execRes();
			}
			QuickMessagePo quickMessagePo = quickMessageService.selectOne(Long.valueOf(messageId),Integer.valueOf(shopId));
			if (quickMessagePo == null){
				return ResUtils.execRes();
			}
			quickMessagePo.setShopid(Integer.valueOf(shopId));
			quickMessagePo.setCustid(Integer.valueOf(custId));
			quickMessagePo.setMessage(message);
			quickMessagePo.setUpdatetime(new Date());
			quickMessageService.updateMessage(quickMessagePo);
			return ResUtils.okRes();
		} catch (Exception e) {
			return ResUtils.execRes();
		}
	}
	
	/**
	 * 删除一条快捷回复
	 */
	@RequestMapping(value="/quickMessageDelete")
	public String delete(String messageid, String shopid){
		try {
			if (StringUtils.isBlank(messageid) || StringUtils.isBlank(shopid)){
				return ResUtils.execRes();
			} else {
				Long messageId = Long.valueOf(messageid);
				int shopId = Integer.valueOf(shopid);
				int ret = quickMessageService.deleteMessage(messageId, shopId);
				if (ret == 0){
					return ResUtils.execRes();
				}
				return ResUtils.okRes();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResUtils.execRes();
		}
	}
	
	/**
	 * 保存一条会话记录
	 */
	@RequestMapping(value= "/dialogMessageSave",method = RequestMethod.POST)
	public String saveMessage(HttpServletRequest request){
		try {
			String shopid = request.getParameter("shopid");
			String userid = request.getParameter("userid");
			String custid = request.getParameter("custid");
			String roomid = request.getParameter("roomid");
			if (StringUtils.isBlank(userid) || StringUtils.isBlank(shopid)  || Integer.valueOf(shopid) < 0){
				return ResUtils.execRes();
			}
			Message message = new Message();
//			
//			if (StringUtils.isNotBlank(roomid)){
//				message.setRoomid(roomid);
//			}
			message.setShopid(Integer.valueOf(shopid));
			message.setUserid(userid);
			if (StringUtils.isNotBlank(custid) && Integer.valueOf(custid) > 0){
				message.setCustid(Integer.valueOf(custid));
			}
			message.setUsername(request.getParameter("username"));
			message.setCustname(request.getParameter("custname"));
			message.setMessage(request.getParameter("message"));
			message.setMsgtype(request.getParameter("msgtype"));
//			message.setJoinway(request.getParameter("joinway"));
			message.setSendway(request.getParameter("sendway"));
			messageService.saveMessage(message);
			return ResUtils.okRes();
		} catch (Exception e) {
			e.printStackTrace();
			return ResUtils.execRes();
		}
	}
	
	/**
	 * 返回所有会话记录
	 */
	@RequestMapping(value = "/dialogMessageList")
	public String dialogMessageList(String userid, String shopid, String messageid, PageTemp page){
		try {
			if (StringUtils.isBlank(userid) || StringUtils.isBlank(shopid) || Integer.valueOf(shopid) < 0){
				return ResUtils.execRes();
			}
			int shopId = Integer.valueOf(shopid);

			PageInfo<Message> mesList = messageService.findAllRecords(shopId, userid, messageid,page);

			return ResUtils.okRes(mesList); 
		} catch (Exception e) {
			 e.printStackTrace();
			 return ResUtils.execRes();
		}
	}
	/**
	 * 搜索该客服目前当前服务的所有的用户的count的数量
	 * @param shopId
	 * @param custId
	 * @return
	 */
	@RequestMapping(value = "/unReadMScount")
	public String getUnReadMessageCount(String shopId,String custId) {
		if (StringUtils.isNotBlank(shopId) && StringUtils.isNotBlank(custId)) {
			return ResUtils.execRes();
		}
		
		List<String> userList = dialogCache.getDialogAllList(shopId, custId);
		long msCount = 0;
		for (String userId : userList) {
			msCount += messageCountCache.getMessageCount(shopId, userId, custId);
		}
		return ResUtils.okRes(msCount);
		
	}

	

}
