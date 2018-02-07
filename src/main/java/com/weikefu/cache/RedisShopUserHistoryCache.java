package com.weikefu.cache;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.weikefu.constant.ContextConstant;
import com.weikefu.dao.IRedisBaseDao;
import com.weikefu.vo.PageTemp;

/**
 * 商店为用户分配客服提供服务的历史
 * @author Administrator
 *
 */
@Service
public class RedisShopUserHistoryCache {
	
	@Autowired
	@Qualifier("redis")
	private IRedisBaseDao<String> redis;
	
	
	/**
	 * 添加商户历史记录
	 * @param shopId
	 * @param value
	 */
	public void addShopHist(String shopId, String value){  
		synchronized(RedisDialogCache.class){
			//客服服务历史队列，先删除以前存储的重复数据，然后再插入，优先插入队列前面
			redis.lrem(ContextConstant.REDIS_SHOP_USERHISTORY+shopId, ContextConstant.REDESREM0, value, ContextConstant.REDES_DATABASE0);
			redis.lpush(ContextConstant.REDIS_SHOP_USERHISTORY+shopId, value, ContextConstant.REDES_DATABASE0);
		}
    } 
	
	
	/*添加商户历史记录*/
	public int addBatchShopHist(String shopId, List<String> userIdList){  
		if(null==userIdList||userIdList.size()==0){
			return 0;
		}
		synchronized(RedisDialogCache.class){
			for(String userId : userIdList){
				//客服服务历史队列，先删除以前存储的重复数据，然后再插入，优先插入队列前面
				redis.lrem(ContextConstant.REDIS_SHOP_USERHISTORY+shopId, ContextConstant.REDESREM0, userId, ContextConstant.REDES_DATABASE0);
				redis.lpush(ContextConstant.REDIS_SHOP_USERHISTORY+shopId, userId, ContextConstant.REDES_DATABASE0);
			}
			return 1;
		}
    } 
	
	
	/* * 获取商户历史记录指定数量用户*/
	public List<String> getShopHistListBySize(String shopId, int size){  
		List<String> dialogList = redis.range(ContextConstant.REDIS_SHOP_USERHISTORY+shopId, 0, size, ContextConstant.REDES_DATABASE0);
		return dialogList;
	}
	
	
	/* * 根据分页获取商户历史记录*/
	 
	public List<String> getShopHistListByPage(String shopId, PageTemp page){  
		List<String> dialogList = redis.range(ContextConstant.REDIS_SHOP_USERHISTORY+shopId, (page.getPageNum()-1)*page.getPageSize(), page.getPageNum()*page.getPageSize(), ContextConstant.REDES_DATABASE0);
		return dialogList;
	}
}
