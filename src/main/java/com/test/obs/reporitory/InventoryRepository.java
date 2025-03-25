package com.test.obs.reporitory;

import com.test.obs.model.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author jerrySuparman
 */

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    Page<Inventory> findAll(Pageable pageable);
}
