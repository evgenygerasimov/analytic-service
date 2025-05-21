package org.site.analyticservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "order_info")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_info_id")
    private UUID orderInfoId;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "bucket_time")
    private LocalDateTime bucketTime;

    @Column(name = "total_items_count")
    private Integer totalItemsCount;

    @Column(name = "total_items_amount")
    private Double totalItemsAmount;
}
