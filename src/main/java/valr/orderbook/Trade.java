package valr.orderbook;

import java.time.Instant;
import java.util.UUID;

//Once the trade happened, it is not going to change, so create a record
public record Trade
        (double price, double quantity, String market, Instant tradedAt, String side, int sequenceId, UUID id) {
}


