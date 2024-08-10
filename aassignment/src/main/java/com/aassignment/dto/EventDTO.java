package com.aassignment.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class EventDTO {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    private String type;

    private String remark;
}
