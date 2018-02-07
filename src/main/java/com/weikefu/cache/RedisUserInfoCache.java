package com.weikefu.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.weikefu.constant.ContextConstant;
import com.weikefu.dao.IRedisBaseDao;
import com.weikefu.transfer.po.UserInfo;
import com.weikefu.util.JacksonUtils;

/**
 * 用于保存每一次用户的信息的
 * @author Administrator
 *
 */
@Service
public class RedisUserInfoCache {
	
	@Autowired
	@Qualifier("redis")
	private IRedisBaseDao<String> redis;
	
	/**
	 * 添加用户信息到redis
	 * @param userid
	 * @param userInfo
	 */
	public void addUserInfo(String userid,UserInfo userInfo){
		synchronized(RedisDialogCache.class){
			redis.put(ContextConstant.REDIS_USER_INFO, userid, JSON.toJSONString(userInfo), ContextConstant.REDES_DATABASE0);
		}
	}
	
	/**
	 * 从redis获取用户信息
	 * @param userid
	 * @return
	 */
	public UserInfo getUserInfo(String userid){
		Object userInfo = redis.getMap(ContextConstant.REDIS_USER_INFO, userid, ContextConstant.REDES_DATABASE0);
		if(userInfo==null){
			return null;
		}
		try {
			return JacksonUtils.toObject(userInfo.toString(), UserInfo.class);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
}
