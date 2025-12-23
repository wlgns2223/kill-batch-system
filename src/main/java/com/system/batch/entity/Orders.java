package com.system.batch.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
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
