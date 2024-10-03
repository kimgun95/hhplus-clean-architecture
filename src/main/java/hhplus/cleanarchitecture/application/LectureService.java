package hhplus.cleanarchitecture.application;

import hhplus.cleanarchitecture.application.dto.LectureApplicationDto;
import hhplus.cleanarchitecture.application.dto.LectureDto;
import hhplus.cleanarchitecture.application.exception.LectureErrorCode;
import hhplus.cleanarchitecture.application.exception.RestApiException;
import hhplus.cleanarchitecture.domain.Lecture;
import hhplus.cleanarchitecture.domain.LectureApplication;
import hhplus.cleanarchitecture.infrastructure.LectureApplicationRepository;
import hhplus.cleanarchitecture.infrastructure.LectureRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureRepository lectureRepository;
    private final LectureApplicationRepository lectureApplicationRepository;

    public List<LectureDto> searchAllLectures() {
        return lectureRepository.findAll().stream()
                .map(LectureDto::from)
                .toList();
    }

    @Transactional
    public void applyForLecture(LectureApplicationDto lectureApplicationDto) {
        final Long lectureId = lectureApplicationDto.lectureId();
        final String applicantId = lectureApplicationDto.applicantId();

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new RestApiException(LectureErrorCode.LECTURE_NOT_FOUND));

        // 이미 종료된 특강인지 확인
        if (!lecture.isRegistrationAvailable()) {
            throw new RestApiException(LectureErrorCode.EXPIRED_LECTURE);
        }

        // 잔여 수강 인원 확인
        int currentParticipants = lectureApplicationRepository.countByLectureId(lectureId);
        if (!lecture.hasAvailableSeats(currentParticipants)) {
            throw new RestApiException(LectureErrorCode.NOT_ENOUGH_LECTURE_SEATS);
        }

        // 이미 수강 신청을 했는지 확인
        try {
            lectureApplicationRepository.save(LectureApplication.of(lectureId, applicantId));
        } catch (Exception e) {
            throw new RestApiException(LectureErrorCode.DUPLICATE_LECTURE_APPLICATION);
        }
    }

}
