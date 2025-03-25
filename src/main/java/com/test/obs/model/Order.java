package com.test.obs.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

/**
 * @author jerrySuparman
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order")
public class Order {
    @Id
    @Column(name = "order_no",unique = true, nullable = false)
    private String orderNo;
    @Column(name = "item_id")
    private Integer itemId;
    private Integer qty;
    private Integer price;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Item item;
}
