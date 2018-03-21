package com.weikefu.dao.mangodb;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.weikefu.po.CustUserHistory;
import com.weikefu.po.ShopUserHistory;


@Repository
public interface CustUserHistoryDao extends MongoRepository<CustUserHistory, Long>{


	Page<ShopUserHistory> findByShopidAndCustidOrderByIdDesc(String shopId, String custId, Pageable pageRequest);

	List<ShopUserHistory> findByShopidAndCustidAndUseridOrderByIdDesc(String shopId, String custId, String userId);

}
