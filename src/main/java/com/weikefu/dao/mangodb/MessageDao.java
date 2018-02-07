package com.weikefu.dao.mangodb;



import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.weikefu.po.Message;
@Repository
public interface MessageDao extends MongoRepository<Message, Long> {

	
}
