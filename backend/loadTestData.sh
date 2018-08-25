#!/usr/bin/env bash -e

# Script works correctly only on clean database - will fail if any entry already exists (e.g. account name)

# TODO Use loops and arrays instead of copy pasting
# TODO error handling

# Add accounts
ACCOUNT_ALIOR=$(curl -X POST "http://localhost:8088/accounts" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"balance\": 1438.89, \"name\": \"Alior Bank savings\"}")
echo "Added account Alior under id $ACCOUNT_ALIOR"

ACCOUNT_IDEA=$(curl -X POST "http://localhost:8088/accounts" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"balance\": 10000.89, \"name\": \"Idea Bank\"}")
echo "Added account Idea under id $ACCOUNT_IDEA"

ACCOUNT_MBANK=$(curl -X POST "http://localhost:8088/accounts" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"balance\": 2387.42, \"name\": \"mBank\"}")
echo "Added account mBank under id $ACCOUNT_MBANK"

ACCOUNT_CASH=$(curl -X POST "http://localhost:8088/accounts" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"balance\": 247.42, \"name\": \"Cash\"}")
echo "Added account Cash under id $ACCOUNT_CASH"


# Add categories
CATEGORY_CAR=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Car\"}")
echo "Added category Car under id $CATEGORY_CAR"

CATEGORY_CAR_FUEL=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Car Fuel\", \"parentCategoryId\": $CATEGORY_CAR}")
echo "Added category Car Fuel under id $CATEGORY_CAR_FUEL"

CATEGORY_CAR_SERVICE=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Car Service\", \"parentCategoryId\": $CATEGORY_CAR}")
echo "Added category Car Fuel under id $CATEGORY_CAR_SERVICE"

CATEGORY_CAR_TYRES=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Car Tires\", \"parentCategoryId\": $CATEGORY_CAR}")
echo "Added category Car Fuel under id $CATEGORY_CAR_TYRES"

CATEGORY_SHOPPING=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Shopping\"}")
echo "Added category Shopping under id $CATEGORY_CAR"

CATEGORY_SHOPPING_FOOD=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Food\", \"parentCategoryId\": $CATEGORY_SHOPPING}")
echo "Added category Food under id $CATEGORY_SHOPPING_FOOD"

CATEGORY_SHOPPING_CLOTHS=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Cloths\", \"parentCategoryId\": $CATEGORY_SHOPPING}")
echo "Added category Cloths under id $CATEGORY_SHOPPING_CLOTHS"

CATEGORY_SHOPPING_ELECTRONICS=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Electronics\", \"parentCategoryId\": $CATEGORY_SHOPPING}")
echo "Added category Electronics under id $CATEGORY_SHOPPING_ELECTRONICS"

CATEGORY_ENTERTAINMENT=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Entertainment\"}")
echo "Added category Car under id $CATEGORY_ENTERTAINMENT"

CATEGORY_EATING_OUTSIDE=$(curl -X POST "http://localhost:8088/categories" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Eating outside\"}")
echo "Added category Car under id $CATEGORY_EATING_OUTSIDE"


# Add transactions
TRANSACTION_CINEMA=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_MBANK, \"categoryId\": $CATEGORY_ENTERTAINMENT, \"date\": \"2018-12-31\", \"description\": \"Cinema - Star Wars 5\", \"price\": 15.99}")
echo "Added transaction Cinema under id $TRANSACTION_CINEMA"

TRANSACTION_MCDONALDS=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_MBANK, \"categoryId\": $CATEGORY_EATING_OUTSIDE, \"date\": \"2018-10-12\", \"description\": \"Big Mac\", \"price\": 10.25}")
echo "Added transaction BigMac under id $TRANSACTION_MCDONALDS"

TRANSACTION_BURGERKING=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_CASH, \"categoryId\": $CATEGORY_EATING_OUTSIDE, \"date\": \"2018-06-12\", \"description\": \"Whopper\", \"price\": 9.89}")
echo "Added transaction Whopper under id $TRANSACTION_BURGERKING"

TRANSACTION_CAR=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_CAR, \"date\": \"2017-11-20\", \"description\": \"Alfa Romeo Stelvio\", \"price\": 210890}")
echo "Added transaction Car under id $TRANSACTION_CAR"

TRANSACTION_CAR_FUEL=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_CASH, \"categoryId\": $CATEGORY_CAR_FUEL, \"date\": \"2017-11-20\", \"description\": \"Fuel - Orlen\", \"price\": 300.23}")
echo "Added transaction Car fuel under id $TRANSACTION_CAR_FUEL"

TRANSACTION_CAR_FUEL=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_IDEA, \"categoryId\": $CATEGORY_CAR_FUEL, \"date\": \"2017-11-27\", \"description\": \"Fuel - Shell\", \"price\": 290.64}")
echo "Added transaction Car fuel under id $TRANSACTION_CAR_FUEL"

TRANSACTION_CAR_FUEL=$(curl -X POST "http://localhost:8088/transactions" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"accountId\": $ACCOUNT_MBANK, \"categoryId\": $CATEGORY_CAR_FUEL, \"date\": \"2017-12-14\", \"description\": \"Fuel - Tesco\", \"price\": 111.03}")
echo "Added transaction Car fuel under id $TRANSACTION_CAR_FUEL"


# Add filters
FILTER_SIGNIFICANT=$(curl -X POST "http://localhost:8088/filters" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"name\": \"Significant expenses\", \"priceFrom\": 1000}")
echo "Added filter Significant expenses under id $FILTER_SIGNIFICANT"

FILTER_CASH=$(curl -X POST "http://localhost:8088/filters" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"dateFrom\": \"2018-01-01\", \"dateTo\": \"2018-12-31\", \"name\": \"Cash payments 2018\", \"accountIds\": [ $ACCOUNT_CASH ]}")
echo "Added filter cash expenses under id $FILTER_CASH"

FILTER_CAR=$(curl -X POST "http://localhost:8088/filters" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"dateFrom\": \"2017-01-01\", \"dateTo\": \"2018-12-31\", \"name\": \"Car expenses < 500\", \"priceFrom\": 0, \"priceTo\": 500, \"categoryIds\": [ 1 ]}")
echo "Added filter car expenses under id $FILTER_CAR"