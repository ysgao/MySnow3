package org.ihtsdo.mysnow.neo4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class Neo4jConfig {
    private static final String PROP_PAGECACHE = "dbms.pagecache.memory";
    private static final String DEFAULT_CONFIG_PATH = System.getProperty("user.home") + "/.mysnow2/neo4j.conf";

    private final Long pageCacheMiB;

    private Neo4jConfig(Long pageCacheMiB) {
        this.pageCacheMiB = pageCacheMiB;
    }

    public static Neo4jConfig load() {
        String override = System.getProperty("mysnow.neo4j.conf");
        File configFile = new File(override != null && !override.isBlank() ? override : DEFAULT_CONFIG_PATH);
        return load(configFile);
    }

    public static Neo4jConfig load(File configFile) {
        if (configFile == null || !configFile.exists()) {
            return new Neo4jConfig(null);
        }

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(configFile)) {
            props.load(in);
        } catch (IOException e) {
            return new Neo4jConfig(null);
        }

        Long pageCache = parseMemoryMiB(props.getProperty(PROP_PAGECACHE));
        return new Neo4jConfig(pageCache);
    }

    public Long getPageCacheMiB() {
        return pageCacheMiB;
    }

    private static Long parseMemoryMiB(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim().toLowerCase();
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            if (trimmed.endsWith("g")) {
                return Long.parseLong(trimmed.substring(0, trimmed.length() - 1)) * 1024;
            }
            if (trimmed.endsWith("m")) {
                return Long.parseLong(trimmed.substring(0, trimmed.length() - 1));
            }
            return Long.parseLong(trimmed) / (1024 * 1024);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
