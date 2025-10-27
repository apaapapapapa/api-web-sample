package com.example.sample.api.dto;

import com.example.sample.dto.DetailRowView;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JSON payload for detail information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetailResponse {

    private Long detailId;

    private String title;

    private String status;

    public static DetailResponse fromDetailRowView(final DetailRowView rowView) {
        return new DetailResponse(rowView.getDetailId(), rowView.getTitle(), rowView.getStatus());
    }
}
