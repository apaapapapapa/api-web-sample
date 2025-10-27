package com.example.sample.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response payload returned after a successful apply request.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplyResponse {

    private String message;
}
