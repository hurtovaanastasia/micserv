package com.example.weather.cache;

import com.example.weather.model.Root;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class WeatherCache {

    private static class cacheEntry {
        Root data;
        long time;

        cacheEntry(Root data) {
            this.data = data;
            this.time = Instant.now().getEpochSecond();
        }
    }

    private final Map<String, cacheEntry> cache = new HashMap<>();
    private static final long liveTime = 60;

    public Root get(String key) {
        cacheEntry entry = cache.get(key);
        if (entry == null) {
            return null; // нет в кэше
        }

        long now = Instant.now().getEpochSecond();
        if (now - entry.time > liveTime) {
            cache.remove(key);
            return null;
        }

        return entry.data;
    }

    public void putToCache(String key, Root data) {
        cache.put(key, new cacheEntry(data));
    }
}
