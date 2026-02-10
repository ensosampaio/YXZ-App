package com.enzo.yxzapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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











}
