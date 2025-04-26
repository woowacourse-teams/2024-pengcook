package net.pengcook.block.test;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.Objects;
import net.pengcook.block.domain.Ownable;

@Entity
public class TestOwnable implements Ownable {

    @Id
    @GeneratedValue
    private Long ownerId;

    private String name;

    protected TestOwnable() {
    }

    public TestOwnable(Long ownerId) {
        this.ownerId = ownerId;
    }


    @Override
    public long getOwnerId() {
        return ownerId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        TestOwnable ownable = (TestOwnable) object;
        return Objects.equals(ownerId, ownable.ownerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerId);
    }
}
