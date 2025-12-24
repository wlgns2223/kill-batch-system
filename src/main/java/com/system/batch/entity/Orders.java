package com.system.batch.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "orders")
public class Orders {

    @Id
    private Integer orderNumber;

    private LocalDateTime orderDate;

    private LocalDateTime requiredDate;

    private LocalDateTime shippedDate;

    private String status;

    private String comments;

    private int customerNumber;

}
