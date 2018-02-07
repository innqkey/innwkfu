package com.weikefu.cache;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.weikefu.constant.ContextConstant;
import com.weikefu.dao.IRedisBaseDao;
import com.weikefu.vo.PageTemp;

/**
 *  客服服务用户的缓存
 * @author Administrator
 *
 */
@Service
public class RedisCustUserHistoryCache {
	
	@Autowired
	@Qualifier("redis")
	private IRedisBaseDao<String> redis;
	
	/*
	 * 添加客服历史记录
	 */
	public long addCustHist(String shopId, String custId, String value){  
		synchronized(RedisDialogCache.class){
			//客服服务历史队列，先删除以前存储的重复数据，然后再插入，优先插入队列前面
			redis.lrem(ContextConstant.REDIS_CUSTUSER_HISTORY+shopId+"-"+custId, ContextConstant.REDESREM0, value, ContextConstant.REDES_DATABASE0);
			return redis.lpush(ContextConstant.REDIS_CUSTUSER_HISTORY+shopId+"-"+custId, value, ContextConstant.REDES_DATABASE0);
		}
    } 
	
	/*
	 * 添加客服历史记录
	 */
	public int addBatchCustHist(String shopId, String custId, List<String> userIdList){  
		if(null==userIdList||userIdList.size()==0){
			return 0;
		}
		synchronized(RedisDialogCache.class){
			for(String userId : userIdList){
				//客服服务历史队列，先删除以前存储的重复数据，然后再插入，优先插入队列前面
				redis.lrem(ContextConstant.REDIS_CUSTUSER_HISTORY+shopId+"-"+custId, ContextConstant.REDESREM0, userId, ContextConstant.REDES_DATABASE0);
				redis.lpush(ContextConstant.REDIS_CUSTUSER_HISTORY+shopId+"-"+custId, userId, ContextConstant.REDES_DATABASE0);
			}
		}
		return 1;
    } 
	
	/*
	 * 获取客服历史记录指定数量用户
	 */
	public List<String> getCustHistListBySize(String shopId, String custId, PageTemp page){  
		List<String> dialogList = redis.range(ContextConstant.REDIS_CUSTUSER_HISTORY+shopId+"-"+custId, (page.getPageNum()-1)*page.getPageSize(), page.getPageNum()*page.getPageSize(), ContextConstant.REDES_DATABASE0);
		return dialogList;
	}
	
}
