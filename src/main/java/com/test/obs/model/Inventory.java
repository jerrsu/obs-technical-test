package com.test.obs.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author jerrySuparman
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventory_test")
public class Inventory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer qty;
    @Column(name = "item_id")
    private Integer itemId;

    @Enumerated(EnumType.STRING)
    private InventoryType type;
    public enum InventoryType {
        T, W
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Item item;
}
