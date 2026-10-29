package com.innowise.academy.userservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserCacheEvictor {
    private final CacheManager cacheManager;

    public void evict(Long userId) {
        Cache cache = cacheManager.getCache(RedisCacheConfig.USERS_CACHE);
        if (cache != null) {
            cache.evict(userId);
        }
    }
}


