package com.test.obs.reporitory;

import com.test.obs.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author jerrySuparman
 */

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    Page<Item> findAll(Pageable pageable);

}
