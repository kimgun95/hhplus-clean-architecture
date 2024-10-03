package hhplus.cleanarchitecture.lecture;

import hhplus.cleanarchitecture.application.LectureService;
import hhplus.cleanarchitecture.application.dto.LectureApplicationDto;
import hhplus.cleanarchitecture.application.dto.LectureDto;
import hhplus.cleanarchitecture.application.exception.LectureErrorCode;
import hhplus.cleanarchitecture.application.exception.RestApiException;
import hhplus.cleanarchitecture.domain.Lecture;
import hhplus.cleanarchitecture.domain.LectureApplication;
import hhplus.cleanarchitecture.infrastructure.LectureApplicationRepository;
import hhplus.cleanarchitecture.infrastructure.LectureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LectureServiceTest {

    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private LectureApplicationRepository lectureApplicationRepository;

    @InjectMocks
    private LectureService sut;

    @Test
    void 모든특강을조회한다_성공() {
        // given
        Lecture lecture1 = createLecture(1L, "강의1", LocalDateTime.now().plusDays(1));
        Lecture lecture2 = createLecture(2L, "강의2", LocalDateTime.now().plusDays(2));
        given(lectureRepository.findAll()).willReturn(List.of(lecture1, lecture2));

        // when
        List<LectureDto> result = sut.searchAllLectures();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).lectureTitle()).isEqualTo("강의1");
        assertThat(result.get(1).lectureTitle()).isEqualTo("강의2");
    }

    @Nested
    @DisplayName("특강 신청 테스트")
    class ApplyForLecture {

        private LectureApplicationDto lectureApplicationDto;
        private Lecture lecture;

        @BeforeEach
        void setUp() {
            lectureApplicationDto = new LectureApplicationDto(1L, "hong-gil-dong");
            lecture = createLecture(1L, "철수의 Spring 강의", LocalDateTime.now().plusDays(1));
        }

        @Test
        void 특강신청_성공() {
            // given
            given(lectureRepository.findByIdWithPessimisticLock(1L)).willReturn(Optional.of(lecture));
            given(lectureApplicationRepository.countByLectureId(1L)).willReturn(0);

            // when
            sut.applyForLecture(lectureApplicationDto);

            // then
            verify(lectureApplicationRepository).save(any(LectureApplication.class));
        }

        @Test
        void 존재하지않는특강에신청_에러발생() {
            // given
            given(lectureRepository.findByIdWithPessimisticLock(1L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.applyForLecture(lectureApplicationDto))
                    .isInstanceOf(RestApiException.class)
                    .hasFieldOrPropertyWithValue("errorCode", LectureErrorCode.LECTURE_NOT_FOUND);
        }

        @Test
        void 이미종료된특강에신청_에러발생() {
            // given
            Lecture expiredLecture = createLecture(1L, "종료된 강의", LocalDateTime.now().minusDays(1));
            given(lectureRepository.findByIdWithPessimisticLock(1L)).willReturn(Optional.of(expiredLecture));

            // when & then
            assertThatThrownBy(() -> sut.applyForLecture(lectureApplicationDto))
                    .isInstanceOf(RestApiException.class)
                    .hasFieldOrPropertyWithValue("errorCode", LectureErrorCode.EXPIRED_LECTURE);
        }

        @Test
        void 정원초과된특강에신청_에러발생() {
            // given
            given(lectureRepository.findByIdWithPessimisticLock(1L)).willReturn(Optional.of(lecture));
            given(lectureApplicationRepository.countByLectureId(1L)).willReturn(lecture.getLectureMaxParticipants());

            // when & then
            assertThatThrownBy(() -> sut.applyForLecture(lectureApplicationDto))
                    .isInstanceOf(RestApiException.class)
                    .hasFieldOrPropertyWithValue("errorCode", LectureErrorCode.NOT_ENOUGH_LECTURE_SEATS);
        }

        @Test
        void 동일특강에중복신청_에러발생() {
            // given
            given(lectureRepository.findByIdWithPessimisticLock(1L)).willReturn(Optional.of(lecture));
            given(lectureApplicationRepository.countByLectureId(1L)).willReturn(0);
            given(lectureApplicationRepository.save(any())).willThrow(new RuntimeException());

            // when & then
            assertThatThrownBy(() -> sut.applyForLecture(lectureApplicationDto))
                    .isInstanceOf(RestApiException.class)
                    .hasFieldOrPropertyWithValue("errorCode", LectureErrorCode.DUPLICATE_LECTURE_APPLICATION);
        }
    }

    private Lecture createLecture(Long id, String title, LocalDateTime startTime) {
        return Lecture.builder()
                .id(id)
                .lectureTitle(title)
                .lectureStartTime(startTime)
                .lectureEndTime(startTime.plusHours(2))
                .lectureMaxParticipants(30)
                .build();
    }
}
