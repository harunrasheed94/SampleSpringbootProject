package com.shipt.shopping.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shipt.shopping.service.ProductService;

@RestController
public class ProductController {
	private ProductService productServiceObj;

	@Autowired
	public ProductController(ProductService theproductServiceObj) {
		productServiceObj = theproductServiceObj;
	}

	//For getting all the products and their details
	@RequestMapping(value = "/product", method= RequestMethod.GET)
	public String getAllProducts() throws Exception {
		JSONObject retObj = productServiceObj.getAllProductJSONObjects();
		return retObj.toString();
	}

	//For getting all the products sold by quantity per day/week/month
	@RequestMapping(value = "/product/order", method= RequestMethod.GET)
	public String getAllProductsSold(@RequestParam String startDate, @RequestParam String endDate, @RequestParam int groupBy) throws Exception {
		JSONObject retObj = productServiceObj.getOrderedProducts(startDate, endDate, groupBy);
		return retObj.toString();
	}
}
