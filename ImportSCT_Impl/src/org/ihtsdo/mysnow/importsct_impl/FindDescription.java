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
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author yoga
 */
public class FindDescription {
    
    private final String filepath = System.getProperty("user.home") + "/neo4j-sct3-store";
    private final File inserter_store = new File(filepath);
    private final EmbeddedDbManager dbManager = new EmbeddedDbManager(inserter_store);
    private final Label conceptLabel = Label.label("Concept");
    private final Label descLabel = Label.label("Description");
    private ArrayList<Node> descNodes;
    private Node node;
    private String fsn;
    
   public void findDescription(){
    
   GraphDatabaseService graphDB = dbManager.start(Neo4jConfig.load());
   try ( Transaction tx = graphDB.beginTx() ){
        try(ResourceIterator<Node> descNode_iterator = tx.findNodes(descLabel)){
            descNodes = new ArrayList<>();
            while(descNode_iterator.hasNext()){
                descNodes.add(descNode_iterator.next());
                
            }
            for(Node currentnode : descNodes){
                System.out.println("Status is " + currentnode.getProperty("active"));
                System.out.println("Effective time is " + currentnode.getProperty("effectiveTime"));
                System.out.println("Term is " + currentnode.getProperty("term"));
            }
        }
    tx.commit();
    }
   finally {
       dbManager.shutdown();
   }
    }
   
   public String getFSN(long sctid){
    GraphDatabaseService graphDB = dbManager.start(Neo4jConfig.load());
    try(Transaction tx = graphDB.beginTx()){        
        Node node = tx.findNode(conceptLabel, "sctid", sctid);
        fsn = node.getProperty("fsn").toString();
        tx.commit();
        return fsn;
        }
    finally {
        dbManager.shutdown();
    }
   }   
    
}
