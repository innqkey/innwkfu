package com.weikefu.cache;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.weikefu.constant.ContextConstant;
import com.weikefu.dao.IRedisBaseDao;

/*
 * 商户在线客服队列封装，结构采用key-List,key==redis_shop_cust_online+shopid,List==List<userids>
 * caoxt
 * 2017-11-29
 */
@Service
public class RedisShopCustOnlineCache {

	@Autowired
	@Qualifier("redis")
	private IRedisBaseDao<String> redis;
	
	/*
	 * 放入商户在线客服队列，添加到队列最后，先进先出，并保证list数据不重复添加
	 */
	public long addOnlineCust(String shopId, String value){  
		synchronized(shopId){
			List<String> list = redis.rangeAll(ContextConstant.REDIS_SHOP_CUSTONLINE+shopId, ContextConstant.REDES_DATABASE0);
			//如果队里已经存在数据，不添加，过滤重复数据
			if(null!=list&&list.size()>0&&list.contains(value)){
				return 0;
			}
			return redis.rpush(ContextConstant.REDIS_SHOP_CUSTONLINE+shopId, value, ContextConstant.REDES_DATABASE0);
		}
    } 
	
	
	/*
	 * 取出商户所有在线客服列表
	 */
	public List getOnlineCustAllList(String shopId){  
		List<String> list = redis.rangeAll(ContextConstant.REDIS_SHOP_CUSTONLINE+shopId, ContextConstant.REDES_DATABASE0);
		return list;
    }
	
	/*
	 * 统计商户在线客服数量
	 */
	public int countOnlineCustSize(String shopId){  
		long onlineSize = redis.lsize(ContextConstant.REDIS_SHOP_CUSTONLINE+shopId, ContextConstant.REDES_DATABASE0);
		return new Long(onlineSize).intValue();
	}
	
	/*
	 * 统计商户在线客服数量
	 * 当count=0时，移除所有匹配的元素；
	 */
	public int removeOnlineCust(String shopId, String value){  
		//默认传入count=0，移除所有匹配的元素
		long remLong = redis.lrem(ContextConstant.REDIS_SHOP_CUSTONLINE+shopId, ContextConstant.REDESREM0, value, ContextConstant.REDES_DATABASE0);
		return new Long(remLong).intValue();
	}
}
