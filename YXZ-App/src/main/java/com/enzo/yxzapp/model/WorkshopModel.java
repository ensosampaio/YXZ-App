package com.enzo.yxzapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workshops")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkshopModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String school;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkshopType type;

    @Column(name = "school_contact", nullable = false)
    private String schoolContact;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Segment segment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Shift shift;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClassGroup classGroup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkshopStatus status = WorkshopStatus.AGENDADA;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_id", nullable = false)
    private UserModel user;

    @Column(name = "created_name", nullable = false)
    private String userName;

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_update_id")
    private UserModel lastUpdate;

    @Column(name = "last_update_name")
    private String lastUpdateName;

    @UpdateTimestamp
    @Column(name = "update_data")
    private LocalDateTime updateData;

    @ElementCollection
    @CollectionTable(name = "workshop_instructors", joinColumns = @JoinColumn(name = "workshop_id"))
    @Column(name = "instructor")
    private List<String> instructor = new ArrayList<>();




}
