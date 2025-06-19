package com.cozentus.oes.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.cozentus.oes.dto.QuestionBankDTO;
import com.cozentus.oes.helpers.Difficulty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "QUESTION_BANK") // Optional: specify table name explicitly
@Table(name = "question_bank")
@Data                       // Lombok: getters, setters, toString, equals & hashCode
@Builder                    // Lombok: fluent builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50, nullable = false, unique = true)
    private String code;

                           // large text column (maps to MySQL TEXT)
    @Column(nullable = false)
    private String question;

   
    @Column(name = "option_a")
    private String optionA;


    @Column(name = "option_b")
    private String optionB;


    @Column(name = "option_c")
    private String optionC;


    @Column(name = "option_d")
    private String optionD;

    /**
     * Stores the letter of the correct option (A–D).
     * `char` maps well to the CHAR(1) column.
     */
    
    @Column(name = "correct_option", length = 1)
    private Character correctOption;

    @Column(columnDefinition = "TEXT")
    private String comments;


    @Column(columnDefinition = "boolean default true")
    private Boolean enabled = Boolean.TRUE;

    @Enumerated(EnumType.STRING)
    @Column(length = 6)          // EASY, MEDIUM, HARD
    private Difficulty difficulty;
    
    private Integer marks;
    
    private Integer duration; // Duration in minutes

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
    

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;
    
    
    public QuestionBank(QuestionBankDTO questionBankDTO) {
    	this.code = questionBankDTO.code();
    	this.question = questionBankDTO.question();
    	this.optionA = questionBankDTO.options().get(0);
    	this.optionB = questionBankDTO.options().get(1);
    	this.optionC = questionBankDTO.options().get(2);
    	this.optionD = questionBankDTO.options().get(3);
    	this.correctOption = questionBankDTO.correctOption().charAt(0); // Assuming single character for correct option
    	this.comments = questionBankDTO.comments()!=null ? questionBankDTO.comments() : "NA";
    	this.enabled = true;
    	this.difficulty = questionBankDTO.difficulty();
    	this.marks = questionBankDTO.marks();
    	
    }


    
	public void updateQuestionFromDTO(QuestionBankDTO questionBankDTO) {
    	this.code = questionBankDTO.code();
    	this.question = questionBankDTO.question();
    	this.optionA = questionBankDTO.options().get(0);
    	this.optionB = questionBankDTO.options().get(1);
    	this.optionC = questionBankDTO.options().get(2);
    	this.optionD = questionBankDTO.options().get(3);

    	this.correctOption = questionBankDTO.correctOption().charAt(0); // Assuming single character for correct option
    	this.comments = questionBankDTO.comments()!=null ? questionBankDTO.comments() : "NA";
    	this.enabled = true;
    	this.difficulty = questionBankDTO.difficulty();
    	this.marks = questionBankDTO.marks();
	}
		// Default constructor for JPA
    
    
}
