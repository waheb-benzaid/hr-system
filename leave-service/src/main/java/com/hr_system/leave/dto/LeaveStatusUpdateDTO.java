package com.hr_system.leave.dto;

import com.hr_system.leave.model.LeaveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body for updating the status of a leave request")
public class LeaveStatusUpdateDTO {

    @Schema(
            description = "New status for the leave request. Only APPROVED or REJECTED are valid values. " +
                    "Transitions are only allowed from PENDING.",
            example = "APPROVED",
            allowableValues = {"APPROVED", "REJECTED"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Status is required")
    private LeaveStatus status;
}
