package valr.orderbook;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import org.codehaus.jackson.map.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TestMarketController {

    @Autowired
    private MockMvc mvc;
    public static String valrToken="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ2YWxydXNlcjk5OSIsImV4cCI6MTY5ODEzOTk4NSwiaWF0IjoxNjY2NjAzOTg1fQ.WjYcG4tS6P6WWhX6PoVQHPvP3c7fvUezS6ZJiHRKIi3LvmWMUCMmKOIxMbzW2BrmO4XU3N9LcwNTZarac6s2qA";
    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void addLimitBuyOrder() throws Exception {
        OrderItemDao marketItemDao = new OrderItemDao();
        marketItemDao.setName("btcinr");
        marketItemDao.setPrice(148.78);
        marketItemDao.setQty(25);
        mvc.perform(MockMvcRequestBuilders.post("/limit/buy/order")
                .content(asJsonString(marketItemDao))
                .header("X-VALRToken",valrToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("success: bid added.")));
    }

    @Test
    public void addLimitSellOrder() throws Exception {
        OrderItemDao marketItemDao = new OrderItemDao();
        marketItemDao.setName("btcinr");
        marketItemDao.setPrice(158.78);
        marketItemDao.setQty((float) 20.53);
        mvc.perform(MockMvcRequestBuilders.post("/limit/sell/order")
                .content(asJsonString(marketItemDao)).header("X-VALRToken",valrToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("success: ask added.")));
    }

    @Test
    public void verifyAuthOnLimitOrderWithoutToken() throws Exception {
        OrderItemDao marketItemDao = new OrderItemDao();
        marketItemDao.setName("btcinr");
        marketItemDao.setPrice(158.78);
        marketItemDao.setQty(20);
        mvc.perform(MockMvcRequestBuilders.post("/limit/buy/order")
                        .content(asJsonString(marketItemDao))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void verifyOrderBookApi() throws Exception {
        OrderItemDao marketItemDao = new OrderItemDao();
        marketItemDao.setName("btcinr");
        marketItemDao.setPrice(158.78);
        marketItemDao.setQty(20);
        mvc.perform(MockMvcRequestBuilders.post("/limit/buy/order")
                        .content(asJsonString(marketItemDao))
                        .header("X-VALRToken",valrToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("success: bid added.")));

        mvc.perform(MockMvcRequestBuilders.get("/market/btcinr/orderbook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }

    @Test
    public void verifyTradeHistoryApi() throws Exception {
        OrderItemDao bidMarketItemDao = new OrderItemDao();
        bidMarketItemDao.setName("btcinr");
        bidMarketItemDao.setPrice(158.78);
        bidMarketItemDao.setQty(20);
        mvc.perform(MockMvcRequestBuilders.post("/limit/buy/order")
                        .content(asJsonString(bidMarketItemDao))
                        .header("X-VALRToken",valrToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("success: bid added.")));

        OrderItemDao sellMarketItemDao = new OrderItemDao();
        sellMarketItemDao.setName("btcinr");
        sellMarketItemDao.setPrice(158.78);
        sellMarketItemDao.setQty(20);
        mvc.perform(MockMvcRequestBuilders.post("/limit/sell/order")
                        .content(asJsonString(sellMarketItemDao))
                        .header("X-VALRToken",valrToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("success: ask added.")));

        mvc.perform(MockMvcRequestBuilders.get("/market/btcinr/tradehistory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
