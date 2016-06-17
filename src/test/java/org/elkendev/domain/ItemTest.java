package org.elkendev.domain;

import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemTest {

    @Test
    public void testFindHighestBid() {

        Item onTest = new Item("my item");
        onTest.addBid(new Bid("user 1", new BigDecimal("10.99")));
        onTest.addBid(new Bid("user 2", new BigDecimal("50.00")));
        onTest.addBid(new Bid("user 3", new BigDecimal("50.00")));

        Bid highestBid = onTest.getHighestBid();

        assertThat(highestBid.getUser()).isEqualTo("user 2");
        assertThat(highestBid.getAmount()).isEqualTo(new BigDecimal("50.00"));

    }

}
