package org.ihtsdo.mysnow.neo4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.configuration.Config;
import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.graphdb.GraphDatabaseService;
import static org.junit.Assert.*;

public class EmbeddedDbManagerTest {
    private Path tempDbDir;
    private EmbeddedDbManager dbManager;

    @Before
    public void setUp() throws IOException {
        tempDbDir = Files.createTempDirectory("mysnow-test-db-");
        dbManager = new EmbeddedDbManager(tempDbDir.toFile());
    }

    @After
    public void tearDown() throws IOException {
        if (dbManager != null) {
            dbManager.shutdown();
        }
        if (tempDbDir != null && Files.exists(tempDbDir)) {
            Files.walk(tempDbDir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        }
    }

    @Test
    public void testSettingsApplied() {
        GraphDatabaseService db = dbManager.start();
        assertNotNull(db);
        
        DatabaseManagementService managementService = dbManager.getManagementService();
        Config config = ((org.neo4j.kernel.internal.GraphDatabaseAPI) db).getDependencyResolver().resolveDependency(Config.class);
        
        assertFalse("File watcher should be disabled", config.get(GraphDatabaseSettings.filewatcher_enabled));
        assertFalse("UDC should be disabled", config.get(GraphDatabaseSettings.udc_enabled));
        assertFalse("Page cache warmup should be disabled", config.get(GraphDatabaseSettings.pagecache_warmup_enabled));
    }
}
