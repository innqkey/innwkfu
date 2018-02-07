package com.weikefu.config;

import java.lang.reflect.Field;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.weikefu.annotation.AutoIncKey;
import com.weikefu.po.SeqInfo;

/**
 * 保存文档监听类，
 * 在保存对应的时候，通过反射方法生成ID
 * @author Administrator
 *
 */
@Component
public class SaveEventListener extends AbstractMongoEventListener<Object> {
	@Autowired
	private MongoTemplate mongo;
	
	/**
	 * 在转换之前进行数据的转换
	 */
	@Override
	public void onBeforeConvert(BeforeConvertEvent<Object> event) {
	   if (event != null) {
		   Object source = event.getSource();
		   if (source != null){
			   ReflectionUtils.doWithFields(source.getClass(), new ReflectionUtils.FieldCallback() {  
				   public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {  
					   ReflectionUtils.makeAccessible(field);  
					   // 如果字段添加了我们自定义的AutoIncKey注解  
					   if (field.isAnnotationPresent(AutoIncKey.class)) {  
						   // 设置自增ID  
						   field.set(source, getNextId(source.getClass().getSimpleName()));  
					   }  
				   }  
			   });  
			   
		   }
        }  
	}
	
	 /** 
     * 获取下一个自增ID 
     * @param collName 集合（这里用类名，就唯一性来说最好还是存放长类名）名称 
     * @return 序列值 
     */  
    private Long getNextId(String collName) {  
        Query query = new Query(Criteria.where("collName").is(collName));  
        Update update = new Update();  
        //这个操作是原子类，要么成功要么失败
        update.inc("seqId", 1);  
        FindAndModifyOptions options = new FindAndModifyOptions();  
        options.upsert(true);
        options.returnNew(true);  
        SeqInfo seq = mongo.findAndModify(query, update, options, SeqInfo.class);  
        return seq.getSeqId();  
    }  

}
