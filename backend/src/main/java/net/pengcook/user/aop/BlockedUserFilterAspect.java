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
    public void repositoryMethodsReturningListOfAuthorAble() {
    }

    @Around("repositoryMethodsReturningListOfAuthorAble()")
    public Object filterAuthorAbles(ProceedingJoinPoint joinPoint) throws Throwable {
        List<AuthorAble> authorAbles = (List<AuthorAble>) joinPoint.proceed();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        UserInfo userInfo = (UserInfo) requestAttributes.getAttribute(UserInfo.class.getName(), RequestAttributes.SCOPE_REQUEST);

        if (userInfo == null) {
            return authorAbles;
        }

        BlockedUserGroup blockedUserGroup = userService.getBlockedUserGroup(userInfo.getId());

        return authorAbles.stream()
                .filter(item -> !blockedUserGroup.isBlocked(item.getAuthorId()))
                .toList();
    }

    @Pointcut("execution(java.util.Optional<net.pengcook.user.domain.AuthorAble+> net.pengcook..repository..*(..))")
    public void repositoryFindMethodsReturningSingleAuthorAble() {
    }

    @Around("repositoryFindMethodsReturningSingleAuthorAble()")
    public Object filterAuthorAbleFind(ProceedingJoinPoint joinPoint) throws Throwable {
        Optional<AuthorAble> authorAbleOptional = (Optional<AuthorAble>) joinPoint.proceed();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        UserInfo userInfo = (UserInfo) requestAttributes.getAttribute(UserInfo.class.getName(), RequestAttributes.SCOPE_REQUEST);

        if (userInfo == null) {
            return authorAbleOptional;
        }
        BlockedUserGroup blockedUserGroup = userService.getBlockedUserGroup(userInfo.getId());

        if (authorAbleOptional.isPresent()) {
            AuthorAble authorAble = authorAbleOptional.get();
            if (!blockedUserGroup.isBlocked(authorAble.getAuthorId())) {
                return authorAbleOptional;
            }
        }
        return Optional.empty();
    }

    @Pointcut("execution(net.pengcook.user.domain.AuthorAble+ net.pengcook..repository..*(..))")
    public void repositoryGetMethodsReturningSingleAuthorAble() {
    }

    @Around("repositoryGetMethodsReturningSingleAuthorAble()")
    public Object filterAuthorAbleGet(ProceedingJoinPoint joinPoint) throws Throwable {
        AuthorAble authorAble = (AuthorAble) joinPoint.proceed();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        UserInfo userInfo = (UserInfo) requestAttributes.getAttribute(UserInfo.class.getName(), RequestAttributes.SCOPE_REQUEST);

        if (userInfo == null) {
            return authorAble;
        }
        BlockedUserGroup blockedUserGroup = userService.getBlockedUserGroup(userInfo.getId());

        if (blockedUserGroup.isBlocked(authorAble.getAuthorId())) {
            return null;
        }
        return authorAble;
    }
}
