package valr.orderbook;

import lombok.Data;

@Data
public class OrderItemDao {
    private String name;
    private double price;
    private double qty;
}
