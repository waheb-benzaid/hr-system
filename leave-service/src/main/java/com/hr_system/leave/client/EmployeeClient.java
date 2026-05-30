package com.hr_system.leave.client;

import com.hr_system.leave.exception.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeClient {

    private final RestClient employeeRestClient;

    /**
     * Validates that an employee exists via the Employee Service.
     *
     * @param employeeId the UUID of the employee to validate
     * @return true if the employee exists, false if not found (404)
     * @throws ServiceUnavailableException if the Employee Service is unreachable or returns a server error
     */
    public boolean employeeExists(UUID employeeId) {
        try {
            employeeRestClient.get()
                    .uri("/api/v1/employees/{id}", employeeId)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Employee validated successfully: {}", employeeId);
            return true;

        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
                log.warn("Employee not found with id: {}", employeeId);
                return false;
            }
            log.error("Employee Service returned error {}: {}", ex.getStatusCode(), ex.getMessage());
            throw new ServiceUnavailableException(
                    "Employee Service returned an unexpected error. Please try again later.");

        } catch (ResourceAccessException ex) {
            log.error("Employee Service is unavailable: {}", ex.getMessage());
            throw new ServiceUnavailableException(
                    "Employee Service is currently unavailable. Please try again later.");

        } catch (Exception ex) {
            log.error("Unexpected error calling Employee Service: {}", ex.getMessage());
            throw new ServiceUnavailableException(
                    "Unable to validate employee. Please try again later.");
        }
    }
}
