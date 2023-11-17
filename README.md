# Shipt Shopping Application

This is a basic REST API application developed in Java 8 using the SpringBoot framework.

## Prerequisites
1) Java 8 or above
2) Spring Tool Suite or Eclipse. (IDE)
3) Postman to perform the API requests

## Instructions to run the project
1) Open Eclipse IDE or Spring Tool Suite IDE.
2) Go to the import option.
3) Select import Existing Maven Project.
4) Select the shopping folder that has been unzipped from the directory where the file has been downloaded. 
5) Click finish once the project has been selected. Now the project has been imported and ready to run.
6) Run the "ShoppingApplication.java" file located in src directory. (src\main\java\com\shipt\shopping)
7) Open postman and perform the API calls.

## API

1,2,3 are the APIs requested in the assignment. 4,5,6 and 7 APIs can be used to get details required to perform requests 1,2 and 3.

1) To fetch all the products sold by quantity within a date range by day/week/month. 
    1) Url Format: http://localhost:8080/product/order?startDate=2021/03/10&endDate=2021/04/15&groupBy=3  
    2) HTTP Method: GET
    3) Query Params:

        a) startDate = YYYY/MM/DD
        
        b) endDate = YYYY/MM/DD

        c) groupBy =  1|2|3  
        (1 = group by day, 2 = group by week, 3 = group by month)

    4) Response: JSONObject with productIds and all the products sold by their quantity, product name and day/weekNumber/monthNumber with respect to the start date.

 2) To fetch all the orders of a customer sorted by date. (By default sorting is done in descending order)
    1) Url Format: http://localhost:8080/customer/{customerId}/order
    2) Example: http://localhost:8080/customer/2/order
    3) Path Variable: 
        
        a) customerId
    4) HTTP Method: GET
    5) Response: JSONObject with orderIds and all the orders of the customer along with the productIds in each order, order status and order date.

 3) To add an order for a customer with productIds.
    1) Url Format: http://localhost:8080/customer/{customerId}/order
    2) Example: http://localhost:8080/customer/4/order
    3) Path Variable: 

        a) customerId
    4) HTTP Method: POST

    5) Request Body:
        
        a) JSONObject with "productIds" string as key and array of productIds as the value.

        b) Example:  {"productIds":[20,19,1,5,2,4] }

    6) Response: JSONObject with orderId of the new order created and the productIds of the products that were added to the order.

4) For adding a new customer
    1) Url Format: http://localhost:8080/customer/
    2) HTTP Method: POST
    3) Request Body:

        a) JSONObject with "firstName" string as key and firstName as value and "lastName" string as key and lastName as value.
        b) Example: {"firstName":"Lionel" "lastName":"Messi"}
    4) Response: JSONObject with the new customerId created. 

5) For getting all the customers and their details
    1) Url Format: http://localhost:8080/customer/
    2) HTTP Method: GET
    3) Response: JSONObject of all the customerIDs and the firstName and lastName of all the customers.

6) For getting a customer and his detail
    1) Url Format: http://localhost:8080/customer/{customerId}/
    2) Example: http://localhost:8080/customer/1/ (for customer with customerId 1)
    3) HTTP Method: GET
    4) Response: JSONObject with the customerId and his details including firstName and lastName.

7) For getting all the products and their details
    1) Url Format: http://localhost:8080/product/
    2) HTTP Method: GET
    3) Response: JSONObject of all the productIds and the product Name and the base price of each product per LB.


## Assumptions

1) All the products were assumed to be of infinite quantity. But I have added a quantity column in the product table which can be used to update the quantities.

2) For fetching the orders of a customer by date, sorting was done in descending order.

3) For displaying the products sold by quantity, week number and the month number are with respect to the start date given. For example if a date is in the same week as the start date then it will be considered as week 1 relative to the start date.

4) Start and end date are assumed to be in "YYYY/MM/DD" format and time zone is the system time zone.

5) Base price for each product in the product table is assumed to be per lb (pound). And quantity ordered is assumed to be 1lb for each product in every order but this can be modified later.

## Validations

I have covered few validations that I performed for every request within the time constraint provided.

1) For every requests associated with a customer, customerId will be validated and response will be returned only if it is a valid customerId.

2) Request Parameters and Request Body has to be in the given format provided in the API, otherwise bad request error will be thrown.

3) For an order to be made, if some of the productIds given are invalid and not present, then those productIds will be removed from the order. Order will be made if atleast one valid product is present in the request.

4) Date range provided for fetching the products has to be valid i.e. Start Date should be smaller than the End Date. 

## Database Design

Schema is provided separately in an XML file. Description of the tables are provided below. For more details regarding the keys please look into the schema provided.

MS-SQL Server is the database used. DAO classes will have all the SQL queries required to fetch the data from the database. 

1) Customer table will consist of all the necessary details of the customer such as their first name, last name, emailId and phone number.

2) Product table will have all the products information such as their name, base price per lb.

3) Category table will have all the categories and the category ids.

4) CategoryProductMapping is used to map every product to a category and vice versa. Since a category can have many products and a product can belong to many categories, a separate mapping table is created. (Many to Many)

5) Order table will have all the orders, order_date for each order and the customerId of the customer who ordered. (Since a customer can have many orders but an order can belong to only one customer, no separate mapping table is needed.)

6) OrderProductMapping table will have the productIds for each order along with the quantity ordered for each product in every order. (Many to Many mapping since an order can have multiple products and a product can belong to multiple orders).

