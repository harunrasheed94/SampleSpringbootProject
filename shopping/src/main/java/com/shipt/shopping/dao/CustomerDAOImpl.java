package com.shipt.shopping.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

import com.shipt.shopping.model.Customer;
import com.shipt.shopping.model.Order;

@Repository
public class CustomerDAOImpl implements CustomerDAO {
	@Autowired 
	private JdbcTemplate jdbcTemplate;

	//Retrieve operations from database
	public List<Map<String,Object>> getAllCustomers() {

		String sql = "Select * from dbo.CUSTOMER";
		List<Map<String,Object>> rows = jdbcTemplate.queryForList(sql);
		return rows;
	}

	public List<Map<String, Object>> getCustomerDetail(long customerId){
		String sql = "Select * from dbo.CUSTOMER where CUSTOMER_ID='"+customerId+"'";
		List<Map<String,Object>> rows = jdbcTemplate.queryForList(sql);
		return rows;
	}
	
	public List<Map<String, Object>> getAllOrdersRowsforCustomer(long customerId){
		String sql = "select T1.ORDER_ID, T1.ORDER_STATUS, T1.ORDER_DATE, T2.PRODUCT_ID, T2.QUANTITYORDERED from "
				+ "(select ORDER_ID, ORDER_STATUS, ORDER_DATE from ORDERS where CUSTOMER_ID ='" +customerId+"') as T1 "
				+ "inner join "
				+ "(select ORDER_ID, PRODUCT_ID, QUANTITYORDERED from ORDERSPRODUCTMAPPING where ORDER_ID in (select ORDER_ID from ORDERS where CUSTOMER_ID = '"+customerId+"')) as T2 "
				+ "on T1.ORDER_ID = T2.ORDER_ID order by T1.ORDER_DATE desc";
		List<Map<String,Object>> rows = jdbcTemplate.queryForList(sql);
		return rows;
	}
	

	//Addition operations in Database
	public Long addANewCustomer(Customer customer){
		Long newCustomerId = null;
		if(customer != null) {
			String customerFirstName = customer.getFirstName();
			String customerLastName = customer.getLastName();
			final String INSERT_SQL = "Insert into CUSTOMER (CUSTOMER_FIRSTNAME, CUSTOMER_LASTNAME) values (?,?)";
			KeyHolder holder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, customerFirstName);
					ps.setString(2, customerLastName);
					return ps;
				}
			}, holder);
			newCustomerId = holder.getKey().longValue();
		}
		return newCustomerId;
	}

	public Long addAOrder(Order order) {
		Long newOrderId = null;
		if(order != null) {
			long customerId = order.getCustomerId();
			int orderStatus = order.getOrderStatus();
			long orderDate = order.getOrderDate();
			final String INSERT_SQL = "Insert into ORDERS (ORDER_STATUS, CUSTOMER_ID, ORDER_DATE) values (?,?,?)";
			KeyHolder holder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
					ps.setInt(1, orderStatus);
					ps.setLong(2, customerId);
					ps.setLong(3, orderDate);
					return ps;
				}
			}, holder);
			newOrderId = holder.getKey().longValue();
			order.setOrderId(newOrderId);
			addProductsToOrder(order);
		}
		return newOrderId;
	}

	private void addProductsToOrder(Order order) {

		long orderId = order.getOrderId();
		Map<Long,Double> productsQuantity = order.getProductsQuantity();
		List<Long> productIds = new ArrayList<Long>(productsQuantity.keySet());
		int[] returnedRows = this.jdbcTemplate.batchUpdate(
				"Insert into ORDERSPRODUCTMAPPING (ORDER_ID, PRODUCT_ID, QUANTITYORDERED) values(?,?,?)",
				new BatchPreparedStatementSetter() {

					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setLong(1, orderId);
						ps.setLong(2, productIds.get(i));
						ps.setBigDecimal(3, BigDecimal.valueOf(productsQuantity.get(productIds.get(i))));
					}

					public int getBatchSize() {
						return productIds.size();
					}

				});
		System.out.println(Arrays.toString(returnedRows));
	}

}
