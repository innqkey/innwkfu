package com.weikefu.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.weikefu.dao.mangodb.MessageDao;
import com.weikefu.po.Message;
import com.weikefu.service.MessageService;
import com.weikefu.vo.PageInfo;
import com.weikefu.vo.PageTemp;

/** 
* @author qinkai 
* @date 2017年12月5日
*/

@Service
public class MessageServiceImpl implements MessageService{
	
	
	@Autowired
	private  MessageDao messageDao;
	@Autowired
	private MongoTemplate mongoTemplate;
	
	/***
	 *  修改排序的工作，
	 *  mongodb查找先从id第一位开始取数据的。
	 *  所以我们查找历史记录的话，先进行日期排序，然后根据id向下查找，limit数据即可。
	 */
	@Override
	public PageInfo<Message> findAllRecords(int shopid, String userid, String messageid, PageTemp pageTemp){
		Long mesId = StringUtils.isNotBlank(messageid)?Long.valueOf(messageid):null;
		Query query = new Query();
		Criteria criteria = Criteria.where("shopid").is(shopid)
								.and("userid").is(userid);
		//判断是否为空的操作
		if (StringUtils.isNotBlank(messageid)) {
			criteria.andOperator(Criteria.where("_id").lt(Long.valueOf(messageid)));
		}else {
			int offset = (pageTemp.getPageNum() - 1) * pageTemp.getPageSize();
			query.skip(offset);
		}
		
		Sort sort = new Sort(Direction.DESC, "_id");
	    query.with(sort);
		query.addCriteria(criteria);
		//进行对应的分页操作
		query.limit(pageTemp.getPageSize());
		List<Message>  all= mongoTemplate.find(query, Message.class);
		return new PageInfo<>(all);
	}

	@Override
	public void saveMessage(Message message) {
		messageDao.save(message);
	}


	
}
