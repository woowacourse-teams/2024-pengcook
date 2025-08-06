package net.pengcook.block.handler;

import java.util.Collection;
import net.pengcook.block.domain.BlackList;
import net.pengcook.block.domain.Ownable;
import org.springframework.stereotype.Component;

@Component
public class CollectionFilteringHandler implements FilteringHandler {

    @Override
    public boolean canHandle(Object object) {
        return object instanceof Collection<?>;
    }

    @Override
    public Collection<?> handleFiltering(Object object, BlackList blackList) {
        Collection<?> collection = (Collection<?>) object;

        collection.removeIf(item -> item instanceof Ownable ownable && blackList.contains(ownable.getOwnerId()));

        return collection;
    }
}
