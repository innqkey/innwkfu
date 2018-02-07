package com.weikefu.service.impl;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;

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
import com.weikefu.dao.mangodb.ShopUserHistoryDao;
import com.weikefu.po.ImagePo;
import com.weikefu.po.ShopUserHistory;
import com.weikefu.service.ShopUserHistorySerivce;
import com.weikefu.vo.PageTemp;


/**
* 类说明：
* @author 
* @version 
* 创建时间：2018年2月1日 下午8:02:22
* 
*/
@Service
public class ShopUserHistoryImpl implements ShopUserHistorySerivce{
	@Autowired
	private ShopUserHistoryDao shopUserHistoryDao;
	@Autowired
	private MongoTemplate mongoTemplate;
	
	
	/***
	 * 添加历史记录到shop中去
	 */
	public void addShopHist(String shopId, String value){
		ShopUserHistory shopUserHistory = new ShopUserHistory();
		shopUserHistoryDao.deleteByShopidAndUserid(shopId,value);
		shopUserHistory.setShopid(shopId);
		shopUserHistory.setUserid(value);
		shopUserHistoryDao.insert(shopUserHistory);
	}

	@Override
	public int addBatchShopHist(String shopId, List<String> userIdList) {
		if(null == userIdList || userIdList.size() == 0){
			return 0;
		}
		synchronized(RedisDialogCache.class){
			for(String userId : userIdList){
				//客服服务历史队列，先删除以前存储的重复数据，然后再插入，优先插入队列前面
				shopUserHistoryDao.deleteByShopidAndUserid(shopId,userId);
				ShopUserHistory shopUserHistory = new ShopUserHistory();
				shopUserHistory.setShopid(shopId);
				shopUserHistory.setUserid(userId);
				shopUserHistoryDao.insert(shopUserHistory);
			}
			return 1;
		}
	}

	
	@Override
	public List<String> getShopHistListBySize(String shopId, int size) {
		Criteria criteria = Criteria.where("shopid").is(shopId);
		Query query = new Query(criteria);
		query.limit(size);
		List<ShopUserHistory> list = mongoTemplate.find(query,ShopUserHistory.class);
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
	 * 分页获取用户的信息
	 */
	@Override
	public List<String> getShopHistListByPage(String shopId, PageTemp pageTemp) {
		PageRequest pageRequest = new PageRequest(pageTemp.getPageNum() - 1, pageTemp.getPageSize());
		Page<ShopUserHistory> resultpage = shopUserHistoryDao.findByShopid(shopId,pageRequest);
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
