package net.pengcook.block.handler;

import net.pengcook.block.domain.BlackList;
import net.pengcook.block.domain.Ownable;
import org.springframework.stereotype.Component;

@Component
public class OwnableFilteringHandler implements FilteringHandler {

    @Override
    public boolean canHandle(Object object) {
        return object instanceof Ownable;
    }

    @Override
    public Ownable handleFiltering(Object object, BlackList blackList) {
        Ownable ownable = (Ownable) object;

        if (blackList.contains(ownable.getOwnerId())) {
            return null;
        }

        return ownable;
    }
}
