package org.elkendev.repository;

import org.elkendev.domain.Bid;
import org.elkendev.domain.Item;

import java.util.Collection;

public interface ItemRepository {

    /**
     * Saves an item
     * @param item - item to save
     * @return the item
     */
    Item save(Item item);

    /**
     * Place a bid on an item with the given Id
     * @param id - the item id
     * @param bid - the bid to be placed
     */
    Item placeBid(long id, Bid bid);

    /**
     * Find an item by its Id, return null if not found
     * @param id - the item id
     * @return the item or null if not found
     */
    Item find(Long id);

    /**
     * Find all items which a user has bid on
     * @param userName - the username to search with
     * @return collection of items
     */
    Collection<Item> findItemsForUser(String userName);
}
