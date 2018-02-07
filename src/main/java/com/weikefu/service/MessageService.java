package com.weikefu.service;


import com.weikefu.po.Message;
import com.weikefu.vo.PageInfo;
import com.weikefu.vo.PageTemp;

/** 
* @author qinkai 
* @date 2017年12月5日
*/

public interface MessageService {
	//查询消息记录
	public PageInfo<Message> findAllRecords(int shopid, String userid,  String messageid, PageTemp pageTemp);

	//保存一条消息
	public void saveMessage(Message message);
}
