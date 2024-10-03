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
    @DisplayName("동시에 40명이 수강신청을 시도하면 30명만 성공해야 한다")
    void concurrentApplicationTest() throws InterruptedException {
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
    @DisplayName("이미 수강신청한 학생이 다시 신청하면 예외가 발생해야 한다")
    void duplicateApplicationTest() {
        String applicantId = "student1";
        LectureApplicationDto dto = new LectureApplicationDto(testLecture.getId(), applicantId);

        // 첫 번째 신청
        lectureService.applyForLecture(dto);

        // 두 번째 신청
        assertThrows(RestApiException.class, () -> {
            lectureService.applyForLecture(dto);
        });
    }

}
