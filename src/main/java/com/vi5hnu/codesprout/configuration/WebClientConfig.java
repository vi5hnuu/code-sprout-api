package com.vi5hnu.codesprout.configuration;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    /**
     * Dedicated WebClient for the judge service, backed by a tuned Netty
     * connection pool. Key settings:
     *
     * - maxConnections: cap concurrent judge connections; prevents judge overload
     * - maxIdleTime: return idle connections to the pool promptly
     * - pendingAcquireTimeout: fail-fast if pool is saturated (feeds circuit breaker)
     * - connectTimeout: 3 s — fast fail on network partition
     * - responseTimeout: 35 s — generous; judge acks immediately, so this should
     *   never fire unless the judge itself is hanging
     */
    @Bean
    public WebClient webClient(
            @Value("${judge.connection-pool-size:200}") int poolSize) {

        ConnectionProvider provider = ConnectionProvider.builder("judge-pool")
                .maxConnections(poolSize)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofMinutes(5))
                // fail-fast: throw an error after 10s waiting for a free connection
                .pendingAcquireTimeout(Duration.ofSeconds(10))
                .evictInBackground(Duration.ofSeconds(60))
                .build();

        HttpClient httpClient = HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3_000)
                .responseTimeout(Duration.ofSeconds(35))
                .keepAlive(true);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
