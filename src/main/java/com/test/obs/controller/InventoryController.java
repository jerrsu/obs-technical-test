package com.test.obs.controller;

import com.test.obs.model.Inventory;
import com.test.obs.model.Item;
import com.test.obs.service.InventoryService;
import com.test.obs.service.ItemService;
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
@RequestMapping("/inventory")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ItemService itemService;

    @GetMapping("/{id}")
    public ResponseEntity<?> findByIdInventory(@PathVariable int id) {
        Inventory inventory = inventoryService.findById(id);
        if (inventory != null) {
            return new ResponseEntity<>(new ApiResponse<>("success","Data successfully retrieved",inventory), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse<>("failed","Data not found",null), HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<?> pageListInventory(Pageable pageable){
        Page<Inventory> inventory = inventoryService.findAll(pageable);
        return new ResponseEntity<>(new ApiResponse<>("success","Data successfully retrieved",inventory), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> saveInventory(@RequestBody Inventory input) {
        Item item = itemService.findById(input.getItemId());
        if (item != null) {
            String result = inventoryService.checkType(input);
            return switch (result) {
                case "OK" -> {
                    inventoryService.save(input);
                    yield new ResponseEntity<>(new ApiResponse<>("success","Data successfully added",input), HttpStatus.CREATED);
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
    public ResponseEntity<?> deleteInventory(@PathVariable int id) {
        Inventory inventory = inventoryService.findById(id);
        if (inventory != null) {
            inventoryService.delete(id);
            return new ResponseEntity<>(new ApiResponse<>("success","Data successfully deleted",null), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse<>("failed","Data not found",null), HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateInventory(@PathVariable int id, @RequestBody Inventory input) {
        Inventory inventory = inventoryService.findById(id);
        if (inventory != null) {
            String result = inventoryService.counting(id, input);
            return switch (result) {
                case "OK" -> {
                    Inventory update = inventoryService.update(id, input);
                    yield new ResponseEntity<>(new ApiResponse<>("success", "Data successfully updated", update), HttpStatus.OK);
                }
                case "Out of stock" ->
                        new ResponseEntity<>(new ApiResponse<>("failed", "Item Out of stock", null), HttpStatus.CONFLICT);
                case "Item ID and type cannot be updated" ->
                        new ResponseEntity<>(new ApiResponse<>("failed", "Item ID and type cannot be updated", null), HttpStatus.NOT_FOUND);
                default ->
                        new ResponseEntity<>(new ApiResponse<>("failed", "Data failed to updated", null), HttpStatus.UNPROCESSABLE_ENTITY);
            };
        }
        return new ResponseEntity<>(new ApiResponse<>("failed","Data not found",null), HttpStatus.NOT_FOUND);
    }
}
