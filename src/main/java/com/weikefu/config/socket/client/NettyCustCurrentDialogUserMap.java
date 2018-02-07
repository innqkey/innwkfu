package com.weikefu.config.socket.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * netty在线客服当前对话用户的存储对象key=shop+cust,value=user
 * @author caoxt
 *
 */
public class NettyCustCurrentDialogUserMap {

	private static ConcurrentMap<String, String> custCurUserMap = new ConcurrentHashMap<String, String>();
	
	public static void putCurrentUserId(String shopId, String custId, String userId){
		custCurUserMap.put(shopId+"-"+custId, userId);
	}
	
	public static String getCurrentUserId(String shopId, String custId){
		return custCurUserMap.get(shopId+"-"+custId);
	}
}
