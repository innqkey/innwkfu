package com.weikefu.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;





import com.weikefu.dao.mangodb.ImageDao;
import com.weikefu.po.ImagePo;
import com.weikefu.service.FileService;
import com.weikefu.vo.PageTemp;

@Service
public class FileServiceImpl implements FileService {

	@Autowired
	private ImageDao imageDao;
	/**
	 *  第二版，将对应的数据库转换为mongodb数据库
	 */
	@Override
	public Page<ImagePo> findImageByShopIdAndCustId(String shopId, String custId,PageTemp pageTemp) {
		Sort sort = new Sort(Direction.DESC, "createtime");
		//注意jpa的分页是从0开始的所以必须减去1
		PageRequest pageRequest = new PageRequest(pageTemp.getPageNum() - 1, pageTemp.getPageSize(),sort);
		Page<ImagePo> result = imageDao.findByShopidAndCustid(shopId,custId,pageRequest);
		return result;
	}
	/**
	 * 保存图片
	 */
	@Override
	public void saveImage(ImagePo imagePo) {
		imageDao.save(imagePo);
	}
	@Override
	public ImagePo findByUrlAndShopid(String imageUrl, String shopId) {
		return imageDao.findByUrlAndShopid(imageUrl,shopId);
	}



}
