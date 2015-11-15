package com.sambi.app.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;

@Service
public class AppUserStorage {
    
    @Autowired
    private Cache appUserCache;
    
    public AppUser getAppUser(AppUser appUser) {
        if (appUser == null) return null;
        if (appUser.getUsername() == null) return null;
        return appUserCache.get(appUser.getUsername(), AppUser.class);
    }
    
    public boolean isAppUserPresent(String username) {
        if (username == null) return false;
        return (appUserCache.get(username) != null);
    }
    
    public void removeAppUser(String username) {
        if (username == null) return;
        appUserCache.evict(username);
    }
    
    public void storeAppUser(AppUser appUser) {
        if (appUser != null) {
            appUserCache.evict(appUser.getUsername());
            appUserCache.put(appUser.getUsername(), appUser);
        }
    }
}

