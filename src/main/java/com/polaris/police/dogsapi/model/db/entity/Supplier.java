package com.polaris.police.dogsapi.model.db.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "supplier")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "supplier_name")
    private String supplierName;
}
