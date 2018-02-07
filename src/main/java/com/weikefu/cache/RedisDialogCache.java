package com.weikefu.cache;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.weikefu.constant.ContextConstant;
import com.weikefu.dao.IRedisBaseDao;

/*
 * 会话队列封装，结构采用key-List结构，redis_dialog+shopid-custid,List<userids>
 * caoxt
 * 2017-11-29
 */
@Service
public class RedisDialogCache {

	@Autowired
	@Qualifier("redis")
	private IRedisBaseDao<String> redis;
	
	public long addDialog(String shopId, String custId, String value){  
		synchronized(RedisDialogCache.class){

			List<String> list = redis.rangeAll(ContextConstant.REDIS_DIALOG+shopId+"-"+custId, ContextConstant.REDES_DATABASE0);
			if(null!=list&&list.size()>0&&list.contains(value)){
				return 0;
			}
			return redis.rpush(ContextConstant.REDIS_DIALOG+shopId+"-"+custId, value, ContextConstant.REDES_DATABASE0);
		}
    } 
	
	public int addBatchDialog(String shopId, String custId, List<String> userIdList){  
		if(null==userIdList||userIdList.size()==0){
			return 0;
		}
		synchronized(RedisDialogCache.class){
			for(String userId : userIdList){
				List<String> list = redis.rangeAll(ContextConstant.REDIS_DIALOG+shopId+"-"+custId, ContextConstant.REDES_DATABASE0);
				if(null!=list&&list.size()>0&&list.contains(userId)){
					continue;
				}
				redis.rpush(ContextConstant.REDIS_DIALOG+shopId+"-"+custId, userId, ContextConstant.REDES_DATABASE0);
			}
			return 1;
		}
    } 
	
	public int countDialogSize(String shopId, String custId){  
		int size = redis.lsize(ContextConstant.REDIS_DIALOG+shopId+"-"+custId, ContextConstant.REDES_DATABASE0);
		return size;
	}
	
	/*
	 * 移除会话队列，当关闭用户对话是调用
	 * 当count=0时，移除所有匹配的元素；
	 */
	public int removeDialog(String shopId, String custId, String value){  
		//默认传入count=0，移除所有匹配的元素
		long remLong = redis.lrem(ContextConstant.REDIS_DIALOG+shopId+"-"+custId, ContextConstant.REDESREM0, value, ContextConstant.REDES_DATABASE0);
		return new Long(remLong).intValue();
	}
	
	/*
	 * 获取会话队列的指定数量用户
	 */
	public List<String> getDialogListBySize(String shopId, String custId, int size){  
		List<String> dialogList = redis.range(ContextConstant.REDIS_DIALOG+shopId+"-"+custId, 0, size, ContextConstant.REDES_DATABASE0);
		return dialogList;
	}
	
	/*
	 * 获取会话队列的指定数量用户
	 */
	public List<String> getDialogAllList(String shopId,String custId){  
		List<String> dialogList = redis.rangeAll(ContextConstant.REDIS_DIALOG+shopId + "-" + custId, ContextConstant.REDES_DATABASE0);

		return dialogList;
	}
	
	/*
	 * 移除所有的会话队列
	 */
	public Object lpopDialogAll(String shopId,String custId){
		Object object = redis.lpop(ContextConstant.REDIS_DIALOG+shopId + "-" + custId, ContextConstant.REDES_DATABASE0);
		return object;
	}
}
