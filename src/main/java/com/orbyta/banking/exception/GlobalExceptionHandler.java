package com.orbyta.banking.exception;

import java.util.HashMap;
import java.util.Map;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import com.orbyta.banking.model.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingParams(MissingServletRequestParameterException ex) {
        String paramName = ex.getParameterName();

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("code", "MISSING_PARAMETER");
        errorDetails.put("description", "Required parameter '" + paramName + "' is missing");
        errorDetails.put("param", paramName);

        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("KO");
        apiResponse.setError(errorDetails);

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiResponse<Object>> handleRestClientException(RestClientException ex) {
        Map<String, Object> errorDetails = new HashMap<>();

        if (ex instanceof HttpClientErrorException) {
            HttpClientErrorException httpEx = (HttpClientErrorException) ex;
            errorDetails.put("code", "API_ERROR");
            errorDetails.put("description", "Error calling external API: " + httpEx.getStatusCode());
            errorDetails.put("details", httpEx.getResponseBodyAsString());

            ApiResponse<Object> apiResponse = new ApiResponse<>();
            apiResponse.setStatus("KO");
            apiResponse.setError(errorDetails);

            return new ResponseEntity<>(apiResponse, httpEx.getStatusCode());
        }

        errorDetails.put("code", "API_ERROR");
        errorDetails.put("description", "Error calling external API");
        errorDetails.put("details", ex.getMessage());

        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("KO");
        apiResponse.setError(errorDetails);

        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("code", "INTERNAL_ERROR");
        errorDetails.put("description", "An unexpected error occurred");
        errorDetails.put("details", ex.getMessage());

        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("KO");
        apiResponse.setError(errorDetails);

        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = error instanceof FieldError
                    ? ((FieldError) error).getField()
                    : error.getObjectName();

            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("code", "VALIDATION_ERROR");
        errorDetails.put("description", "Errore di validazione");
        errorDetails.put("validationErrors", validationErrors);

        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("KO");
        apiResponse.setError(errorDetails);

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
}
