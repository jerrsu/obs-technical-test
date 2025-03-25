package com.test.obs;

import com.test.obs.model.Item;
import com.test.obs.reporitory.ItemRepository;
import com.test.obs.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
/**
 * @author jerrySuparman
 */
    
public class ItemServiceTest {
    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        item = new Item(1, "Test Item", 100, 50); // assuming constructor is Item(id, name, price, stock)
    }

    @Test
    void testFindById() {
        when(itemRepository.findById(1)).thenReturn(java.util.Optional.of(item));

        Item foundItem = itemService.findById(1);

        assertNotNull(foundItem);
        assertEquals(1, foundItem.getId());
        assertEquals("Test Item", foundItem.getName());
        verify(itemRepository, times(1)).findById(1);
    }

    @Test
    void testFindById_ItemNotFound() {
        when(itemRepository.findById(2)).thenReturn(java.util.Optional.empty());

        Item foundItem = itemService.findById(2);

        assertNull(foundItem);
        verify(itemRepository, times(1)).findById(2);
    }

    @Test
    void testFindAll() {
        Page<Item> items = new PageImpl<>(java.util.List.of(item));
        when(itemRepository.findAll(Mockito.any(Pageable.class))).thenReturn(items);

        Page<Item> foundItems = itemService.findAll(PageRequest.of(0, 10));

        assertNotNull(foundItems);
        assertEquals(1, foundItems.getTotalElements());
        verify(itemRepository, times(1)).findAll(Mockito.any(Pageable.class));
    }

    @Test
    void testSave() {
        when(itemRepository.save(Mockito.any(Item.class))).thenReturn(item);

        Item savedItem = itemService.save(item);

        assertNotNull(savedItem);
        assertEquals("Test Item", savedItem.getName());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void testDelete() {
        doNothing().when(itemRepository).deleteById(1);

        itemService.delete(1);

        verify(itemRepository, times(1)).deleteById(1);
    }

    @Test
    void testUpdate_ItemExists() {
        Item updatedItem = new Item(1, "Updated Item", 120, 50);
        when(itemRepository.findById(1)).thenReturn(java.util.Optional.of(item));
        when(itemRepository.save(Mockito.any(Item.class))).thenReturn(updatedItem);

        Item result = itemService.update(1, updatedItem);

        assertNotNull(result);
        assertEquals("Updated Item", result.getName());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void testUpdate_ItemNotFound() {
        Item updatedItem = new Item(1, "Updated Item", 120, 50);
        when(itemRepository.findById(1)).thenReturn(java.util.Optional.empty());

        Item result = itemService.update(1, updatedItem);

        assertNull(result);
        verify(itemRepository, times(1)).findById(1);
    }

    @Test
    void testTopUp() {
        when(itemRepository.findById(1)).thenReturn(java.util.Optional.of(item));
        when(itemRepository.save(Mockito.any(Item.class))).thenReturn(item);

        itemService.topUp(1, 10);

        assertEquals(60, item.getStock());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void testWithDrawal_SufficientStock() {
        when(itemRepository.findById(1)).thenReturn(java.util.Optional.of(item));
        when(itemRepository.save(Mockito.any(Item.class))).thenReturn(item);

        String result = itemService.withDrawal(1, 10);

        assertEquals("OK", result);
        assertEquals(40, item.getStock()); // stock should decrease by 10
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void testWithDrawal_InsufficientStock() {
        when(itemRepository.findById(1)).thenReturn(java.util.Optional.of(item));

        String result = itemService.withDrawal(1, 60);

        assertNull(result); // should return null when stock is insufficient
        verify(itemRepository, times(0)).save(item);
    }
}
