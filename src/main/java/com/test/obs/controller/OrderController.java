package com.test.obs.controller;

import com.test.obs.model.Item;
import com.test.obs.model.Order;
import com.test.obs.service.ItemService;
import com.test.obs.service.OrderService;
import com.test.obs.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author jerrySuparman
 */

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemService itemService;

    @GetMapping("/{id}")
    public ResponseEntity<?> findByIdOrder(@PathVariable String id) {
        Order order = orderService.findById(id);
        if (order != null) {
            return new ResponseEntity<>(new ApiResponse<>("success","Data successfully retrieved",order), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse<>("failed","Data not found",null), HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<?> pageListOrder(Pageable pageable){
        Page<Order> order = orderService.findAll(pageable);
        return new ResponseEntity<>(new ApiResponse<>("success","Data successfully retrieved",order), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> saveOrder(@RequestBody Order input) {
        Item item = itemService.findById(input.getItemId());
        if (item != null) {
            String result = itemService.withDrawal(input.getItemId(), input.getQty());
            return switch (result){
                case "OK" -> {
                    Order order = orderService.save(input);
                    yield new ResponseEntity<>(new ApiResponse<>("success","Data successfully added",order), HttpStatus.CREATED);
                }
                case "Out of stock" ->
                        new ResponseEntity<>(new ApiResponse<>("failed","Item Out of stock",null), HttpStatus.CONFLICT);
                case "Invalid type" ->
                        new ResponseEntity<>("Invalid inventory type. Only 'W' (withdrawal) or 'T' (top-up) are allowed.", HttpStatus.FORBIDDEN);
                default -> new ResponseEntity<>(new ApiResponse<>("failed","Data failed to add",null), HttpStatus.UNPROCESSABLE_ENTITY);
            };
        }
        return new ResponseEntity<>(new ApiResponse<>("failed","Item not found",null), HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable String id) {
        Order order = orderService.findById(id);
        if (order != null) {
            orderService.delete(id);
            return new ResponseEntity<>(new ApiResponse<>("success","Data successfully deleted",null), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse<>("failed","Data not found",null), HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable String id, @RequestBody Order input) {
        Order order = orderService.findById(id);
        if (order != null) {
            String result = orderService.counting(id, input);
            return switch (result){
                case "OK" -> {
                    Order update = orderService.update(id,input);
                    yield new ResponseEntity<>(new ApiResponse<>("success","Data successfully added",update), HttpStatus.CREATED);
                }
                case "Out of stock" ->
                        new ResponseEntity<>(new ApiResponse<>("failed","Item Out of stock",null), HttpStatus.CONFLICT);
                case "Invalid type" ->
                        new ResponseEntity<>("Invalid inventory type. Only 'W' (withdrawal) or 'T' (top-up) are allowed.", HttpStatus.FORBIDDEN);
                default -> new ResponseEntity<>(new ApiResponse<>("failed","Data failed to add",null), HttpStatus.UNPROCESSABLE_ENTITY);
            };
        }
        return new ResponseEntity<>(new ApiResponse<>("failed","Data not found",null), HttpStatus.NOT_FOUND);
    }
}
