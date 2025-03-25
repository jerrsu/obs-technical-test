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
        Inventory dto = inventoryService.findById(id);
        if (dto == null) {
            return new ResponseEntity<>(new ApiResponse<>("failed","Data not found",null), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ApiResponse<>("success","Data successfully retrieved",dto), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> pageListInventory(Pageable pageable){
        Page<Inventory> dto = inventoryService.findAll(pageable);
        return new ResponseEntity<>(new ApiResponse<>("success","Data successfully retrieved",dto), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> saveInventory(@RequestBody Inventory inputDto) {
        Item item = itemService.findById(inputDto.getItemId());
        if (item != null) {
            String result = inventoryService.checkType(inputDto);
            if (result.equals("OK")) {
                try {
                    inventoryService.save(inputDto);
                    return new ResponseEntity<>(new ApiResponse<>("success","Data successfully added",inputDto), HttpStatus.CREATED);
                } catch (Exception e) {
                    return new ResponseEntity<>(new ApiResponse<>("failed","Data failed to add",null), HttpStatus.UNPROCESSABLE_ENTITY);
                }
            } else if (result.equals("Out of stock")) {
                return new ResponseEntity<>(new ApiResponse<>("failed","Item Out of stock",null), HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>("Invalid inventory type. Only 'W' (withdrawal) or 'T' (top-up) are allowed.", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(new ApiResponse<>("failed","Item not found",null), HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInventory(@PathVariable int id) {
        Inventory dto = inventoryService.findById(id);
        if (dto == null) {
            return new ResponseEntity<>(new ApiResponse<>("failed","Data not found",null), HttpStatus.NOT_FOUND);
        }
        inventoryService.delete(id);
        return new ResponseEntity<>(new ApiResponse<>("success","Data successfully deleted",null), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateInventory(@PathVariable int id, @RequestBody Inventory inputDto) {
        Inventory dto= inventoryService.update(id,inputDto);
        if (dto == null) {
            return new ResponseEntity<>(new ApiResponse<>("failed","Data not found",null), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ApiResponse<>("success","Data successfully updated",dto), HttpStatus.OK);
    }

}
