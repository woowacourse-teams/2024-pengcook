package net.pengcook.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    @ColumnDefault("''")
    private String introduction;

    @Column(nullable = false)
    @ColumnDefault("0")
    private long followerCount;

    @Column(nullable = false)
    @ColumnDefault("0")
    private long followeeCount;

    public User(
            String email,
            String username,
            String nickname,
            String image,
            String region,
            String introduction
    ) {
        this(0L, email, username, nickname, image, region, introduction, 0, 0);
    }

    public boolean isSameUser(long userId) {
        return this.id == userId;
    }

    public void update(String username, String nickname, String image, String region, String introduction) {
        this.username = username;
        this.nickname = nickname;
        this.image = image;
        this.region = region;
        this.introduction = introduction;
    }

    public void increaseFollowerCount() {
        followerCount++;
    }

    public void decreaseFollowerCount() {
        followerCount--;
    }

    public void increaseFolloweeCount() {
        followeeCount++;
    }

    public void decreaseFolloweeCount() {
        followeeCount--;
    }
}
