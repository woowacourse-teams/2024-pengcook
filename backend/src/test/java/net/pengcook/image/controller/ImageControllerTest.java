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
import net.pengcook.image.dto.UploadUrlResponse;
import net.pengcook.image.service.ImageClientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;

class ImageControllerTest extends RestDocsSetting {

    @MockBean
    @Qualifier("imageClientService")
    private ImageClientService imageClientService;

    @Test
    @DisplayName("upload url을 반환한다.")
    void getUploadUrl() {
        String fileName = "guiny.jpg";
        String uploadUrl = "https://objectstorage.region.oraclecloud.com/p/REDACTED_TOKEN/n/namespace/b/bucketName/o/"
                + fileName;

        when(imageClientService.generateUploadUrl(any())).thenReturn(new UploadUrlResponse(uploadUrl));

        RestAssured.given(spec).log().all()
                .filter(document(DEFAULT_RESTDOCS_PATH,
                        "이미지 업로드를 위한 Upload URL을 요청합니다.",
                        "Upload URL 요청 API",
                        queryParameters(
                                parameterWithName("fileName").description("저장할 이미지 파일 이름")
                        ),
                        responseFields(
                                fieldWithPath("url").description("Upload URL")
                        )))
                .when()
                .queryParam("fileName", fileName)
                .get("/image")
                .then().log().all()
                .statusCode(200)
                .body("url", equalTo(uploadUrl));
    }
}
