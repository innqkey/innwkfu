package com.weikefu.dao.mangodb;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.weikefu.po.ShopUserHistory;

/**
* 类说明：
* @author 
* @version 创建时间：2018年2月1日 下午7:59:55
* 
*/
@Repository
public interface ShopUserHistoryDao extends MongoRepository<ShopUserHistory, Long> {

	void deleteByShopidAndUserid(String shopId, String userId);

	Page<ShopUserHistory> findByShopid(String shopId, Pageable pageRequest);

}
