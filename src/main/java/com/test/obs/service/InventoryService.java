package com.test.obs.service;

import com.test.obs.model.Inventory;
import com.test.obs.model.Item;
import com.test.obs.reporitory.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
        oldInventory.setType(newInventory.getType());
        oldInventory.setItemId(newInventory.getItemId());
        oldInventory.setQty(newInventory.getQty());
        return inventoryRepository.save(oldInventory);
    }

    public String counting(Integer id, Inventory newInventory) {
        Inventory oldInventory = findById(id);
        Item item = itemService.findById(oldInventory.getItemId());
        if (newInventory.getType() != oldInventory.getType() || !Objects.equals(newInventory.getItemId(), oldInventory.getItemId())) {
            return "Item ID and type cannot be updated";
        }
        int count;
        if (oldInventory.getType().equals(Inventory.InventoryType.W)){
            if (newInventory.getQty() > item.getStock()) {
                return "Out of stock";
            }
            else if (oldInventory.getQty() >= newInventory.getQty()) {
                count = oldInventory.getQty() - newInventory.getQty();
                itemService.topUp(oldInventory.getItemId(), count);
                return "OK";
            }
            count = newInventory.getQty() - oldInventory.getQty();
            itemService.withDrawal(oldInventory.getItemId(), count);
            return "OK";
        }

        if (oldInventory.getType().equals(Inventory.InventoryType.T)) {
            if (oldInventory.getQty() >= newInventory.getQty()) {
                count = oldInventory.getQty() - newInventory.getQty();
                itemService.withDrawal(oldInventory.getItemId(), count);
                return "OK";
            }
            count = newInventory.getQty() - oldInventory.getQty();
            itemService.topUp(oldInventory.getItemId(), count);
            return "OK";
        }
        return "Invalid type";
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
