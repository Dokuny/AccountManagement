package com.dokuny.accountmanagement.dto;


import com.dokuny.accountmanagement.type.ErrorCode;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExceptionResponse {
    private LocalDateTime timesStamp;
    private Integer status;
    private ErrorCode errorCode;
    private String errorMessage;

}
