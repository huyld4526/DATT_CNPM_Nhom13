package com.sachcu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStatusResponse {
    private Integer userID;
    private String status;
}
