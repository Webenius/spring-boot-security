package com.sambi.app.rest.exception;

import org.springframework.http.HttpStatus;

public enum RestExceptionCode {

    // warnings
    WC_FE_001(HttpStatus.METHOD_NOT_ALLOWED, "Für diesen Lauf können keine Statistiken angezeigt werden, da es sich um einen Simulationslauf handelt", false),
    WC_FE_008(HttpStatus.BAD_REQUEST, "Formatierung  der Artikelnummer entspricht nicht der Vorgabe: 7-stellig positiver Wert", false),
    // errors
	EC_FE_001(HttpStatus.UNAUTHORIZED, "Username und PW stimmen nicht überein", false),
	EC_FE_002(HttpStatus.NOT_ACCEPTABLE, "Der Benutzer ist bereits eingeloggt", false),
    EC_FE_003(HttpStatus.SERVICE_UNAVAILABLE, "App steht aktuell nicht zur Verfügung", true),
	EC_FE_004(HttpStatus.NOT_FOUND, "Auf das Protokoll-File kann nicht zugegriffen werden", false),
	EC_FE_005(HttpStatus.NOT_ACCEPTABLE, "Der Lauf ist bereits freigegeben/verworfen worden und steht nicht mehr als offene Freigabe zur Verfügung", false),
	EC_FE_006(HttpStatus.UNAUTHORIZED, "User is not enabled", true),
    EC_FE_014(HttpStatus.BAD_REQUEST, "Eingebene ID existiert nicht im Kontext der Filterkriterien", false),
    EC_FE_016(HttpStatus.BAD_REQUEST, "Es ist keine Artikelnummer eingegeben", false),
    EC_FE_017(HttpStatus.BAD_REQUEST, "Es sind nicht alle Felder mit Daten befüllt", false),
    EC_FE_023(HttpStatus.BAD_REQUEST, "Artikelnummer existiert bereits in der HW Blackliste", false),
    EC_FE_024(HttpStatus.BAD_REQUEST, "Artikelnummer existiert in FV HW und kann daher nicht als Dummy gespeichert werden", false),
    EC_FE_025(HttpStatus.BAD_REQUEST, "Dummy-Flag muss gesetzt werden oder Artikelno kann nicht gespeichert werden", false),
	// fatal errors
	FC_RE_001(HttpStatus.INTERNAL_SERVER_ERROR, "Interner Fehler", true);

	private HttpStatus httpStatus;
	private String error;
	private boolean logError;
	
	RestExceptionCode(HttpStatus httpStatus, String error, boolean logError) {
		this.httpStatus = httpStatus;
		this.error = error;
		this.logError = logError;
	}
	
	public static RestExceptionCode fromString(String code) {
		try {
			return RestExceptionCode.valueOf(code.trim().toUpperCase());
		} catch (NullPointerException ex) {
			return null;
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
	
	public String getError() {
		return error;
	}

	public boolean isLogError() {
		return logError;
	}
}
