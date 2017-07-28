package com.inksmallfrog.frogjbf.test.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.inksmallfrog.frogjbf.global.JBFContext;
import com.inksmallfrog.frogjbf.test.entity.DeptBean;
import com.inksmallfrog.frogjbf.util.datasource.DBSession;

public class DeptDao {
	public BigDecimal insertDept(DeptBean dept){
		DBSession session = JBFContext.getAppContext().getDbSessionFactory().getDefaultDBSession();
		return (BigDecimal) session.insertRow("INSERT INTO DEPT(TITLE) VALUES(?)", "ID", dept.getTitle());
	}
	
	public List<DeptBean> queryAllDepts(){
		DBSession session = JBFContext.getAppContext().getDbSessionFactory().getDefaultDBSession();
		List<DeptBean> depts = new ArrayList<DeptBean>();
		List<Map<String, Object>> res = session.queryRows("SELECT * FROM DEPT");
		for(Map<String, Object> row : res){
			depts.add(new DeptBean(((BigDecimal)row.get("ID")).intValue(),(String) row.get("TITLE")));
		}
		return depts;
	}
}
