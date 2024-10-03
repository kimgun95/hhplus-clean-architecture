package hhplus.cleanarchitecture.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lecture_application",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"lecture_id", "applicant_id"})
        })
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class LectureApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LECTURE_APPLICATION_ID")
    private Long id;

    @Column(name = "lecture_id")
    private Long lectureId;

    @Column(name = "applicant_id")
    private String applicantId;

    private LectureApplication(Long lectureId, String applicantId) {
        this.lectureId = lectureId;
        this.applicantId = applicantId;
    }

    public static LectureApplication of(Long lectureId, String applicantId) {

        return new LectureApplication(
                lectureId,
                applicantId
        );
    }
}
