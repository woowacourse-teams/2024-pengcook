package net.pengcook.user.aop;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.user.domain.Ownable;
import net.pengcook.user.domain.BlockedUserGroup;
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

        BlockedUserGroup blockedUserGroup = userService.getBlockedUserGroup(userInfo.getId());

        return filterBlockedUsers(ownables, blockedUserGroup);
    }

    @Pointcut("execution(java.util.Optional<net.pengcook.user.domain.Ownable+> net.pengcook..repository..*(..))")
    public void repositoryMethodsReturningOptionalOwnable() {
    }

    @Around("repositoryMethodsReturningOptionalOwnable()")
    public Object filterBlockedAuthorFromOptional(ProceedingJoinPoint joinPoint) throws Throwable {
        Optional<Ownable> ownableOptional = (Optional<Ownable>) joinPoint.proceed();

        UserInfo userInfo = getCurrentUserInfo();
        if (userInfo == null || ownableOptional.isEmpty()) {
            return ownableOptional;
        }

        BlockedUserGroup blockedUserGroup = userService.getBlockedUserGroup(userInfo.getId());
        if (blockedUserGroup.isBlocked(ownableOptional.get().getOwnerId())) {
            return Optional.empty();
        }

        return ownableOptional;
    }

    @Pointcut("execution(net.pengcook.user.domain.Ownable+ net.pengcook..repository..*(..))")
    public void repositoryMethodsReturningOwnable() {
    }

    @Around("repositoryMethodsReturningOwnable()")
    public Object filterBlockedAuthor(ProceedingJoinPoint joinPoint) throws Throwable {
        Ownable ownable = (Ownable) joinPoint.proceed();

        UserInfo userInfo = getCurrentUserInfo();
        if (userInfo == null) {
            return ownable;
        }

        BlockedUserGroup blockedUserGroup = userService.getBlockedUserGroup(userInfo.getId());
        if (blockedUserGroup.isBlocked(ownable.getOwnerId())) {
            return null;
        }

        return ownable;
    }

    private UserInfo getCurrentUserInfo() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            return (UserInfo) requestAttributes.getAttribute(UserInfo.class.getName(), RequestAttributes.SCOPE_REQUEST);
        } catch (IllegalStateException e) {
            return null;
        }
    }

    private List<Ownable> filterBlockedUsers(List<Ownable> ownables, BlockedUserGroup blockedUserGroup) {
        return ownables.stream()
                .filter(item -> !blockedUserGroup.isBlocked(item.getOwnerId()))
                .toList();
    }
}
