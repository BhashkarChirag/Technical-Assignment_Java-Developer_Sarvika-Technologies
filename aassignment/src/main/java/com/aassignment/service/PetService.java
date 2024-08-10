package com.aassignment.service;

import com.aassignment.dto.EventDTO;
import com.aassignment.dto.PetDTO;
import com.aassignment.entity.EventEntity;
import com.aassignment.entity.PetEntity;
import com.aassignment.repository.EventRepository;
import com.aassignment.repository.PetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PetService {
    @Autowired
    PetRepository petRepository;

    @Autowired
    EventRepository eventRepository;

    public List<String> getAllPets(String species) {
        List<String> petsList = new ArrayList<>();
        List<PetEntity> petEntities;
        try {
            if (species != null && !species.trim().isEmpty()) {
                log.info("Fetching pets with species: {}", species);
                petEntities = petRepository.findBySpecies(species);
            } else {
                log.info("Fetching all pets.");
                petEntities = petRepository.findAll();
            }
            for (PetEntity petEntity : petEntities) {
                petsList.add(petEntity.getName());
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching pets", e);
            throw new RuntimeException("Error occurred while fetching pets", e);
        }
        return petsList;
    }


    public Map<String, List<EventDTO>> getPetByIdWithEvents(int id, String sortKey, String sortOrder) {
        Map<String, List<EventDTO>> petEvents = new HashMap<>();
        try {
            PetEntity petEntity = petRepository.findById(id);
            if (petEntity == null) {
                log.warn("Pet with ID {} not found", id);
                return petEvents;
            }
            List<EventEntity> eventEntities = petEntity.getEventEntities();
            log.info("Retrieved {} events for pet {}", eventEntities.size(), petEntity.getName());

            Comparator<EventEntity> comparator;
            if (sortKey != null && !sortKey.trim().isEmpty()) {
                switch (sortKey.toLowerCase()) {
                    case "date":
                        comparator = Comparator.comparing(EventEntity::getDate);
                        break;
                    case "type":
                        comparator = Comparator.comparing(EventEntity::getType);
                        break;
                    default:
                        comparator = Comparator.comparing(EventEntity::getDate); // Default sorting by date
                        break;
                }
            } else {
                comparator = Comparator.comparing(EventEntity::getDate); // Default sorting by date
            }

            boolean ascending = "asc".equalsIgnoreCase(sortOrder);
            if (!ascending) {
                comparator = comparator.reversed();
            }

            eventEntities.sort(comparator);

            log.info("Sorted events for pet {}: {}", petEntity.getName(), eventEntities);

            List<EventDTO> eventDTOs = new ArrayList<>();
            for (EventEntity eventEntity : eventEntities) {
                EventDTO eventDTO = convertToEventDTO(eventEntity);
                eventDTOs.add(eventDTO);
            }

            PetDTO petDTO = convertToPetDTO(petEntity);
            petEvents.put(petDTO.getName(), eventDTOs);
            log.info("Created event DTOs for pet {}", petDTO.getName());

        } catch (Exception e) {
            log.error("An error occurred while processing events for pet with ID {}", id, e);
        }
        return petEvents;
    }

    public PetDTO addPet(PetDTO petCreationDTO) {
        PetEntity petEntity = new PetEntity();
        try {
            if (petCreationDTO.getName() == null || petCreationDTO.getName().trim().isEmpty()) {
                log.error("Pet name cannot be null or empty");
                throw new IllegalArgumentException("Pet name cannot be null or empty");
            }
            petEntity.setName(petCreationDTO.getName());
            petEntity.setOwner(petCreationDTO.getOwner());
            petEntity.setSpecies(petCreationDTO.getSpecies());
            petEntity.setSex(petCreationDTO.getSex());
            petEntity.setBirth(petCreationDTO.getBirth());
            petEntity.setDeath(petCreationDTO.getDeath());

            PetEntity savedPetEntity = petRepository.save(petEntity);
            log.info("Successfully created pet with ID {}", savedPetEntity.getId());
            return convertToPetDTO(savedPetEntity);

        } catch (IllegalArgumentException e) {
            log.error("Validation error while creating pet: {}", e.getMessage());
            throw e; // Re-throw exception to be handled by the controller
        } catch (Exception e) {
            log.error("An error occurred while creating pet: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create pet", e); // Wrap and re-throw
        }
    }

    public PetDTO updatePet(int id, PetDTO petDTO) {
        try {
            PetEntity petEntity = petRepository.findById(id);
            if (petEntity == null) {
                log.error("Pet with ID {} not found", id);
                throw new IllegalArgumentException("Pet not found with ID " + id);
            }
            if (petDTO.getName() == null || petDTO.getName().trim().isEmpty()) {
                log.error("Pet name cannot be null or empty");
                throw new IllegalArgumentException("Pet name cannot be null or empty");
            }

            petEntity.setName(petDTO.getName());
            petEntity.setOwner(petDTO.getOwner());
            petEntity.setSpecies(petDTO.getSpecies());
            petEntity.setSex(petDTO.getSex());
            petEntity.setBirth(petDTO.getBirth());
            petEntity.setDeath(petDTO.getDeath());

            PetEntity updatedPetEntity = petRepository.save(petEntity);
            log.info("Successfully updated pet with ID {}", updatedPetEntity.getId());
            return convertToPetDTO(updatedPetEntity);

        } catch (IllegalArgumentException e) {
            // Handle specific validation errors
            log.error("Validation error while updating pet: {}", e.getMessage());
            throw e; // Re-throw exception to be handled by the controller
        } catch (Exception e) {
            // Handle general exceptions
            log.error("An error occurred while updating pet with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update pet", e); // Wrap and re-throw
        }
    }

    public EventDTO createEventForPet(int petId, EventDTO eventDTO) {
        try {
            PetEntity petEntity = petRepository.findById(petId);
            if (petEntity == null) {
                log.error("Pet with ID {} not found", petId);
                throw new IllegalArgumentException("Pet not found with ID " + petId);
            }

            EventEntity eventEntity = new EventEntity();
            eventEntity.setDate(eventDTO.getDate());
            eventEntity.setType(eventDTO.getType());
            eventEntity.setRemark(eventDTO.getRemark());
            eventEntity.setPetEntity(petEntity);

            EventEntity savedEventEntity = eventRepository.save(eventEntity);

            petEntity.getEventEntities().add(savedEventEntity);
            petRepository.save(petEntity);

            log.info("Successfully created event with ID {} for pet with ID {}", savedEventEntity.getId(), petId);
            return convertToEventDTO(savedEventEntity);

        } catch (IllegalArgumentException e) {
            // Handle pet not found error
            log.error("Error creating event for pet with ID {}: {}", petId, e.getMessage());
            throw e; // Re-throw exception to be handled by the controller
        } catch (Exception e) {
            // Handle general exceptions
            log.error("An error occurred while creating event for pet with ID {}: {}", petId, e.getMessage(), e);
            throw new RuntimeException("Failed to create event for pet with ID " + petId, e); // Wrap and re-throw
        }
    }

    public void deletePet(int id) {
        try {
            PetEntity petEntity = petRepository.findById(id);
            if (petEntity == null) {
                log.error("Pet with ID {} not found", id);
                throw new IllegalArgumentException("Pet not found with ID " + id);
            }
            petRepository.delete(petEntity);
            log.info("Successfully deleted pet with ID {}", id);

        } catch (IllegalArgumentException e) {
            log.error("Error deleting pet with ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("An error occurred while deleting pet with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete pet with ID " + id, e);
        }
    }

    private PetDTO convertToPetDTO(PetEntity petEntity) {
        PetDTO petDTO = new PetDTO();
        petDTO.setName(petEntity.getName());
        petDTO.setOwner(petEntity.getOwner());
        petDTO.setSpecies(petEntity.getSpecies());
        petDTO.setSex(petEntity.getSex());
        petDTO.setBirth(petEntity.getBirth());
        petDTO.setDeath(petEntity.getDeath());
        return petDTO;
    }

    private EventDTO convertToEventDTO(EventEntity eventEntity) {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setDate(eventEntity.getDate());
        eventDTO.setType(eventEntity.getType());
        return eventDTO;
    }

}
