package com.test.obs.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jerrySuparman
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T extends Object> {
    private String status;
    private String message;
    private T data;
}
