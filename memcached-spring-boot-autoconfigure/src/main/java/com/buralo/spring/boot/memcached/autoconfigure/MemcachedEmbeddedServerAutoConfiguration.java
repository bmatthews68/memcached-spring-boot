package com.buralo.spring.boot.memcached.autoconfigure;

import com.thimbleware.jmemcached.CacheImpl;
import com.thimbleware.jmemcached.Key;
import com.thimbleware.jmemcached.LocalCacheElement;
import com.thimbleware.jmemcached.MemCacheDaemon;
import com.thimbleware.jmemcached.storage.CacheStorage;
import com.thimbleware.jmemcached.storage.hash.ConcurrentLinkedHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
@ConditionalOnClass(MemCacheDaemon.class)
@EnableConfigurationProperties(MemcachedProperties.class)
public class MemcachedEmbeddedServerAutoConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Autowired
    @ConditionalOnMissingBean
    public MemCacheDaemon<LocalCacheElement> embeddedMemcachedDaemon(final MemcachedProperties properties) {
        final MemcachedProperties.Embedded embedded = properties.getEmbedded();
        final CacheStorage<Key, LocalCacheElement> storage = ConcurrentLinkedHashMap.create(
                ConcurrentLinkedHashMap.EvictionPolicy.FIFO,
                embedded.getMaxItems(),
                embedded.getMaxBytes());
        final MemCacheDaemon<LocalCacheElement> daemon = new MemCacheDaemon<>();
        daemon.setCache(new CacheImpl(storage));
        daemon.setAddr(new InetSocketAddress(embedded.getPort()));
        daemon.setVerbose(embedded.isVerbose());
        return daemon;
    }
}
