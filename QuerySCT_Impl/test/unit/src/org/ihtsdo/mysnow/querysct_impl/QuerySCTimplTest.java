/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.mysnow.querysct_impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.traversal.Traverser;

/**
 *
 * @author yoga
 */
public class QuerySCTimplTest {
    GraphDatabaseService graphDB;
    QuerySCTimpl instance;
    private static final String SCTID_KEY = "sct_id";
    private static final String FSN_KEY = "fsn";
    private static final String CONSTATUS_KEY = "concept_status";
    private static final String ISPRIMITIVE_KEY = "isprimitive";    
    private static final String DESCID_KEY = "desc_id";
    private static final String DESCSTATUS_KEY = "desc_status";
    private static final String TERM_KEY = "term";
    private static final String INITCAP_KEY = "initcap";
    private static final String DESCTYPE_KEY = "desc_type";
    private static final String LANG_KEY = "language";
    private static final String PCOUNT_KEY = "parent_count";
    private static final String CCOUNT_KEY = "child_count";
    private static final String DCOUNT_KEY = "decendant_count";
    private static final String CHARACT_KEY="CHARACTERISTICTYPE";
    private static final String REFINE_KEY ="REFINABILITY";
    private static final String RG_KEY = "RELATIONSHIPGROUP";
    
    public QuerySCTimplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new QuerySCTimpl();
        instance.startGraphDB();
        graphDB = instance.getGraphDB();
        
        
    }
    


    /**
     * Test of getSctID method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testGetSctID() {
        System.out.println("getSctID");
        Node node = null;
        String expResult = "";
        String result = instance.getSctID(node);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSctFSN method, of class QuerySCTimpl.
     */
    @Test
    public void testGetSctFSN() {
        System.out.println("getSctFSN");
        Node node = instance.getNodebyID(71388002);
        String expResult = "Procedure (procedure)";
        String result = instance.getSctFSN(node);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getSctStatus method, of class QuerySCTimpl.
     */
    @Test
    public void testGetSctStatus() {
        System.out.println("getSctStatus");
        Node node = instance.getNodebyID(118234003);
        Integer expResult = 0;
        Integer result = instance.getSctStatus(node);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getNodebyID method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testGetNodebyID() {
        System.out.println("getNodebyID");
//        IndexManager index = graphDB.index();
//        boolean indexExists = index.existsForNodes( "nodes" );
//        System.out.println("Index exists ? " + indexExists);
        nodeindex = graphDB.index().forNodes("nodes");
        IndexHits<Node> hits = nodeindex.query(FSN_KEY, "Ablation using magnetic resonance imaging guidance (procedure)");
        Node concept=hits.getSingle();
        System.out.println(concept.getProperty(FSN_KEY).toString());
        long sctid = 440102005;
        Node expResult = concept;
        Node result = instance.getNodebyID(sctid);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getTerms method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testGetTerms() {
        System.out.println("getTerms");
        Node node = instance.getNodebyID(440102005);
        Collection expResult = new ArrayList<String>();
//        expResult.add("Ablation using magnetic resonance imaging guidance");
//        expResult.add("Ablation using magnetic resonance imaging guidance (procedure)");

        Collection result = instance.getTerms(node);
        
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getTermsActive method, of class QuerySCTimpl.
     */
    
    @Test
    public void testGetTermsActive() {
        System.out.println("getTermsActiveOnly");
        Node node = instance.getNodebyID(173637002);
        //173637002|Fiberoptic esophagoscopy and laser (procedure)
        Node node1 = instance.getNodebyID(431216004);
        //431216004 Insertion of catheter into pulmonary artery using fluoroscopic guidance (procedure)
        Collection expResult = null;
        Collection<String> result = instance.getTermsActiveOnly(node);
        for(String st: result){
            System.out.println(st);
        }
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
    
//    @Test
//    public void testGetIndexHits(){
//        System.out.println("getIndexHits");
//        String searchst = "Contact*";
//        for(Iterator<Node> iterator = instance.getIndexHits(searchst); iterator.hasNext();){
//            Node tnode = iterator.next();
//            System.out.println("T nodes are: " + tnode.toString());
//            
//        }
//        System.out.println("Failed getIndexHits");
//    }
    

    /**
     * Test of getDescFSN method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testGetDescFSN() {
        System.out.println("getDescFSN");
        Node node = null;
        String expResult = "";
        String result = instance.getDescFSN(node);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDescPT method, of class QuerySCTimpl.
     */
    
    @Test
    public void testGetDescPT() {
        System.out.println("getDescPT");
        Node node = instance.getNodebyID(449404007);
        //449404007|Cryosurgery of lesion of liver (procedure)
        //173637002|Fiberoptic esophagoscopy and laser (procedure)
        Node node1 = instance.getNodebyID(431216004);
        //431216004 Insertion of catheter into pulmonary artery using fluoroscopic guidance (procedure)
//        Collection expResult = null;
        Collection<String> result = instance.getDescPT(node);
//        System.out.println("Expected: \n"
//                + "P [en-GB]  Fibreoptic oesophagoscopy and laser\n" +
//                "P [en-US]  Fiberoptic esophagoscopy and laser");
        for(String st: result){
            System.out.println(st);
        }
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getDescSyn method, of class QuerySCTimpl.
     */
    
    @Test
    public void testGetDescSyn() {
        System.out.println("getDescSyn");
        Node node = instance.getNodebyID(169443000);
//        Node node = instance.getNodebyID(431216004);
//        Collection expResult = null;
        Collection<String> result = instance.getDescSyn(node);
        for(String st: result){
            System.out.println(st);
        }
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getDescType method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testGetDescType() {
        System.out.println("getDescType");
        Node node = null;
        int expResult = 0;
        long result = instance.getDescType(node);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIsPrimitive method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testGetIsPrimitive() {
        System.out.println("getIsPrimitive");
        Node node = null;
        int expResult = 0;
        long result = instance.getIsPrimitive(node);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDescStatus method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testGetDescStatus() {
        System.out.println("getDescStatus");
        Node node = null;
        int expResult = 0;
        int result = instance.getDescStatus(node);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLang method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testGetLang() {
        System.out.println("getLang");
        Node node = null;
        String expResult = "";
        String result = instance.getLang(node);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFindingSite method, of class QuerySCTimpl.
     */
//    @Ignore
//    @Test
//    public void testGetFindingSite() {
//        System.out.println("getFindingSite");
//        Node node = null;
//        Collection expResult = null;
//        Collection result = instance.getFindingSite(node);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of getRGFindingSite method, of class QuerySCTimpl.
     */
//    @Ignore
//    @Test
//    public void testGetRGFindingSite() {
//        System.out.println("getRGFindingSite");
//        Node node = null;
//        Collection expResult = null;
//        Collection result = instance.getRGFindingSite(node);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of getRGs method, of class QuerySCTimpl.
     */
    
//    @Test
//    public void testGetRGs() {
//        System.out.println("getRGs");
//        Node node = instance.getNodebyID(438529005);
//        Collection expResult = null;
//        Collection result = instance.getRGs(node);
//        //assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        //fail("The test case is a prototype.");
//    }
    
        /**
     * Test of getModels method, of class QuerySCTimpl.
     */
    @Test
    public void testGetModels() {
        System.out.println("getModels");
        Node node = instance.getNodebyID(438529005);
        Collection expResult = null;
        Collection result = instance.getModels(node);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getTermsActiveOnly method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testGetTermsActiveOnly() {
        System.out.println("getTermsActiveOnly");
        Node node = instance.getNodebyID(173637002);
        //173637002|Fiberoptic esophagoscopy and laser (procedure)
        Node node1 = instance.getNodebyID(431216004);
        //431216004 Insertion of catheter into pulmonary artery using fluoroscopic guidance (procedure)
        Collection expResult = null;
        Collection<String> result = instance.getTermsActiveOnly(node);
        for(String st: result){
            System.out.println(st);
        }
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of startGraphDB method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testStartGraphDB() {
        System.out.println("startGraphDB");
        instance.startGraphDB();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of shutdownGraphDB method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testShutdownGraphDB() {
        System.out.println("shutdownGraphDB");
        instance.shutdownGraphDB();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIndex method, of class QuerySCTimpl.
     */
//    @Ignore
//    @Test
//    public void testGetIndex() {
//        System.out.println("getIndex");
//        Index expResult = null;
//        Index result = instance.getIndex();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of getGraphDB method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testGetGraphDB() {
        System.out.println("getGraphDB");
        GraphDatabaseService expResult = null;
        GraphDatabaseService result = instance.getGraphDB();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFSNbyID method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testGetFSNbyID() {
        System.out.println("getFSNbyID");
        long sctid = 0L;
        String expResult = "";
        String result = instance.getFSNbyID(sctid);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getISATraverser method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testGetISATraverser() {
        System.out.println("getISATraverser");
        Node node = null;
        Traverser expResult = null;
        Traverser result = instance.getISATraverser(node);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getISAiTraverser method, of class QuerySCTimpl.
     */
//    @Ignore
//    @Test
//    public void testGetISAiTraverser() {
//        System.out.println("getISAiTraverser");
//        Node node = null;
//        Traverser expResult = null;
//        Traverser result = instance.getISAiTraverser(node);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of findDirectSuperNodes method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testFindDirectSuperNodes() {
        System.out.println("findDirectSuperNodes");
        long sctid = 0L;
        instance.findDirectSuperNodes(sctid);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findSuperNodes method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testFindSuperNodes() {
        System.out.println("findSuperNodes");
        long sctid = 0L;
        instance.findSuperNodes(sctid);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findDirectSubNodes method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testFindDirectSubNodes() {
        System.out.println("findDirectSubNodes");
        long sctid = 0L;
        instance.findDirectSubNodes(sctid);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDirectSuperNodes method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testGetDirectSuperNodes() {
        System.out.println("getDirectSuperNodes");
        Node sctnode = null;
        Collection expResult = null;
        Collection result = instance.getDirectSuperNodes(sctnode);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getParentNodeDescrition method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testGetParentNodeDescrition() {
        System.out.println("getParentNodeDescrition");
        Node sctnode = null;
        Collection expResult = null;
        Collection result = instance.getParentNodeDescrition(sctnode);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDirectSuperNodeCount method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testGetDirectSuperNodeCount() {
        System.out.println("getDirectSuperNodeCount");
        Node sctnode = null;
        String expResult = "";
        int result = instance.getDirectSuperNodeCount(sctnode);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDirectSubNodes method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testGetDirectSubNodes() {
        System.out.println("getDirectSubNodes");
        Node sctnode = null;
        Collection expResult = null;
        Collection result = instance.getDirectSubNodes(sctnode);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSubConceptNo method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testGetSubConceptNo() {
        System.out.println("getSubConceptNo");
        Node sctnode = null;
        String expResult = "";
        int result = instance.getSubConceptNo(sctnode);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDescendantNodeCount method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testGetDescendantNodeCount() {
        System.out.println("getDescendantNodeCount");
        Node sctnode = null;
        String expResult = "";
        int result = instance.getDescendantNodeCount(sctnode);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllSctNodes method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testGetAllSctNodes() {
        System.out.println("getAllSctNodes");
        Node sctnode = null;
        Collection expResult = null;
        Collection result = instance.getAllSctNodes(sctnode);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findAllSubNodes method, of class QuerySCTimpl.
     */

    @Test
    public void testFindAllSubNodes() {
        System.out.println("findAllSubNodes");
        long sctid = 225287004;
        instance.findAllSubNodes(sctid);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of findAllSubandInactiveNodes method, of class QuerySCTimpl.
     */
//    @Ignore
//    @Test
//    public void testFindAllSubandInactiveNodes() {
//        System.out.println("findAllSubandInactiveNodes");
//        long sctid = 0L;
//        instance.findAllSubandInactiveNodes(sctid);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of findSameAsNodes method, of class QuerySCTimpl.
     */
//    @Ignore
//    @Test
//    public void testFindSameAsNodes() {
//        System.out.println("findSameAsNodes");
//        long sctid = 0L;
//        instance.findSameAsNodes(sctid);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of findSameAsForAllNodes method, of class QuerySCTimpl.
     */
//    @Ignore
//    @Test
//    public void testFindSameAsForAllNodes() {
//        System.out.println("findSameAsForAllNodes");
//        long sctid = 0L;
//        instance.findSameAsForAllNodes(sctid);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of findMayBeForAllNodes method, of class QuerySCTimpl.
     */
//    @Ignore
//    @Test
//    public void testFindMayBeForAllNodes() {
//        System.out.println("findMayBeForAllNodes");
//        long sctid = 0L;
//        instance.findMayBeForAllNodes(sctid);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of findReplacedByForAllNodes method, of class QuerySCTimpl.
     */
//    @Ignore
//    @Test
//    public void testFindReplacedByForAllNodes() {
//        System.out.println("findReplacedByForAllNodes");
//        long sctid = 0L;
//        instance.findReplacedByForAllNodes(sctid);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of findWasAForAllNodes method, of class QuerySCTimpl.
     */
//    @Ignore
//    @Test
//    public void testFindWasAForAllNodes() {
//        System.out.println("findWasAForAllNodes");
//        long sctid = 0L;
//        instance.findWasAForAllNodes(sctid);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of findMayBeReplacedForAllNodes method, of class QuerySCTimpl.
     */
//    @Ignore
//    @Test
//    public void testFindMayBeReplacedForAllNodes() {
//        System.out.println("findMayBeReplacedForAllNodes");
//        long sctid = 0L;
//        instance.findMayBeReplacedForAllNodes(sctid);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of findAllHistoryForAllNodes method, of class QuerySCTimpl.
     */
//    @Ignore
//    @Test
//    public void testFindAllHistoryForAllNodes() {
//        System.out.println("findAllHistoryForAllNodes");
//        long sctid = 0L;
//        instance.findAllHistoryForAllNodes(sctid);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of findSameAsAllSubNodes method, of class QuerySCTimpl.
     */
//    @Ignore
//    @Test
//    public void testFindSameAsAllSubNodes() {
//        System.out.println("findSameAsAllSubNodes");
//        long sctid = 0L;
//        instance.findSameAsAllSubNodes(sctid);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of findSameAsDirectSubNodes method, of class QuerySCTimpl.
     */
//    @Ignore
//    @Test
//    public void testFindSameAsDirectSubNodes() {
//        System.out.println("findSameAsDirectSubNodes");
//        long sctid = 0L;
//        instance.findSameAsDirectSubNodes(sctid);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of findISATCNodes method, of class QuerySCTimpl.
     */
    @Ignore
    @Test
    public void testFindISATCNodes() {
        System.out.println("findISATCNodes");
        long sctid = 0L;
        instance.findISATCNodes(sctid);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findSameAsISATCNodes method, of class QuerySCTimpl.
     */
//    @Ignore
//    @Test
//    public void testFindSameAsISATCNodes() {
//        System.out.println("findSameAsISATCNodes");
//        long sctid = 0L;
//        instance.findSameAsISATCNodes(sctid);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of findDaisyNodes method, of class QuerySCTimpl.
     */
//    @Ignore
//    @Test
//    public void testFindDaisyNodes() {
//        System.out.println("findDaisyNodes");
//        long sctid = 0L;
//        instance.findDaisyNodes(sctid);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of findMayBeDaisyNodes method, of class QuerySCTimpl.
     */
//    @Ignore
//    @Test
//    public void testFindMayBeDaisyNodes() {
//        System.out.println("findMayBeDaisyNodes");
//        long sctid = 0L;
//        instance.findMayBeDaisyNodes(sctid);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }


    
    
    @After
    public void tearDown() {
       // graphDB.shutdown();
    }
}
