package valr.orderbook;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Double;
import java.util.*;


@Data
public class MarketList
{
    static String BuySide="buy";
    static String SellSide="sell";
    private final Logger logger = LoggerFactory.getLogger(MarketList.class);
    private Map<String, OrderBook> orderBooks = null;
    private static Map<String, TradeHistory> tradeHistory = null;

    public MarketList()
    {
        //initialized orderbook and tradehistory maps
        orderBooks = new HashMap<String, OrderBook>();
        tradeHistory = new HashMap<String, TradeHistory>();
    }

    public List<String> GetList()
    {
      List<String> list = new ArrayList<String>(orderBooks.keySet());  
      return list;
    }

    public void Add(String name)
    {
      if (!orderBooks.containsKey(name)) {
          orderBooks.put(name, new OrderBook(name));
      }
      if (!tradeHistory.containsKey(name)) {
            tradeHistory.put(name, new TradeHistory(name));
        }
    }

    public void AddBuyOrder(OrderItemDao buy)
    {
    	if (orderBooks.containsKey(buy.getName())) {
    		OrderBook book = orderBooks.get(buy.getName());
    		book.addBid(buy.getPrice(), buy.getQty());
    	}
    }

    public void AddSellOrder(OrderItemDao sell)
    {
    	if (orderBooks.containsKey(sell.getName())) {
    		OrderBook book = orderBooks.get(sell.getName());
    		book.addAsk(sell.getPrice(), sell.getQty());
    	}
    }

    public Map<Double, List<Order>> GetBuyMap(String market)
    {
    	logger.info("MarketList GetBuyMap market : {}", market);
    	
    	if (orderBooks.containsKey(market)) {
    		OrderBook book = orderBooks.get(market);
            return book.getBidMap();
    	}
    	return new HashMap<Double, List<Order>>();
    }

    public Map<Double, List<Order>> GetSellMap(String market)
    {
    	logger.info("MarketList GetSellMap name : {}", market);

    	if (orderBooks.containsKey(market)) {
    		OrderBook book = orderBooks.get(market);

            return book.getAskMap();
    	}
        return new HashMap<Double, List<Order>>();
    }

    public List<Trade> GetRecentTrades(String marketName)
    {
        logger.info("MarketList GetRecentTrades market : {}", marketName);

        if (tradeHistory.containsKey(marketName)) {
            TradeHistory history = tradeHistory.get(marketName);

            return history.getTradeList();
        }
        return new ArrayList<>();
    }

    //this method will add trade to the tradeHistory corresponding to the market
    public static void addTrade(String market, double quantity, double price, String takerSide)
    {
        TradeHistory history = tradeHistory.get(market);
        history.addSuccessfulTrade(market, quantity, price, takerSide);
    }
  }
