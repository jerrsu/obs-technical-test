package com.test.obs.service;

import com.test.obs.model.Item;
import com.test.obs.model.Order;
import com.test.obs.reporitory.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author jerrySuparman
 */

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private InventoryService inventoryService;

    public Order findById(String id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Order save(Order order) {
        Item item = itemService.findById(order.getItemId());
        String con = orderRepository.findLastOrderId();
        order.setOrderNo(generateOrderNo(con));
        order.setPrice(item.getPrice());
        return orderRepository.save(order);
    }

    private String generateOrderNo(String orderNo) {
        if (orderNo == null) {
            return "O1";
        }
        long jumlah = Long.parseLong(orderNo.substring(1)) + 1;
        return String.format("O"+jumlah);
    }

    public void delete(String id) {
        orderRepository.deleteById(id);
    }

    public Order update(String id,Order neworder) {
        Order oldorder = findById(id);
        oldorder.setQty(neworder.getQty());
        return orderRepository.save(oldorder);
    }

    public String counting(String id, Order neworder) {
        Order oldorder = findById(id);
        Item item = itemService.findById(neworder.getItemId());
        if (!Objects.equals(neworder.getItemId(), oldorder.getItemId())) {
            return "Item ID cannot be updated";
        }
        int count;
        if (neworder.getQty() > item.getStock()) {
            return "Out of stock";
        }
        else if (oldorder.getQty() >= neworder.getQty()) {
            count = oldorder.getQty() - neworder.getQty();
            itemService.topUp(oldorder.getItemId(), count);
            return "OK";
        }
        count = neworder.getQty() - oldorder.getQty();
        itemService.withDrawal(oldorder.getItemId(), count);
        return "OK";
    }

}
