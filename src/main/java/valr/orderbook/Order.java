package valr.orderbook;

import lombok.Data;

import java.util.List;

@Data
public class Order
{
    private double price;
    private double quantity;
    private int order_count;

    public Order(double price, double quantity)
    {
        this.price = price;
        this.quantity = quantity;
    }

    //sums up the quantity of limit orders for the same price
    static float getOrderSumQuantity(List<Order> v) {
        float sum = 0L;
        for(Order order: v){
            sum += order.getQuantity();
        }
        return sum;
    }
}
