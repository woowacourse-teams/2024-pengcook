package net.pengcook.block.handler;

import java.util.ArrayList;
import java.util.List;
import net.pengcook.block.domain.BlackList;
import net.pengcook.block.domain.Ownable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;

@Component
public class SliceFilteringHandler implements FilteringHandler {

    @Override
    public boolean canHandle(Object object) {
        return object instanceof Slice<?>;
    }

    @Override
    public Slice<Ownable> handleFiltering(Object object, BlackList blackList) {
        Slice<?> slice = (Slice<?>) object;
        List<?> content = slice.getContent();

        List<Ownable> filtered = new ArrayList<>();
        for (Object o : content) {
            if (o instanceof Ownable ownable && !blackList.contains(ownable.getOwnerId())) {
                filtered.add(ownable);
            }
        }

        if (slice instanceof Page<?> page) {
            return new PageImpl<>(
                    filtered,
                    page.getPageable(),
                    page.getTotalElements()
            );
        }

        return new SliceImpl<>(
                filtered,
                slice.getPageable(),
                slice.hasNext()
        );
    }
}
