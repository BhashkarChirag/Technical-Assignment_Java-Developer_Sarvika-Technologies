package com.aassignment.repository;

import com.aassignment.entity.PetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<PetEntity, Integer> {

    List<PetEntity> findBySpecies(String species);

    PetEntity findById(int id);
}
