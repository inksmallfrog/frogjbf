package com.inksmallfrog.frogjbf.datasource.impl;

import com.inksmallfrog.frogjbf.datasource.inte.SQLGenerator;

public class MysqlSQLGenerator implements SQLGenerator {
	@Override
	public String getSlicePageSQL(String querySql, int minRow, int pageSize) {
		return querySql + " LIMIT " + minRow + ", " + pageSize;
	}
}
