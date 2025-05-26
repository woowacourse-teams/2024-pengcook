package net.pengcook.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
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

    public User(String email, String username, String nickname, String image, String region) {
        this(0L, email, username, nickname, image, region);
    }

    public boolean isSameUser(long userId) {
        return this.id == userId;
    }

    public void update(String username, String nickname, String image, String region) {
        this.username = username;
        this.nickname = nickname;
        this.image = image;
        this.region = region;
    }
}
