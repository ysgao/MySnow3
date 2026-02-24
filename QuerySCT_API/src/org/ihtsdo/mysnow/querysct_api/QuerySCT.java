/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.mysnow.querysct_api;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.traversal.Traverser;

/**
 *
 * @author yoga
 */
public interface QuerySCT {
    public void startGraphDB();
    public void shutdownGraphDB();
    
    public GraphDatabaseService getGraphDB();
    
    
//    public Index<Node> getIndex();
    
    /*
     * @method Return BigInteger SCT_ID by Neo4j node 
    */
    
    public String getSctID(Node node);
    
    /*
     *@method Return String SCT_FSN by Neo4j node 
     */
    public String getSctFSN(Node node);
    
    /*
     *@method Return Integer SCT_Status by Neo4j node
     */
    public int getSctStatus(Node node);
    
//    public Iterator<Node> getIndexHits(String searchst);
    
    public Collection<String> getTerms(Node node);
    
    public Collection<String> getTermsActiveOnly(Node node);
    
    public Collection<String> getTermsActiveType(Node node);
    
    
    public String getDescFSN(Node node);
            
    public Collection<String> getDescPT(Node node);
    
    public Collection<String> getDescSyn(Node node);
    
//    public Collection<String> getDesUnspec(Node node);
        
    public long getDescType(Node node);
    
    public int getDescStatus(Node node);
    
    public long getIsPrimitive(Node node);
    
    public String getLang(Node node);
    
//    public Collection<String> getRGFindingSite(Node node);
    
//    public Collection<String> getFindingSite(Node node);
    
 //   public Collection<String> getRGs(Node node);
    
    public Collection<String> getModels(Node node);
    public Collection<String> getStatedModels(Node node);
    
    /*
     * @method Return String FSN by the input of string concept ID
     */
    public String getFSNbyID(long sctid);
    
    
    public Node getNodebyID(long sctid);
    
    public Node getNodebyTermNode(Node tnode);
    
    public int getSubConceptNo(Node node);
    
    public int getDirectSuperNodeCount(Node sctnode);
    
    public int getDescendantNodeCount(Node sctnode);
    
    public Collection<Node> getAllSctNodes(Node sctnode);
    
    public Iterator<Node> getAllSctNodeIterator(long sctid);
    
    public void findDirectSuperNodes(long sctid);
    
    
    public void findSuperNodes(long sctid);
    
    
    public void findDirectSubNodes(long sctid);
    
    public Collection<Node> getDirectSuperNodes(Node sctnode);
    
    public Collection<String> getParentNodeDescrition(Node sctnode);
    public Collection<String> getStatedParentNodeDescrition(Node sctnode);
    
    public Collection<String> getPrimitiveParentDescription(Node sctnode);
    
    public Collection<String> getTextDefinition(Node sctnode);
    
    public Collection<String> getSimpleMapRefset(Node sctnode);
    
    public Collection<Node> getDirectSubNodes(Node sctnode);
    
    /*
     * @Create a list of all descendent concepts
     */    
    public void findAllSubNodes(long sctid);
    

    
    
    /*
     *  @Crete a standard Transitive Closure table for ISA relationship 
     */    
    public void findISATCNodes(long sctid);
    
    
    
//    public void findSameAsNodes(long sctid);
//    
//    
//    
//    public void findSameAsDirectSubNodes(long sctid);
    
    
    /*
     * @Create a list of all nodes linked by same as and moved from relationships. 
     * It is substititution table from inactive to active concepts by
     * same as and moved from.
     */
//    public void findSameAsForAllNodes (long sctid); 
//    
//    public void findMayBeForAllNodes(long sctid);
//    
//    public void findReplacedByForAllNodes(long sctid);
//    
//    public void findWasAForAllNodes(long sctid);
//    
//    public void findMayBeReplacedForAllNodes(long sctid);
//    
//    public void findAllHistoryForAllNodes(long sctid);
//    
//    
//    /*
//     * @Create a list of all subnodes and their same as nodes
//     */
//    public void findSameAsAllSubNodes(long sctid);
//    
//    
//    /*
//     * @Create a list of subnodes which includes active by ISA and inactive concepts by iISA relationships
//     */
//    public void findAllSubandInactiveNodes(long sctid);
//    
//    
//    
//    /*
//     * @Create Daisy table by ISA and SAME AS relationships
//     */
//    public void findSameAsISATCNodes(long sctid);
//       
//    /*
//     * @Create daisy table by iISA and ISA relationships (TC table with extension of SameAs and MovedFrom relationships)
//     */
//    public void findDaisyNodes(long sctid);
//    
//    
//    
//    /*
//     * Creat a daisy table with MaybeA and WasA extension (TC table with extension of SameAs, MoveFrom, MayBeA, WasA, ReplacedBy relationships )
//     */
//    public void findMayBeDaisyNodes(long sctid);
    
    public Traverser getISATraverser(Node node);

    /**
     * @return short summary of relationship counts for diagnostics
     */
    public String getRelationshipSummary(Node node);
    
//    public Traverser getISAiTraverser(Node node);
    
    
}
