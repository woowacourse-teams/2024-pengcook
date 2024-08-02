package net.pengcook.image.controller;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import io.restassured.RestAssured;
import net.pengcook.RestDocsSetting;
import net.pengcook.image.dto.PresignedUrlResponse;
import net.pengcook.image.service.S3ClientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

class S3ControllerTest extends RestDocsSetting {

    @MockBean
    private S3ClientService s3ClientService;

    @Test
    @DisplayName("presigned url을 요청한다.")
    void getPresignedUrl() {
        String presignedUrl =
                "https://bucketName.s3.region.amazonaws.com/serviceName/image/fileName?X-Amz-Security-Token=REDACTED&X-Amz-Algorithm=REDACTED&X-Amz-Date=REDACTED&X-Amz-SignedHeaders=REDACTED&X-Amz-Expires=REDACTED&X-Amz-Credential=REDACTED&X-Amz-Signature=REDACTED";

        when(s3ClientService.generatePresignedPutUrl(any()))
                .thenReturn(new PresignedUrlResponse(presignedUrl));

        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "이미지 업로드를 위한 presigned url을 요청합니다.",
                        "presigned url 요청 API",
                        queryParameters(
                                parameterWithName("fileName").description("이미지 파일 이름")
                        ),
                        responseFields(
                                fieldWithPath("url").description("presigned url")
                        )))
                .when()
                .queryParam("fileName", "testImage.jpg")
                .get("/image")
                .then().log().all()
                .statusCode(200)
                .body("url", equalTo(presignedUrl));
    }
}
