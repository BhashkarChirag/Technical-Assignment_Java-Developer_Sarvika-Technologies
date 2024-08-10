package com.aassignment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "event")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    private PetEntity petEntity;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(length = 15)
    private String type;

    @Column(length = 255)
    private String remark;
}
