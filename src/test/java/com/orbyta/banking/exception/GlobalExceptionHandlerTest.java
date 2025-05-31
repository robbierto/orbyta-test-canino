package com.orbyta.banking.exception;

import com.orbyta.banking.constants.ApiConstants;
import com.orbyta.banking.constants.ErrorConstants;
import com.orbyta.banking.model.ApiResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    /**
     * Test per la gestione dei parametri mancanti nelle richieste.
     * 
     * Questo test verifica che quando un parametro obbligatorio è mancante nella
     * richiesta,
     * il gestore delle eccezioni restituisca una risposta con:
     * - Status HTTP 400 BAD_REQUEST
     * - Status API "KO"
     * - Codice errore "MISSING_PARAMETER"
     * - Descrizione che contiene il nome del parametro mancante
     * - Il parametro mancante nella risposta
     */
    @Test
    void handleMissingParams_shouldReturnProperErrorResponse() {
        // Given
        String paramName = "fromAccountingDate";
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException(
                paramName, "String");

        // When
        ResponseEntity<ApiResponse<Object>> response = exceptionHandler.handleMissingParams(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ApiResponse<Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(ApiConstants.STATUS_KO, body.getStatus());

        @SuppressWarnings("unchecked")
        Map<String, Object> error = (Map<String, Object>) body.getError();
        assertNotNull(error);
        assertEquals(ErrorConstants.MISSING_PARAMETER, error.get(ErrorConstants.CODE));
        assertTrue(error.get(ErrorConstants.DESCRIPTION).toString().contains(paramName));
        assertEquals(paramName, error.get(ErrorConstants.PARAM));
    }

    /**
     * Test per la gestione delle eccezioni durante le chiamate ai servizi REST
     * esterni.
     * 
     * Questo test verifica che quando si verifica un errore durante una chiamata a
     * un'API esterna,
     * il gestore delle eccezioni restituisca una risposta con:
     * - Status HTTP 500 INTERNAL_SERVER_ERROR
     * - Status API "KO"
     * - Codice errore "API_ERROR"
     * - Dettagli dell'errore originale
     */
    @Test
    void handleRestClientException_shouldReturnProperErrorResponse() {
        // Given
        RestClientException ex = new RestClientException("API connection timeout");

        // When
        ResponseEntity<ApiResponse<Object>> response = exceptionHandler.handleRestClientException(ex);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ApiResponse<Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(ApiConstants.STATUS_KO, body.getStatus());

        @SuppressWarnings("unchecked")
        Map<String, Object> error = (Map<String, Object>) body.getError();
        assertNotNull(error);
        assertEquals(ErrorConstants.API_ERROR, error.get(ErrorConstants.CODE));
        assertNotNull(error.get(ErrorConstants.DESCRIPTION));
        assertEquals("API connection timeout", error.get(ErrorConstants.DETAILS));
    }

    /**
     * Test per la gestione degli errori HTTP client (4xx) dalle API esterne.
     * 
     * Questo test verifica che quando un'API esterna restituisce un errore client
     * (es. 400, 401, 403),
     * il gestore delle eccezioni restituisca una risposta con:
     * - Lo stesso Status HTTP dell'errore originale (BAD_REQUEST in questo caso)
     * - Status API "KO"
     * - Codice errore "API_ERROR"
     * - Descrizione che include il codice HTTP e il messaggio originale
     */
    @Test
    void handleHttpClientErrorException_shouldReturnProperErrorResponse() {
        // Given
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                null,
                "Invalid request".getBytes(),
                null);

        // When
        ResponseEntity<ApiResponse<Object>> response = exceptionHandler.handleRestClientException(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ApiResponse<Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(ApiConstants.STATUS_KO, body.getStatus());

        @SuppressWarnings("unchecked")
        Map<String, Object> error = (Map<String, Object>) body.getError();
        assertNotNull(error);
        assertEquals(ErrorConstants.API_ERROR, error.get(ErrorConstants.CODE));
        assertTrue(error.get(ErrorConstants.DESCRIPTION).toString().contains("400 BAD_REQUEST"));
    }

    /**
     * Test per la gestione delle eccezioni generiche non previste.
     * 
     * Questo test verifica che quando si verifica un'eccezione non gestita
     * specificamente,
     * il gestore delle eccezioni restituisca una risposta con:
     * - Status HTTP 500 INTERNAL_SERVER_ERROR
     * - Status API "KO"
     * - Codice errore "INTERNAL_ERROR"
     * - Dettagli dell'errore originale
     */
    @Test
    void handleGenericException_shouldReturnProperErrorResponse() {
        // Given
        Exception ex = new RuntimeException("Unexpected error");

        // When
        ResponseEntity<ApiResponse<Object>> response = exceptionHandler.handleGenericException(ex);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ApiResponse<Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(ApiConstants.STATUS_KO, body.getStatus());

        @SuppressWarnings("unchecked")
        Map<String, Object> error = (Map<String, Object>) body.getError();
        assertNotNull(error);
        assertEquals(ErrorConstants.INTERNAL_ERROR, error.get(ErrorConstants.CODE));
        assertNotNull(error.get(ErrorConstants.DESCRIPTION));
        assertEquals("Unexpected error", error.get(ErrorConstants.DETAILS));
    }

    /**
     * Test per la gestione degli errori di validazione dei dati di input.
     * 
     * Questo test verifica che quando una richiesta contiene dati non validi,
     * il gestore delle eccezioni restituisca una risposta con:
     * - Status HTTP 400 BAD_REQUEST
     * - Status API "KO"
     * - Codice errore "VALIDATION_ERROR"
     * - Mappa di errori di validazione (campo -> messaggio di errore)
     */
    @Test
    void handleValidationExceptions_shouldReturnProperErrorResponse() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError("moneyTransferRequest", "creditor", "Il creditore è obbligatorio"));
        fieldErrors.add(new FieldError("moneyTransferRequest", "amount", "L'importo è obbligatorio"));

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(new ArrayList<>(fieldErrors));

        // When
        ResponseEntity<ApiResponse<Object>> response = exceptionHandler.handleValidationExceptions(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ApiResponse<Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(ApiConstants.STATUS_KO, body.getStatus());

        @SuppressWarnings("unchecked")
        Map<String, Object> error = (Map<String, Object>) body.getError();
        assertNotNull(error);
        assertEquals(ErrorConstants.VALIDATION_ERROR, error.get(ErrorConstants.CODE));

        @SuppressWarnings("unchecked")
        Map<String, String> validationErrors = (Map<String, String>) error.get(ErrorConstants.VALIDATION_ERRORS);
        assertNotNull(validationErrors);
        assertEquals(2, validationErrors.size());
        assertEquals("Il creditore è obbligatorio", validationErrors.get("creditor"));
        assertEquals("L'importo è obbligatorio", validationErrors.get("amount"));
    }
}
