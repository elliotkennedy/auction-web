package org.elkendev.repository;

import org.elkendev.domain.Bid;
import org.elkendev.domain.Item;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {

    private AtomicLong idCounter = new AtomicLong(0);
    private Map<Long, Item> items = new ConcurrentHashMap<>();

    @Override
    public Item saveOrUpdate(Item item) {
        if (item.getId() == null) {
            item.setId(idCounter.incrementAndGet());
        }
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item find(Long id) {
        return items.get(id);
    }

    @Override
    public Collection<Item> findItemsForUser(String userName) {

        return items.values()
                .parallelStream()
                .filter(item -> containsBidsForUser(item.getBids(), userName))
                .collect(Collectors.toSet());

    }

    private boolean containsBidsForUser(Collection<Bid> bids, String userName) {
        for (Bid bid : bids) {
            if (bid.getUser().equals(userName)) {
                return true;
            }
        }
        return false;
    }

}
