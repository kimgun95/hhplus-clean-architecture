package hhplus.cleanarchitecture.interfaces.dto;

import hhplus.cleanarchitecture.application.dto.LectureDto;

import java.time.LocalDateTime;

public record LectureResponse(
        Long lectureId,
        String lectureTitle,
        LocalDateTime lectureStartTime,
        LocalDateTime lectureEndTime,
        int lectureMaxParticipants
) {

    public static LectureResponse from(LectureDto lectureDto) {
        return new LectureResponse(
                lectureDto.lectureId(),
                lectureDto.lectureTitle(),
                lectureDto.lectureStartTime(),
                lectureDto.lectureEndTime(),
                lectureDto.lectureMaxParticipants()
        );
    }
}
