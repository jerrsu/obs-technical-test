package com.test.obs;

import com.test.obs.model.Item;
import com.test.obs.model.Order;
import com.test.obs.reporitory.ItemRepository;
import com.test.obs.reporitory.OrderRepository;
import com.test.obs.service.OrderService;
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

public class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemRepository itemRepository;

    private Order order;
    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        item = new Item(1, "Kurupuk", 100, 100);
        order = new Order("O1",1,5,100,item);
    }

    @Test
    void testFindById() {
        when(orderRepository.findById("O1")).thenReturn(java.util.Optional.of(order));

        Order foundOrder = orderService.findById("O1");

        assertNotNull(foundOrder);
        assertEquals("O1", foundOrder.getOrderNo());
        assertEquals(1, foundOrder.getItemId());
        verify(orderRepository, times(1)).findById("O1");
    }

    @Test
    void testFindById_OrderNotFound() {
        when(orderRepository.findById("O2")).thenReturn(java.util.Optional.empty());

        Order foundOrder = orderService.findById("O2");

        assertNull(foundOrder);
        verify(orderRepository, times(1)).findById("O2");
    }

    @Test
    void testFindAll() {
        Page<Order> orders = new PageImpl<>(java.util.List.of(order));
        when(orderRepository.findAll(Mockito.any(Pageable.class))).thenReturn(orders);

        Page<Order> foundOrders = orderService.findAll(PageRequest.of(0, 10));

        assertNotNull(foundOrders);
        assertEquals(1, foundOrders.getTotalElements());
        verify(orderRepository, times(1)).findAll(Mockito.any(Pageable.class));
    }

    @Test
    void testSave() {
        when(itemRepository.findById(1)).thenReturn(java.util.Optional.of(item));
        when(orderRepository.findLastOrderId()).thenReturn("O1");
        when(orderRepository.save(Mockito.any(Order.class))).thenReturn(order);

        Order savedOrder = orderService.save(order);

        assertNotNull(savedOrder);
        assertEquals("O2", savedOrder.getOrderNo());  // order number should be incremented
        assertEquals(100, savedOrder.getPrice());
        verify(orderRepository, times(1)).save(order);
        verify(itemRepository, times(1)).findById(1);
    }

    @Test
    void testDelete() {
        doNothing().when(orderRepository).deleteById("O1");

        orderService.delete("O1");

        verify(orderRepository, times(1)).deleteById("O1");
    }

    @Test
    void testUpdate_OrderExists() {
        Order updatedOrder = new Order("O1", 1, 2, 100,item);  // Updated quantity and price
        when(orderRepository.findById("O1")).thenReturn(java.util.Optional.of(order));
        when(orderRepository.save(Mockito.any(Order.class))).thenReturn(updatedOrder);

        Order result = orderService.update("O1", updatedOrder);

        assertNotNull(result);
        assertEquals(2, result.getQty());
        assertEquals(100, result.getPrice());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testUpdate_OrderNotFound() {
        Order updatedOrder = new Order("O1", 1, 2, 100,item);  // Updated quantity and price
        when(orderRepository.findById("O1")).thenReturn(java.util.Optional.empty());

        Order result = orderService.update("O1", updatedOrder);

        assertNull(result);
        verify(orderRepository, times(1)).findById("O1");
    }
}
