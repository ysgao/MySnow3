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
public class FindConceptByID {
    
String filepath = System.getProperty("user.home") + "/neo4j-sct2-store";
File inserter_store = new File(filepath);
private final EmbeddedDbManager dbManager = new EmbeddedDbManager(inserter_store);
private static GraphDatabaseService graphDB;
long conceptid;
Label conceptLabel = Label.label("Concept");
ArrayList<Node> conceptNodes;
Node node;


   
    public FindConceptByID(){}
    
    public void findConceptByID(){
    graphDB = dbManager.start(Neo4jConfig.load());
    try (Transaction tx = graphDB.beginTx()) {
        node = tx.findNode(conceptLabel, "sctid", 138875005);
        System.out.println("concept id is 138875005 and node id = " + node.getId());
        System.out.println("concept fsn = " + node.getProperty("fsn").toString());
    
        try(ResourceIterator<Node> concepts = tx.findNodes(conceptLabel)){
            conceptNodes = new ArrayList<>();
            while(concepts.hasNext()){
                conceptNodes.add(concepts.next());
                
            }
            for(Node node : conceptNodes){
                conceptid = Long.parseLong(node.getProperty("sctid").toString());
//                System.out.println("Status is " + node.getProperty("active"));
//                System.out.println("Effective time is " + node.getProperty("effectiveTime"));
//                System.out.println("Definition status is " + node.getProperty("definition_status"));
//               if (id==conceptid){
//                   System.out.println("concept id " + node.getProperty("sctid"));
//                    break;
//                                 }
            }
        }
        tx.commit();
    }
    finally {
        dbManager.shutdown();
    }
    }
    
}
    
