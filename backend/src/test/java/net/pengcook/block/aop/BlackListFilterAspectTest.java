package net.pengcook.block.aop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import io.restassured.RestAssured;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.pengcook.authentication.annotation.WithLoginUser;
import net.pengcook.authentication.annotation.WithLoginUserTest;
import net.pengcook.block.test.TestOwnable;
import net.pengcook.block.test.TestOwnableRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;


@WithLoginUserTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/data/blocked-user.sql")
class BlackListFilterAspectTest {

    @LocalServerPort
    int port;
    @SpyBean
    private BlackListFilterAspect filterAspect;

    @Autowired
    private TestOwnableRepository ownableRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    @DisplayName("실제 api를 이용해서 테스트 한다.")
    class RealApiTest {

        @Test
        @WithLoginUser(email = "loki@pengcook.net")
        @DisplayName("레시피 목록을 조회할때 차단한 사용자들의 레시피가 보이지 않는다.")
        void filterOwnables() {
            List<Long> authorIds = RestAssured.given().log().all()
                    .queryParam("pageNumber", 0)
                    .queryParam("pageSize", 10)
                    .when()
                    .get("/recipes")
                    .then().log().all()
                    .extract().jsonPath()
                    .getList("author.authorId", Long.class);

            assertThat(authorIds).doesNotContain(2L, 4L, 6L, 8L);
        }

        @Test
        @WithLoginUser(email = "loki@pengcook.net")
        @DisplayName("레시피를 조회할때 내가 차단한 사용자의 레시피가 보이지 않는다.")
        void filterBlockeeOwnable() {
            RestAssured.given().log().all()
                    .when()
                    .get("/recipes/{id}", 2L)
                    .then().log().all()
                    .statusCode(404)
                    .body("detail", equalTo("존재하지 않는 레시피입니다."));
        }

        @Test
        @WithLoginUser(email = "ela@pengcook.net")
        @DisplayName("레시피를 조회할때 나를 차단한 사용자의 레시피가 보이지 않는다.")
        void filterBlockerOwnable() {
            RestAssured.given().log().all()
                    .when()
                    .get("/recipes/{id}", 1L)
                    .then().log().all()
                    .statusCode(404)
                    .body("detail", equalTo("존재하지 않는 레시피입니다."));
        }
    }

    @Nested
    @DisplayName("JPA 기본 메서드를 이용해서 테스트 한다.")
    class BasicJpaMethodTest {

        @Test
        @WithLoginUser(email = "loki@pengcook.net")
        @DisplayName("List<TestOwnable> findAll()")
        void findAll() throws Throwable {
            List<TestOwnable> list = ownableRepository.findAll();

            verify(filterAspect).filterBlackList(any(ProceedingJoinPoint.class));
        }

        @Test
        @WithLoginUser(email = "loki@pengcook.net")
        @DisplayName("Optional<TestOwnable> findById(Long id)")
        void findById() throws Throwable {
            Optional<TestOwnable> optional = ownableRepository.findById(1L);

            verify(filterAspect).filterBlackList(any(ProceedingJoinPoint.class));
        }

        @Test
        @WithLoginUser(email = "loki@pengcook.net")
        @DisplayName("List<TestOwnable> findAllById(Iterable<Long> ids)")
        void findAllById() throws Throwable {
            List<TestOwnable> list = ownableRepository.findAllById(List.of(1L, 2L));

            verify(filterAspect).filterBlackList(any(ProceedingJoinPoint.class));
        }

        @Test
        @WithLoginUser(email = "loki@pengcook.net")
        @DisplayName("Page<TestOwnable> findAll(Pageable pageable)")
        void findAllWithPageable() throws Throwable {
            Page<TestOwnable> page = ownableRepository.findAll(PageRequest.of(0, 10));

            verify(filterAspect).filterBlackList(any(ProceedingJoinPoint.class));
        }

        @Test
        @WithLoginUser(email = "loki@pengcook.net")
        @DisplayName("List<TestOwnable> findAll(Sort sort)")
        void findAllWithSort() throws Throwable {
            List<TestOwnable> list = ownableRepository.findAll(Sort.by("name"));

            verify(filterAspect).filterBlackList(any(ProceedingJoinPoint.class));
        }
    }

    @Nested
    @DisplayName("JPA 커스텀 메서드를 이용해서 테스트 한다.")
    class CustomJpaMethodTest {

        @Test
        @WithLoginUser(email = "loki@pengcook.net")
        @DisplayName("List<TestOwnable> findAllByOwnerIdInOrderByName(List<Long> ids)")
        void findAllByOwnerIdInOrderByName() throws Throwable {
            List<TestOwnable> list = ownableRepository.findAllByOwnerIdInOrderByName(List.of(1L, 2L));

            verify(filterAspect).filterBlackList(any(ProceedingJoinPoint.class));
        }

        @Test
        @WithLoginUser(email = "loki@pengcook.net")
        @DisplayName("Set<TestOwnable> findByOwnerId(Long ownerId)")
        void findByOwnerId() throws Throwable {
            Set<TestOwnable> set = ownableRepository.findByOwnerId(1L);

            verify(filterAspect).filterBlackList(any(ProceedingJoinPoint.class));
        }

        @Test
        @WithLoginUser(email = "loki@pengcook.net")
        @DisplayName("Optional<TestOwnable> findByName(String name)")
        void findByName() throws Throwable {
            Optional<TestOwnable> optional = ownableRepository.findByName("foo");

            verify(filterAspect).filterBlackList(any(ProceedingJoinPoint.class));
        }

        @Test
        @WithLoginUser(email = "loki@pengcook.net")
        @DisplayName("List<TestOwnable> findByOwnerIdIn(List<Long> ownerIds)")
        void findByOwnerIdIn() throws Throwable {
            List<TestOwnable> list = ownableRepository.findByOwnerIdIn(List.of(1L, 2L, 3L));

            verify(filterAspect).filterBlackList(any(ProceedingJoinPoint.class));
        }

        @Test
        @WithLoginUser(email = "loki@pengcook.net")
        @DisplayName("Page<TestOwnable> searchByNameLike(String keyword, Pageable pageable)")
        void searchByNameLike() throws Throwable {
            Page<TestOwnable> page = ownableRepository.searchByNameLike("bar", PageRequest.of(0, 5));

            verify(filterAspect).filterBlackList(any(ProceedingJoinPoint.class));
        }

        @Test
        @WithLoginUser(email = "loki@pengcook.net")
        @DisplayName("Slice<TestOwnable> searchByNameLike(String keyword, Pageable pageable)")
        void searchByName() throws Throwable {
            Slice<TestOwnable> page = ownableRepository.searchByName("bar", PageRequest.of(0, 5));

            verify(filterAspect).filterBlackList(any(ProceedingJoinPoint.class));
        }
    }
}
