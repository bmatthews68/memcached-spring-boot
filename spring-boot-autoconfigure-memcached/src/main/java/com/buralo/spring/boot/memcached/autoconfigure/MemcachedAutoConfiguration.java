package com.buralo.spring.boot.memcached.autoconfigure;

import com.thimbleware.jmemcached.CacheImpl;
import com.thimbleware.jmemcached.Key;
import com.thimbleware.jmemcached.LocalCacheElement;
import com.thimbleware.jmemcached.MemCacheDaemon;
import com.thimbleware.jmemcached.storage.CacheStorage;
import com.thimbleware.jmemcached.storage.hash.ConcurrentLinkedHashMap;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.MemcachedClientIF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnClass(MemcachedClientIF.class)
@ConditionalOnMissingBean(MemcachedClientIF.class)
@EnableConfigurationProperties(MemcachedProperties.class)
public class MemcachedAutoConfiguration {

    @Autowired
    private MemcachedProperties properties;

    private static InetSocketAddress convert(final MemcachedProperties.Server server) {
        return new InetSocketAddress(server.getHostname(), server.getPort());
    }

    @Bean
    public MemcachedClientIF memcachedClient() throws IOException {
        final List<InetSocketAddress> addresses = properties.getServers()
                .stream()
                .map(MemcachedAutoConfiguration::convert)
                .collect(Collectors.toList());
        return new MemcachedClient(addresses);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnClass(MemCacheDaemon.class)
    public MemCacheDaemon<LocalCacheElement> embeddedMemcachedDaemon() {
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
