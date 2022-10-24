package valr.orderbook;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestTrade
{
    private OrderBook market;
    private MarketList marketList;

    @Before
    public void initMarket()
    {
        market = new OrderBook("btcinr");
        marketList = new MarketList();
        marketList.Add("btcinr");
    }

    /*******************************************
     *
     *   addBuy Tests
     *
     *******************************************/

    @Test
    public void noTradesWhenOnlySingleBuyOrder()
    {
        initMarket();
        assertTrue(market.getBidMap().isEmpty());
        market.addBid(12.0, 1);
        assertEquals(0, marketList.GetRecentTrades(market.getItemName()).size());
    }

    @Test
    public void noTradesWhenDuplicateBuyOrder()
    {
        initMarket();
        assertTrue(market.getBidMap().isEmpty());
        market.addBid(12.0, 1);
        market.addBid(12.0, 2);
        assertEquals(0, marketList.GetRecentTrades(market.getItemName()).size());

    }

    /*******************************************
     *
     *   addAsk Tests
     *
     *******************************************/

    @Test
    public void noTradesWhenOnlySingleSellOrder()
    {
        initMarket();
        assertTrue(market.getAskMap().isEmpty());
        market.addAsk(12.0, 1);
        assertEquals(0, marketList.GetRecentTrades(market.getItemName()).size());

    }

    @Test
    public void noTradesWhenDuplicateSellOrder()
    {
        initMarket();
        assertTrue(market.getAskMap().isEmpty());
        market.addAsk(12.0, 1);
        market.addAsk(12.0, 2);
        assertEquals(0, marketList.GetRecentTrades(market.getItemName()).size());
    }



    /*******************************************
     *
     *   matchOrders Tests
     *
     *******************************************/

    @Test
    public void CorrectTradesShouldHappenWhenBuyQuantityGreaterThanSellQuantity()
    {
        initMarket();

        market.addAsk(12.0, 6);
        market.addBid(12.0, 9);
        assertEquals(1, marketList.GetRecentTrades(market.getItemName()).size());
        // Offer correctly closed
    }

    @Test
    public void SellQuantityShouldCorrectlyDecrementWhenGreaterThanBidQuantity()
    {
        initMarket();
        market.addBid(12.0, 5);
        market.addAsk(12.0, 10);
        assertEquals(1, marketList.GetRecentTrades(market.getItemName()).size());

    }

    @Test
    public void TradeCountWhenBothBuyAndSellAreEqualQuantity()
    {
        initMarket();
        market.addBid(12.0, 5);
        market.addAsk(12.0, 5);
        assertEquals(1, marketList.GetRecentTrades(market.getItemName()).size());
    }

    @Test
    public void MultipleTradesWithSingleBuyOrder()
    {
        initMarket();
        market.addAsk(50.0, 10);
        market.addAsk(60.0, 5);
        market.addBid(70.0, 18);
        assertEquals(2, marketList.GetRecentTrades(market.getItemName()).size());
    }

    @Test
    public void MultipleTradesWithSingleSellOrder()
    {
        initMarket();
        market.addBid(20.0, 5);
        market.addBid(15.0, 3);
        market.addAsk(10.0, 8);
        assertEquals(2, marketList.GetRecentTrades(market.getItemName()).size());
    }

}
