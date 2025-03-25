package com.test.obs.controller;

import com.test.obs.model.Item;
import com.test.obs.service.ItemService;
import com.test.obs.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author jerrySuparman
 */

@RestController
@RequestMapping("/item")
public class ItemController {
    @Autowired
    private ItemService itemService;

    @GetMapping("/{id}")
    public ResponseEntity<?> findByIdItem(@PathVariable int id) {
        Item dto = itemService.findById(id);
        if (dto == null) {
            return new ResponseEntity<>(new ApiResponse<>("failed","Data not found",null), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ApiResponse<>("success","Data successfully retrieved",dto), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> pageListItem(@PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable){
        Page<Item> dto = itemService.findAll(pageable);
        return new ResponseEntity<>(new ApiResponse<>("success","Data successfully retrieved",dto), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> saveItem(@RequestBody Item inputDto) {
        try {
            Item dto = itemService.save(inputDto);
            return new ResponseEntity<>(new ApiResponse<>("success","Data successfully added",dto), HttpStatus.CREATED);
        }
        catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("failed","Data failed to add",null), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable int id) {
        Item dto = itemService.findById(id);
        if (dto == null) {
            return new ResponseEntity<>(new ApiResponse<>("failed","Data not found",null), HttpStatus.NOT_FOUND);
        }
        itemService.delete(id);
        return new ResponseEntity<>(new ApiResponse<>("success","Data successfully deleted",null), HttpStatus.OK);

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable int id, @RequestBody Item inputDto) {
        Item dto = itemService.update(id,inputDto);
        if (dto == null) {
            return new ResponseEntity<>(new ApiResponse<>("failed","Data not found",null), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ApiResponse<>("success","Data successfully updated",dto), HttpStatus.OK);
    }
}
