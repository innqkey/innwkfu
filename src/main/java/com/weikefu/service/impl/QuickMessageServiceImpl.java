package com.weikefu.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.WriteResult;
import com.weikefu.dao.mangodb.QuickMessageDao;
import com.weikefu.po.QuickMessagePo;
import com.weikefu.service.QuickMessageService;

/** 
* @author qinkai 
* @date 2017年12月6日
*/

@Service
public class QuickMessageServiceImpl implements QuickMessageService {

	
	@Autowired
	private QuickMessageDao quickMessageDao;
	
	@Autowired
	private MongoTemplate  mongoTemplate;
	
	/**
	 * 
	 */
	@Override
	public void saveMessage(QuickMessagePo quickMessagePo) {
		quickMessageDao.save(quickMessagePo);
	}
	
	/**
	 * 由于save 方法中无法执行更新方法，
	 * 所以我们使用原始的方法执行
	 */
	
	public void updateMessage(QuickMessagePo quickMessagePo){
		//添加查询的条件
		Criteria criteria = Criteria.where("shopid")
								.is(quickMessagePo.getShopid())
								.and("messageid")
								.is(quickMessagePo.getMessageid());
		Query query = new Query(criteria);
		//添加更新的内容
		Update update = new Update();
		update.set("message", quickMessagePo.getMessage());
		update.set("updatetime", new Date());
		//更新第一条查找到的数据
		mongoTemplate.findAndModify(query, update, QuickMessagePo.class);
	}

	/**
	 * 删除，修改成为mongodb
	 */
	@Override
	public int deleteMessage(Long messageid, Integer shopid) {
		QuickMessagePo quickMessagePo = new QuickMessagePo();
		quickMessagePo.setMessageid(messageid);
		quickMessagePo.setShopid(shopid);
		Example<QuickMessagePo> example = Example.of(quickMessagePo);
		QuickMessagePo selectPo = quickMessageDao.findOne(example);
		if (selectPo != null){
			//由于只有上面的save只有插入和保存的操作，  所以这里使用 原理的api进行操作
			Criteria criteria = Criteria.where("shopid")
					.is(quickMessagePo.getShopid())
					.and("messageid")
					.is(quickMessagePo.getMessageid());
			Query query = new Query(criteria);
			Update update = new Update();
			update.set("status", 2);
			update.set("updatetime", new Date());
			 QuickMessagePo findAndModify = mongoTemplate.findAndModify(query, update, QuickMessagePo.class);
			if (findAndModify != null){
				return 1;
			}
			return 0;
		} else {
			return 0;
		}
	}

	@Override
	public List<QuickMessagePo> selectAll(int shopid) {
		Sort sort = new Sort(Direction.DESC, "createtime");
		return quickMessageDao.findByShopidAndStatus(shopid,1,sort);
	}

	@Override
	public QuickMessagePo selectOne(Long messageid, Integer shopid) {
		QuickMessagePo quickMessagePo = new QuickMessagePo();
		quickMessagePo.setMessageid(messageid);
		quickMessagePo.setShopid(shopid);
		Example<QuickMessagePo> example = Example.of(quickMessagePo);
		QuickMessagePo selectPo = quickMessageDao.findOne(example);
		return selectPo;
	}

	
}
