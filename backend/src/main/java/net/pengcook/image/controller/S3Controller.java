package net.pengcook.image.controller;

import lombok.RequiredArgsConstructor;
import net.pengcook.image.dto.PresignedUrlResponse;
import net.pengcook.image.service.S3ClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class S3Controller {

    private final S3ClientService s3ClientService;

    @GetMapping
    public PresignedUrlResponse getPresignedURL(@RequestParam String keyName) {
        return s3ClientService.generatePresignedPutUrl(keyName);
    }
}
