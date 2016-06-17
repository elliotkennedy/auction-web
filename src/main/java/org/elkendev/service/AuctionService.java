package org.elkendev.service;

import org.elkendev.domain.Bid;
import org.elkendev.domain.Item;
import org.elkendev.exception.ItemNotFoundException;

import java.util.Collection;

public interface AuctionService {

    Collection<Bid> findAllBids(Long id) throws ItemNotFoundException;

    Bid findHighestBid(Long id) throws ItemNotFoundException;

    void placeBid(Long id, Bid bid) throws ItemNotFoundException;

    Collection<Item> findItemsForUser(String userName);

}
