package com.sambi.app.rest.security;

public enum Claims {
    
    EXP("exp"),
    JTI("jti"),
    AUD("aud"),
    SUB("sub"),
    ISS("iss"),
    IAT("iat");     
    
    final String claim;
    
    Claims(String claim) {
        this.claim = claim;
    }
    
    @Override
    public String toString() {
        return claim;
    }   
}