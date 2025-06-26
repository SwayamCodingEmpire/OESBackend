package com.cozentus.oes.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "results",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_exam_student_section",
        columnNames = {"exam_student_id", "exam_section_id"}
    )
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Results {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "exam_student_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "results_ibfk_1")
    )
    private ExamStudent examStudent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "exam_section_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_results_exam_section")
    )
    private ExamSection examSection;

    @Column(name = "marks_scored", nullable = false)
    private Integer marksScored;

    @Column(name = "publish_date")
    private LocalDate publishDate;
    
    @Column(name="time_taken_in_seconds")
    private Long timeTakenInSeconds;

    @Builder.Default
    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean enabled = Boolean.TRUE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 50, updatable = false)
    private String createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;
}

