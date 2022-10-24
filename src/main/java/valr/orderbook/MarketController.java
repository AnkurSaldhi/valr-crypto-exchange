package valr.orderbook;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.lang.Double;

import java.util.*;
import java.util.List; import java.util.stream.Collectors;

import static valr.orderbook.JwtUtil.validateAuthenticatedRequest;

@RestController
public class MarketController
{
	private final Logger logger = LoggerFactory.getLogger(MarketController.class);
    private final MarketList marketList;

	@Autowired
	private HttpServletRequest request;

    // Initializes marketplace
    public MarketController()
    {
        marketList = new MarketList();
    }

    @RequestMapping(value = "/limit/{marketSide}/order", method = RequestMethod.POST,consumes="application/json")
  	public ResponseEntity<String> AddLimitOrder(@RequestBody OrderItemDao orderItem, @PathVariable(value="marketSide") String marketSide) {
		boolean validUser = validateAuthenticatedRequest(request.getHeader("X-VALRToken"));
		if(!validUser){
			return new ResponseEntity<String>("failed: Invalid ValrToken", HttpStatus.UNAUTHORIZED);
		}
		if(Objects.equals(marketSide, MarketList.BuySide)){
			logger.info("MarketController bid name:{} price:{} qty:{}", orderItem.getName(),orderItem.getPrice(),orderItem.getQty());
			marketList.Add(orderItem.getName());
			marketList.AddBuyOrder(orderItem);
  		return new ResponseEntity<String>("success: bid added.", HttpStatus.OK);
		} else if (Objects.equals(marketSide, MarketList.SellSide)){
			logger.info("MarketController ask name:{} price:{} qty:{}", orderItem.getName(),orderItem.getPrice(),orderItem.getQty());
			marketList.Add(orderItem.getName());
			marketList.AddSellOrder(orderItem);
			return new ResponseEntity<String>("success: ask added.", HttpStatus.OK);
		}
		return new ResponseEntity<String>("failed: Invalid side", HttpStatus.BAD_REQUEST);

	}

    @GetMapping(value = "/market/{market}/orderbook",consumes="application/json")
  	public ResponseEntity<Map<String, List<Order>>> GetMarketOffer(@PathVariable(value="market") String market) {
		logger.info("MarketController Get OrderBook market : {}", market);
		HashMap<String, List<Order>> orderBook = new HashMap<String, List<Order>>();

		//Bids
		Map<Double, List<Order>> bidOrderBook = marketList.GetBuyMap(market);
		List<Order> cumulativeOrderbook = new ArrayList<>();
		for (Map.Entry<Double, List<Order>> doubleListEntry : bidOrderBook.entrySet()) {
			if (doubleListEntry.getValue().isEmpty())
					continue;
			Order order = new Order(doubleListEntry.getKey(), Order.getOrderSumQuantity(doubleListEntry.getValue()));
			order.setOrder_count(doubleListEntry.getValue().size());
			cumulativeOrderbook.add(order);
		}
		List<Order> flattenedBidList = cumulativeOrderbook.stream().sorted(Comparator.comparing(Order::getPrice, Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
		orderBook.put("Bids", flattenedBidList);

		//Asks
		Map<Double, List<Order>> askOrderBook = marketList.GetSellMap(market);
		cumulativeOrderbook = new ArrayList<>();
		for (Map.Entry<Double, List<Order>> doubleListEntry : askOrderBook.entrySet()) {
			if (doubleListEntry.getValue().isEmpty())
				continue;
			Order order = new Order(doubleListEntry.getKey(), Order.getOrderSumQuantity(doubleListEntry.getValue()));
			order.setOrder_count(doubleListEntry.getValue().size());
			cumulativeOrderbook.add(order);
		}
		List<Order> flattenedAskList = cumulativeOrderbook.stream().sorted(Comparator.comparing(Order::getPrice)).collect(Collectors.toList());
		orderBook.put("Asks", flattenedAskList);

		return new ResponseEntity<Map<String, List<Order>>>(orderBook, HttpStatus.OK);
  	}


	//Returns the tradeHistory of the recent records for a particular market
	@GetMapping(value = "/market/{market}/tradehistory", consumes="application/json")
	public ResponseEntity<List<Trade>> GetTrades(@PathVariable(value="market") String market) {
		logger.info("MarketController Get OrderBook name : {}", market);

		List<Trade> trades = marketList.GetRecentTrades(market);
		return new ResponseEntity<List<Trade>>(trades, HttpStatus.OK);
	}

  }
