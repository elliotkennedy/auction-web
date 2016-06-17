package org.elkendev.controller;

import org.elkendev.domain.Bid;
import org.elkendev.domain.Item;
import org.elkendev.exception.ItemNotFoundException;
import org.elkendev.service.AuctionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class AuctionController {

    private AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @RequestMapping(path = "/item/{id}/bid", method = RequestMethod.POST)
    public void placeBid(@PathVariable Long id, @RequestBody Bid bid) throws ItemNotFoundException {
        auctionService.placeBid(id, bid);
    }

    @RequestMapping(path = "/item/{id}/bids", method = RequestMethod.GET)
    public Collection<Bid> listBids(@PathVariable Long id) throws ItemNotFoundException {
        return auctionService.findAllBids(id);
    }

    @RequestMapping(path = "/item/{id}/bid", method = RequestMethod.GET)
    public Bid getHighestBid(@PathVariable Long id) throws ItemNotFoundException {
        return auctionService.findHighestBid(id);
    }

    @RequestMapping(path = "/items/user/{userName}", method = RequestMethod.GET)
    public Collection<Item> findItemsForUser(@PathVariable String userName) {
        return auctionService.findItemsForUser(userName);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Item does not exist")
    public void handleItemNotFound() {}

}
