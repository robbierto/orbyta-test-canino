package com.orbyta.banking.constants;

public final class ValidationConstants {

    private ValidationConstants() {
        
    }

    // Beneficiary types
    public static final String NATURAL_PERSON = "NATURAL_PERSON";
    public static final String LEGAL_PERSON = "LEGAL_PERSON";
    public static final String BENEFICIARY_TYPE_PATTERN = NATURAL_PERSON + "|" + LEGAL_PERSON;

    // Fee types
    public static final String FEE_TYPE_PATTERN = "SHA|OUR|BEN";

    // Tax relief IDs
    public static final String TAX_RELIEF_ID_PATTERN = "119R|DL50|L296|L449|L234";

    // Validation limits
    public static final int MAX_DESCRIPTION_LENGTH = 140;
    public static final int MAX_CREDITOR_NAME_LENGTH = 70;
    public static final int MAX_ADDRESS_LENGTH = 40;
}
