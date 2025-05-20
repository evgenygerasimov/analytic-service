CREATE TABLE IF NOT EXISTS order_info (
                                          order_id uuid,
                                          bucket_time timestamp,
                                          total_items_count int,
                                          total_items_amount double,
                                          PRIMARY KEY (order_id, bucket_time)
) WITH CLUSTERING ORDER BY (bucket_time DESC);
