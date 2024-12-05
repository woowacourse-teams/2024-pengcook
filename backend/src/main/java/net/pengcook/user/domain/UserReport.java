package net.pengcook.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "reportee_id")
    private User reportee;

    @Enumerated(EnumType.STRING)
    private Reason reason;

    @Enumerated(EnumType.STRING)
    private Type type;

    private long targetId;

    private String details;

    private LocalDateTime createdAt;

    public UserReport(User reporter, User reportee, Reason reason, Type type, long targetId, String details) {
        this(0L, reporter, reportee, reason, type, targetId, details, LocalDateTime.now());
    }
}
