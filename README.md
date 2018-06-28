#### How To

##### Run
```./mvnw install && ./mvnw spring-boot:run```

By default it will run at port `8088`

##### Test
```./mvnw clean test```

    
#### Note on Solution
##### Implementation

- This application provide two endpoints `POST /transactions` and `GET /statistics`
- Application maintain a running in memory statistics
- In memory statistics is implemented as thread-safe singleton with `ConcurrentHashMap` as field to store running statistics 
- When new `Transaction` arrived, it will be validated. Invalid transaction will be ignored
- Each valid Transaction received will update running statistics as describe below:
    - Find existing `RunningSummary` in `ConcurrentHashMap`. It use `Transaction#epochSecond` as key for lookup
    - If summary is already exist, proceed with update
    - Otherwise create `RunningSummary` for new transaction and put it in `ConcurrentHashMap`
- Request for latest statistics will be simple read from in memory statistics
    - Get current second since epoch as references
    - Loop trough 60 keys to read `RunningSummary` of last 60 seconds statistics, then combine them into single statistics

##### Complexity
- `GET /statistics` will have constant time and memory since it does not depend on number of transaction.
- It will involve `ConcurrentHashMap#get` for 60 times. `ConcurrentHashMap#get` is O(1). So time complexity will be O(1)
- Regardless of the number of transactions, at worst case we will only have to store 60 pairs of key–value to maintain our statistics. So space complexity will also be O(1)

##### Misc
- This implementation ignored old (stale) statistics. In the real world we should have a way to clean entry older than 60 seconds. Something like a simple `TimerTask`   

     
