package valr.orderbook;

import java.lang.Double;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

public class TradeHistory {
    private final Logger logger = LoggerFactory.getLogger(OrderBook.class);

    private final String itemName;
    // Maps use List as value to hold multiple values with same hash
    private final List<Trade> trades = new ArrayList<Trade>();
    private AtomicInteger sequenceId = null;

    // Initializes marketplace
    public TradeHistory(String name) {
        itemName = name;
        sequenceId = new AtomicInteger();
    }


    // Returns bucket list if price match, otherwise returns new list
    public List<Order> getBucket(Map<Double, List<Order>> hashmap, Double price) {
        List<Order> bucket;
        if (hashmap.containsKey(price)) {
            bucket = hashmap.get(price);
        } else {
            bucket = new LinkedList<Order>();
        }
        return bucket;
    }

    // Returns the string printed for a successful trade.
    public void addSuccessfulTrade(String market, double quantity, double price, String takerSide) {
        logger.info("successfulTrade bidQuantity : {}", quantity);
        logger.info("successfulTrade lowestOffer : {}", price);
        trades.add(new Trade(price, quantity, market, Instant.now(), takerSide, sequenceId.incrementAndGet(), UUID.randomUUID()));
    }


    public List<Trade> getTradeList(int limit) {
        if (CollectionUtils.isEmpty(trades)) {
            return Collections.emptyList();
        }
        trades.sort((t1, t2) -> t2.sequenceId() - t1.sequenceId());
        return trades.subList(0, Math.min(trades.size(), limit));
    }

    public List<Trade> getTradeList() {
        //20 recent trades only if we have otherwise all
        return getTradeList(20);
    }

}
