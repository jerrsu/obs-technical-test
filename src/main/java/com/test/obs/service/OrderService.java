package com.test.obs.service;

import com.test.obs.model.Item;
import com.test.obs.model.Order;
import com.test.obs.reporitory.ItemRepository;
import com.test.obs.reporitory.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author jerrySuparman
 */

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    public Order findById(String id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Order save(Order order) {
        Item item = itemRepository.findById(order.getItemId()).orElse(null);
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
        if (oldorder != null) {
            if (neworder.getQty() != null) {
                oldorder.setQty(neworder.getQty());
            }
            if (neworder.getPrice() != null) {
                oldorder.setPrice(neworder.getPrice());
            }
            if (neworder.getItemId() != null) {
                oldorder.setItemId(neworder.getItemId());
            }
            return orderRepository.save(oldorder);
        }
        return null;
    }
}
