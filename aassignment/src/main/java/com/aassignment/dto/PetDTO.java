package com.aassignment.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class PetDTO {
    private String name;

    private String owner;

    private String species;

    private String sex;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birth;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date death;

    private List<EventDTO> events;
}
