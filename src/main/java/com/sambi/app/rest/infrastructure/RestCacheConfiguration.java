package com.sambi.app.rest.infrastructure;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.interceptor.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@EnableCaching
public class RestCacheConfiguration implements CachingConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(RestCacheConfiguration.class);
    private static final long TTL_ONE_HOUR = (60 * 60);
    
    @Bean(destroyMethod="shutdown")
    public net.sf.ehcache.CacheManager ehCacheManager() {
        return net.sf.ehcache.CacheManager.create(
            new net.sf.ehcache.config.Configuration()
                // due running application without restart just keep specifications for an hour
                .cache(cacheConfig("specification", 100000, TTL_ONE_HOUR))
                .cache(cacheConfig("data", 2000, TTL_ONE_HOUR))
                .cache(cacheConfig("general_description", 500, TTL_ONE_HOUR))
                // app user is only valid for 30 minutes
                .cache(cacheConfig("appuser", 500, (30*60)))
            );
    }

    private static CacheConfiguration cacheConfig(String name, int maxEntries, long timeToLiveSeconds) {
        return new CacheConfiguration(name, maxEntries)
            .timeToIdleSeconds(0)
            .timeToLiveSeconds(timeToLiveSeconds)
            .eternal(false)
            .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
            .overflowToDisk(false)
            .statistics(false);
    }

    @Scope
    @Bean
    @Override
    public CacheManager cacheManager() {
        LOG.trace("Create new cache manager using ehcache");
        return new EhCacheCacheManager(ehCacheManager());
    }

    @Override
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    @Override
    public CacheResolver cacheResolver() {
        return null;
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler();
    }
    
    @Bean
    public Cache appUserCache() {
        return cacheManager().getCache("appuser");
    }
}
