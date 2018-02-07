package com.weikefu.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.weikefu.constant.ContextConstant;
import com.weikefu.dao.IRedisBaseDao;
import com.weikefu.po.Message;
import com.weikefu.po.UserInfo;
import com.weikefu.util.JacksonUtils;

/**
 * 用于保存用户接入店铺的最后服务客服坐席
 * @author Administrator
 *
 */
@Service
public class RedisUserShopLastCustCache {
	
	@Autowired
	@Qualifier("redis")
	private IRedisBaseDao<String> redis;
	
	/**
	 * 添加用户信息到redis
	 * @param userid
	 * @param userInfo
	 */
	public void addUserShopLastCust(String userId, String shopId, String custId){
		synchronized(RedisDialogCache.class){
			redis.put(ContextConstant.REDIS_USER_SHOP_LAST_CUST, userId+"_"+shopId, custId, ContextConstant.REDES_DATABASE0);
		}
	}
	
	/**
	 * 从redis获取用户信息
	 * @param userid
	 * @return
	 */
	public String getLastCustId(String userId, String shopId){
		Object custObj = redis.getMap(ContextConstant.REDIS_USER_SHOP_LAST_CUST, userId+"_"+shopId, ContextConstant.REDES_DATABASE0);
		if(null!=custObj){
			return String.valueOf(custObj);
		}
		return null;
	}
}
