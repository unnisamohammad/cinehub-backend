package com.razkart.cinehub.common.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Flyway configuration to ensure migrations run on startup.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class FlywayConfig {

    private final DataSource dataSource;

    @PostConstruct
    public void migrate() {
        log.info("Starting Flyway migration...");

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();

        // Repair any failed migrations first
        flyway.repair();

        // Run migrations
        var result = flyway.migrate();

        log.info("Flyway migration completed. {} migrations applied.",
                result.migrationsExecuted);
    }
}
