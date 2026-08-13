package com.saattech.dto.request;

import com.saattech.enums.LicenseStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class LicenseRequestDto {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private LicenseStatus status;
}
