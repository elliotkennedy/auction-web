package org.elkendev.service;

import org.elkendev.domain.Bid;
import org.elkendev.domain.Item;
import org.elkendev.exception.ItemNotFoundException;
import org.elkendev.repository.ItemRepository;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class AuctionServiceImpl implements AuctionService {

    private ItemRepository itemRepository;

    public AuctionServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Collection<Bid> findAllBids(Long id) throws ItemNotFoundException {
        return findItem(id).getBids();
    }

    @Override
    public Bid findHighestBid(Long id) throws ItemNotFoundException {
        return findItem(id).getHighestBid();
    }

    /**
     * In a persistent application you could use a database to avoid synchronization here
     * see BiddingIntegrationTest to prove monitor is required
     */
    @Override
    public synchronized void placeBid(Long id, Bid bid) throws ItemNotFoundException {
        Item item = findItem(id);
        item.addBid(bid);
        itemRepository.saveOrUpdate(item);
    }

    @Override
    public Collection<Item> findItemsForUser(String userName) {
        return itemRepository.findItemsForUser(userName);
    }

    private Item findItem(Long id) throws ItemNotFoundException {
        Item item = itemRepository.find(id);
        if (item == null) {
            throw new ItemNotFoundException(id);
        }
        return item;
    }
}
