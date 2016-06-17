package org.elkendev.repository;

import org.elkendev.domain.Bid;
import org.elkendev.domain.Item;
import org.elkendev.exception.ItemNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ItemRepositoryImplTest {

    private ItemRepository onTest;

    @Before
    public void setup() {
        onTest = new ItemRepositoryImpl();
    }

    @Test
    public void testSaveAndFind() throws Exception {

        Item item = new Item("description");
        item.addBid(new Bid("user", new BigDecimal("1.99")));

        Item saved = onTest.saveOrUpdate(item);

        Item retrieved = onTest.find(saved.getId());

        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getId()).isNotNull();
        assertThat(retrieved.getId()).isEqualTo(saved.getId());
        assertThat(retrieved.getDescription()).isEqualTo("description");
        assertThat(retrieved.getBids()).hasSize(1);

    }

    @Test
    public void testUpdateItem() throws Exception {

        Item item = new Item("desc");
        item.addBid(new Bid("user", new BigDecimal("1.99")));
        Item saved = onTest.saveOrUpdate(item);

        Item item2 = new Item("updated");
        item2.setId(saved.getId());
        onTest.saveOrUpdate(item2);

        Item retrieved = onTest.find(saved.getId());

        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getId()).isEqualTo(saved.getId());
        assertThat(retrieved.getDescription()).isEqualTo("updated");
        assertThat(retrieved.getBids()).isEmpty();

    }

    @Test
    public void testGeneratingIds() throws Exception {

        Item item1 = onTest.saveOrUpdate(new Item("item1"));
        Item item2 = onTest.saveOrUpdate(new Item("item2"));

        assertThat(item1.getId()).isEqualTo(1L);
        assertThat(item2.getId()).isEqualTo(2L);

    }

    @Test
    public void testFind_returnsNullWhenItemDoesNotExist() throws Exception {

        assertThat(onTest.find(-100L)).isNull();

    }

    @Test
    public void testFindItemsForUser() throws Exception {

        Item item = new Item("desc");
        item.addBid(new Bid("user", new BigDecimal("1.99")));
        item.addBid(new Bid("user2", new BigDecimal("3.99")));
        item.addBid(new Bid("user", new BigDecimal("9.99")));
        onTest.saveOrUpdate(item);

        Item item2 = new Item("desc2");
        item2.addBid(new Bid("user", new BigDecimal("1.99")));
        item2.addBid(new Bid("user1", new BigDecimal("9.99")));
        onTest.saveOrUpdate(item2);

        Item item3 = new Item("desc3");
        item3.addBid(new Bid("user3", new BigDecimal("1.99")));
        item3.addBid(new Bid("user1", new BigDecimal("9.99")));
        onTest.saveOrUpdate(item3);

        Collection<Item> items = onTest.findItemsForUser("user");

        assertThat(items).hasSize(2);
        assertThat(items).contains(item, item2);

    }

    @Test
    public void testFindItemsForUser_whenNoneFound_returnEmptyCollection() throws Exception {

        Item item = new Item("desc");
        item.addBid(new Bid("user", new BigDecimal("1.99")));
        item.addBid(new Bid("user2", new BigDecimal("3.99")));
        onTest.saveOrUpdate(item);

        Collection<Item> items = onTest.findItemsForUser("noItems");

        assertThat(items).isEmpty();

    }

    /**
     * Test saving items concurrently - proves that item id generation is atomic
     * @throws Exception
     */
    @Test
    public void testSavingItemsConcurrently() throws Exception {

        int expectedNumberOfItems = 10000;

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

        List<Callable<Void>> callables = new ArrayList<>();
        for (int i = 0; i < expectedNumberOfItems; i++) {
            Item item = new Item("description");
            item.addBid(new Bid("user", new BigDecimal("1.00")));
            callables.add(() -> {
                onTest.saveOrUpdate(item);
                return null;
            });
        }

        List<Future<Void>> futures = executorService.invokeAll(callables);

        // wait until all calls complete
        for (Future<Void> future : futures) {
            future.get();
        }

        Collection<Item> itemsForUser = onTest.findItemsForUser("user");
        assertThat(itemsForUser).hasSize(expectedNumberOfItems);

    }

}
