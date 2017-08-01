package com.inksmallfrog.frogjbf.datasource.impl;

import com.inksmallfrog.frogjbf.datasource.inte.SQLGenerator;

public class OracleSQLGenerator implements SQLGenerator {

	@Override
	public String getSlicePageSQL(String querySql, int minRow, int pageSize) {
		String maxRowSql = "SELECT TOTAL_TABLE.*, ROWNUM RN " + "FROM( "
				+ querySql + " ) TOTAL_TABLE " + "WHERE ROWNUM < " + minRow + pageSize;
		String sql = "SELECT * " + "FROM ( " + maxRowSql + " ) "
				+ "WHERE RN >= " + minRow;;
		return sql;
	}
}
