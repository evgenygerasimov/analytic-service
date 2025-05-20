package org.site.analyticservice.repositiry;

import org.site.analyticservice.entity.OrderInfo;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderInfoRepository extends CassandraRepository<OrderInfo, String> {
}
