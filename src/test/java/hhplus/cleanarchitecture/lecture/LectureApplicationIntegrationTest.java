package hhplus.cleanarchitecture.lecture;

import hhplus.cleanarchitecture.application.LectureService;
import hhplus.cleanarchitecture.application.dto.LectureApplicationDto;
import hhplus.cleanarchitecture.application.exception.RestApiException;
import hhplus.cleanarchitecture.domain.Lecture;
import hhplus.cleanarchitecture.infrastructure.LectureApplicationRepository;
import hhplus.cleanarchitecture.infrastructure.LectureRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LectureApplicationIntegrationTest {

    @Autowired
    private LectureService lectureService;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private LectureApplicationRepository lectureApplicationRepository;

    private Lecture testLecture;

    @BeforeAll
    void setUp() {
        testLecture = lectureRepository.save(
                Lecture.builder()
                        .id(1L)
                        .lectureTitle("자바/스프링 개발자를 위한 실용주의 프로그래밍")
                        .lectureStartTime(LocalDateTime.now().plusHours(1))
                        .lectureEndTime(LocalDateTime.now().plusHours(3))
                        .lectureMaxParticipants(30)
                        .build()
        );
    }

    @AfterEach
    void tearDown() {
        lectureApplicationRepository.deleteAll();
    }

    @Test
    void 동시다발적으로특강에신청이들어오면_정원만큼만성공하고나머진실패() throws InterruptedException {
        int numberOfThreads = 40;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < numberOfThreads; i++) {
            final String applicantId = "student" + i;
            executorService.submit(() -> {
                try {
                    lectureService.applyForLecture(
                            new LectureApplicationDto(testLecture.getId(), applicantId)
                    );
                    successCount.incrementAndGet();
                } catch (RestApiException e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        assertEquals(30, successCount.get(), "성공한 수강신청 수가 30이어야 합니다");
        assertEquals(10, failCount.get(), "실패한 수강신청 수가 10이어야 합니다");
        assertEquals(30, lectureApplicationRepository.countByLectureId(testLecture.getId()));
    }

    @Test
    void 특강에중복신청을한다면_에러발생() {
        String applicantId = "student1";
        LectureApplicationDto dto = new LectureApplicationDto(testLecture.getId(), applicantId);

        // 첫 번째 신청
        lectureService.applyForLecture(dto);

        // 2번째 ~ 5번째 신청 - 모두 실패해야 함
        for (int i = 2; i <= 5; i++) {
            assertThrows(RestApiException.class, () -> {
                lectureService.applyForLecture(dto);
            });
        }
    }

}
