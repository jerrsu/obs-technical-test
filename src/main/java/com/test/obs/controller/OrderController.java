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
        Order dto = orderService.findById(id);
        if (dto == null) {
            return new ResponseEntity<>(new ApiResponse<>("failed","Data not found",null), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ApiResponse<>("success","Data successfully retrieved",dto), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> pageListItem(Pageable pageable){
        Page<Order> dto = orderService.findAll(pageable);
        return new ResponseEntity<>(new ApiResponse<>("success","Data successfully retrieved",dto), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> saveItem(@RequestBody Order inputDto) {
        Item item = itemService.findById(inputDto.getItemId());
        if (item != null) {
            String result = itemService.withDrawal(inputDto.getItemId(), inputDto.getQty());
            if (result != null) {
                try {
                    Order dto = orderService.save(inputDto);
                    return new ResponseEntity<>(new ApiResponse<>("success","Data successfully added",dto), HttpStatus.CREATED);
                }
                catch (Exception e) {
                    return new ResponseEntity<>(new ApiResponse<>("failed","Data failed to add",null), HttpStatus.UNPROCESSABLE_ENTITY);
                }
            }
            return new ResponseEntity<>(new ApiResponse<>("failed","Item Out of stock",null), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(new ApiResponse<>("failed","Item not found",null), HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable String id) {
        Order dto = orderService.findById(id);
        if (dto == null) {
            return new ResponseEntity<>(new ApiResponse<>("failed","Data not found",null), HttpStatus.NOT_FOUND);
        }
        orderService.delete(id);
        return new ResponseEntity<>(new ApiResponse<>("success","Data successfully deleted",null), HttpStatus.OK);

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable String id, @RequestBody Order inputDto) {
        Order dto = orderService.update(id,inputDto);
        if (dto == null) {
            return new ResponseEntity<>(new ApiResponse<>("failed","Data not found",null), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ApiResponse<>("success","Data successfully updated",dto), HttpStatus.OK);
    }
}
