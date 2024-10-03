package hhplus.cleanarchitecture.application.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum LectureErrorCode implements ErrorCode {

    LECTURE_NOT_FOUND(HttpStatus.BAD_REQUEST, "특강을 찾을 수 없습니다"),
    EXPIRED_LECTURE(HttpStatus.BAD_REQUEST, "이미 종료된 특강입니다"),
    NOT_ENOUGH_LECTURE_SEATS(HttpStatus.BAD_REQUEST, "특강 정원이 꽉 찼습니다"),
    DUPLICATE_LECTURE_APPLICATION(HttpStatus.BAD_REQUEST, "이미 신청한 특강입니다")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
