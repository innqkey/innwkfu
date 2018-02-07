package com.weikefu.dao.mangodb;


import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.weikefu.po.QuickMessagePo;
@Repository
public interface QuickMessageDao extends MongoRepository<QuickMessagePo, Long> {
	/**
	 * status如果是1表示没有删除的
	 * @param shopid
	 * @param status
	 * @param sort
	 * @return
	 */
	List<QuickMessagePo> findByShopidAndStatus(Integer shopid, Integer status, Sort sort);

	QuickMessagePo findByMessageidAndShopid(Long messageid, Integer shopid);



}
