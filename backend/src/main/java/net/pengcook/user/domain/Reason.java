package net.pengcook.user.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Reason {

    INAPPROPRIATE_CONTENT("Contains inappropriate content"),
    SPAM_CONTENT("Excessive posting or spamming"),
    ABUSIVE_LANGUAGE("Contains abusive language"),
    COPYRIGHT_INFRINGEMENT("Violates copyright"),
    OTHERS("Report for other reasons"),
    ;

    private final String message;
}
