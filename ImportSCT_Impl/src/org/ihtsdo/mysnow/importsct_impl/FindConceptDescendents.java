/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.mysnow.importsct_impl;


import java.io.File;
import java.util.ArrayList;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;


/**
 *
 * @author yoga
 */
public class FindConceptDescendents {
    
String filepath = System.getProperty("user.home") + "/neo4j-sct3-store";
File inserter_store = new File(filepath);
private static GraphDatabaseService graphDB;
long conceptid;
Label conceptLabel = Label.label("Concept");
ArrayList<Node> conceptNodes;
Node node;
    
    public FindConceptDescendents(){}
    
    public void findConceptDescendents(long sctid){
//                   int n = 0;
//              Node startNode = nodeIndex.get(SCTID_KEY, sctid).getSingle();
//              Traverser myTraverser = getISAExcludeStartTraverser(startNode);
//              String fn = System.getProperty("user.home") + "/Descendants_" + startNode.getProperty(SCTID_KEY)+".txt";
//              File descendenttable = new File(fn);
////              try(BufferedWriter writer = Files.newBufferedWriter(daisyfile,charset, StandardOpenOption.CREATE)){
//              try{
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fn),"UTF-8"));
////                writer.write("SubConcept" + "\t" + "SuperConcept" + "\n");
//                for (Path subPath : myTraverser){                  
// //                 System.out.println(
// //                         "Has subconcepts at depth: " + subPath.length()+ " "
//                    String conceptid = subPath.endNode().getProperty(SCTID_KEY).toString();                          
//                    String conceptfsn = subPath.endNode().getProperty(FSN_KEY).toString();
////                    String daisytc = subPath.endNode().getProperty(SCTID_KEY).toString()+ " | "
////                                    + subPath.endNode().getProperty(FSN_KEY);
////                    System.out.println(sctid + " has descendent: " + conceptid + " | " + conceptfsn);
//                         
////                   try{                           
////                         writer.write(conceptid + "  " + conceptfsn + "\n");
//                    writer.write(conceptid + "|" + conceptfsn + "\n");
////                    System.out.println(conceptid + "\t" + sctid + "\n");
//                       n = n+1;
//                    }
//                writer.close();
//                }catch (IOException x) {
////                System.err.format("IOException: %s%n", x);
//                  io.getErr().println("IOException: %s%n" + x);
//                  io.getErr().close();
//                }
//                
////               }catch (IOException x){
////                  System.err.println(x.getStackTrace());
//            try{  
//            io.select();
//            io.getOut().println(" =============================== ");
//            io.getOut().println("Created file for descendants at: ");
//            io.getOut().println(descendenttable.getAbsolutePath(), new OpenFileHyperlinkListener());
//            io.getOut().println("Totoal number of descendants: " + n);
//            io.getOut().println(" =============================== ");
//            io.getOut().println("");
//            io.getOut().close();
//            }catch(IOException e){
//                e.printStackTrace();
//            }  
        
        
    }
    
//    private static Traverser getDirectSubTraverser(final Node node){
//        TraversalDescription td = graphDB.traversalDescription()
//                .breadthFirst()
//                .relationships(RelTypes.116680003, Direction.INCOMING)
//                .evaluator(Evaluators.includingDepths(1, 1));
//        return td.traverse(node);        
//    }
    
}
