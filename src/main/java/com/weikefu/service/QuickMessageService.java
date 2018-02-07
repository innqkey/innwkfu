package com.weikefu.service;

import java.util.List;

import com.weikefu.po.QuickMessagePo;

/** 
* @author qinkai 
* @date 2017年12月6日
*/

public interface QuickMessageService {

	// 新增一条快捷回复
	void saveMessage(QuickMessagePo quickMessagePo);

	void updateMessage(QuickMessagePo quickMessagePo);

	// 删除一条快捷回复
	int deleteMessage(Long messageid, Integer shopid);

	// 返回所有快捷回复
	List<QuickMessagePo> selectAll(int shopid);
	
	// 查找一个快捷回复
	QuickMessagePo selectOne(Long messageid, Integer shopid);

}
