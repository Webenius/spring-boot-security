package com.sambi.app.rest.security;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.InvalidSignatureException;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.jwt.crypto.sign.Signer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TokenService {

    private final static Logger logger = LoggerFactory.getLogger(TokenService.class);

    private final Signer signer;
    private final SignatureVerifier verifier;
    private final int maxAuthMinutes;
    private final ObjectMapper jsonMapper;

    /**
     * Signer and Verifier (MAC or RSA) can be configured in the security bean
     * configuration. The minutes until timeout are defined in the
     * authentication properties file.
     */
    public TokenService(Signer signer, SignatureVerifier verifier, int maxAuthMinutes) {
        this.signer = signer;
        this.verifier = verifier;
        this.maxAuthMinutes = maxAuthMinutes;
        this.jsonMapper = new ObjectMapper();
    }

    /**
     * Creates a signed and encoded JSON Web Token with the given
     * jsonUserDetails, an issue time and an expiration time according to the
     * timeout configured in the authentication properties file.
     */
    public String createToken(String jsonUserDetails) throws JsonProcessingException {
        final Date issueTime = new Date();
        final Date expirationTime = DateUtils.addMinutes(issueTime, maxAuthMinutes);

        // collect JWT claims
        Map<Claims, Object> claimSet = new HashMap<>();
        claimSet.put(Claims.SUB, jsonUserDetails);
        claimSet.put(Claims.IAT, issueTime.getTime() / DateUtils.MILLIS_PER_SECOND);
        claimSet.put(Claims.EXP, expirationTime.getTime() / DateUtils.MILLIS_PER_SECOND);

        // convert claims to JSON and create JSON Web Token
        Jwt token = JwtHelper.encode(jsonMapper.writeValueAsString(claimSet), signer);
        token.verifySignature(verifier);

        // return the encoded token as a string
        return token.getEncoded();
    }

    /**
     * Extracts the JSON UserDetails from the given token.
     */
    public String extractJsonUserDetails(String encodedToken) throws InvalidSignatureException {
        return extractString(Claims.SUB, encodedToken);
    }

    /**
     * Extracts the date and time when the token was issued.
     */
    public Date extractIssueDate(String encodedToken) throws InvalidSignatureException {
        return extractDate(Claims.IAT, encodedToken);
    }

    /**
     * Extracts the date and time when the token will expire or has expired.
     */
    public Date extractExpirationDate(String encodedToken) throws InvalidSignatureException {
        return extractDate(Claims.EXP, encodedToken);
    }

    /**
     * Extracts a Date value of the given claim from a token.
     */
    private Date extractDate(Claims claim, String encodedToken) throws InvalidSignatureException {
        // only IAT and EXP claims contain a Date value according to the JWT spec
        if (!claim.equals(Claims.IAT) && !claim.equals(Claims.EXP)) {
            throw new IllegalArgumentException("Only the claims IAT and EXP can be extracted as dates");
        }
        // the date value is defined in UNIX time
        int value = (int) extractValue(claim, encodedToken);
        return new Date(value * DateUtils.MILLIS_PER_SECOND);
    }

    /**
     * Extracts a String value of the given claim from a token.
     */
    private String extractString(Claims claim, String encodedToken) throws InvalidSignatureException {
        return (String) extractValue(claim, encodedToken);
    }

    /**
     * Extracts a value of the given claim from a token.
     */
    private Object extractValue(Claims claim, String encodedToken) throws InvalidSignatureException {
        // decode the token
        Jwt token = JwtHelper.decodeAndVerify(encodedToken, verifier);
    
        // parse claims which are encoded as stringified JSON
        Map<String, Object> map;
        try {
            map = jsonMapper.readValue(token.getClaims(), Map.class);
        } catch (IOException e) {
            logger.error("Failed to extract json values", e);
            return null;
        }           
        return map.get(claim.toString());
    }
}
