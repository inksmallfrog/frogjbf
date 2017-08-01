package com.inksmallfrog.frogjbf.test.service;

import java.math.BigDecimal;
import java.util.List;

import com.inksmallfrog.frogjbf.datasource.PageInfo;
import com.inksmallfrog.frogjbf.global.JBFContext;
import com.inksmallfrog.frogjbf.test.dao.DeptDao;
import com.inksmallfrog.frogjbf.test.entity.DeptBean;

public class DeptService {
	public BigDecimal createDept(DeptBean dept){
		DeptDao dao = (DeptDao) JBFContext.getAppContext().getDao("deptDao");
		return dao.insertDept(dept);
	}
	public List<DeptBean> getDeptsAtPage(PageInfo pageInfo){
		DeptDao dao = (DeptDao) JBFContext.getAppContext().getDao("deptDao");
		return dao.queryDeptsSliceByPage(pageInfo);
	}
	public List<DeptBean> getAllDepts(){
		DeptDao dao = (DeptDao) JBFContext.getAppContext().getDao("deptDao");
		return dao.queryAllDepts();
	}
}
