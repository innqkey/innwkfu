package com.weikefu.config;


import org.apache.log4j.Logger;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import redis.clients.jedis.JedisPoolConfig;


/**
 * redis的配置类
 * @author Administrator
 *
 */
@Configuration  
@EnableAutoConfiguration  
public class RedisConfig {  
  
    private static Logger logger = Logger.getLogger(RedisConfig.class);  
      
    @Bean  
    @ConfigurationProperties(prefix="spring.redis")  
    public JedisPoolConfig getRedisConfig(){  
        JedisPoolConfig config = new JedisPoolConfig();  
        return config;  
    }  
      
    @Bean  
    @ConfigurationProperties(prefix="spring.redis")  
    public JedisConnectionFactory getConnectionFactory(){  
        JedisConnectionFactory factory = new JedisConnectionFactory();  
        JedisPoolConfig config = getRedisConfig();  
        factory.setPoolConfig(config);  
        logger.info("JedisConnectionFactory bean init success.");  
        return factory;  
    }  
      
    
    @Bean("redisTemplate")  
    public RedisTemplate<?, ?> getRedisTemplate(){  
        RedisTemplate<?,?> template = new StringRedisTemplate();  
        template.setConnectionFactory(getConnectionFactory());
        RedisSerializer stringSerializer = new StringRedisSerializer();
        template.setStringSerializer(stringSerializer);
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);//key的序列化适配器  
        template.setHashValueSerializer(stringSerializer);//value的序列化适配器，也可以自己编写，大部分场景StringRedisSerializer足以满足需求了。  
        return template;  
    }  
}
