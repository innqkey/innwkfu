package com.weikefu.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.weikefu.constant.ContextConstant;
import com.weikefu.dao.IRedisBaseDao;

/**
 * 用于消息数量的缓存
 * @author Administrator
 *
 */
@Service
public class RedisMessageCountCache {
	@Autowired
	@Qualifier("redis")
	private IRedisBaseDao<String> redis;
	

	/**
	 * 添加增加 对应的消息的数量
	 * @param roomId (房间号是商品的id 奖赏Userid）
	 * @param custId
	 */
	
	public void increaseMessageCount(String shopId,String userId,String custId){
		synchronized (RedisMessageCountCache.class) {
			StringBuilder key = new StringBuilder().append(ContextConstant.REDIS_MESSAGE_COUNT).append(shopId).append("-").append(userId).append("-").append(custId);
			redis.incr(key.toString(), 1L);
		}
	}
	
	/**
	 * 获取对应的缓存的数量
	 * @param roomId(房间号是商品的id 奖赏Userid）
	 * @param custId
	 * @return
	 */
	public long getMessageCount(String shopId,String userId,String custId){
		StringBuilder key = new StringBuilder().append(ContextConstant.REDIS_MESSAGE_COUNT).append(shopId).append("-").append(userId).append("-").append(custId);
			return redis.getIncrCount(key.toString());
	}
	
	/**
	 * 请求对应的缓存的数量
	 * @param roomId (房间号是商品的id 奖赏Userid）
	 * @param custId
	 */
	public void clearMessageCount(String shopId,String userId,String custId){
		synchronized (RedisMessageCountCache.class) {
			
			StringBuilder key = new StringBuilder().append(ContextConstant.REDIS_MESSAGE_COUNT).append(shopId).append("-").append(userId).append("-").append(custId);
			redis.clearCount(key.toString());
		}
	}
}
