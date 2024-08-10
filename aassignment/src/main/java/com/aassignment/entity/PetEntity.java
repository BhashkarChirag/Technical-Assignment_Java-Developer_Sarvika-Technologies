package com.aassignment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "pet")
public class PetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(length = 20)
    private String owner;

    @Column(length = 20)
    private String species;

    @Column(length = 1)
    private String sex;

    @Column
    @Temporal(TemporalType.DATE)
    private Date birth;

    @Column
    @Temporal(TemporalType.DATE)
    private Date death;

    @OneToMany(mappedBy = "petEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventEntity> eventEntities;
}
