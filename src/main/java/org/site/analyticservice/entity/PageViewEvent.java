package org.site.analyticservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "page_view_event")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageViewEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "page_view_event_id")
    private UUID pageViewEventId;

    @Column(name = "visit_time")
    private LocalDateTime visitTime;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "path")
    private String path;

    @Column(name = "ip")
    private String ip;

    @Column(name = "user_agent")
    private String userAgent;
}
