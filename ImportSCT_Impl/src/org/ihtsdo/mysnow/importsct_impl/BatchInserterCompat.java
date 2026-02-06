package org.ihtsdo.mysnow.importsct_impl;

import java.util.Map;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

final class BatchInserterCompat implements AutoCloseable {
    private final GraphDatabaseService graphDb;
    private final int batchSize;
    private Transaction tx;
    private int opCount;

    BatchInserterCompat(GraphDatabaseService graphDb, int batchSize) {
        this.graphDb = graphDb;
        this.batchSize = Math.max(1, batchSize);
    }

    long createNode(Map<String, Object> properties, Label... labels) {
        ensureTx();
        Node node = tx.createNode(labels);
        if (properties != null) {
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                node.setProperty(entry.getKey(), entry.getValue());
            }
        }
        bump();
        return node.getId();
    }

    void setNodeProperty(long nodeId, String key, Object value) {
        ensureTx();
        tx.getNodeById(nodeId).setProperty(key, value);
        bump();
    }

    void createRelationship(long fromId, long toId, RelationshipType type, Map<String, Object> properties) {
        ensureTx();
        Node from = tx.getNodeById(fromId);
        Node to = tx.getNodeById(toId);
        Relationship relationship = from.createRelationshipTo(to, type);
        if (properties != null) {
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                relationship.setProperty(entry.getKey(), entry.getValue());
            }
        }
        bump();
    }

    Long findNodeId(Label label, String key, Object value) {
        ensureTx();
        Node node = tx.findNode(label, key, value);
        if (node == null) {
            return null;
        }
        return node.getId();
    }

    Object getNodeProperty(long nodeId, String key) {
        ensureTx();
        return tx.getNodeById(nodeId).getProperty(key, null);
    }

    void createSchemaIndex(Label label, String propertyKey) {
        flush();
        try (Transaction schemaTx = graphDb.beginTx()) {
            schemaTx.schema().indexFor(label).on(propertyKey).create();
            schemaTx.commit();
        }
    }

    void flush() {
        if (tx != null) {
            tx.commit();
            tx.close();
            tx = null;
            opCount = 0;
        }
    }

    void shutdown() {
        close();
    }

    @Override
    public void close() {
        flush();
    }

    private void ensureTx() {
        if (tx == null) {
            tx = graphDb.beginTx();
            opCount = 0;
        }
    }

    private void bump() {
        opCount++;
        if (opCount >= batchSize) {
            flush();
        }
    }
}
