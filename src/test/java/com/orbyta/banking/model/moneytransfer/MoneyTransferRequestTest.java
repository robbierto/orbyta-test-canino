package com.orbyta.banking.model.moneytransfer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.orbyta.banking.constants.ValidationConstants;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class MoneyTransferRequestTest {

    private Validator validator;
    private MoneyTransferRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // Crea una richiesta di bonifico valida per i test
        request = createValidMoneyTransferRequest();
    }

    /**
     * Test che verifica che una richiesta di bonifico valida passi tutte le
     * validazioni.
     * 
     * Questo test crea una richiesta di bonifico completa con tutti i campi
     * obbligatori
     * e verifica che non ci siano violazioni di validazione.
     */
    @Test
    void validRequest_shouldPassValidation() {
        // When
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty(), "Valid request should have no violations");
    }

    /**
     * Test che verifica la validazione quando manca il creditore.
     * 
     * Il creditore è un campo obbligatorio per un bonifico e questo test
     * assicura che venga generato un errore di validazione appropriato
     * quando questo campo manca.
     */
    @Test
    void missingCreditor_shouldFailValidation() {
        // Given
        request.setCreditor(null);

        // When
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Il creditore è obbligatorio", violations.iterator().next().getMessage());
    }

    /**
     * Test che verifica la validazione quando manca la descrizione.
     * 
     * La descrizione è un campo obbligatorio che spiega lo scopo del bonifico
     * e questo test assicura che venga generato un errore di validazione
     * quando questo campo manca.
     */
    @Test
    void missingDescription_shouldFailValidation() {
        // Given
        request.setDescription(null);

        // When
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("La descrizione è obbligatoria", violations.iterator().next().getMessage());
    }

    /**
     * Test che verifica la validazione quando la descrizione è troppo lunga.
     * 
     * Questo test assicura che venga generato un errore di validazione quando
     * la descrizione supera la lunghezza massima consentita.
     */
    @Test
    void descriptionTooLong_shouldFailValidation() {
        // Given
        StringBuilder longDescription = new StringBuilder();
        for (int i = 0; i < ValidationConstants.MAX_DESCRIPTION_LENGTH + 10; i++) {
            longDescription.append("a");
        }
        request.setDescription(longDescription.toString());

        // When
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage()
                .contains("La descrizione non deve superare i " + ValidationConstants.MAX_DESCRIPTION_LENGTH
                        + " caratteri"));
    }

    /**
     * Test che verifica la validazione quando manca l'importo.
     * 
     * L'importo è un campo obbligatorio per un bonifico e questo test
     * assicura che venga generato un errore di validazione appropriato
     * quando questo campo manca.
     */
    @Test
    void missingAmount_shouldFailValidation() {
        // Given
        request.setAmount(null);

        // When
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("L'importo è obbligatorio", violations.iterator().next().getMessage());
    }

    /**
     * Test che verifica la validazione quando manca la valuta.
     * 
     * La valuta specifica in quale moneta viene effettuato il bonifico
     * e questo test assicura che venga generato un errore di validazione
     * quando questo campo manca.
     */
    @Test
    void missingCurrency_shouldFailValidation() {
        // Given
        request.setCurrency(null);

        // When
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("La valuta è obbligatoria", violations.iterator().next().getMessage());
    }

    /**
     * Test che verifica la validazione quando il tipo di commissione non è valido.
     * 
     * Questo test assicura che venga generato un errore di validazione quando
     * il tipo di commissione non è uno dei valori consentiti (SHA, OUR, BEN).
     */
    @Test
    void invalidFeeType_shouldFailValidation() {
        // Given
        request.setFeeType("INVALID");

        // When
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage()
                .contains("Il tipo di commissione deve essere uno tra: SHA, OUR, BEN"));
    }

    /**
     * Test che verifica la validazione dei dettagli fiscali per persone fisiche.
     * 
     * Questo test assicura che quando il beneficiario è una persona fisica,
     * i dettagli specifici della persona fisica siano obbligatori.
     */
    @Test
    void naturalPersonBeneficiaryWithoutDetails_shouldFailValidation() {
        // Given
        MoneyTransferRequest.TaxRelief taxRelief = new MoneyTransferRequest.TaxRelief();
        taxRelief.setBeneficiaryType(ValidationConstants.NATURAL_PERSON);
        taxRelief.setCreditorFiscalCode("ABCDEF12G34H567I");
        taxRelief.setCondoUpgrade(false);
        request.setTaxRelief(taxRelief);

        // When
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        boolean hasExpectedViolation = false;
        for (ConstraintViolation<MoneyTransferRequest> violation : violations) {
            if (violation.getMessage().contains("I dettagli della persona fisica sono obbligatori")) {
                hasExpectedViolation = true;
                break;
            }
        }
        assertTrue(hasExpectedViolation, "Should have validation error for missing natural person details");
    }

    /**
     * Test che verifica la validazione dei dettagli fiscali per persone giuridiche.
     * 
     * Questo test assicura che quando il beneficiario è una persona giuridica
     * i dettagli specifici della persona giuridica siano obbligatori.
     */
    @Test
    void legalPersonBeneficiaryWithoutDetails_shouldFailValidation() {
        // Given
        MoneyTransferRequest.TaxRelief taxRelief = new MoneyTransferRequest.TaxRelief();
        taxRelief.setBeneficiaryType(ValidationConstants.LEGAL_PERSON);
        taxRelief.setCreditorFiscalCode("12345678901");
        taxRelief.setCondoUpgrade(false);
        request.setTaxRelief(taxRelief);

        // When
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        boolean hasExpectedViolation = false;
        for (ConstraintViolation<MoneyTransferRequest> violation : violations) {
            if (violation.getMessage().contains("I dettagli della persona giuridica sono obbligatori")) {
                hasExpectedViolation = true;
                break;
            }
        }
        assertTrue(hasExpectedViolation, "Should have validation error for missing legal person details");
    }

    private MoneyTransferRequest createValidMoneyTransferRequest() {
        MoneyTransferRequest request = new MoneyTransferRequest();

        MoneyTransferRequest.Creditor creditor = new MoneyTransferRequest.Creditor();
        creditor.setName("John Doe");

        MoneyTransferRequest.Creditor.Account account = new MoneyTransferRequest.Creditor.Account();
        account.setAccountCode("IT60X0542811101000000123456");
        creditor.setAccount(account);

        request.setCreditor(creditor);
        request.setDescription("Test money transfer");
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("EUR");
        request.setExecutionDate(LocalDate.now().plusDays(1));
        request.setFeeType("SHA");

        return request;
    }
}
