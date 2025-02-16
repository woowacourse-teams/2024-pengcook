package net.pengcook.user.aop;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.user.domain.BlockeeGroup;
import net.pengcook.user.domain.BlockerGroup;
import net.pengcook.user.domain.Ownable;
import net.pengcook.user.exception.ForbiddenException;
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
public class BlockedUserFilterAspect {

    private final UserService userService;

    @Pointcut("execution(java.util.List<net.pengcook.user.domain.Ownable+> net.pengcook..repository..*(..))")
    public void repositoryMethodsReturningOwnableList() {
    }

    @Around("repositoryMethodsReturningOwnableList()")
    public Object filterBlockedAuthorsFromList(ProceedingJoinPoint joinPoint) throws Throwable {
        List<Ownable> ownables = (List<Ownable>) joinPoint.proceed();

        UserInfo userInfo = getCurrentUserInfo();
        if (userInfo == null) {
            return ownables;
        }

        BlockeeGroup blockeeGroup = userService.getBlockeeGroup(userInfo.getId());
        BlockerGroup blockerGroup = userService.getBlockerGroup(userInfo.getId());

        return filterBlockedUsers(ownables, blockeeGroup, blockerGroup);
    }

    @Pointcut("execution(java.util.Optional<net.pengcook.user.domain.Ownable+> net.pengcook..repository..*(..))")
    public void repositoryMethodsReturningOptionalOwnable() {
    }

    @Pointcut("execution(net.pengcook.user.domain.Ownable+ net.pengcook..repository..*(..))")
    public void repositoryMethodsReturningOwnable() {
    }

    @Around("repositoryMethodsReturningOwnable() || repositoryMethodsReturningOptionalOwnable()")
    public Object filterBlockedAuthor(ProceedingJoinPoint joinPoint) throws Throwable {
        Ownable ownable = getOwnable(joinPoint);
        UserInfo userInfo = getCurrentUserInfo();
        if (userInfo == null || ownable == null) {
            return ownable;
        }

        BlockeeGroup blockeeGroup = userService.getBlockeeGroup(userInfo.getId());
        if (blockeeGroup.contains(ownable.getOwnerId())) {
            throw new ForbiddenException("내가 차단한 사용자의 게시글을 이용할 수 없습니다.");
        }

        BlockerGroup blockerGroup = userService.getBlockerGroup(userInfo.getId());
        if (blockerGroup.contains(ownable.getOwnerId())) {
            throw new ForbiddenException("나를 차단한 사용자의 게시글을 이용할 수 없습니다.");
        }

        return ownable;
    }

    private Ownable getOwnable(ProceedingJoinPoint joinPoint) throws Throwable {
        Object proceed = joinPoint.proceed();
        if (proceed instanceof Optional<?>) {
            if (((Optional<?>) proceed).isPresent()) {
                return ((Optional<Ownable>) proceed).get();
            }
            return null;
        }
        return (Ownable) proceed;
    }

    private UserInfo getCurrentUserInfo() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            return (UserInfo) requestAttributes.getAttribute(UserInfo.class.getName(), RequestAttributes.SCOPE_REQUEST);
        } catch (IllegalStateException e) {
            return null;
        }
    }

    private List<Ownable> filterBlockedUsers(
            List<Ownable> ownable,
            BlockeeGroup blockeeGroup,
            BlockerGroup blockerGroup
    ) {
        return ownable.stream()
                .filter(item -> !blockeeGroup.contains(item.getOwnerId()))
                .filter(item -> !blockerGroup.contains(item.getOwnerId()))
                .toList();
    }
}
