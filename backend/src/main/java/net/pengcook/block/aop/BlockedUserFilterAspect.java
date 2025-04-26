package net.pengcook.block.aop;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.block.domain.BlockeeGroup;
import net.pengcook.block.domain.BlockerGroup;
import net.pengcook.block.domain.Ownable;
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

    @Pointcut("execution(java.util.List<net.pengcook.block.domain.Ownable+> net.pengcook..repository..*(..))")
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

    @Pointcut("within(org.springframework.data.jpa.repository.JpaRepository+)")
    public void singleOwnableRepositoryMethods() {
    }

    @Around("singleOwnableRepositoryMethods()")
    public Object filterBlockedAuthor(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        Ownable ownable = extractOwnable(result);
        if (ownable == null) {
            return result;
        }

        UserInfo userInfo = getCurrentUserInfo();
        if (userInfo == null) {
            return result;
        }

        BlockeeGroup blockeeGroup = userService.getBlockeeGroup(userInfo.getId());
        if (blockeeGroup.contains(ownable.getOwnerId())) {
            throw new ForbiddenException("내가 차단한 사용자의 게시글을 이용할 수 없습니다.");
        }

        BlockerGroup blockerGroup = userService.getBlockerGroup(userInfo.getId());
        if (blockerGroup.contains(ownable.getOwnerId())) {
            throw new ForbiddenException("나를 차단한 사용자의 게시글을 이용할 수 없습니다.");
        }

        return result;
    }

    private Ownable extractOwnable(Object result) {
        if (result instanceof Optional<?>) {
            return (Ownable) ((Optional<?>) result)
                    .filter(entity -> entity instanceof Ownable)
                    .orElse(null);
        } else if (result instanceof Ownable) {
            return (Ownable) result;
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
