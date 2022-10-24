# VALR Order Book
OrderBook for a cryptocurrency exchange

## Building It
This project uses Maven to build the project.


## Test Using Curl

Get AuthToken for placing limit order: (For demo purpose, we are generating the token directly)
```
curl http://localhost:8090/user/authtoken
```

Place Limit Buy Order
```
curl -H "Content-Type: application/json" -H "X-VALRToken: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ2YWxydXNlcjk5OSIsImV4cCI6MTY5ODEzOTk4NSwiaWF0IjoxNjY2NjAzOTg1fQ.WjYcG4tS6P6WWhX6PoVQHPvP3c7fvUezS6ZJiHRKIi3LvmWMUCMmKOIxMbzW2BrmO4XU3N9LcwNTZarac6s2qA" -d '{"name":"btcinr", "price":100.53, "qty":20}' -X POST http://localhost:8090/limit/buy/order
```

Place Limit Sell Order
```
curl -H "Content-Type: application/json" -H "X-VALRToken: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ2YWxydXNlcjk5OSIsImV4cCI6MTY5ODEzOTk4NSwiaWF0IjoxNjY2NjAzOTg1fQ.WjYcG4tS6P6WWhX6PoVQHPvP3c7fvUezS6ZJiHRKIi3LvmWMUCMmKOIxMbzW2BrmO4XU3N9LcwNTZarac6s2qA" -d '{"name":"btcinr", "price":80.91, "qty":10}' -X POST http://localhost:8090/limit/sell/order
```

Get Market Orderbook
```
curl -H "Content-Type: application/json" -X GET http://localhost:8090/market/btcinr/orderbook
```

Get Market Tradehistory: (Limited to latest 20 records for the market)
```
curl -H "Content-Type: application/json" -d -X GET http://localhost:8090/market/btcinr/tradehistory
```

## Dev Environment Requirements

- Mac OS(Intel)
- Java SDK
- Spring Boot
- Maven
- Curl
