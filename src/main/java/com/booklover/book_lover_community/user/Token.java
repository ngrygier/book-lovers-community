package com.booklover.book_lover_community.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.repository.config.AuditingBeanDefinitionParser;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {

    @Id
    @GeneratedValue
    private Integer id;
    private String token;
    public LocalDateTime createdAt;
    public LocalDateTime expiresAt;
    public LocalDateTime validatedAt;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

}
