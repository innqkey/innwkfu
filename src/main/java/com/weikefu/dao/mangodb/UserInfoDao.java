package com.weikefu.dao.mangodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.weikefu.po.UserInfo;
@Repository
public interface UserInfoDao extends MongoRepository<UserInfo, Long> {

	UserInfo findByUserId(String userid);


	UserInfo findByWeiuseridAndJoinway(String weiuserid, String joinway);

	UserInfo findByOpenidAndJoinway(String openid, String joinway);

}
