package hhplus.cleanarchitecture.infrastructure;

import hhplus.cleanarchitecture.domain.LectureApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LectureApplicationRepository extends JpaRepository<LectureApplication, Long> {

    @Query("SELECT COUNT(la) FROM LectureApplication la WHERE la.id = :lectureId")
    int countByLectureId(@Param("lectureId") Long lectureId);
}
