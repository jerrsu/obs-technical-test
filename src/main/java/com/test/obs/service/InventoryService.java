package com.test.obs.service;

import com.test.obs.model.Inventory;
import com.test.obs.reporitory.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author jerrySuparman
 */

@Service
public class InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ItemService itemService;

    public Inventory findById(int id) {
        return inventoryRepository.findById(id).orElse(null);
    }

    public Page<Inventory> findAll(Pageable pageable) {
        return inventoryRepository.findAll(pageable);
    }

    public Inventory save(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public void delete(Integer id) {
        inventoryRepository.deleteById(id);
    }

    public Inventory update(Integer id,Inventory newInventory) {
        Inventory oldInventory = findById(id);
        if (oldInventory != null) {
            if (newInventory.getQty() != null) {
                oldInventory.setQty(newInventory.getQty());
            }
            if (newInventory.getType() != null) {
                oldInventory.setType(newInventory.getType());
            }
            if (newInventory.getItemId() != null) {
                oldInventory.setItemId(newInventory.getItemId());
            }
            return inventoryRepository.save(oldInventory);
        }
        return null;
    }

    public String checkType(Inventory inventory) {
        if (inventory.getType().equals(Inventory.InventoryType.W)) {
            String withDrawal = itemService.withDrawal(inventory.getItemId(), inventory.getQty());
            if (withDrawal != null) {
                return "OK";
            } return "Out of stock";
        }
        if (inventory.getType().equals(Inventory.InventoryType.T)) {
            itemService.topUp(inventory.getItemId(), inventory.getQty());
            return "OK";
        }
        return null;
    }

}
