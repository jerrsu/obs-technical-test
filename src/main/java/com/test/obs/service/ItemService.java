package com.test.obs.service;

import com.test.obs.model.Item;
import com.test.obs.reporitory.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author jerrySuparman
 */

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    public Item findById(int id) {
        return itemRepository.findById(id).orElse(null);
    }

    public Page<Item> findAll(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public void delete(Integer id) {
        itemRepository.deleteById(id);
    }

    public Item update(Integer id,Item newItem) {
        Item oldItem = findById(id);
        if (oldItem != null) {
            if (newItem.getName() != null) {
                oldItem.setName(newItem.getName());
            }
            if (newItem.getPrice() != null) {
                oldItem.setPrice(newItem.getPrice());
            }
            return itemRepository.save(oldItem);
        }
        return null;
    }

    public void topUp(Integer id, Integer qty) {
        Item oldItem = findById(id);
        oldItem.setStock(oldItem.getStock() + qty);
        itemRepository.save(oldItem);
    }

    public String withDrawal(Integer id, Integer qty) {
        Item oldItem = findById(id);
        if (oldItem.getStock() >= qty) {
            oldItem.setStock(oldItem.getStock() - qty);
            itemRepository.save(oldItem);
            return "OK";
        }  return null;
    }
}
