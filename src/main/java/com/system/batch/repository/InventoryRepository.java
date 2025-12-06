package com.system.batch.repository;

import com.system.batch.entity.ItemStock;

import java.util.List;

public interface InventoryRepository {
    List<ItemStock> findLowStockItems(int stock);
}
