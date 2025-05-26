package net.pengcook.user.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.pengcook.user.exception.BadArgumentException;

@Entity
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Getter
public class UserBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "blocker_id")
    private User blocker;

    @ManyToOne
    @JoinColumn(name = "blockee_id")
    private User blockee;

    public UserBlock(User blocker, User blockee) {
        this(0L, blocker, blockee);
    }

    public UserBlock(long id, User blocker, User blockee) {
        validate(blocker, blockee);

        this.id = id;
        this.blocker = blocker;
        this.blockee = blockee;
    }

    private void validate(User blocker, User blockee) {
        if (blocker.equals(blockee)) {
            throw new BadArgumentException("자기 자신을 차단할 수 없습니다.");
        }
    }
}
