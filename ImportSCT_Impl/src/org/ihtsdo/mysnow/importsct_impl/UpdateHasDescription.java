/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.mysnow.importsct_impl;

import java.io.File;
import java.util.ArrayList;
import org.ihtsdo.mysnow.neo4j.EmbeddedDbManager;
import org.ihtsdo.mysnow.neo4j.Neo4jConfig;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author yoga
 */
public class UpdateHasDescription {
    private static final String DB_PATH = System.getProperty("user.home")+"/neo4j-sct3-store";
    private static final File File_Store = new File(DB_PATH);
    private static final EmbeddedDbManager dbManager = new EmbeddedDbManager(File_Store);
    private static GraphDatabaseService graphDB;
    Label descLabel = Label.label("Description");
    Label conceptLabel = Label.label("Concept");
    ArrayList<Node> descNodes;
    private static int counter =0;
    private static int MAX = 100000;
    private static int total;
    
    
    
    public UpdateHasDescription(){}
    
    public void updateHasDescription(){
        graphDB = dbManager.start(Neo4jConfig.load());
        registerShutdownHook(dbManager);
        Transaction tx = graphDB.beginTx();
        try{
            try(ResourceIterator<Node> descNode_iterator = tx.findNodes(descLabel)){
                descNodes = new ArrayList<>();
                while(descNode_iterator.hasNext()){
                    descNodes.add(descNode_iterator.next());
                }
                for(Node currentNode : descNodes){
                long conceptid = (long)currentNode.getProperty("conceptid");
                Node conceptNode = tx.findNode(conceptLabel, "sctid", conceptid);
                Relationship relationship = conceptNode.createRelationshipTo(currentNode, RelTypes.Has_Description);

                counter = counter+1;
                tx = storeData(counter,tx);
    }
  }
            tx.commit();
        } finally {
            tx.close();
        }
        
        dbManager.shutdown();
}
    
    private static enum RelTypes implements RelationshipType{
        Has_Description
    }
    
    
        private static void registerShutdownHook(final EmbeddedDbManager manager)
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running example before it's completed)
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                manager.shutdown();
            }
        } );
    }
        
   private Transaction storeData(int acounter, Transaction tx){
       if(acounter >= MAX){
         total = total + acounter;
         counter = 0;
         tx.commit();
         tx.close();
         System.out.println("Committed " + total + " ... ");
         
        }
       return tx = graphDB.beginTx();
   }        
        
}
