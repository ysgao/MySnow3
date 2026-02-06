/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.mysnow.importsct_impl;

import java.io.File;
import org.ihtsdo.mysnow.neo4j.EmbeddedDbManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author yoga
 */
public class UpdateHasDescriptionTest {
    
    public UpdateHasDescriptionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    Label conceptLabel = Label.label("Concept");
    /**
     * Test of updateHasDescription method, of class UpdateHasDescription.
     */
    @Test
    public void testUpdateHasDescription() {
        System.out.println("updateHasDescription");
//        UpdateHasDescription instance = new UpdateHasDescription();
//        instance.updateHasDescription();
        
        //194965002|Fibrosis of pericardium (disorder)
        File store = new File(System.getProperty("user.home") + "/no4j-sct2-store");
        EmbeddedDbManager dbManager = new EmbeddedDbManager(store);
        GraphDatabaseService graphDB = dbManager.start();
        try(Transaction tx=graphDB.beginTx()){
           Node node = graphDB.findNode(conceptLabel, "sctid", 419739000); //419739000|Ultrasound scan and biopsy of liver (procedure)
           Iterable<Relationship> rel_iterable = node.getRelationships();
           if(rel_iterable.iterator().hasNext()){
               Relationship rel = rel_iterable.iterator().next();
               System.out.println("Concept 419739000 has relaitonship:  " +rel.getType().name());
           }
        }
        finally {
            dbManager.shutdown();
        }
            
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
