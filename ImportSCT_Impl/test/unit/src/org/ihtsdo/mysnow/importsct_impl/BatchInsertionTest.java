/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.mysnow.importsct_impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import org.ihtsdo.mysnow.importsct_impl.BatchInsertion.RelTypes;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.neo4j.graphdb.Direction;
import org.ihtsdo.mysnow.neo4j.EmbeddedDbManager;
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
public class BatchInsertionTest {
    
    public BatchInsertionTest() {
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

String filepath = System.getProperty("user.home") + "/neo4j-sct2-store";
File inserter_store = new File(filepath);
private final EmbeddedDbManager dbManager = new EmbeddedDbManager(inserter_store);
private GraphDatabaseService graphDB;
//String conceptfilepath="/Users/yoga/Downloads/Snapshot/Terminology/sct2_Concept_Snapshot_INT_20150731.txt";
//String DescFilepath="/Users/yoga/Downloads/Snapshot/Terminology/sct2_Description_Snapshot-en_INT_20150731.txt";
//String relfilepath = "/Users/yoga/Downloads/Snapshot/Terminology/sct2_Relationship_Snapshot_INT_20150731.txt";
//String LangFilePath = "/Users/yoga/Downloads/Snapshot/Refset/Language/der2_cRefset_LanguageSnapshot-en_INT_20150731.txt";

String conceptfilepath="/Users/yoga/Downloads/Snapshot/sct2_Concept_Snapshot_INT_20160131.txt";
String DescFilepath="/Users/yoga/Downloads/Snapshot/sct2_Description_Snapshot-en_INT_20160131.txt";
String relfilepath = "/Users/yoga/Downloads/Snapshot/sct2_Relationship_Snapshot_INT_20160131.txt";
String statedRelfilepath = "/Users/yoga/Downloads/Snapshot/sct2_StatedRelationship_Snapshot_INT_20160131.txt";
String LangFilePath = "/Users/yoga/Downloads/Snapshot/der2_cRefset_LanguageSnapshot-en_INT_20160131.txt";
String DefinitionFilePath="/Users/yoga/Downloads/Snapshot/sct2_TextDefinition_Snapshot-en_INT_20160131.txt";
String SimpleMapFilePath="/Users/yoga/Downloads/Snapshot/der2_sRefset_SimpleMapSnapshot_INT_20160131.txt";
long conceptid;
Label conceptLabel = Label.label("Concept");
Label descLabel = Label.label("Description");
ArrayList<Node> Nodes=new ArrayList<>();
int count;

    
    /**
     * Test of insertConcepts method, of class BatchInsertion.
     */
    @Test
    public void testInsertConcepts() throws Exception {
        
       System.out.println("insertConcepts");
       BatchInsertion instance = new BatchInsertion();
       System.out.println("Start importing SNOMED CT RF2 files ...");
       
       //Import files
//       instance.importConcepts(conceptfilepath);
//       instance.importSimpleMapRefset(SimpleMapFilePath);
//    
//       instance.importDescriptions(DescFilepath);
//       instance.importTextDefinitions(DefinitionFilePath);
//   
//       instance.importRefsetLangauge(LangFilePath);
//   
//       instance.importRelationships(relfilepath);
//       instance.importInferredRelaitonships(relfilepath);
//       instance.importStatedRelationships(statedRelfilepath);
//
//       instance.assignCounts();
       
       //End of importing

    /**
     *  Perforeming assignment for parent, children, descendant counts
     *  TODO: address commit and transaction issue:
     *  Transaction was marked as successful, but unable to commit transaction so rolled back.
org.neo4j.graphdb.TransactionFailureException: Transaction was marked as successful, but unable to commit transaction so rolled back.
	at org.neo4j.kernel.TopLevelTransaction.close(TopLevelTransaction.java:121)
	at org.ihtsdo.mysnow.importsct_impl.BatchInsertion.assignCounts(BatchInsertion.java:726)
     */
        


        
        System.out.println("Import SNOMED CT completed." +"\n" +"Starting the tests ...");
        graphDB = dbManager.start();
        try(Transaction tx = graphDB.beginTx()){
    /**
     *  Test import number count of concepts.
     */     
            try(ResourceIterator<Node> cNode_i = graphDB.findNodes(conceptLabel)){
            while(cNode_i.hasNext()){
                Nodes.add(cNode_i.next());
                count = Nodes.size();
                }
            }
            int cCount =424759;
            System.out.println("Expected import concept number = " + cCount +"\n"
            + "Answer = " + count);
        assertEquals(count, cCount);    
            
    /**
     *  Test import number count of descriptions.
     */
//            try(ResourceIterator<Node> cNode_i = graphDB.findNodes(descLabel)){
//            while(cNode_i.hasNext()){
//                Nodes.add(cNode_i.next());
//                count = Nodes.size();
//                }
//            }
//            int dCount =1259398;
//            System.out.println("Expected import description number = " + dCount +"\n"
//            + "Answer = " + count);
//        assertEquals(count, dCount); 

    /**
     *  Test sctid and concept node id mapping.
     *  conceptid 138875005 - nodeid 39969
     */
//        long answer_cNode_id = graphDB.findNode(conceptLabel, "sctid", 138875005).getId();
//        long expected_cNode_id = 39969L;
//        assertEquals(answer_cNode_id, expected_cNode_id);


     /**
     *  Test get sctid from concept node by property "sctid".
     */
        Node node = graphDB.findNode(conceptLabel, "sctid", 138875005);
        long expected = 138875005L;
        long answer = (long)node.getProperty("sctid");
        System.out.println("concept id is 138875005 \n" + "node id = " + node.getId());
        assertEquals(expected, answer); 
        
    /**
    *   Test get fsn from concept node by property "fsn".
    */
        Node node1 = graphDB.findNode(conceptLabel, "sctid", 132141000119106L);
        //132141000119106|Chronic deep venous thrombosis of lower extremity (disorder)
        String expected1 ="Chronic deep venous thrombosis of lower extremity (disorder)";
        String answer1 = node1.getProperty("fsn").toString();
        System.out.println("Exepected FSN = Chronic deep venous thrombosis of lower extremity (disorder)\n" 
                + "Anwser = " + answer1);
        //410662002|Concept model attribute (attribute)
        System.out.println("FSN - semantic tag = " + answer1.substring(0, answer1.length()-11));
        assertEquals(expected1, answer1);
        
    /**
     *  Test get descid from description node by property.
     */
        //termid: 31887011 term: Radiography of soft tissue of elbow
        Node desc_node1 = graphDB.findNode(descLabel, "descid", 31887011);
        long expected2 = 31887011L;
        long answer2 = (long)desc_node1.getProperty("descid");

        System.out.println("Exepected descriptino id = " +expected2 +"\n"
        + "Anwser = " + answer2);
        assertEquals(expected2, answer2);
        
    /**
     *  Test get term from description node by property term.
     */
        String expected3 = "Radiography of soft tissue of elbow";
        String answer3 = desc_node1.getProperty("term").toString();
        
        System.out.println("Expected term = " + expected3 +"\n" 
        + "Anwser = " + answer3);
        assertEquals(expected3, answer3);
    
    /**
     *  Test get terms of by concept node.
     */ 
        Collection<String> terms = new ArrayList<>();
        for(Relationship r : node.getRelationships(RelTypes.Has_description, Direction.OUTGOING)){
          terms.add(r.getEndNode().getProperty("term").toString());
        }
        System.out.println("Concept 138875005 has "+ terms.size() + " inactive and active terms");
        for(String t : terms){
            System.out.println("List of terms: "+t);
        }
    
    /**
     *  Test get active terms of by concept node.
     */ 
        Collection<String> active_terms = new ArrayList<>();
        for(Relationship r : node.getRelationships(RelTypes.Has_description, Direction.OUTGOING)){
            int status =(int)r.getEndNode().getProperty("active");
            if(status==1){
                active_terms.add(r.getEndNode().getProperty("term").toString());}
            }
            System.out.println("Concept 138875005 has "+ active_terms.size() + " active terms");
            for(String t : active_terms){
                System.out.println("List of terms: "+t);
            }
     /**
     *  Test get PT active terms of by concept node.
     * //acceptabilityid 900000000000548007 preferred   900000000000549004 acceptable
     * 302914006|Barrett's esophagus (disorder)
     */ 
//        ArrayList<String> pt_terms = new ArrayList<>();
        ArrayList<Node> pt_nodes = new ArrayList<>();
        String lang=null;
        long accept;
        Node lannode = graphDB.findNode(conceptLabel, "sctid", 132141000119106L);
        
        for(Relationship r : lannode.getRelationships(RelTypes.Has_description, Direction.OUTGOING)){
            Node descNode = r.getEndNode();
           
            int status =(int)descNode.getProperty("active");           
            long descriptiontype = (long)descNode.getProperty("desc_type");
            for(Relationship i_rel : descNode.getRelationships(RelTypes.Member_of_language_refset, Direction.OUTGOING)){
                accept = (long)i_rel.getProperty("accetabilityid");
                lang = i_rel.getEndNode().getProperty("refset").toString();
                if(status==1 && descriptiontype==900000000000013009L &&accept==900000000000548007L){
//                    pt_terms.add(r.getEndNode().getProperty("term").toString());}
                    pt_nodes.add(r.getEndNode());
                }
            }
        }
//            if(pt_terms.get(0).equals(pt_terms.get(1))){

            if(pt_nodes.get(0).getProperty("term").equals(pt_nodes.get(1).getProperty("term")))
                {
                    System.out.println("Concept  P [en] " + pt_nodes.get(0).getProperty("term"));
                } 
            else {
    //            System.out.println("Concept " + lannode.getProperty("sctid")+" has " + pt_nodes.size() + " active PT terms");
                for(Node n : pt_nodes){
                    if(n.hasProperty("US")){
                        System.out.println("Concept " + lannode.getProperty("sctid")+ " has PT :\n" + "P[en-US] " +n.getProperty("term"));}
                    if(n.hasProperty("GB")){
                    System.out.println("P[en-US] " + n.getProperty("term"));
                }
            }
        }
        
    /**
     *  Test Has_description relationship exists or not.
     */
        for(Relationship rel : node1.getRelationships(RelTypes.Has_description)){
            Node description = rel.getEndNode();
            System.out.println("terms are " + description.getProperty("term"));
            System.out.println("Expected terms are Chronic deep venous thrombosis of lower extremity (disorder) ");
                    
        }
        
    /**
     *  Test relationship exists or not.
     */    
        for(Relationship rel : node1.getRelationships(RelationshipType.withName("Finding site"))){
            Node siteNode = rel.getEndNode();
            System.out.println("Finding site = " + siteNode.getProperty("fsn").toString());
        }
        System.out.println("Concept modeling for Kidney biopsy");

        Node nodeatt = graphDB.findNode(conceptLabel, "sctid", 7246002);
        TreeSet<Integer> rgset = new TreeSet<>();
        for (Relationship r : nodeatt.getRelationships(Direction.OUTGOING)){
            if(r.hasProperty("rg")){
            int rg = (int)r.getProperty("rg");
            rgset.add(rg);
            }
        }
        System.out.println(rgset);
        Iterator i = rgset.iterator();
            while(i.hasNext()){
                int rgno = (int)i.next();
                System.out.println("RolGroup: " + rgno);
                for(Relationship rel : nodeatt.getRelationships(Direction.OUTGOING)){
                    if(rel.hasProperty("stated") && (int)rel.getProperty("stated")==1 && (int)rel.getProperty("rg")==rgno){            
                        String relName = rel.getType().name();
                        System.out.println("Stated Attribute: " + relName +" = "+ rel.getEndNode().getProperty("fsn").toString() 
                        + " RG= " + rel.getProperty("rg").toString());
                    }     
//            if(rel.hasProperty("stated") && (int)rel.getProperty("stated")==0 && (int)rel.getProperty("rg")==rgno ){
//            String relName = rel.getType().name();
//            System.out.println("Inferred Attribute: " + relName +" = "+ rel.getEndNode().getProperty("fsn").toString()
//                    + " RG= "+ rel.getProperty("rg").toString());
//            }
        }
        }
        
        
    /**
     *  Test ISA relationship exists or not.  FSNs are more than one. TODO
     */    
        for(Relationship rel : node1.getRelationships(RelationshipType.withName("Is a"), Direction.OUTGOING)){
            Node siteNode = rel.getEndNode();
            System.out.println("Parent Concepts = " + siteNode.getProperty("sctid")+ " | " 
                    + siteNode.getProperty("fsn").toString() + " | "
                    + siteNode.getProperty("active") + " | "
                    + siteNode.getProperty("effectiveTime"));
        }    
        
    /**
     *  Test role grouping
     */    
        Relationship rel = graphDB.getRelationshipById(78950);
        System.out.println("rg missing on this relationship: " +rel.getType().name());
    
        for(Relationship r: node1.getRelationships(Direction.OUTGOING)){
//            if(r.getType().name() == null ? 
//                    RelTypes.Has_description.name() != null : 
//                        !r.getType().name().equals(RelTypes.Has_description.name())
//                        &&!r.getType().name().equals(RelTypes.In_language_refset.name())
//                        &&!r.getType().name().equals(RelTypes.IS_A.name())
//                    )
            if(r.hasProperty("rg") && !r.getType().name().equals("Is a"))
            {
               System.out.println("Node name : " 
                        +r.getType().name() +"\n "
                        + "role group" +r.getProperty("rg"));
            }
        }
        

        
    
    Label USlangLabel = Label.label("US");
    Node usNode = graphDB.findNode(USlangLabel, "refset", "US");
    System.out.println("US refset node id = "+ usNode.getId());
        
    Label GBlangLabel = Label.label("GB");
    Node gbNode = graphDB.findNode(GBlangLabel, "refset", "GB");
    System.out.println("GB refset node id = " + gbNode.getId());
    
    
    
    
    Node UStermNode = graphDB.findNode(descLabel, "descid", 743848012L);   
        if(UStermNode.hasProperty("US")){
        System.out.println("743848012 | "+ UStermNode.getProperty("term").toString() + " Language = en-US");}
        else if(UStermNode.hasProperty("GB")){
        System.out.println("743848012 | "+ UStermNode.getProperty("term").toString() + " Langauge = en-GB");}
        
//    Iterable<Relationship> rel_i = UStermNode.getRelationships(RelTypes.In_language_refset, Direction.OUTGOING);
//     ArrayList<Relationship> rel_it1 = new ArrayList<>();
////     ArrayList<Node> UStermNodes = new ArrayList<>();
//    while(rel_i.iterator().hasNext()){
//        System.out.println("Term = " + UStermNode.getProperty("term"));   
//        rel_it1.add(rel_i.iterator().next());
//        System.out.println("count of relationship = " +rel_it1.size());
//        for(Relationship r : rel_it1){            
////            UStermNodes.add(r.getEndNode());
////                for(Node langnode : UStermNodes){
//                    String expected_refset = "en-US";
//                    String answer_refset = r.getEndNode().getProperty("refset").toString();
//                    System.out.println("Exepected refset = en-US" +"\n"
//                    + "Answer = " + answer_refset);
//                    assertEquals(expected_refset, answer_refset);
//        }
//    }
    //  fail("The test case is a prototype.");
    
    
    Node GBtermNode = graphDB.findNode(descLabel, "descid", 28383015L);
    if(GBtermNode.hasProperty("US")){
        System.out.println("28383015 | " + GBtermNode.getProperty("term").toString() + " Language = en-US");}
    else if(GBtermNode.hasProperty("GB")){
        System.out.println("28383015 | " + GBtermNode.getProperty("term").toString() + " Langauge = en-GB");}

    Node node_count= graphDB.findNode(conceptLabel, "sctid", 86049000L);
    System.out.println("Parent Count= "+ node_count.getProperty("parent_count") +"\n"  
            + " Child Count= "+node_count.getProperty("child_count") + "\n"
            + " Descendent count = " + node_count.getProperty("descendent_count"));
    
    Iterable<Relationship> re_i = node_count.getRelationships(RelTypes.Has_simple_map, Direction.OUTGOING);
    HashSet<String> reset = new HashSet<>();
    for(Relationship r: re_i){
        if((int)r.getProperty("active")==1){
        String refset = r.getEndNode().getProperty("refset").toString();
        String maptarget = r.getEndNode().getProperty("maptarget").toString();
        reset.add(refset + " map = " + maptarget);
        }
    }
    for(String s:reset){
    System.out.println(s);}
    
        tx.commit();
        } finally {
            dbManager.shutdown();
        }
    }
}

