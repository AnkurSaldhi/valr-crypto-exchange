package valr.orderbook;

import java.lang.Double;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class OrderBook
{
	private final Logger logger = LoggerFactory.getLogger(OrderBook.class);
	
    private String itemName;
    // Maps use List as value to hold multiple values with same hash
    private Map<Double, List<Order>> bidMap = null;
    private Map<Double, List<Order>> askMap = null;

    private Queue<Double> bidMaxPriceList = null;
    private Queue<Double> askMinPriceList = null;

    // Initializes marketplace
    public OrderBook(String name)
    {
        bidMap = new HashMap<Double, List<Order>>();
        askMap = new HashMap<Double, List<Order>>();

        bidMaxPriceList = new PriorityQueue<Double>(20, Collections.reverseOrder()); // top is maximum bid price
        askMinPriceList = new PriorityQueue<Double>();  // top is minimum ask price

        itemName = name;
    }

    /*  Adds buy to map by hashing the price, then
     *  adding buy to list located in that hash bucket
     */
    public void addBid(double price, double quantity)
    {
    	logger.info("addBid qty:{} price:{}", quantity, price);
        List<Order> bucket = getBucket(bidMap, price);
        Order newBid = new Order(price, quantity);
        bucket.add(newBid);
        bidMap.put(newBid.getPrice(), bucket);
        bidMaxPriceList.add(price);
        matchLimitOrders(MarketList.BuySide);
    }

    /*  Adds sell to map by hashing the price, then
     *  adding ask to list located in that hash bucket
     */
    public void addAsk(double price, double quantity)
    {
        List<Order> bucket = getBucket(askMap, price);
        Order newOffer = new Order(price, quantity);
    	logger.info("addAsk qty:{} price:{} size:{}", newOffer.getQuantity(), newOffer.getPrice(), bucket.size());
        bucket.add(newOffer);
    	logger.info("askMap after insert size : {}", bucket.size());
        askMap.put(newOffer.getPrice(), bucket);
        askMinPriceList.add(price);
        matchLimitOrders(MarketList.SellSide);
    }

    // Returns bucket list if price match, otherwise returns new list
    public List<Order> getBucket(Map<Double, List<Order>> hashmap, Double price)
    {
        List<Order> bucket;
        if(hashmap.containsKey(price))
        {
            bucket = hashmap.get(price);
        }
        else
        {
            bucket = new LinkedList<Order>();
        }
        return bucket;
    }

    // Matches asks and bids based on price priority
    public void matchLimitOrders(String takerSide)
    {
        List<Order> bidBucket = null;
        List<Order> askBucket = null;
        Double lowestAsk = null;
        Double highestBid = null;
        boolean finished = false;

        while(!finished)
        {
            // Peek because we don't want to remove the top element until the order is closed
            highestBid = bidMaxPriceList.peek();
            lowestAsk = askMinPriceList.peek();

            // No possible trade if below condition is true
            if(lowestAsk == null || highestBid == null || lowestAsk > highestBid)
            {
                finished = true;
            	logger.info("OrderBook matchOrders finished!");
            }
            else
            {
                // Gets buckets for both maps
                bidBucket = bidMap.get(bidMaxPriceList.peek());
                askBucket = askMap.get(askMinPriceList.peek());

                // Gets first element from each bucket since they're the oldest
                double bidQuantity = bidBucket.get(0).getQuantity();
                double askQuantity = askBucket.get(0).getQuantity();

                if(bidQuantity > askQuantity)
                {
                    tradeWhenBidQuantityMoreThanAsk(takerSide, bidBucket, askBucket, lowestAsk, bidQuantity, askQuantity);
                }
                else if(askQuantity > bidQuantity)
                {
                    tradeWhenAskQuantityMoreThanBid(takerSide, bidBucket, askBucket, lowestAsk, bidQuantity, askQuantity);
                }
                else
                {
                    // bidQuantity is an arbitrary choice because both quantities are equal.
                    // lowestAsk is chosen because it's the price at which the trade is made.
                    tradeWhenAskQuantityEqualsBid(takerSide, bidBucket, askBucket, lowestAsk, bidQuantity);
                }
            }
        }
    }

    private void tradeWhenAskQuantityEqualsBid(String takerSide, List<Order> bidBucket, List<Order> askBucket, Double lowestAsk, double bidQuantity) {
        logger.info("bidQuantity = askQuantity");
        System.out.println(successfulTrade(bidQuantity, lowestAsk, takerSide));
        // Removes bid and offer because they're both closed
        bidBucket.remove(0);
        bidMaxPriceList.remove();
        askBucket.remove(0);
        askMinPriceList.remove();
    }

    private void tradeWhenBidQuantityMoreThanAsk(String takerSide, List<Order> bidBucket, List<Order> askBucket, Double lowestAsk, double bidQuantity, double askQuantity) {
        logger.info("bidQuantity > askQuantity");
        System.out.println(successfulTrade(askQuantity, lowestAsk, takerSide));

        // Decrements quantity in bid
        bidQuantity -= askQuantity;
        bidBucket.get(0).setQuantity(bidQuantity);
        logger.info("bidQuantity remaining qty : {}", bidQuantity);

        // Closes previous offer
        askBucket.remove(0);
        askMinPriceList.remove();
    }

    private void tradeWhenAskQuantityMoreThanBid(String takerSide, List<Order> bidBucket, List<Order> askBucket, Double lowestAsk, double bidQuantity, double offerQuantity) {
        logger.info("bidQuantity < askQuantity");
        System.out.println(successfulTrade(bidQuantity, lowestAsk, takerSide));

        // Decrements quantity in offer
        offerQuantity -= bidQuantity;
        askBucket.get(0).setQuantity(offerQuantity);
        logger.info("askQuantity remaining qty : {}", offerQuantity);

        //  Closes previous bid
        bidBucket.remove(0);
        bidMaxPriceList.remove();
    }

    // Returns the string printed for a successful trade.
    public String successfulTrade(double quantity, double price, String takerSide)
    {
    	logger.info("successfulTrade bidQuantity:{}, lowestAsk:{}", quantity, price);
        MarketList.addTrade(itemName,quantity,price,takerSide);

        return quantity + " at price:" + price + " per token";
    }

    public Map<Double, List<Order>> getBidMap()
    {
        return bidMap;
    }

    public Map<Double, List<Order>> getAskMap()
    {
        return askMap;
    }
}
