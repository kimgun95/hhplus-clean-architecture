package hhplus.cleanarchitecture.interfaces.controller;

import hhplus.cleanarchitecture.application.LectureService;
import hhplus.cleanarchitecture.interfaces.dto.LectureApplicationRequest;
import hhplus.cleanarchitecture.interfaces.dto.LectureResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;

    @GetMapping("/lecture")
    public ResponseEntity<List<LectureResponse>> getAllLectures() {

        return ResponseEntity.status(HttpStatus.OK)
                .body(lectureService.searchAllLectures().stream()
                        .map(LectureResponse::from)
                        .toList()
                );
    }

    @PostMapping("/lecture/apply")
    public ResponseEntity<String> applyLecture(
            @RequestBody LectureApplicationRequest request) {

        lectureService.applyForLecture(LectureApplicationRequest.toLectureApplicationDto(request));
        return ResponseEntity.status(HttpStatus.OK)
                .body("성공적으로 신청되었습니다.");
    }

}
