package com.weikefu.service.impl;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.weikefu.constant.ContextConstant;
import com.weikefu.dao.mangodb.ShopCustDao;
import com.weikefu.po.ShopCustPo;
import com.weikefu.po.UserInfo;
import com.weikefu.service.ShopCustPoStatusService;


@Service
public class ShopCustPoStatusServiceImpl implements ShopCustPoStatusService {
	
	private final static Logger logger = LoggerFactory.getLogger(ShopCustPoStatusServiceImpl.class);
	
	@Autowired
	private ShopCustDao shopCustDao;
	@Autowired
	private MongoTemplate mongoTemplate;
	
	/**
	 *  将数据库换成mongodb
	 */
	@Override
	public String loginShopCustPo(ShopCustPo shopCustPo) {
		List<ShopCustPo> poList = shopCustDao.findByShopidAndCustid(shopCustPo.getShopid(),shopCustPo.getCustid());
		if(null != poList && poList.size() > 0 && poList.size() < 1.5){
			//判断目前在线的状态
			ShopCustPo custPo = poList.get(0);
			if (custPo.getCustserverstatus() != null){
				custPo.setCustserverstatus(ContextConstant.CustomerStatus.ONLINE.toString());
				custPo.setCustname(shopCustPo.getCustname());
				custPo.setHeadurl(shopCustPo.getHeadurl());
				shopCustDao.save(custPo);
				return ContextConstant.LOGIN_OK;
			}else {
				return ContextConstant.EXIST_NAME;
			}
			
		}else if(poList.size() > 1){
				logger.error("custid is  the " +  shopCustPo.getCustid()  + " of number  exceed 1");
				return ContextConstant.LOGIN_EXCEPTION;
		}else {
			//如果为空，说明还没有保存数据呢，这个时候初始化cust的数据
			shopCustPo.setCustserverstatus(ContextConstant.CustomerStatus.ONLINE.toString());
			shopCustPo.setCreatetime(new Date());
			shopCustPo.setMaxusernum(5);
			shopCustPo.setJoinusernum(0);
			shopCustDao.insert(shopCustPo);
			return ContextConstant.LOGIN_OK;
		}
	}

//	@Override
//	public boolean ShopCustPo(String shopId, String custId) {
//		ShopCustPo searchPo = new ShopCustPo();
//		searchPo.setCustid(Integer.valueOf(custId));
//		searchPo.setShopid(Integer.valueOf(shopId));
//		List<ShopCustPo> poList = shopCustMapper.select(searchPo);
//		if(null!=poList&&poList.size()>0){
//			ShopCustPo custPo = poList.get(0);
//			Integer joinNum = custPo.getJoinusernum();
//			Integer maxNum = custPo.getMaxusernum();
//			//最大用户接入数大于已经接入的用户数，则++1
//			if(maxNum>joinNum){
//				joinNum = joinNum+1;
//			}
//			custPo.setJoinusernum(joinNum);
//			shopCustMapper.updateByPrimaryKeySelective(custPo);
//		}
//		return false;
//	}RRRR:;>.>

	/**
	 * ok
	 * 更新使用mongodb数据库
	 */
	@Override
	public void joinUserNum(String shopid, String custid, int num) {
		Query query = new Query();
		Criteria criteria = Criteria.where("custid").is(Integer.valueOf(custid))
				.and("shopid").is(Integer.valueOf(shopid));
		query.addCriteria(criteria);
		Update update = new Update();
		update.inc("joinusernum", num);
		update.set("updatetime", new Date());
		mongoTemplate.findAndModify(query, update, ShopCustPo.class);
	}

	
	@Override
	public ShopCustPo selectShopCustPo(String shopId, String custId) {

		List<ShopCustPo> list = shopCustDao.findByShopidAndCustid(Integer.valueOf(shopId), Integer.valueOf(custId));
		if(null!=list&&list.size()>0){
			return list.get(0);
		}
		return null;
	}

	
	@Override
	public String selectMinCust(Integer shopid) {
	
		//查询在线的加入人数的
		Query query = new Query();
		Criteria criteria = Criteria.where("shopid").is(shopid)
					.and("custserverstatus").is("online")
					.andOperator(Criteria.where("$where").is("this.maxusernum > this.joinusernum"));
		query.addCriteria(criteria);
		//排序然后找到最小值。
		Sort sort = new Sort(Direction.ASC, "joinusernum");
		query.with(sort).limit(1);
		List<ShopCustPo> minPos = mongoTemplate.find(query, ShopCustPo.class);
		
		if(null!=minPos&&minPos.size()>0){
			Integer custId = minPos.get(0).getCustid();
			return String.valueOf(custId);
		}else{
			return null;
		}
	}
	

	@Override
	public List<ShopCustPo> findListByCustIdAndShopId(String custId,
			String shopId) {
		return shopCustDao.findByShopidAndCustid(Integer.valueOf(shopId), Integer.valueOf(custId));
	}
	
	@Override
	public void changeCustServiceUser(String id, String serveruserid) {
		Query query = new Query();
		Criteria criteria = Criteria.where("_id").is(id);
		Update update = new Update();
		update.set("serveruserid", serveruserid);
		query.addCriteria(criteria);
		update.set("updatetime", new Date());
		mongoTemplate.findAndModify(query, update, ShopCustPo.class);
	}
	
	@Override
	public List<ShopCustPo> findAllCust(Integer shopId) {
		 List<ShopCustPo> list = shopCustDao.findByShopid(shopId);
		return list;
	}
	
	/**
	 * 模糊查询
	 */
	@Override
	public List<ShopCustPo> searchShopCustBykeyword(String shopId,String keyWord) {
		StringBuilder builder = new StringBuilder();
		StringBuilder append = builder.append("^.*").append(keyWord).append(".*$");
	    Pattern pattern = Pattern.compile(append.toString(), Pattern.CASE_INSENSITIVE);
	    Query regexQuery = new Query(Criteria.where("custname").regex(pattern).and("shopid").is(Integer.valueOf(shopId)));
	    List<ShopCustPo> list = mongoTemplate.find(regexQuery, ShopCustPo.class);
		return list;
	}

	
	@Override
	public void updateCustOnlineTime(Integer shopId, Integer custId, String custServerStatus) {
		List<ShopCustPo> shopCustPos = shopCustDao.findByShopidAndCustid(shopId, custId);
		if (shopCustPos != null && shopCustPos.size() > 0 ) {
			ShopCustPo select = shopCustPos.get(0);
			if (null != select && ContextConstant.CustomerStatus.ONLINE.toString().equals(select.getCustserverstatus())){
				if ("online".equals(custServerStatus)){
					
				} else {
					Long onlinetimelong = select.getOnlinetimelong();
					Date updatetime = select.getUpdatetime();
					if (updatetime == null) {
						updatetime = new Date();
					}
					
					if (onlinetimelong == null) {
						onlinetimelong = 0L;
					}
					
					Date now = new Date();
					onlinetimelong += (now.getTime() - updatetime.getTime()) / (60 * 1000);
					select.setOnlinetimelong(onlinetimelong);
					select.setCustserverstatus(custServerStatus);
					shopCustDao.save(select);
				}
			}else{
				select.setCustserverstatus(custServerStatus);
				shopCustDao.save(select);
			}
		}
	}
	
	
	/**
	 * 改变用户的状态
	 * 当serviceId为1的时候，那么对的serviceId就会被枝为空的
	 */
	@Override
	public void changeCustStatus(String shopId, String custId,String status,int serviceId) {

		Criteria criteria = Criteria.where("shopid")
				.is(Integer.valueOf(shopId))
				.and("custid")
				.is(Integer.valueOf(custId));
		Query query = new Query(criteria);
		Update update = new Update();
		if (StringUtils.isNotBlank(status)) {
			update.set("custserverstatus",status);
		}
		if (serviceId == 1) {
			update.set("serveruserid",null);
		}
		update.set("updatetime", new Date());
		mongoTemplate.findAndModify(query, update, ShopCustPo.class);
	}


	
	@Override
	public void updateMaxusernum(String shopId, String custId, Integer maxusernum) {

		Criteria criteria = Criteria.where("shopid")
				.is(Integer.valueOf(shopId))
				.and("custid")
				.is(Integer.valueOf(custId));
		Query query = new Query(criteria);
		Update update = new Update();
		update.set("maxusernum", maxusernum);
		mongoTemplate.findAndModify(query, update, ShopCustPo.class);
	}
	
	
	

	@Override
	public List<ShopCustPo> findBycustserverstatus(String shopId, String custserverstatus) {
		/**
		 * 	 select *
  		 from kefu_shop_cust_status 
	  	 where shopid=#{shopid} and custserverstatus=#{custserverstatus}
		 */
		 List<ShopCustPo>  list = shopCustDao.findByShopidAndCustserverstatus(Integer.valueOf(shopId),custserverstatus);
		return list;
	}
	/**
	 *  目前先不做的
	 * 采用用户对应的搜索，
	 */
	@Override
	public List<UserInfo> searchUser(String shopId, String keyWord) {
		
		return null;

	}

	@Override
	public boolean isStatusByShopCustStatus(String shopId, String custId, String...custStatus) {
		List<ShopCustPo> poList = shopCustDao.findByShopidAndCustid(Integer.valueOf(shopId), Integer.valueOf(custId));
		if(null != poList && poList.size() > 0){
			//判断目前在线的状态
			ShopCustPo custPo = poList.get(0);
			//在线返回true
			for(String status : custStatus){
				if (null!=custPo.getCustserverstatus() && status.equals(custPo.getCustserverstatus())){
					return true;
				}
			}
		
		}
		return false;
	}
	
	

}
