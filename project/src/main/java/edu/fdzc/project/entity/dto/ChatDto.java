package edu.fdzc.project.entity.dto;


import lombok.Data;

@Data
public class ChatDto {

    private Long courseId;

    private Boolean memory;

    private String msg;

}