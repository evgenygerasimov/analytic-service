package org.site.analyticservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED;
import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED;

@Table("order_info")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfo {

    @PrimaryKeyColumn(name = "order_id", type = PARTITIONED )
    private UUID orderId;

    @PrimaryKeyColumn(name = "bucket_time", type = CLUSTERED)
    @Column("bucket_time")
    private LocalDateTime bucketTime;

    @Column("total_items_count")
    private Integer totalItemsCount;

    @Column("total_items_amount")
    private Double totalItemsAmount;
}
