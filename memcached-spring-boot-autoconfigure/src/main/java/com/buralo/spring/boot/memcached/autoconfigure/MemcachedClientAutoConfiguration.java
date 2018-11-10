package com.buralo.spring.boot.memcached.autoconfigure;

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.MemcachedClientIF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnClass(MemcachedClientIF.class)
@EnableConfigurationProperties(MemcachedProperties.class)
public class MemcachedClientAutoConfiguration {

    private static InetSocketAddress convert(final MemcachedProperties.Server server) {
        return new InetSocketAddress(server.getHostname(), server.getPort());
    }

    @Bean
    @Autowired
    @Lazy
    @ConditionalOnMissingBean
    public MemcachedClientIF memcachedClient(final MemcachedProperties properties) throws IOException {
        final List<InetSocketAddress> addresses = properties.getServers()
                .stream()
                .map(MemcachedClientAutoConfiguration::convert)
                .collect(Collectors.toList());
        return new MemcachedClient(addresses);
    }
}
