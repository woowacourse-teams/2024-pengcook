package net.pengcook.block.handler;

import java.util.Optional;
import net.pengcook.block.domain.BlackList;
import net.pengcook.block.domain.Ownable;
import org.springframework.stereotype.Component;

@Component
public class OptionalFilteringHandler implements FilteringHandler {

    @Override
    public boolean canHandle(Object object) {
        return object instanceof Optional<?>;
    }

    @Override
    public Optional<?> handleFiltering(Object object, BlackList blackList) {
        Optional<?> optional = (Optional<?>) object;
        if (optional.isEmpty()) {
            return optional;
        }

        Object value = optional.get();
        if (!(value instanceof Ownable ownable)) {
            return optional;
        }

        if (blackList.contains(ownable.getOwnerId())) {
            return Optional.empty();
        }
        return optional;
    }
}
