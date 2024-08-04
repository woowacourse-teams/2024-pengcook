package net.pengcook.user.aop;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.user.domain.AuthorAble;
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
@AllArgsConstructor
public class BlockedUserFilterAspect {

    private final UserService userService;

    @Pointcut("execution(java.util.List<net.pengcook.user.domain.AuthorAble+> net.pengcook..repository..*(..))")
    public void repositoryMethodsReturningAuthorAbleList() {
    }

    @Around("repositoryMethodsReturningAuthorAbleList()")
    public Object filterBlockedAuthorsFromList(ProceedingJoinPoint joinPoint) throws Throwable {
        List<AuthorAble> authorAbles = (List<AuthorAble>) joinPoint.proceed();

        UserInfo userInfo = getCurrentUserInfo();
        if (userInfo == null) {
            return authorAbles;
        }

        BlockedUserGroup blockedUserGroup = userService.getBlockedUserGroup(userInfo.getId());

        return filterBlockedUsers(authorAbles, blockedUserGroup);
    }

    @Pointcut("execution(java.util.Optional<net.pengcook.user.domain.AuthorAble+> net.pengcook..repository..*(..))")
    public void repositoryMethodsReturningOptionalAuthorAble() {
    }

    @Around("repositoryMethodsReturningOptionalAuthorAble()")
    public Object filterBlockedAuthorFromOptional(ProceedingJoinPoint joinPoint) throws Throwable {
        Optional<AuthorAble> authorAbleOptional = (Optional<AuthorAble>) joinPoint.proceed();

        UserInfo userInfo = getCurrentUserInfo();
        if (userInfo == null || authorAbleOptional.isEmpty()) {
            return authorAbleOptional;
        }

        BlockedUserGroup blockedUserGroup = userService.getBlockedUserGroup(userInfo.getId());
        if (blockedUserGroup.isBlocked(authorAbleOptional.get().getAuthorId())) {
            return Optional.empty();
        }

        return authorAbleOptional;
    }

    @Pointcut("execution(net.pengcook.user.domain.AuthorAble+ net.pengcook..repository..*(..))")
    public void repositoryMethodsReturningAuthorAble() {
    }

    @Around("repositoryMethodsReturningAuthorAble()")
    public Object filterBlockedAuthor(ProceedingJoinPoint joinPoint) throws Throwable {
        AuthorAble authorAble = (AuthorAble) joinPoint.proceed();

        UserInfo userInfo = getCurrentUserInfo();
        if (userInfo == null) {
            return authorAble;
        }

        BlockedUserGroup blockedUserGroup = userService.getBlockedUserGroup(userInfo.getId());
        if (blockedUserGroup.isBlocked(authorAble.getAuthorId())) {
            return null;
        }

        return authorAble;
    }

    private UserInfo getCurrentUserInfo() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            return (UserInfo) requestAttributes.getAttribute(UserInfo.class.getName(), RequestAttributes.SCOPE_REQUEST);
        } catch (IllegalStateException e) {
            return null;
        }
    }

    private List<AuthorAble> filterBlockedUsers(List<AuthorAble> authorAbles, BlockedUserGroup blockedUserGroup) {
        return authorAbles.stream()
                .filter(item -> !blockedUserGroup.isBlocked(item.getAuthorId()))
                .toList();
    }
}
