package com.cozentus.oes.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "credentials")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Credentials {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;
    private String email;
    private String password;
    private String role;

   
}
