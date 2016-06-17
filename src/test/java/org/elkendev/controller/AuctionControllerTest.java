package org.elkendev.controller;

import org.elkendev.domain.Bid;
import org.elkendev.domain.Item;
import org.elkendev.exception.ItemNotFoundException;
import org.elkendev.service.AuctionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuctionController.class)
@RunWith(SpringRunner.class)
public class AuctionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuctionService auctionService;

    @Test
    public void testPlaceBid() throws Exception {

        ArgumentCaptor<Bid> captor = ArgumentCaptor.forClass(Bid.class);

        String bidJson = "{\"user\":\"user1\",\"amount\":0.99}";

        mockMvc.perform(post("/item/{id}/bid", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bidJson))
                .andExpect(status().isOk());

        verify(auctionService).placeBid(eq(1L), captor.capture());
        Bid bid = captor.getValue();
        assertThat(bid.getAmount()).isEqualTo(new BigDecimal("0.99"));
        assertThat(bid.getUser()).isEqualTo("user1");

    }

    @Test
    public void testPlaceBid_return404WhenItemNotFound() throws Exception {

        willThrow(new ItemNotFoundException(1L)).given(auctionService).placeBid(eq(1L), any(Bid.class));

        String bidJson = "{\"user\":\"user1\",\"amount\":10.99}";

        mockMvc.perform(post("/item/{id}/bid", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bidJson))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testListBids() throws Exception {

        Collection<Bid> bids = Arrays.asList(new Bid("user1", new BigDecimal("10.99")), new Bid("user2", new BigDecimal("9.01")));

        given(auctionService.findAllBids(eq(1L))).willReturn(bids);

        String expected = "[{\"user\":\"user1\",\"amount\":10.99},{\"user\":\"user2\",\"amount\":9.01}]";

        mockMvc.perform(get("/item/{id}/bids", 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));

    }

    @Test
    public void testListBids_return404WhenItemNotFound() throws Exception {

        given(auctionService.findAllBids(eq(1L))).willThrow(new ItemNotFoundException(1L));

        mockMvc.perform(get("/item/{id}/bids", 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testGetHighestBid() throws Exception {

        given(auctionService.findHighestBid(eq(1L))).willReturn(new Bid("user1", new BigDecimal("0.99")));

        String expected = "{\"user\":\"user1\",\"amount\":0.99}";

        mockMvc.perform(get("/item/{id}/bid", 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));

    }

    @Test
    public void testGetHighestBid_return404WhenItemNotFound() throws Exception {

        given(auctionService.findHighestBid(eq(1L))).willThrow(new ItemNotFoundException(1L));

        mockMvc.perform(get("/item/{id}/bid", 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testFindItemsForUser() throws Exception {

        Item item1 = new Item("desc1");
        item1.setId(1L);
        item1.addBid(new Bid("user1", new BigDecimal("1.00")));
        item1.addBid(new Bid("user2", new BigDecimal("2.00")));

        Item item2 = new Item("desc2");
        item2.setId(2L);
        item2.addBid(new Bid("user3", new BigDecimal("3.00")));
        item2.addBid(new Bid("user4", new BigDecimal("4.00")));

        String expected = "[{\"id\":1,\"description\":\"desc1\",\"bids\":[{\"user\":\"user1\",\"amount\":1.00},{\"user\":\"user2\",\"amount\":2.00}]}," +
                "{\"id\":2,\"description\":\"desc2\",\"bids\":[{\"user\":\"user3\",\"amount\":3.00},{\"user\":\"user4\",\"amount\":4.00}]}]";

        given(auctionService.findItemsForUser(eq("user1"))).willReturn(Arrays.asList(item1, item2));

        mockMvc.perform(get("/items/user/{id}", "user1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));

    }

}
