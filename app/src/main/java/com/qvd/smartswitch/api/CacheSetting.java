package com.qvd.smartswitch.api;

import com.qvd.smartswitch.MyApplication;

import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;

/**
 * Created by xushiwei on 2018/8/19.
 */

public class CacheSetting {
    private static CacheProviders cacheProviders;

    public synchronized static CacheProviders getCache() {
        if (cacheProviders == null) {
            cacheProviders = new RxCache.Builder()
                    .persistence(MyApplication.getContext().getExternalCacheDir(), new GsonSpeaker())
                    .using(CacheProviders.class);
        }
        return cacheProviders;
    }
}
