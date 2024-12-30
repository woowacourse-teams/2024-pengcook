package net.pengcook.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pengcook.user.exception.BadArgumentException;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"follower_id", "followee_id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Getter
public class UserFollow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private User follower;

    @ManyToOne
    @JoinColumn(name = "followee_id")
    private User followee;

    public UserFollow(User follower, User followee) {
        validate(follower, followee);
        this.follower = follower;
        this.followee = followee;
    }

    private void validate(User follower, User followee) {
        if (follower.equals(followee)) {
            throw new BadArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }
    }
}
