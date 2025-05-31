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
import jakarta.validation.constraints.AssertTrue;
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
    @NotNull(message = "Il creditore è obbligatorio")
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
    @NotBlank(message = "La descrizione è obbligatoria")
    @Size(max = 140, message = "La descrizione non deve superare i 140 caratteri")
    private String description;

    /**
     * Obbligatorio. L'importo del bonifico.
     */
    @NotNull(message = "L'importo è obbligatorio")
    private BigDecimal amount;

    /**
     * Obbligatorio. La valuta del bonifico.
     */
    @NotBlank(message = "La valuta è obbligatoria")
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
    @Pattern(regexp = "SHA|OUR|BEN", message = "Il tipo di commissione deve essere uno tra: SHA, OUR, BEN")
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
        @NotBlank(message = "Il nome del creditore è obbligatorio")
        @Size(max = 70, message = "Il nome del creditore non deve superare i 70 caratteri")
        private String name;

        /**
         * Obbligatorio. Le informazioni sul conto del creditore.
         */
        @NotNull(message = "Il conto del creditore è obbligatorio")
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
            @NotBlank(message = "Il codice del conto è obbligatorio")
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
            @Size(max = 40, message = "L'indirizzo non deve superare i 40 caratteri")
            private String address;

            /**
             * Opzionale. La città del creditore.
             */
            private String city;

            /**
             * Opzionale. Il codice paese del creditore, conforme allo standard ISO 3166-1
             * alpha 2.
             */
            @Pattern(regexp = "^[A-Z]{2}$", message = "Il codice paese deve essere un codice valido ISO 3166-1 alpha-2")
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
        @Pattern(regexp = "119R|DL50|L296|L449|L234", message = "L'ID della detrazione fiscale deve essere uno tra: 119R, DL50, L296, L449, L234")
        private String taxReliefId;

        /**
         * Obbligatorio. Flag per indicare se la detrazione fiscale è relativa a lavori
         * di ristrutturazione
         * di spazi condominiali comuni.
         */
        @NotNull(message = "Il campo isCondoUpgrade è obbligatorio")
        @JsonProperty("isCondoUpgrade")
        private boolean isCondoUpgrade;

        /**
         * Obbligatorio. Il codice fiscale del creditore del bonifico.
         */
        @NotBlank(message = "Il codice fiscale del creditore è obbligatorio")
        private String creditorFiscalCode;

        /**
         * Obbligatorio. Il tipo di beneficiario della detrazione fiscale.
         * Valori validi: NATURAL_PERSON, LEGAL_PERSON
         */
        @NotBlank(message = "Il tipo di beneficiario è obbligatorio")
        @Pattern(regexp = "NATURAL_PERSON|LEGAL_PERSON", message = "Il tipo di beneficiario deve essere NATURAL_PERSON o LEGAL_PERSON")
        private String beneficiaryType;

        /**
         * Opzionale. Obbligatorio se beneficiaryType è NATURAL_PERSON.
         */
        @Valid
        private NaturalPersonBeneficiary naturalPersonBeneficiary;

        /**
         * Opzionale. Obbligatorio se beneficiaryType è LEGAL_PERSON.
         */
        private LegalPersonBeneficiary legalPersonBeneficiary;
        
        /**
         * Verifica che se il tipo di beneficiario è LEGAL_PERSON, il legalPersonBeneficiary sia valido.
         * @return true se la validazione passa, false altrimenti
         */
        @AssertTrue(message = "I dettagli della persona giuridica sono obbligatori quando il tipo di beneficiario è LEGAL_PERSON")
        public boolean isLegalPersonBeneficiaryValid() {
            // Se il tipo è LEGAL_PERSON, verifica che legalPersonBeneficiary non sia null
            if ("LEGAL_PERSON".equals(beneficiaryType)) {
                return legalPersonBeneficiary != null && 
                       legalPersonBeneficiary.getFiscalCode() != null && 
                       !legalPersonBeneficiary.getFiscalCode().trim().isEmpty();
            }
            // Negli altri casi, non importa
            return true;
        }
        
        /**
         * Verifica che se il tipo di beneficiario è NATURAL_PERSON, il naturalPersonBeneficiary sia valido.
         * @return true se la validazione passa, false altrimenti
         */
        @AssertTrue(message = "I dettagli della persona fisica sono obbligatori quando il tipo di beneficiario è NATURAL_PERSON")
        public boolean isNaturalPersonBeneficiaryValid() {
            // Se il tipo è NATURAL_PERSON, verifica che naturalPersonBeneficiary non sia null
            if ("NATURAL_PERSON".equals(beneficiaryType)) {
                return naturalPersonBeneficiary != null && 
                       naturalPersonBeneficiary.getFiscalCode1() != null && 
                       !naturalPersonBeneficiary.getFiscalCode1().trim().isEmpty();
            }
            // Negli altri casi, non importa
            return true;
        }
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class NaturalPersonBeneficiary {
            /**
             * Obbligatorio. Il Codice Fiscale italiano del primo beneficiario.
             */
            @NotBlank(message = "Il codice fiscale 1 è obbligatorio")
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
             * Obbligatorio. Il Codice Fiscale italiano della persona giuridica.
             */
            @NotBlank(message = "Il codice fiscale della persona giuridica è obbligatorio")
            private String fiscalCode;
            private String legalRepresentativeFiscalCode;
        }
    }
}
