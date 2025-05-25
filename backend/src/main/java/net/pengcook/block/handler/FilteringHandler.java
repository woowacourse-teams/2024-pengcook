package net.pengcook.block.handler;

import net.pengcook.block.domain.BlackList;

public interface FilteringHandler {

    boolean canHandle(Object object);

    Object handleFiltering(Object object, BlackList blackList);
}
