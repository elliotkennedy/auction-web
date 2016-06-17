package org.elkendev.service;

import org.elkendev.domain.Bid;
import org.elkendev.domain.Item;
import org.elkendev.exception.ItemNotFoundException;
import org.elkendev.repository.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AuctionServiceImplTest {

    private AuctionService onTest;
    private ItemRepository itemRepository;

    @Before
    public void setup() throws Exception {

        itemRepository = mock(ItemRepository.class);
        onTest = new AuctionServiceImpl(itemRepository);

    }

    @Test
    public void testFindAllBids() throws Exception {

        Item item = new Item("blah");
        item.setId(1L);
        item.addBid(new Bid("user1", new BigDecimal("10.99")));
        item.addBid(new Bid("user2", new BigDecimal("20.99")));

        given(itemRepository.find(eq(1L))).willReturn(item);

        Collection<Bid> bids = onTest.findAllBids(item.getId());

        assertThat(bids).hasSize(2);

        Iterator<Bid> iterator = bids.iterator();
        Bid bid1 = iterator.next();
        assertThat(bid1.getUser()).isEqualTo("user1");
        assertThat(bid1.getAmount()).isEqualTo(new BigDecimal("10.99"));

        Bid bid2 = iterator.next();
        assertThat(bid2.getUser()).isEqualTo("user2");
        assertThat(bid2.getAmount()).isEqualTo(new BigDecimal("20.99"));

    }

    @Test(expected = ItemNotFoundException.class)
    public void testFindAllBids_whenNoItem_throwItemNotFoundException() throws Exception {

        given(itemRepository.find(eq(1L))).willReturn(null);

        onTest.findAllBids(1L);

    }

    @Test
    public void testFindHighestBid() throws Exception {

        Item item = new Item("blah");
        item.setId(1L);
        item.addBid(new Bid("user1", new BigDecimal("10.99")));
        item.addBid(new Bid("user2", new BigDecimal("20.99")));

        given(itemRepository.find(eq(1L))).willReturn(item);

        Bid highestBid = onTest.findHighestBid(item.getId());

        assertThat(highestBid.getUser()).isEqualTo("user2");
        assertThat(highestBid.getAmount()).isEqualTo(new BigDecimal("20.99"));
    }

    @Test(expected = ItemNotFoundException.class)
    public void testFindHighestBid_whenNoItem_throwItemNotFoundException() throws Exception {

        given(itemRepository.find(eq(1L))).willReturn(null);

        onTest.findHighestBid(-1L);

    }

    @Test
    public void testFindHighestBid_returnNullWhenNoBids() throws Exception {

        Item item = new Item("noBidItem");
        item.setId(2L);
        given(itemRepository.find(eq(2L))).willReturn(item);

        Bid highestBid = onTest.findHighestBid(2L);

        assertThat(highestBid).isNull();

    }

    @Test
    public void testPlaceBid() throws Exception {

        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);

        Item item = new Item("desc");
        item.setId(1L);
        given(itemRepository.find(eq(1L))).willReturn(item);

        onTest.placeBid(1L, new Bid("user1", new BigDecimal("1.00")));

        verify(itemRepository).saveOrUpdate(captor.capture());
        Item itemWithBid = captor.getValue();
        assertThat(itemWithBid.getBids()).hasSize(1);
        Bid bid = itemWithBid.getBids().iterator().next();
        assertThat(bid.getUser()).isEqualTo("user1");
        assertThat(bid.getAmount()).isEqualTo(new BigDecimal("1.00"));

    }

    @Test(expected = ItemNotFoundException.class)
    public void testPlaceBid_whenNoItem_throwItemNotFoundException() throws Exception {

        given(itemRepository.find(eq(1L))).willReturn(null);

        onTest.placeBid(1L, new Bid("user", new BigDecimal("10.99")));

    }

    @Test
    public void testFindItemsForUser() throws Exception {

        Item item = new Item("item1");
        item.addBid(new Bid("user", new BigDecimal("1.00")));
        Item item2 = new Item("item2");
        item2.addBid(new Bid("user", new BigDecimal("1.00")));

        given(itemRepository.findItemsForUser(eq("user"))).willReturn(Arrays.asList(item, item2));

        Collection<Item> itemsForUser = onTest.findItemsForUser("user");

        assertThat(itemsForUser).hasSize(2);
        assertThat(itemsForUser).contains(item, item2);

    }

}