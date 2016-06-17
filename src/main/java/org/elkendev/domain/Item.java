package org.elkendev.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

public class Item {

    private Long id;
    private final String description;
    private final SortedSet<Bid> bids = new TreeSet<>();

    public Item(String description) {
        this.description = description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void addBid(Bid bid) {
        bids.add(bid);
    }

    @JsonIgnore
    public Bid getHighestBid() {
        if (bids.isEmpty()) {
            return null;
        }
        return bids.last();
    }

    public Collection<Bid> getBids() {
        return bids;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", bids=" + bids +
                '}';
    }

}
