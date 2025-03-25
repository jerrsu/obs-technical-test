package com.test.obs.model;

import lombok.*;
import javax.persistence.*;
import java.io.Serializable;

/**
 * @author jerrySuparman
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item")
public class Item implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer price;
    private Integer stock;


}
