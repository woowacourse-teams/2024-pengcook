package net.pengcook.image.controller;

import lombok.RequiredArgsConstructor;
import net.pengcook.image.dto.UploadUrlResponse;
import net.pengcook.image.service.ImageClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageClientService imageClientService;

    @GetMapping
    public UploadUrlResponse getUploadURL(@RequestParam String fileName) {
        return imageClientService.generateUploadUrl(fileName);
    }
}
