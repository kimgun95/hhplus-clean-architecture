package hhplus.cleanarchitecture.infrastructure;

import hhplus.cleanarchitecture.domain.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

}
