package com.virtuallive.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "role_cards")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleCard {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private User user;
    
    @Column(nullable = false)
    private String name;
    
    private String gender;
    
    private String birthday; // Format: YYYY-MM-DD
    
    private Integer height;
    
    private String hobby;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "role_card_personality_tags", joinColumns = @JoinColumn(name = "role_card_id"))
    @Column(name = "tag")
    private List<String> personalityTags;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "role_card_race_tags", joinColumns = @JoinColumn(name = "role_card_id"))
    @Column(name = "tag")
    private List<String> raceTags;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "role_card_appearance_tags", joinColumns = @JoinColumn(name = "role_card_id"))
    @Column(name = "tag")
    private List<String> appearanceTags;
    
    @Column(columnDefinition = "TEXT")
    private String backgroundStory;
    
    @Column(columnDefinition = "LONGTEXT") // Base64 or URL
    private String portrait;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.pending;
    
    @CreationTimestamp
    private LocalDateTime submitTime;
    
    public enum Status {
        pending, approved, rejected
    }
}
