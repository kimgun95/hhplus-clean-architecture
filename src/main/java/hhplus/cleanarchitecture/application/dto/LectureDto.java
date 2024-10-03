package hhplus.cleanarchitecture.application.dto;

import hhplus.cleanarchitecture.domain.Lecture;

import java.time.LocalDateTime;

public record LectureDto(
        Long lectureId,
        String lectureTitle,
        LocalDateTime lectureStartTime,
        LocalDateTime lectureEndTime,
        int lectureMaxParticipants
) {

    public static LectureDto from(Lecture lecture) {
        return new LectureDto(
                lecture.getId(),
                lecture.getLectureTitle(),
                lecture.getLectureStartTime(),
                lecture.getLectureEndTime(),
                lecture.getLectureMaxParticipants()
        );
    }
}
