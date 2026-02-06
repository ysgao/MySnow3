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
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author yoga
 */
public class UpdateDescription {
   
    private static final String DB_PATH = System.getProperty("user.home")+"/neo4j-sct2-store";
    private static final File File_Store = new File(DB_PATH);
    private static final FileObject File_Store_FO = FileUtil.toFileObject(File_Store);
    private static GraphDatabaseService graphDB; 
    private static final EmbeddedDbManager dbManager = new EmbeddedDbManager(File_Store);
    Label descLabel = Label.label("Description");
    Label conceptLabel = Label.label("Concept");
    ArrayList<Node> descNodes;
    private static int counter =0;
    private static int MAX = 10000;
    private static int total;
    
    public UpdateDescription(){
        graphDB= dbManager.start(Neo4jConfig.load());
        registerShutdownHook(dbManager);
    }
    
    public void updateDescription(){
        Transaction tx = graphDB.beginTx();
        try {
            try(ResourceIterator<Node> descNode_iterator = tx.findNodes(descLabel)){
                descNodes = new ArrayList<>();
                while(descNode_iterator.hasNext()){
                    descNodes.add(descNode_iterator.next());
                }
                for(Node currentNode : descNodes){
                    Object conceptid =currentNode.getProperty("conceptid");
                    Node conceptNode = tx.findNode(conceptLabel, "sctid", conceptid);
                
                    //add FSN_KEY to concept node, terms are not inactivated when concept is retired.
                    String fsnid="900000000000003001";
                    String desctype=currentNode.getProperty("desc_type").toString();
                    int status=Integer.parseInt(currentNode.getProperty("active").toString());
                    
                    if(desctype.equals(fsnid) && status==1){
                    String fsn = currentNode.getProperty("term").toString();
//                    System.out.println("Updating FSN in concept node: " + fsn);
                    conceptNode.setProperty("fsn", fsn);
                
                    counter = counter+1;
                    tx = storeData(counter,tx);
                    }
                }
            }
            tx.commit();
        } finally {
            tx.close();
        }
        dbManager.shutdown();
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
