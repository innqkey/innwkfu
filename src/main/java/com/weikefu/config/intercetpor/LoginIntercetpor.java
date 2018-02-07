package com.weikefu.config.intercetpor;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.weikefu.constant.ContextConstant;
import com.weikefu.util.Base64Utils;
import com.weikefu.util.MD5Utils;
import com.weikefu.util.ResUtils;

/**
 * 用来对登录进行简单的验证
 * @author Administrator
 *
 */
@Component
public class LoginIntercetpor extends HandlerInterceptorAdapter  {
	
	@Value("${http.token}")
	private String token;
	
	/**
	 * 目前的验证规则，是修改对应的编码请求
	 */
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		if ("1".equals(token)) {
			try {
				String requestURI = request.getRequestURI();
				System.out.println(requestURI);
				String accessToken = request.getParameter("crm_token");
				Cookie[] cookies = request.getCookies();
				for (Cookie cookie : cookies) {
					if (ContextConstant.COOKIE_TOKEN.equals(cookie.getName()) && StringUtils.isNotBlank(cookie.getValue())) {
						String[] split = Base64Utils.decode(URLDecoder.decode(cookie.getValue(), "utf-8")).split("-");
						String token = new StringBuilder().append(split[0]).append("-").append(split[1]).append(ContextConstant.TOKEN_SALT).toString();
						token = MD5Utils.md5Encode(MD5Utils.md5Encode(token));
						if (accessToken.equals(token)) {
							return true;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			writeContent(ResUtils.tokenErrRes());
			return false;
		}
		return true;
		
	}
	
	
	private void writeContent(String content) {
    	HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    	response.reset();
    	response.setCharacterEncoding("UTF-8");
    	response.setHeader("Content-Type", "text/plain;charset=UTF-8");
    	response.setHeader("icop-content-type", "exception");
    	PrintWriter writer = null;
    	try {
    		writer = response.getWriter();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	writer.print(content);
    	writer.flush();
    	writer.close();
     }


}
