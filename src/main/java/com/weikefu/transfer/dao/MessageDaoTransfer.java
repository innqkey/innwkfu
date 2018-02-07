package com.weikefu.transfer.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.weikefu.transfer.po.Message;




/**
* 类说明：
* @author 
* @version 创建时间：2018年2月2日 上午10:34:50
* 
*/
public interface MessageDaoTransfer extends JpaRepository<Message, Long> {

}
