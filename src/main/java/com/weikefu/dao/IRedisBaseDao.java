package com.weikefu.dao;

import java.util.List;

import com.weikefu.po.Message;

public interface IRedisBaseDao<T> {
	public void set(String key, String value, int database);  
    
    public String get(String key, int database);  
      
    public boolean expire(String key,long expire, int database);  
      
    public <T> boolean setList(String key ,List<T> list, int database);  
      
    public <T> List<T> getList(String key,Class<T> clz, int database);  
    
    public long lpush(String key, String value, int database) ;  
    
    public long rpush(String key,Object obj, int database);  
      
    public Object lpop(String key, int database);  
    
    public void put(String name, String key, String value, int database);  
    
    public Object getMap(String name, String key, int database);
    
    public void remMap(String name, String[] keys, int database);
    
    public List range(String key, long start, long end, int database);
    
    public List rangeAll(String key, int database);
    
    public int lsize(String key, int database); 
    
    public List<T> lpopAll(String key, int database);
    
    public long lrem(String key, long count, String value, int database);

	public void putList(String shopId, String roomName, List<String> member,
			int database);
	
	
	public long incr(String key,long value);
	
	public long getIncrCount(String key);
	
	public void clearCount(String key);
}
