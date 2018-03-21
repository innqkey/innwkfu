package com.weikefu.service;

import java.util.List;

import com.weikefu.vo.PageTemp;
import com.weikefu.vo.UserInfoVo;


public interface CustUserHistoryService {
	
	public void addCustHist(String shopId, String custId, String value);
	
	public int addBatchCustHist(String shopId, String custId, List<String> userIdList);
	
	
	public List<String> getCustHistListBySize(String shopId, String custId, PageTemp page);


	public List<UserInfoVo> searchUser(String shopId, String custId, String trim);
}
