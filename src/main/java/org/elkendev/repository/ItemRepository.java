package org.elkendev.repository;

import org.elkendev.domain.Item;

import java.util.Collection;

public interface ItemRepository {

    /**
     * Saves an item or updates if the item does not have an Id
     * @param item
     * @return the updated item
     */
    Item saveOrUpdate(Item item);

    /**
     * Find an item by its Id, return null if not found
     * @param id
     * @return the item or null if not found
     */
    Item find(Long id);

    /**
     * Find all items which a user has bid on
     * @param userName
     * @return collection of items
     */
    Collection<Item> findItemsForUser(String userName);
}
