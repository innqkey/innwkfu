package com.weikefu.dao.mangodb;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.weikefu.po.ImagePo;
@Repository
public interface ImageDao extends MongoRepository<ImagePo, Long> {
	//@Query(value = "{'name':{'shopid':{'$regex':?1,'$option':'i'},'custid':{'$regex':?2,'$option':'i'}}")
	public Page<ImagePo> findByShopidAndCustid(String shopId, String custId, Pageable pageable);

	public ImagePo findByUrlAndShopid(String imageUrl, String shopId);

}
