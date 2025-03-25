package com.test.obs.reporitory;

import com.test.obs.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author jerrySuparman
 */

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Page<Order> findAll(Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT o.order_no FROM order_test o ORDER BY o.order_no DESC")
    String findLastOrderId();
}
