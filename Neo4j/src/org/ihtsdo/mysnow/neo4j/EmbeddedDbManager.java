package org.ihtsdo.mysnow.neo4j;

import java.io.File;
import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.dbms.api.DatabaseNotFoundException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.io.ByteUnit;

public final class EmbeddedDbManager {
    private static final String DEFAULT_TX_LOG_RETENTION_POLICY = "2 files";
    private static final long DEFAULT_TX_LOG_ROTATION_THRESHOLD_BYTES = ByteUnit.mebiBytes(64);

    private final File databaseDirectory;
    private DatabaseManagementService managementService;

    public EmbeddedDbManager(File databaseDirectory) {
        this.databaseDirectory = databaseDirectory;
    }

    public synchronized GraphDatabaseService start() {
        if (managementService == null) {
            DatabaseManagementServiceBuilder builder = new DatabaseManagementServiceBuilder(databaseDirectory.toPath());
            applyDefaultTxLogSettings(builder);
            managementService = builder.build();
        }
        return getDefaultDatabase();
    }

    public synchronized GraphDatabaseService start(Neo4jConfig config) {
        if (managementService == null) {
            DatabaseManagementServiceBuilder builder = new DatabaseManagementServiceBuilder(databaseDirectory.toPath());
            applyDefaultTxLogSettings(builder);
            if (config != null) {
                if (config.getConfigFile() != null) {
                    builder = builder.loadPropertiesFromFile(config.getConfigFile().toPath());
                }
                if (config.getPageCacheMiB() != null && config.getPageCacheMiB() > 0) {
                    builder = builder.setConfig(GraphDatabaseSettings.pagecache_memory, ByteUnit.mebiBytes(config.getPageCacheMiB()));
                }
            }
            managementService = builder.build();
        }
        return getDefaultDatabase();
    }

    private static void applyDefaultTxLogSettings(DatabaseManagementServiceBuilder builder) {
        builder.setConfig(GraphDatabaseSettings.keep_logical_logs, DEFAULT_TX_LOG_RETENTION_POLICY);
        builder.setConfig(GraphDatabaseSettings.logical_log_rotation_threshold, DEFAULT_TX_LOG_ROTATION_THRESHOLD_BYTES);
    }

    public synchronized DatabaseManagementService getManagementService() {
        return managementService;
    }

    public synchronized void shutdown() {
        if (managementService != null) {
            managementService.shutdown();
            managementService = null;
        }
    }

    private GraphDatabaseService getDefaultDatabase() {
        try {
            return managementService.database(GraphDatabaseSettings.DEFAULT_DATABASE_NAME);
        } catch (DatabaseNotFoundException e) {
            throw new IllegalStateException("Default database not found: " + GraphDatabaseSettings.DEFAULT_DATABASE_NAME, e);
        }
    }
}
