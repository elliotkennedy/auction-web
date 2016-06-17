package org.elkendev.integ;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elkendev.AuctionWebApplication;
import org.elkendev.domain.Bid;
import org.elkendev.domain.Item;
import org.elkendev.exception.ItemNotFoundException;
import org.elkendev.repository.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AuctionWebApplication.class)
@RunWith(SpringRunner.class)
public class BiddingIntegrationTest {

    private MockMvc mockMvc;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper mapper;

    @Before
    public void setup() throws Exception {

        Item item = new Item("description");
        item.setId(1L);

        itemRepository.saveOrUpdate(item);

        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

    }

    /**
     * This test proves multiple bids can be placed concurrently,
     * It should fail if you remove the synchronized statement in AuctionServiceImpl
     * @throws Exception
     */
    @Test
    public void testPlacingBidsConcurrently() throws Exception {

        int expectedNumberOfBids = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

        List<Callable<Void>> callables = new ArrayList<>();
        for (int i = 0; i < expectedNumberOfBids; i++) {
            Bid bid = new Bid("user" + i, new BigDecimal(i + ".00"));
            callables.add(() -> {
                try {
                    mockMvc.perform(post("/item/{id}/bid", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(bid)))
                            .andExpect(status().isOk());
                } catch (ItemNotFoundException e) {
                    fail();
                }
                return null;
            });
        }

        List<Future<Void>> futures = executorService.invokeAll(callables);

        // wait until all calls complete
        for (Future<Void> future : futures) {
            future.get();
        }

        // assert all bids have been placed
        MvcResult mvcResult = mockMvc.perform(get("/item/{id}/bids", 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<Bid> resultingBids = mapper.readValue(mvcResult.getResponse().getContentAsString(), mapper.getTypeFactory().constructCollectionType(List.class, Bid.class));

        assertThat(resultingBids).hasSize(expectedNumberOfBids);

    }

}
