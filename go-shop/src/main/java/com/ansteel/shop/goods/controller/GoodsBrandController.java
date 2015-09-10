package com.ansteel.shop.goods.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ansteel.core.annotation.PathClass;
import com.ansteel.core.annotation.PathDatabaseEntity;
import com.ansteel.core.constant.DHtmlxConstants;
import com.ansteel.core.domain.BaseEntity;
import com.ansteel.core.query.PageUtils;
import com.ansteel.core.utils.ResponseUtils;
import com.ansteel.dhtmlx.jsonclass.UDataSet;
import com.ansteel.report.jasperReports.domain.JasperReport;
import com.ansteel.shop.goods.service.GoodsBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ansteel.core.controller.BaseController;
import com.ansteel.core.domain.EntityInfo;
import com.ansteel.shop.goods.domain.GoodsBrand;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping(value = "/goodsbrand")
public class GoodsBrandController  extends BaseController{

	@Override
	public Collection<EntityInfo> getEntityInfos() {
		Collection<EntityInfo> entityInfos= new ArrayList<EntityInfo>();
		EntityInfo entity = new EntityInfo();
		entity.setClazz(GoodsBrand.class);
		entityInfos.add(entity);
		return entityInfos;
	}

	@Autowired
	GoodsBrandService goodsBrandService;

	@RequestMapping("/a/saveFile/{className}")
	public @ResponseBody
	int saveFileAjax(
			@Valid @PathDatabaseEntity("className") BaseEntity entity,
			@RequestParam(value = "_key", required = false) String key,
			@RequestParam(value = "_value", required = false) String value,
			@RequestParam(value = "file", required = false) MultipartFile file,
			HttpServletRequest request,
			HttpServletResponse response,
			BindingResult result) {
		GoodsBrand goodsBrand=(GoodsBrand) entity;
		Assert.isTrue(!goodsBrandService.isNameRepeat(goodsBrand), goodsBrand.getBrandName() + ",已经存在，请检查！");
		if(file!=null&& file.getSize() > 0){
			goodsBrandService.saveAttachment(file,goodsBrand);
		}else {
			super.saveAjax(goodsBrand, result, key, value, request, response);
		}
		ResponseUtils.setContentType(response);
		return 1;
	}

	public @ResponseBody
	UDataSet loadPageAjax(@PathClass("className")Class clazz,
						  @RequestParam(value="_key",required=false)String key,//过滤字段名（一般用于主从表）
						  @RequestParam(value="_value",required=false)String value,//过滤字段值（一般用于主从表）
						  @RequestParam(value="posStart",required=false)String posStart,//分页当前记录行
						  @RequestParam(value="count",required=false)String count,//分页记录行
						  @RequestParam(value="_order",required=false)String order,//排序字段名
						  HttpServletRequest request,
						  HttpServletResponse response){
		UDataSet dataSet=super.loadPageAjax(clazz, key, value, posStart, count, order, request, response);
		Page page= (Page) dataSet.getResult();
		List<GoodsBrand> goodsBrandList=page.getContent();
		this.setLogoImage(goodsBrandList,request);
		return new UDataSet(request, DHtmlxConstants.UI_ROWS,page);
	}


	public void setLogoImage(List<GoodsBrand> goodsBrandList,HttpServletRequest request) {
		for(GoodsBrand goodsBrand:goodsBrandList){
			String logoImage=goodsBrand.getLogoImage();
			String url=request.getContextPath()+"/att/download/"+logoImage;
			goodsBrand.setLogoImage(url);
		}
	}
}
