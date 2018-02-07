package com.weikefu.cache;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.weikefu.constant.ContextConstant;
import com.weikefu.dao.IRedisBaseDao;
import com.weikefu.service.DialogService;
import com.weikefu.vo.PageTemp;

/*
 * 排队队列封装，结构采用redis_queue<shopid,userids>
 * caoxt
 * 2017-11-28
 */
@Service
public class RedisQueueCache {

	@Autowired
	@Qualifier("redis")
	private IRedisBaseDao<String> redis;
	
	@Autowired
	private DialogService diaService;
	
	/**
	 * 放入排队用户，添加到队列最后，先进先出
	 */
	public long rPushQuequ(String shopId, String value){  
		synchronized(RedisQueueCache.class){
			List<String> list = redis.rangeAll(ContextConstant.REDIS_QUEUE+shopId, ContextConstant.REDES_DATABASE0);
			if(null!=list&&list.size()>0&&list.contains(value)){
				return 0;
			}else{
				long pushNum = redis.rpush(ContextConstant.REDIS_QUEUE+shopId, value, ContextConstant.REDES_DATABASE0);
				diaService.waitDialogList(shopId);
				return pushNum;
			}
		}
    } 
	
	/*
	 * 取出第一个排队消息
	 */
	public List getQuequFirst(String shopId){  
		Object obj = redis.lpop(ContextConstant.REDIS_QUEUE+shopId, ContextConstant.REDES_DATABASE0);
		if(obj instanceof List){
			return (List) obj;
		}
		diaService.waitDialogList(shopId);
		return null;
    }
	
	/*
	 * 取出队列的指定数量用户
	 */
	public List<String> getQuequListBySize(String shopId, int size){  
		size = size - 1;
		List<String> queueList = redis.range(ContextConstant.REDIS_QUEUE+shopId, 0, size, ContextConstant.REDES_DATABASE0);
		if(null!=queueList&&queueList.size()>0){
			for(String userId : queueList){
				redis.lrem(ContextConstant.REDIS_QUEUE+shopId, ContextConstant.REDESREM0, userId, ContextConstant.REDES_DATABASE0);
			}
		}
		return queueList;
	}
	
	/*
	 * 取出队列等待的个数
	 */
	public int getQuequSize(String shopId){
		int quequSize = redis.lsize(ContextConstant.REDIS_QUEUE+shopId, ContextConstant.REDES_DATABASE0);
		return quequSize;
	}
	
	/*
	 * 根据分页信息获取对应的队列里的用户
	 */
	public List<String> getQuequList(String shopId){
		return redis.rangeAll(ContextConstant.REDIS_QUEUE+shopId, ContextConstant.REDES_DATABASE0);
	}
	
	/*
	 * 取出队列的指定数量用户
	 */
	public void removeQuequList(String shopId, List<String> remUserIdList){  
		if(null!=remUserIdList&&remUserIdList.size()>0){
			for(String userId : remUserIdList){
				redis.lrem(ContextConstant.REDIS_QUEUE+shopId, ContextConstant.REDESREM0, userId, ContextConstant.REDES_DATABASE0);
			}
		}
	}
}
