package com.orbyta.banking.constants;

public final class ErrorConstants {

    private ErrorConstants() {
        
    }

    // Error codes
    public static final String MISSING_PARAMETER = "MISSING_PARAMETER";
    public static final String API_ERROR = "API_ERROR";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";

    // Error field names
    public static final String CODE = "code";
    public static final String DESCRIPTION = "description";
    public static final String PARAM = "param";
    public static final String DETAILS = "details";
    public static final String VALIDATION_ERRORS = "validationErrors";
}
