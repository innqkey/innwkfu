package com.weikefu.transfer;

import static org.mockito.Matchers.contains;

import java.io.IOException;
import java.security.KeyStore.PrivateKeyEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weikefu.cache.RedisCustUserHistoryCache;
import com.weikefu.cache.RedisShopUserHistoryCache;
import com.weikefu.cache.RedisUserInfoCache;
import com.weikefu.constant.ContextConstant;
import com.weikefu.dao.IRedisBaseDao;
import com.weikefu.dao.mangodb.ImageDao;
import com.weikefu.dao.mangodb.MessageDao;
import com.weikefu.dao.mangodb.QuickMessageDao;
import com.weikefu.dao.mangodb.ShopCustDao;
import com.weikefu.dao.mangodb.UserInfoDao;
import com.weikefu.po.ImagePo;
import com.weikefu.po.UserInfo;
import com.weikefu.service.CustUserHistoryService;
import com.weikefu.service.ShopUserHistorySerivce;
import com.weikefu.transfer.dao.ImageDaoTransfer;
import com.weikefu.transfer.dao.MessageDaoTransfer;
import com.weikefu.transfer.dao.QuickMessageDaoTransfer;
import com.weikefu.transfer.dao.ShopCustDaoTransfer;
import com.weikefu.transfer.po.Message;
import com.weikefu.transfer.po.QuickMessagePo;
import com.weikefu.transfer.po.ShopCustPo;
import com.weikefu.util.ConvertUtils;
import com.weikefu.util.JacksonUtils;
import com.weikefu.util.ResUtils;
import com.weikefu.vo.PageTemp;


/**
* 类说明：用来将reids中的shopUserHistorycache和RedisUserShopTalkCache这样从
* redis迁移到mongodb， 并且将mysql中数据库中的内容迁移到mongodb中
* @author 
* @version 创建时间：2018年2月2日 上午10:26:21
* 
*/
@RestController
public class Transfer {
	@Autowired
	private ImageDao imageDao;
	@Autowired
	private ImageDaoTransfer imageDaoTransfer;
	
	@Autowired
	private MessageDao messageDao;
	@Autowired
	private MessageDaoTransfer messageDaoTransfer;
	
	@Autowired
	private QuickMessageDao quickMessageDao;
	@Autowired
	private QuickMessageDaoTransfer quickMessageDaoTransfer;
	
	@Autowired
	private ShopCustDao shopCustDao;
	@Autowired
	private ShopCustDaoTransfer shopCustDaoTransfer;
	
	@Autowired
	private IRedisBaseDao<String> redis;
	
	@Autowired
	private RedisUserInfoCache userInfoCache;
	
	@Autowired
	private UserInfoDao userInfoDao;
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private RedisShopUserHistoryCache shopUserHistoryCache;
	@Autowired
	private ShopUserHistorySerivce ShopUserHistory;
	
	@Autowired
	private CustUserHistoryService  custUserHistoryService;
	@Autowired
	private RedisCustUserHistoryCache redisCustUserHistoryCache;
	
	@RequestMapping("/transfer")
	public String transfer() throws Exception {
		try {
			//用于image的转移
			List<com.weikefu.transfer.po.ImagePo> imageAll = imageDaoTransfer.findAll();
			List<ImagePo> images = ConvertUtils.convertDtoAndVo(imageAll, ImagePo.class);
			imageDao.save(images);
			
			//使用message的转移
			List<Message> messageAll = messageDaoTransfer.findAll();
			List<com.weikefu.po.Message> messages = ConvertUtils.convertDtoAndVo(messageAll, com.weikefu.po.Message.class);
			messageDao.save(messages);
			
//		//用于quickMessage的转移
			List<QuickMessagePo> quickMessageAll = quickMessageDaoTransfer.findAll();
			List<com.weikefu.po.QuickMessagePo> quickMessages = ConvertUtils.convertDtoAndVo(quickMessageAll, com.weikefu.po.QuickMessagePo.class);
			quickMessageDao.save(quickMessages);
			
			//用于shopCustPo的转移
			List<ShopCustPo> shopCustPoAll = shopCustDaoTransfer.findAll();
			List<com.weikefu.po.ShopCustPo> shopCustPos = ConvertUtils.convertDtoAndVo(shopCustPoAll, com.weikefu.po.ShopCustPo.class);
			shopCustDao.save(shopCustPos);
			
			//将userInfo里面的转移到mongodb中去
//		for (int i = 0; i < 10; i++) {
//			com.weikefu.transfer.po.UserInfo userInfo = new com.weikefu.transfer.po.UserInfo();
//			userInfo.setCity("sichuan");
//			userInfo.setCountry("china");
//			userInfo.setCreatedat(new Date().toString());
//			userInfo.setHeadimgurl("图片");
//			userInfo.setJoinway("weixin");
//			userInfo.setMobile("15646");
//			userInfo.setNickname("user");
//			userInfo.setOpenid(new Random().nextInt(50000000) + "");
//			userInfo.setProvince("sdfsd");
//			userInfo.setTruename("sdfsd");
//			userInfo.setUserId(i + 1 + "");
//			userInfoCache.addUserInfo(i + 1 + "", userInfo);
//		}
//		用于user迁移
			List<String> users = redisTemplate.opsForHash().values(ContextConstant.REDIS_USER_INFO);
			ArrayList<com.weikefu.transfer.po.UserInfo> userList = new ArrayList<com.weikefu.transfer.po.UserInfo>();
			for (String string : users) {
				com.weikefu.transfer.po.UserInfo object = JacksonUtils.toObject(string, com.weikefu.transfer.po.UserInfo.class);
				if (object.getUserId() != null) {
					userList.add(object);
				}
//			
			}
//		
			List<UserInfo> convertDtoAndVo2 = ConvertUtils.convertDtoAndVo(userList, UserInfo.class);
			for (int i = 0; i < convertDtoAndVo2.size(); i++) {
				convertDtoAndVo2.get(i).setJoinway("weixin");
				convertDtoAndVo2.get(i).setCreated_at(userList.get(i).getCreatedat());
			}
			userInfoDao.save(convertDtoAndVo2);
		} catch (Exception e) {
			e.printStackTrace();
			return ResUtils.execRes();
		}
		
		return ResUtils.okRes();
	}
	
	
	//用于手动将reids中的数据添加
	@RequestMapping("/ShopUserHistory")
	public String insertShopUserHistory(String shopId) {
		List<String> shopHistListBySize = shopUserHistoryCache.getShopHistListBySize(shopId, 20000);
		ShopUserHistory.addBatchShopHist(shopId, shopHistListBySize);
		return ResUtils.okRes();
	}
	
	@RequestMapping("/custUserHistory")
	public String custUserHistory(String shopId,String custId) {
		PageTemp pageTemp = new PageTemp();
		pageTemp.setPageSize(2000);
		List<String> shopHistListBySize = redisCustUserHistoryCache.getCustHistListBySize(shopId, custId, pageTemp);
		custUserHistoryService.addBatchCustHist(shopId,custId, shopHistListBySize);
		return ResUtils.okRes();
	}
	
	
}	
