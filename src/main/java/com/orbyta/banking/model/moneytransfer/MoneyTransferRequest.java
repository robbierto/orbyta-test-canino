package com.orbyta.banking.model.moneytransfer;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoneyTransferRequest {

    /**
     * Obbligatorio. Le informazioni del creditore.
     */
    @NotNull(message = "Creditor is required")
    @Valid
    private Creditor creditor;

    /**
     * Opzionale, ma obbligatorio se isInstant è false.
     * La data in cui il bonifico deve essere eseguito.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate executionDate;

    /**
     * Opzionale. Informazioni di rimessa da consegnare al creditore.
     */
    private String uri;

    /**
     * Obbligatorio. La descrizione del bonifico.
     * Lunghezza massima: 140 caratteri.
     */
    @NotBlank(message = "Description is required")
    @Size(max = 140, message = "Description must not exceed 140 characters")
    private String description;

    /**
     * Obbligatorio. L'importo del bonifico.
     */
    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    /**
     * Obbligatorio. La valuta del bonifico.
     */
    @NotBlank(message = "Currency is required")
    private String currency;

    /**
     * Opzionale. Flag per impostare il bonifico come urgente. Predefinito è false.
     */
    @JsonProperty("isUrgent")
    private boolean isUrgent;

    /**
     * Opzionale. Flag per impostare il bonifico come istantaneo (SCT-Inst).
     * Predefinito è false.
     */
    @JsonProperty("isInstant")
    private boolean isInstant;

    /**
     * Opzionale. Lo schema di commissione da adottare. Predefinito è 'SHA'.
     * Valori validi: SHA, OUR, BEN
     */
    @Pattern(regexp = "SHA|OUR|BEN", message = "Fee type must be one of: SHA, OUR, BEN")
    private String feeType;

    /**
     * Opzionale. L'ID del conto per le commissioni del bonifico.
     */
    private String feeAccountId;

    /**
     * Opzionale. Informazioni sulla detrazione fiscale (solo Italia).
     */
    @Valid
    private TaxRelief taxRelief;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Creditor {
        /**
         * Obbligatorio. Il nome del creditore.
         * Lunghezza massima: 70 caratteri.
         */
        @NotBlank(message = "Creditor name is required")
        @Size(max = 70, message = "Creditor name must not exceed 70 characters")
        private String name;

        /**
         * Obbligatorio. Le informazioni sul conto del creditore.
         */
        @NotNull(message = "Creditor account is required")
        @Valid
        private Account account;

        /**
         * Opzionale. Obbligatorio se il conto del creditore è su una banca SEPA e non
         * SEPA
         * diversa dall'Italia.
         */
        @Valid
        private Address address;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Account {
            /**
             * Obbligatorio. Il codice del conto del creditore (può essere un codice IBAN, o
             * un numero
             * di conto SWIFT).
             */
            @NotBlank(message = "Account code is required")
            private String accountCode; // This might need to be 'iban' based on API expectations

            /**
             * Opzionale. Obbligatorio se un numero di conto SWIFT è fornito come
             * accountCode.
             */
            private String bicCode;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Address {
            /**
             * Opzionale. L'indirizzo del creditore.
             * Lunghezza massima: 40 caratteri.
             */
            @Size(max = 40, message = "Address must not exceed 40 characters")
            private String address;

            /**
             * Opzionale. La città del creditore.
             */
            private String city;

            /**
             * Opzionale. Il codice paese del creditore, conforme allo standard ISO 3166-1
             * alpha 2.
             */
            @Pattern(regexp = "^[A-Z]{2}$", message = "Country code must be a valid ISO 3166-1 alpha-2 code")
            private String countryCode;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaxRelief {
        /**
         * Opzionale. L'ID della detrazione fiscale.
         * Valori validi: 119R, DL50, L296, L449, L234
         */
        @Pattern(regexp = "119R|DL50|L296|L449|L234", message = "Tax relief ID must be one of: 119R, DL50, L296, L449, L234")
        private String taxReliefId;

        /**
         * Obbligatorio. Flag per indicare se la detrazione fiscale è relativa a lavori
         * di ristrutturazione
         * di spazi condominiali comuni.
         */
        @NotNull(message = "isCondoUpgrade is required")
        @JsonProperty("isCondoUpgrade")
        private boolean isCondoUpgrade;

        /**
         * Obbligatorio. Il codice fiscale del creditore del bonifico.
         */
        @NotBlank(message = "Creditor fiscal code is required")
        private String creditorFiscalCode;

        /**
         * Obbligatorio. Il tipo di beneficiario della detrazione fiscale.
         * Valori validi: NATURAL_PERSON, LEGAL_PERSON
         */
        @NotBlank(message = "Beneficiary type is required")
        @Pattern(regexp = "NATURAL_PERSON|LEGAL_PERSON", message = "Beneficiary type must be either NATURAL_PERSON or LEGAL_PERSON")
        private String beneficiaryType;

        /**
         * Opzionale. Obbligatorio se beneficiaryType è NATURAL_PERSON.
         */
        @Valid
        private NaturalPersonBeneficiary naturalPersonBeneficiary;

        /**
         * Opzionale. Obbligatorio se beneficiaryType è LEGAL_PERSON.
         */
        @Valid
        private LegalPersonBeneficiary legalPersonBeneficiary;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class NaturalPersonBeneficiary {
            /**
             * Obbligatorio. Il Codice Fiscale italiano del primo beneficiario.
             */
            @NotBlank(message = "Fiscal code 1 is required")
            private String fiscalCode1;

            /**
             * Opzionale. Il Codice Fiscale italiano dei beneficiari aggiuntivi.
             */
            private String fiscalCode2;
            private String fiscalCode3;
            private String fiscalCode4;
            private String fiscalCode5;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class LegalPersonBeneficiary {
            /**
             * Obbligatorio. La Partita IVA italiana della persona giuridica.
             */
            @NotBlank(message = "Fiscal code is required")
            private String fiscalCode;

            /**
             * Opzionale. Il Codice Fiscale italiano del rappresentante legale.
             */
            private String legalRepresentativeFiscalCode;
        }
    }
}
