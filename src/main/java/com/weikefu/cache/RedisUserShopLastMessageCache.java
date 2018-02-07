package com.weikefu.cache;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.weikefu.constant.ContextConstant;
import com.weikefu.dao.IRedisBaseDao;
import com.weikefu.po.Message;
import com.weikefu.util.JacksonUtils;

/**
 * 用于存储用户消息的最后一条
 * @author Administrator
 */
@Service
public class RedisUserShopLastMessageCache {
	
	@Autowired
	@Qualifier("redis")
	private IRedisBaseDao<String> redis;
	
	/**
	 * 添加最后一条历史记录到redis
	 * @param userid
	 * @param shopid
	 * @param message
	 */
	public void addLastMessage(String userid, String shopid, Message message){
		synchronized(RedisDialogCache.class){
			redis.put(ContextConstant.REDIS_LAST_MES, userid+"-"+shopid,JSON.toJSONString(message), ContextConstant.REDES_DATABASE0);
		}
	}
	
	/**
	 *从redis中获取最后一条历史纪录
	 * @param userid
	 * @param shopid
	 * @param message
	 * @return
	 */
	public Message getLastMessage(String userid, String shopid){
		Object LastMessage = redis.getMap(ContextConstant.REDIS_LAST_MES, userid+"-"+shopid, ContextConstant.REDES_DATABASE0);
		if(LastMessage==null){
			return null;
		}
		try {
			return  JacksonUtils.toObject(LastMessage.toString(),Message.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
