package org.site.analyticservice.entity;

public record OrderStat(String bucketTime, int ordersCount, int totalItemsCount, double totalAmount) {}