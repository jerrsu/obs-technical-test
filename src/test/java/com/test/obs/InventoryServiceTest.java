package com.test.obs;
import com.test.obs.model.Inventory;
import com.test.obs.model.Item;
import com.test.obs.reporitory.InventoryRepository;
import com.test.obs.service.InventoryService;
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
    
public class InventoryServiceTest {
    @InjectMocks
    private InventoryService inventoryService;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ItemService itemService;

    private Inventory inventory;

    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        item = new Item(1, "Kurupuk", 100, 100);
        inventory = new Inventory(1,1,1, Inventory.InventoryType.T,item);
    }

    @Test
    void testFindById() {
        when(inventoryRepository.findById(1)).thenReturn(java.util.Optional.of(inventory));

        Inventory foundInventory = inventoryService.findById(1);

        assertNotNull(foundInventory);
        assertEquals(1, foundInventory.getId());
        verify(inventoryRepository, times(1)).findById(1);
    }

    @Test
    void testFindById_InventoryNotFound() {
        when(inventoryRepository.findById(2)).thenReturn(java.util.Optional.empty());

        Inventory foundInventory = inventoryService.findById(2);

        assertNull(foundInventory);
        verify(inventoryRepository, times(1)).findById(2);
    }

    @Test
    void testFindAll() {
        Page<Inventory> inventories = new PageImpl<>(java.util.List.of(inventory));
        when(inventoryRepository.findAll(Mockito.any(Pageable.class))).thenReturn(inventories);

        Page<Inventory> foundInventories = inventoryService.findAll(PageRequest.of(0, 10));

        assertNotNull(foundInventories);
        assertEquals(1, foundInventories.getTotalElements());
        verify(inventoryRepository, times(1)).findAll(Mockito.any(Pageable.class));
    }

    @Test
    void testSave() {
        when(inventoryRepository.save(Mockito.any(Inventory.class))).thenReturn(inventory);

        Inventory savedInventory = inventoryService.save(inventory);

        assertNotNull(savedInventory);
        assertEquals(1, savedInventory.getId());
        verify(inventoryRepository, times(1)).save(inventory);
    }

    @Test
    void testDelete() {
        doNothing().when(inventoryRepository).deleteById(1);

        inventoryService.delete(1);

        verify(inventoryRepository, times(1)).deleteById(1);
    }

    @Test
    void testUpdate_InventoryExists() {
        Inventory updatedInventory = new Inventory(1, 200, 1, Inventory.InventoryType.T,null);
        when(inventoryRepository.findById(1)).thenReturn(java.util.Optional.of(inventory));
        when(inventoryRepository.save(Mockito.any(Inventory.class))).thenReturn(updatedInventory);

        Inventory result = inventoryService.update(1, updatedInventory);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(200, result.getQty());
        verify(inventoryRepository, times(1)).save(inventory);
    }

    @Test
    void testUpdate_InventoryNotFound() {
        Inventory updatedInventory = new Inventory(1, 200, 1, Inventory.InventoryType.T,null);
        when(inventoryRepository.findById(1)).thenReturn(java.util.Optional.empty());

        Inventory result = inventoryService.update(1, updatedInventory);

        assertNull(result);
        verify(inventoryRepository, times(1)).findById(1);
    }

    @Test
    void testCheckType_WithdrawalSuccess() {
        inventory.setType(Inventory.InventoryType.W); // Set type as withdrawal
        when(itemService.withDrawal(inventory.getItemId(), inventory.getQty())).thenReturn("OK");

        String result = inventoryService.checkType(inventory);

        assertEquals("OK", result);
        verify(itemService, times(1)).withDrawal(inventory.getItemId(), inventory.getQty());
    }

    @Test
    void testCheckType_WithdrawalOutOfStock() {
        inventory.setType(Inventory.InventoryType.W); // Set type as withdrawal
        when(itemService.withDrawal(inventory.getItemId(), inventory.getQty())).thenReturn(null);

        String result = inventoryService.checkType(inventory);

        assertEquals("Out of stock", result);
        verify(itemService, times(1)).withDrawal(inventory.getItemId(), inventory.getQty());
    }

//    @Test
//    void testCheckType_TopUp() {
//        inventory.setType(Inventory.InventoryType.T); // Set type as top-up
//        when(itemService.topUp(inventory.getItemId(), inventory.getQty())).thenReturn("OK");
//
//        String result = inventoryService.checkType(inventory);
//
//        assertEquals("OK", result);
//        verify(itemService, times(1)).topUp(inventory.getItemId(), inventory.getQty());
//    }

    @Test
    void testCheckType_TopUp() {
        inventory.setType(Inventory.InventoryType.T); // Set type as top-up
        doNothing().when(itemService).topUp(inventory.getItemId(), inventory.getQty());  // Mock void method

        String result = inventoryService.checkType(inventory);

        assertEquals("OK", result);
        verify(itemService, times(1)).topUp(inventory.getItemId(), inventory.getQty());
    }

}
