package com.example.setcardgame.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Error {
    private String title;
    private int status;
    private String detail;
    private String instance;
    private String description;
}
