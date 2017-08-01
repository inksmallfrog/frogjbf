package com.inksmallfrog.frogjbf.test.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.inksmallfrog.frogjbf.datasource.DBSession;
import com.inksmallfrog.frogjbf.datasource.PageInfo;
import com.inksmallfrog.frogjbf.global.JBFContext;
import com.inksmallfrog.frogjbf.test.entity.DeptBean;

public class DeptDao {
	public BigDecimal insertDept(DeptBean dept){
		DBSession session = JBFContext.getAppContext().getDbSessionFactory().getDefaultDBSession();
		return (BigDecimal) session.insertRow("INSERT INTO DEPT(TITLE) VALUES(?)", "ID", dept.getTitle());
	}
	
	public List<DeptBean> queryDeptsSliceByPage(PageInfo pageInfo){
		DBSession session = JBFContext.getAppContext().getDbSessionFactory().getDefaultDBSession();
		return session.getComplexDBSession().queryPage(DeptBean.class, pageInfo, "SELECT * FROM DEPT");
	}
	
	public List<DeptBean> queryAllDepts(){
		DBSession session = JBFContext.getAppContext().getDbSessionFactory().getDefaultDBSession();
		return session.queryRows(DeptBean.class, "SELECT * FROM DEPT");
	}
}
