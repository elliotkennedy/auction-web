package org.elkendev.service;

import org.elkendev.domain.Bid;
import org.elkendev.domain.Item;
import org.elkendev.exception.ItemNotFoundException;

import java.util.Collection;

public interface AuctionService {

    Collection<Bid> findAllBids(long id) throws ItemNotFoundException;

    Bid findHighestBid(long id) throws ItemNotFoundException;

    Item placeBid(long id, Bid bid) throws ItemNotFoundException;

    Collection<Item> findItemsForUser(String userName);

}
