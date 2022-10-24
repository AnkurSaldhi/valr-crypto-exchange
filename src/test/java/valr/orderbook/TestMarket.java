package valr.orderbook;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestMarket
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
    public void addNewBidShouldCorrectlyAddNewBid()
    {
        initMarket();
        assertTrue(market.getBidMap().isEmpty());
        market.addBid(12.0, 1);
        assertTrue(market.getBidMap().containsKey(12.0));
    }

    @Test
    public void addDuplicateBidPriceShouldCorrectlyAddNewBid()
    {
        initMarket();
        assertTrue(market.getBidMap().isEmpty());
        market.addBid(12.0, 1);
        market.addBid(12.0, 2);

        // Checks to see if corresponding elements have same quantities.
        assertEquals(1, market.getBidMap().get(12.0).get(0).getQuantity(),0.00001);
        assertEquals(2, market.getBidMap().get(12.0).get(1).getQuantity(), 0.00001);
    }

    /*******************************************
     *
     *   addSell Tests
     *
     *******************************************/

    @Test
    public void addNewOfferShouldCorrectlyAddNewOffer()
    {
        initMarket();
        assertTrue(market.getAskMap().isEmpty());
        market.addAsk(12.0, 1);
        assertTrue(market.getAskMap().containsKey(12.0));
    }

    @Test
    public void addDuplicateOfferPriceShouldCorrectlyAddNewOffer()
    {
        initMarket();
        assertTrue(market.getAskMap().isEmpty());
        market.addAsk(12.0, 1);
        market.addAsk(12.0, 2);

        // Checks to see if corresponding elements have same quantities.
        assertEquals(1, market.getAskMap().get(12.0).get(0).getQuantity(),0.00001);
        assertEquals(2, market.getAskMap().get(12.0).get(1).getQuantity(),0.00001);
    }

    /*******************************************
     *
     *   getBucket Test
     *
     *******************************************/

    @Test
    public void getBucketShouldReturnCorrectList()
    {
        initMarket();
        assertTrue(market.getAskMap().isEmpty());
        market.addAsk(12.0, 1);
        market.addAsk(12.0, 2);

        // Checks to see if corresponding bucket elements have same quantities.
        assertEquals(1, market.getBucket(market.getAskMap(), 12.0).get(0).getQuantity(),0.00001);
        assertEquals(2, market.getBucket(market.getAskMap(), 12.0).get(1).getQuantity(),0.00001);
    }

    /*******************************************
     *
     *   matchOrders Tests
     *
     *******************************************/

    @Test
    public void BidQuantityShouldCorrectlyDecrementWhenGreaterThanOfferQuantity()
    {
        initMarket();

        market.addAsk(12.0, 6);
        market.addBid(12.0, 9);
        assertEquals(3, market.getBidMap().get(12.0).get(0).getQuantity(),0.00001);  // Bid correctly decremented
        assertTrue(market.getAskMap().get(12.0).isEmpty());  // Offer correctly closed
    }

    @Test
    public void OfferQuantityShouldCorrectlyDecrementWhenGreaterThanBidQuantity()
    {
        initMarket();
        market.addBid(12.0, 5);
        market.addAsk(12.0, 10);
        market.matchLimitOrders(MarketList.SellSide);
        assertEquals(5, market.getAskMap().get(12.0).get(0).getQuantity(),0.00001);  // Offer correctly decremented
        assertTrue(market.getBidMap().get(12.0).isEmpty());  // Bid correctly closed
    }

    @Test
    public void BothQuantitiesEqualShouldCorrectlyRemoveBoth()
    {
        initMarket();
        market.addBid(12.0, 5);
        market.addAsk(12.0, 5);
        market.matchLimitOrders(MarketList.SellSide);
        assertTrue(market.getBidMap().get(12.0).isEmpty());   // Bid correctly closed
        assertTrue(market.getAskMap().get(12.0).isEmpty()); // Offer correctly closed
    }

    @Test
    public void BidWithValueAndNoOffersShouldStayTheSame()
    {
        initMarket();
        market.addBid(12.0, 5);
        market.matchLimitOrders(MarketList.BuySide);
        assertEquals(5, market.getBidMap().get(12.0).get(0).getQuantity(),0.00001);   // Bid still has same value
        assertNull(market.getAskMap().get(12.0)); // Offer still null
    }

    @Test
    public void OfferWithValueAndNoBidsShouldStayTheSame()
    {
        initMarket();
        market.addAsk(12.0, 5);
        market.matchLimitOrders(MarketList.SellSide);
        assertEquals(5, market.getAskMap().get(12.0).get(0).getQuantity(),0.00001);   // Offer still has same value
        assertNull(market.getBidMap().get(12.0)); // Bid still null
    }

    @Test
    public void OfferPriceHigherThanBidPriceShouldStayTheSame()
    {
        initMarket();
        market.addAsk(12.0, 7);
        market.addBid(6.0, 5);
        market.matchLimitOrders(MarketList.BuySide);
        assertEquals(7, market.getAskMap().get(12.0).get(0).getQuantity(),0.00001);   // Offer still has same value
        assertEquals(5, market.getBidMap().get(6.0).get(0).getQuantity(),0.00001);   // Bid still has same value
    }
}
