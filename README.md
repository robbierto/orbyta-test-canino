# orbyta-test-canino
TEST del 30/05/2025

## Obbiettivo del progetto
Realizzare un controller Rest che permetta di gestire le seguenti operazioni sul conto:
- Lettura saldo
- Lista di transazioni
- Bonifico

Utilizzando API esposte da Fabrick.


## Architettura

L'applicazione segue un'architettura a 3 livelli:

- **Controller**: gestiscono le richieste HTTP e le risposte
- **Service**: contengono la logica di business e comunicano con l'API esterna
- **Model**: rappresentano gli oggetti tipizzati di scambio tra le API.

## Funzionalità Implementate

- **Gestione Account**: recupero informazioni account
- **Saldo Conto**: recupero del saldo disponibile
- **Transazioni**: recupero storico transazioni filtrate per date
- **Bonifici**: creazione bonifici con validazione sui relativi campi

## Tecnologie Utilizzate

- **Spring Boot**: framework per lo sviluppo dell'applicazione
- **Spring MVC**: per la creazione delle API REST
- **Bean Validation (Jakarta)**: per la validazione dei dati in input
- **JUnit 5 & Mockito**: per i test unitari e di integrazione
- **Logback**: per la configurazione avanzata del logging
- **Jackson**: per la serializzazione/deserializzazione JSON
- **Lombok**: per ridurre il boilerplate code

## Soluzioni Adottate

### Gestione Avanzata Errori
- **GlobalExceptionHandler**: intercetta tutte le eccezioni e le trasforma in risposte API standardizzate
- **ApiResponse**: wrapper per standardizzare tutte le risposte API, sia di successo che di errore
- **Errori Specifici**: gestione dettagliata degli errori di validazione, parametri mancanti e problemi con API esterne

### Validazione Robusta
- **Bean Validation**: utilizzo estensivo per la validazione dei dati in input
- **Validazioni Custom**: implementate con `@AssertTrue` per validazioni complesse (es. validazione dei beneficiari nei bonifici)
- **Messaggi di Errore**: personalizzati e localizzati in italiano

### Gestione Ottimizzata delle URL
- **UriComponentsBuilder**: utilizzato per costruire URL in modo sicuro e programmatico
- **Pattern Builder**: implementato per ottimizzare la costruzione delle URL per le API esterne

### Sicurezza e Performance
- **BigDecimal**: utilizzato per tutti i valori monetari per evitare problemi di precisione
- **Costanti**: utilizzo di classi di costanti per evitare stringhe hardcoded
- **Headers Sicuri**: gestione dei headers di autenticazione e autorizzazione

### Logging Avanzato
- **Aspect Oriented Programming**: implementato per il logging trasversale dei metodi
- **Configurazione Logback**: rotazione dei file di log, formattazione e livelli configurabili
- **Log Contestuali**: inclusione di informazioni di contesto nei log

### Testing Completo
- **Test Unitari**: copertura per servizi, controller e modelli
- **Test di Integrazione**: verifica del comportamento integrato degli endpoint REST
- **Mocking**: utilizzo avanzato di Mockito per simulare dipendenze esterne

## Best Practices Implementate

- **Generics**: implementati per tipizzare correttamente le risposte API
- **Object Mapper Non-Singleton**: configurazione corretta per evitare problemi di concorrenza
- **Costruzione Path API**: utilizzo di costanti e builder pattern
- **Gestione Dati Sensibili**: separazione delle configurazioni in file di properties

## Testing

L'applicazione include una suite completa di test:
- **FabrickServiceTest**: verifica le chiamate all'API esterna
- **MoneyTransferRequestTest**: verifica le validazioni della richiesta di bonifico
- **AccountIntegrationTest**: test di integrazione per gli endpoint REST
- **GlobalExceptionHandlerTest**: test per la gestione delle eccezioni
- **AccountControllerTest**: test unitari per il controller
