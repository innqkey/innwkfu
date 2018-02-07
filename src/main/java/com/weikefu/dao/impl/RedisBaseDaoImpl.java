package com.weikefu.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisCallback;

import com.weikefu.constant.ContextConstant;
import com.weikefu.dao.IRedisBaseDao;

@Component("redis")
@SuppressWarnings("all")
public class RedisBaseDaoImpl<T> implements IRedisBaseDao<T> {

	@Autowired
	@Qualifier("redisTemplate")
    protected RedisTemplate redisTemplate ;

	
	@Override
	public void set(String key, String value, int database) {
//		redisTemplate.getConnectionFactory().getConnection().select(database);
//		redisTemplate.opsForValue().set(key, value);
		
		redisTemplate.execute(new RedisCallback<Boolean>() {  
            @Override  
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {  
            	connection.select(database);
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();  
                connection.set(serializer.serialize(key), serializer.serialize(value));  
                return true;  
            }  
        });  
	}

	@Override
	public String get(String key, int database){  
		return (String) redisTemplate.opsForValue().get(key);
    }  

	@Override
	public boolean expire(String key, long expire, int database) {
		return false;
	}

	@Override
	public <T> boolean setList(String key, List<T> list, int database) {
		return false;
	}

	@Override
	public <T> List<T> getList(String key, Class<T> clz, int database) {
		return null;
	}

	@Override
	public long lpush(String key, String value, int database) {
		return redisTemplate.opsForList().leftPush(key, value);
	}

	@Override
	public long rpush(String key, Object value, int database) {
		return redisTemplate.opsForList().rightPush(key, value);
	}

	@Override
	public Object lpop(String key, int database) {
		return redisTemplate.opsForList().leftPop(key);
	}

	@Override
	public void put(String name, String key, String value, int database) {
		redisTemplate.opsForHash().put(name, key, value);
	}
	
	@Override
	public Object getMap(String name, String key, int database) {
		return redisTemplate.opsForHash().get(name, key);
	}

	@Override
	public List range(String key, long start, long end, int database) {
		return redisTemplate.opsForList().range(key, start, end);
	}
	
	
	
	

	@Override
	public int lsize(String key, int database) {
		long size = redisTemplate.opsForList().size(key);
		return new Long(size).intValue();
	}

	@Override
	public List<T> lpopAll(String key, int database) {
		long size = redisTemplate.opsForList().size(key);
		return redisTemplate.opsForList().range(key, 0, size);
	}

	@Override
	public long lrem(String key, long count, String value, int database) {
		return redisTemplate.opsForList().remove(key, count, value);
	}

	@Override
	public List rangeAll(String key, int database) {
		long size = redisTemplate.opsForList().size(key);
		return range(key,0,size,ContextConstant.REDES_DATABASE0);
	}
	
	/**
	 * 存放list的集合
	 */
	@Override
	public void putList(String shopId, String roomName, List<String> member,
			int database) {
				redisTemplate.execute(new RedisCallback<Boolean>() {  
		            @Override  
		            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {  
		            	connection.select(database);
		                RedisSerializer serializer = new Jackson2JsonRedisSerializer<>(Object.class);
		                connection.hSet(serializer.serialize(shopId), serializer.serialize(roomName),serializer.serialize(member));  
		                return true;  
		            } 
		        });
		
	}

	/**
	 * 用来增加数量的，使用redis的字符类型
	 */
	@Override
	public long incr(String key,long value) {
		return redisTemplate.opsForValue().increment(key, value);
	}
	
	/**
	 * 根据key，获取对应的数据的数量
	 * @param key
	 */
	@Override
	public long getIncrCount(String key) {
		Object object = redisTemplate.opsForValue().get(key);
		if (object == null) {
			return 0;
		}
		return Long.valueOf(String.valueOf(object));
	}
	
	/***
	 * 这里的删除是通过key，将对应的数据的值变为0
	 * @param key
	 */
	@Override
	public void clearCount(String key) {
		redisTemplate.opsForValue().set(key, String.valueOf(0));
	}

	@Override
	public void remMap(String name, String[] keys, int database) {
		// TODO Auto-generated method stub
		redisTemplate.opsForHash().delete(name, keys);
	}

}
