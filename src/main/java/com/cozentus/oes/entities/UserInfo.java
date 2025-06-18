package com.cozentus.oes.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    private String phoneNo; // DB: phone_no
    
    @ManyToOne
    @JoinColumn(name = "credential_id", nullable = false)
    private Credentials credentials;

    // Only the required fields for your API
}
