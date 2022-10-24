package valr.orderbook;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestOrder
{
    private Order order;

    @Test
    public void getPriceShouldReturnCorrectPrice()
    {
        order = new Order(12.0, 1);
                                              // margin of error
        assertEquals(12.0, order.getPrice(), .00001);
    }

    @Test
    public void setPriceShouldCorrectlySetPrice()
    {
        order = new Order(12.0, (float) 30.68);
        order.setPrice(30.68);
        assertEquals(30.68, order.getPrice(), .00001);
    }

    @Test
    public void getQuantityShouldReturnCorrectQuantity()
    {
        order = new Order(12.0, (float) 10.51);
        assertEquals(10.51, order.getQuantity(),0.00001);
    }

    @Test
    public void setQuantityShouldCorrectlySetQuantity()
    {
        order = new Order(12.0, (float) 12.335);
        order.setQuantity(5);
        assertEquals(5, order.getQuantity(),0.00001);
    }

}
