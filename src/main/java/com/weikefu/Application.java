package com.weikefu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableJpaRepositories("com.weikefu.transfer.dao")
public class Application extends SpringBootServletInitializer{
	@Autowired  
    private RestTemplateBuilder builder;  
  
 	// 使用RestTemplateBuilder来实例化RestTemplate对象，spring默认已经注入了RestTemplateBuilder实例
	
    @Bean  
    public RestTemplate restTemplate() {  
    	RestTemplate build = builder.build();
        return build;
    }
    
	public static void main(String[] args) {
		new SpringApplication(Application.class).run(args);
	}
//	
//	@Override
//	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//		// TODO Auto-generated method stub
//		return  builder.sources(Application.class);
//	}

}
