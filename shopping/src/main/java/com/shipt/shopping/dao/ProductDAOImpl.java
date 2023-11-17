package com.shipt.shopping.dao;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductDAOImpl implements ProductDAO {
	@Autowired 
	private JdbcTemplate jdbcTemplate;

	public List<Map<String,Object>> getAllProductsRows(){
		String sql = "Select * from dbo.PRODUCT";
		List<Map<String,Object>> rows = jdbcTemplate.queryForList(sql);
		return rows;
	}

	public List<Map<String,Object>> getAllProductIdRows(){
		String sql = "Select PRODUCT_ID from dbo.PRODUCT";
		List<Map<String,Object>> rows = jdbcTemplate.queryForList(sql);
		return rows;
	}

	public List<Map<String,Object>> getSoldProductsRow(long startDate, long endDate){
		String sql = "select T1.PRODUCT_ID, sum(T1.QUANTITYORDERED) as TOTALQUANTITYORDERED, T2.ORDER_DATE from "
				+ "(select ORDER_ID, PRODUCT_ID, QUANTITYORDERED from ORDERSPRODUCTMAPPING where ORDER_ID in "
				+ "(select ORDER_ID from ORDERS where ORDER_DATE between '"+startDate+"' and '"+endDate+"')) "
				+ "as T1 "
				+ "inner join "
				+ "(select ORDER_ID, ORDER_DATE from ORDERS where ORDER_DATE between '"+startDate+"' and '"+endDate+"') "
				+ "as T2 on T1.ORDER_ID = T2.ORDER_ID GROUP BY T1.PRODUCT_ID, T2.ORDER_DATE ";
		List<Map<String,Object>> rows = jdbcTemplate.queryForList(sql);
		return rows;
	}

	public List<Map<String,Object>> getSpecificProducts(List<Long> productIds){
		StringBuilder inSql = new StringBuilder();
		inSql.append(productIds.get(0));
		for(int i = 1;i<productIds.size();i++) {
			inSql.append(",");
			inSql.append(productIds.get(i));
		}
		String sql = String.format("select PRODUCT_ID, NAME, BASE_PRICE from PRODUCT where PRODUCT_ID in (%s)", inSql.toString());
		List<Map<String,Object>> rows = jdbcTemplate.queryForList(sql);
		return rows;
	}
}
