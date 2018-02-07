package com.weikefu.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.weikefu.cache.RedisDialogCache;
import com.weikefu.constant.ContextConstant;
import com.weikefu.dao.mangodb.CustUserHistoryDao;
import com.weikefu.po.CustUserHistory;
import com.weikefu.po.ShopUserHistory;
import com.weikefu.service.CustUserHistoryService;
import com.weikefu.vo.PageTemp;


@Service
public class CustUserHisotryImpl implements CustUserHistoryService {
	@Autowired
	private CustUserHistoryDao custUserHistoryDao;
	@Autowired
	private MongoTemplate mongoTemplate;
	
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
	
	
	

}
