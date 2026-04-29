package com.citamedica.backend.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlywayMigrationScriptsTest {

    private static final Pattern VERSIONED_SQL = Pattern.compile("^V(\\d+)__.+\\.sql$");

    @Test
    void migrationScriptsAreSequentialAndVersioned() throws IOException, URISyntaxException {
        Path migrationDir = Path.of(getClass().getClassLoader().getResource("db/migration").toURI());

        List<String> migrationFiles = Files.list(migrationDir)
                .map(path -> path.getFileName().toString())
                .filter(name -> name.endsWith(".sql"))
                .sorted()
                .collect(Collectors.toList());

        assertEquals(7, migrationFiles.size(), "Expected exactly 7 base migration scripts");

        for (int i = 0; i < migrationFiles.size(); i++) {
            String fileName = migrationFiles.get(i);
            Matcher matcher = VERSIONED_SQL.matcher(fileName);
            assertTrue(matcher.matches(), "Invalid Flyway script name: " + fileName);

            int version = Integer.parseInt(matcher.group(1));
            assertEquals(i + 1, version, "Migration versions must be contiguous");
        }
    }
}
