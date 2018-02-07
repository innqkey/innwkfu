package com.weikefu.service;

import java.util.List;

import com.weikefu.vo.PageTemp;

/**
* 类说明：
* @author 
* @version 创建时间：2018年2月1日 下午8:01:31
* 
*/
public interface ShopUserHistorySerivce {
	public void addShopHist(String shopId, String value);
	
	public int addBatchShopHist(String shopId, List<String> userIdList);
	
	public List<String> getShopHistListBySize(String shopId, int size);
	
	public List<String> getShopHistListByPage(String shopId, PageTemp page);
}
