package com.weikefu.config.socket;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;

 
/**
 * 配置socket的服务器
 * https.properties
 * @author Administrator
 *
 */
@org.springframework.context.annotation.Configuration
public class ImServerConfiguration{  	
	@Value("${uk.im.server.host}")  
    private String host;  
  
    @Value("${uk.im.server.port}")  
    private Integer port;
    
//    @Value("${key.store.password}")
//    private String password;
//    
//    @Value("${key.store.file}")
//    private String keyStore;
    
    private SocketIOServer server ;
    
    public Integer getWebIMPort() {   
    	return port;   
    }  
    /**
     * 创建对应的socket服务器
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    @Bean
    public SocketIOServer socketIOServer() throws NoSuchAlgorithmException, IOException{  
    	Configuration config = new Configuration();
		config.setPort(port);
		config.setExceptionListener(new WeKeFuExceptionListener());
		//判断是否有htts.properties的加密
//    	if(!StringUtils.isBlank(this.password) && !StringUtils.isBlank(this.keyStore)){
//    		//设置https的服务器的私钥
//    		config.setKeyStorePassword(this.password);
//    	    InputStream stream = PropertyUtils.class.getClassLoader().getResourceAsStream(this.keyStore);
//    	    int available = stream.available();
//    	    config.setKeyStore(stream);
//    	}
    	

		//当前线程乘以2 
        config.setBossThreads(33);
		config.setWorkerThreads(33);
//		config.setStoreFactory(new HazelcastStoreFactory());
		config.setAuthorizationListener(new AuthorizationListener() {
			@Override
			public boolean isAuthorized(HandshakeData data) {
				return true;
			}
		});
		
        return this.server = new SocketIOServer(config);
    }
//    /**
//     * 这来在对象创建的时候判断是否存在OnConnect.class, OnDisconnect.class, OnEvent
//     * 注解，如果存在的话，将添加到socketIOServer对应的事件中去
//     * 在本项目中，已经通过Namespace的方法，将对应的带注解的存放到对应的namespace中去，无须这个方法
//     * @param socketServer
//     * @return
//     */
//    @Bean  
//    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {  
//        return new SpringAnnotationScanner(socketServer);  
//    }  
    /**
     * spring容器关闭前停止服务
     */
    @PreDestroy  
    public void destory() { 
		this.server.stop();
	}
}  