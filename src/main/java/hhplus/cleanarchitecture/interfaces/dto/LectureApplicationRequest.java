package hhplus.cleanarchitecture.interfaces.dto;

import hhplus.cleanarchitecture.application.dto.LectureApplicationDto;

public record LectureApplicationRequest(
        Long lectureId,
        String applicantId
) {

    public static LectureApplicationDto toLectureApplicationDto(LectureApplicationRequest lectureApplicationRequest) {
        return new LectureApplicationDto(
                lectureApplicationRequest.lectureId(),
                lectureApplicationRequest.applicantId()
        );
    }
}
