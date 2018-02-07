package com.weikefu.cache;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.weikefu.constant.ContextConstant;
import com.weikefu.dao.IRedisBaseDao;

/**
 * 用户商店会话消息的缓存队列
 * @author Administrator
 *
 */
@SuppressWarnings("all")
@Service
public class RedisUserShopTalkCache {

	@Autowired
	@Qualifier("redis")
	private IRedisBaseDao<String> redis;
	/**
	 * 存放为该商店为客户服务的客服
	 * @param userId 用户的id
	 * @param shopId 商店的id
	 * @param custId 使用对应的custid
	 * @return
	 */
	public long addShopTalk(String userId, String shopId, String custId){  
		synchronized(RedisDialogCache.class){
			//客服服务历史队列，先删除以前存储的重复数据，然后再插入，优先插入队列前面
			redis.lrem(ContextConstant.REDIS_USER_SHOP_TALK+userId+"-"+shopId, ContextConstant.REDESREM0, custId, ContextConstant.REDES_DATABASE0);
			return redis.rpush(ContextConstant.REDIS_USER_SHOP_TALK+userId+"-"+shopId, custId, ContextConstant.REDES_DATABASE0);
		}
    } 
	
	/**
	 * 获取客服历史记录指定数量用户
	 */
	public List<String> getShopTalkList(String userId, String shopId){  
		List<String> dialogList = redis.rangeAll(ContextConstant.REDIS_USER_SHOP_TALK + userId+"-"+shopId, ContextConstant.REDES_DATABASE0);
		return dialogList;
	}
	
	
	/**
	 * 客服退出聊天室
	 * 当count=0时，移除所有匹配的元素；
	 */
	public int removeTalkCust(String userId, String shopId, String value){  
		//默认传入count=0，移除所有匹配的元素
		long remLong = redis.lrem(ContextConstant.REDIS_USER_SHOP_TALK+userId+"-"+shopId, ContextConstant.REDESREM0, value, ContextConstant.REDES_DATABASE0);
		return new Long(remLong).intValue();
	}
}
