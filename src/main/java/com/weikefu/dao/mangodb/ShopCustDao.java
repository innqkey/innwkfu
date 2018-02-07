package com.weikefu.dao.mangodb;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.weikefu.po.ShopCustPo;
@Repository
public interface ShopCustDao extends MongoRepository<ShopCustPo, String> {

	List<ShopCustPo> findByShopidAndCustid(Integer shopid, Integer custid);

	List<ShopCustPo> findByShopid(Integer shopId);

	List<ShopCustPo> findByShopidAndCustserverstatus(Integer valueOf, String custserverstatus);

}
