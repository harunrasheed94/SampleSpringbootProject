package com.shipt.shopping.dao;

import java.util.List;
import java.util.Map;

public interface ProductDAO {
	public List<Map<String,Object>> getAllProductsRows();
	public List<Map<String,Object>> getAllProductIdRows();
	public List<Map<String,Object>> getSoldProductsRow(long startDate, long endDate);
	public List<Map<String,Object>> getSpecificProducts(List<Long> productIds);
}
