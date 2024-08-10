package com.aassignment.controller;

import com.aassignment.dto.EventDTO;
import com.aassignment.dto.PetDTO;
import com.aassignment.service.PetService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/pets")
public class PetController {
    @Autowired
    private PetService petService;

    @GetMapping
    public ResponseEntity<List<String>> getAllPets(@RequestParam(value = "species", required = false) String species) {
        try {
            List<String> pets = petService.getAllPets(species);
            return new ResponseEntity<>(pets, HttpStatus.OK);
        } catch (Exception e) {
            log.error("An error occurred while retrieving pets: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, List<EventDTO>>> getPetById(@PathVariable int id, @RequestParam(required = false) String sortKey, @RequestParam(required = false) String sortOrder) {
        try {
            Map<String, List<EventDTO>> petEvents = petService.getPetByIdWithEvents(id, sortKey, sortOrder);
            return new ResponseEntity<>(petEvents, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Error while retrieving pet with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("An error occurred while retrieving pet with ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping
    public ResponseEntity<PetDTO> addPet(@RequestBody PetDTO petDTO) {
        try {
            PetDTO createdPet = petService.addPet(petDTO);
            return new ResponseEntity<>(createdPet, HttpStatus.CREATED);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<PetDTO> updatePet(@PathVariable int id, @RequestBody PetDTO petDTO) {
        try {
            PetDTO updatedPet = petService.updatePet(id, petDTO);
            return new ResponseEntity<>(updatedPet, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Validation error while updating pet with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("An error occurred while updating pet with ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/events")
    public ResponseEntity<EventDTO> addEventToPet(@PathVariable int id, @RequestBody EventDTO eventDTO) {
        try {
            EventDTO newEvent = petService.createEventForPet(id, eventDTO);
            return new ResponseEntity<>(newEvent, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.error("Error while adding event to pet with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("An error occurred while adding event to pet with ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable int id) {
        try {
            petService.deletePet(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Successfully deleted
        } catch (IllegalArgumentException e) {
            log.error("Error while deleting pet with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("An error occurred while deleting pet with ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
