package net.pengcook.block.aop;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.block.domain.BlackList;
import net.pengcook.block.handler.FilteringHandler;
import net.pengcook.user.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Aspect
@Component
@RequiredArgsConstructor
public class BlackListFilterAspect {

    private final List<FilteringHandler> handlers;
    private final UserService userService;

    @Pointcut("within(net.pengcook.block.repository.OwnableRepository+)")
    public void ownableRepositoryMethods() {
    }

    @Around("ownableRepositoryMethods()")
    public Object filterBlackList(ProceedingJoinPoint joinPoint) throws Throwable {

        Object object = joinPoint.proceed();

        FilteringHandler handler = getFilteringHandler(object);
        if (handler == null) {
            return object;
        }

        UserInfo userInfo = getCurrentUserInfo();
        if (userInfo == null) {
            return object;
        }
        BlackList blackList = userService.getBlackList(userInfo.getId());

        return handler.handleFiltering(object, blackList);
    }

    private FilteringHandler getFilteringHandler(Object object) {
        for (FilteringHandler handler : handlers) {
            if (handler.canHandle(object)) {
                return handler;
            }
        }
        return null;
    }

    private UserInfo getCurrentUserInfo() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            return (UserInfo) requestAttributes.getAttribute(UserInfo.class.getName(), RequestAttributes.SCOPE_REQUEST);
        } catch (IllegalStateException e) {
            return null;
        }
    }
}
