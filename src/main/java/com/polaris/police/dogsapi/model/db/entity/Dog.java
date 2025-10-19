package com.polaris.police.dogsapi.model.db.entity;

import com.polaris.police.dogsapi.model.enums.DogStatus;
import com.polaris.police.dogsapi.model.enums.Gender;
import com.polaris.police.dogsapi.model.enums.LeavingReason;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dogs")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class Dog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "breed")
    private String breed;

    @Column(name = "badge_id")
    private String badgeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "date_acquired")
    private LocalDate dateAcquired;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status")
    private DogStatus currentStatus;

    @Column(name = "leaving_date")
    private LocalDate leavingDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "leaving_reason")
    private LeavingReason leavingReason;

    @Column(name = "kennelling_characteristic")
    private String kennellingCharacteristic;

    @Column(name = "deleted")
    private Boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
}
