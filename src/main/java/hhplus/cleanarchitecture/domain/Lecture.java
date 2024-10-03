package hhplus.cleanarchitecture.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "lecture")
@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class Lecture extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LECTURE_ID")
    private Long id;

    private String lectureTitle;
    private LocalDateTime lectureStartTime;
    private LocalDateTime lectureEndTime;
    private int lectureMaxParticipants;

    public boolean isRegistrationAvailable() {
        return LocalDateTime.now().isBefore(lectureStartTime);
    }

    public boolean hasAvailableSeats(long currentParticipants) {
        return currentParticipants < lectureMaxParticipants;
    }
}
