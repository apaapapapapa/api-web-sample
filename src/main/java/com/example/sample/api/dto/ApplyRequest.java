package com.example.sample.api.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request payload for applying detail updates.
 */
@Getter
@Setter
@NoArgsConstructor
public class ApplyRequest {

    private String userId;

    private List<Long> detailIds;
}
