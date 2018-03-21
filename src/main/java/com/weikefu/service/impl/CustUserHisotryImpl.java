package com.weikefu.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.weikefu.cache.RedisDialogCache;
import com.weikefu.cache.RedisUserShopLastMessageCache;
import com.weikefu.dao.mangodb.CustUserHistoryDao;
import com.weikefu.dao.mangodb.UserInfoDao;
import com.weikefu.po.CustUserHistory;
import com.weikefu.po.Message;
import com.weikefu.po.ShopUserHistory;
import com.weikefu.po.UserInfo;
import com.weikefu.service.CustUserHistoryService;
import com.weikefu.util.ConvertUtils;
import com.weikefu.vo.PageTemp;
import com.weikefu.vo.UserInfoVo;


@Service
public class CustUserHisotryImpl implements CustUserHistoryService {
	@Autowired
	private CustUserHistoryDao custUserHistoryDao;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private UserInfoDao userInfoDao;
	@Autowired
	private RedisUserShopLastMessageCache lastMessage;
	private Pattern pattern =  Pattern.compile("<\\s*a.*?/a\\s*>");
	
	@Autowired
	private RedisDialogCache redisDialogCache;
	
	
	/**
	 * 添加客服的历史，先删除，后天添加
	 */
	@Override
	public void addCustHist(String userId, String shopId, String custId) {
		Criteria criteria = Criteria.where("shopid").is(shopId).and("custid").is(custId).and("userid").is(userId);
		Query query = new Query(criteria);
		CustUserHistory history = mongoTemplate.findAndRemove(query, CustUserHistory.class);
		if (history != null && history.getId() != null) {
			history.setId(null);
			custUserHistoryDao.insert(history);
		}else {
			//如果是没有发现所以重新新插入
			CustUserHistory custUserHistory = new CustUserHistory();
			custUserHistory.setCustid(custId);
			custUserHistory.setShopid(shopId);
			custUserHistory.setUserid(userId);
			custUserHistoryDao.insert(custUserHistory);
		}
		
		
	}
	/**
	 *批量添加客服历史
	 */
	@Override
	public int addBatchCustHist(String shopId, String custId, List<String> userIdList) {
		if(null == userIdList || userIdList.size() == 0){
			return 0;
		}
		//先删除，在添加
		synchronized(RedisDialogCache.class){
			for(String userId : userIdList){
				Criteria criteria = Criteria.where("shopid").is(shopId).and("custid").is(custId).and("userid").is(userId);
				Query query = new Query(criteria);
				CustUserHistory history = mongoTemplate.findAndRemove(query, CustUserHistory.class);
				if (history != null && history.getId() != null) {
					//id设置为空，重新插入
					history.setId(null);
					custUserHistoryDao.insert(history);
				}else {
					//如果是没有发现所以重新新插入
					CustUserHistory custUserHistory = new CustUserHistory();
					custUserHistory.setCustid(custId);
					custUserHistory.setShopid(shopId);
					custUserHistory.setUserid(userId);
					custUserHistoryDao.insert(custUserHistory);
				}
			}
		}
		return 1;
	
	}
	
	/**
	 * 获取历史记录，
	 * 删除对应的serviceCustid
	 */
	@Override
	public List<String> getCustHistListBySize(String shopId, String custId, PageTemp page){
		PageRequest pageRequest = new PageRequest(page.getPageNum() - 1, page.getPageSize());
		Page<ShopUserHistory> resultpage = custUserHistoryDao.findByShopidAndCustidOrderByIdDesc(shopId, custId,pageRequest);
		List<ShopUserHistory> list = resultpage.getContent();
		if (list != null && list.size() > 0) {
			List<String> result = new ArrayList<String>();
			for (ShopUserHistory shopUserHistory : list) {
				if (StringUtils.isNotBlank(shopUserHistory.getUserid())) {
					result.add(shopUserHistory.getUserid());
				}
			}
			return result;
		}
		return null;
	}
	
	/**
	 * 搜索历史
	 */
	@Override
	public List<UserInfoVo> searchUser(String shopId, String custId, String keyword) {
		
		List<UserInfoVo> userHistoryList = new ArrayList<UserInfoVo>();
		List<UserInfo> userInfos = userInfoDao.findByNicknameLike(keyword);
		if (userInfos != null && userInfos.size() > 0) {
			//获取所有的在线的用户的id
			List<String> dialogAllList = redisDialogCache.getDialogAllList(shopId, custId);
			if (dialogAllList != null && dialogAllList.size() > 0) {
				//因为需要使用到contains的方法，并且hashset效率是list的168倍
				HashSet<String> dialogAllSet = new HashSet<>(dialogAllList);
				for (UserInfo userInfo : userInfos) {
					if (dialogAllSet.contains(userInfo.getUserId())) {
						UserInfoVo userInfoVo = new UserInfoVo();
						ConvertUtils.convertDtoAndVo(userInfo, userInfoVo);
						Message userLastMessage = lastMessage.getLastMessage(userInfo.getUserId(), shopId);
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
			}
			//排序
			if (userHistoryList.size() > 0) {
				insertSort(userHistoryList);
			}
			
		}
		
		return userHistoryList;
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
	

}
