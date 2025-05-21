package org.site.analyticservice.repositiry;

import org.site.analyticservice.entity.PageViewEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PageViewEventRepository extends JpaRepository<PageViewEvent, UUID> {
}
