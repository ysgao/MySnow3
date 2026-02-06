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
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author yoga
 */
public class UpdateDescriptionTest {
    
    public UpdateDescriptionTest() {
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
    
    GraphDatabaseService graphDB;
    Label conceptLabel = Label.label("Concept");
    /**
     * Test of updateDescription method, of class UpdateDescription.
     */
    @Test
    public void testUpdateDescription() {
        System.out.println("updateDescription");
        UpdateDescription instance = new UpdateDescription();
        instance.updateDescription();
        
        File store = new File(System.getProperty("user.home") + "/no4j-sct2-store");
        EmbeddedDbManager dbManager = new EmbeddedDbManager(store);
        graphDB = dbManager.start();
        try(Transaction tx=graphDB.beginTx()){        
            Node testnode = graphDB.findNode(conceptLabel, "sctid", 444848005);
            String testfsn = testnode.getProperty("fsn").toString();
            System.out.println("Test FSN = " + testfsn);
            String answer = "Edema of back (finding)";
            System.out.println("The answer should be Edema of back (finding)");
            if(testfsn.equals(answer)){
            System.out.println("PASSSED, fsn KEY added to concepts. ");
        } 
        else {  fail("The test case is a prototype.");}
        }
        finally {
            dbManager.shutdown();
        }
        
        // TODO review the generated test code and remove the default call to fail.
      
    }
    
}
