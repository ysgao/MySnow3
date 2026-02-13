/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.mysnow.querysct_impl;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ihtsdo.mysnow.neo4j.EmbeddedDbManager;
import org.ihtsdo.mysnow.neo4j.Neo4jConfig;
import org.ihtsdo.mysnow.querysct_api.QuerySCT;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluation;
import static org.neo4j.graphdb.traversal.Evaluation.EXCLUDE_AND_CONTINUE;
import static org.neo4j.graphdb.traversal.Evaluation.INCLUDE_AND_PRUNE;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;
import org.openide.windows.OutputWriter;

/**
 *
 * @author yoga
 */
@ServiceProvider(service = QuerySCT.class)
public class QuerySCTimpl implements QuerySCT {
    private static final String DB_PATH = System.getProperty("user.home")+"/neo4j-sct2-store"; 
//    private static final String DB_PATH = "/graphstore/neo4j-sct-store"; 
    private static final File File_Store = new File(DB_PATH);
    private static final EmbeddedDbManager dbManager = new EmbeddedDbManager(File_Store);
//    private static final FileObject File_Store_FO = FileUtil.toFileObject(File_Store);
//    private static final String DB_PATH = "D:/neo4j-sct-store";
//    private static final File File_Store = new File("D:/neo4j-sct-store");
    private static GraphDatabaseService graphDB;
    private static final ThreadLocal<Transaction> THREAD_TX = new ThreadLocal<>();

    private static String sctid;
    private String fsn;
    private static String sctfsn=null;
    private static int sct_status=0;

  
    private final String PCOUNT_KEY = "parent_count";
    private final String CCOUNT_KEY = "child_count";
    private final String DCOUNT_KEY = "descendent_count";
    private final String CHARACT_KEY="character";
    private final String RG_KEY = "rg"; 
    private final String EFFECTIVETIME_KEY="effectiveTime";
    private final String ACTIVE_KEY="active";
    private final String MODULEID_KEY="moduleid";
    private final String MODIFIER_KEY="modifier";
    private final String RELTYPE_KEY="reltype";
//    private static IndexManager index = graphDB.index();
//    private static Index<Node> nodeIndex=null; 
    private static Node node=null;
//    private static String subconceptno = null;
//    private static String superconceptno=null;
//    private static String sctid;
//    private static Charset charset = Charset.forName("UTF-8");
//    private static java.nio.file.Path daisyfile = Paths.get("daisy.txt");
//    private static DataOutputStream writer;
    private InputOutput io = IOProvider.getDefault().getIO("Query Status", false);
    private OutputWriter ow = io.getOut();
    private Collection<Node> sctNodes = new ArrayList<Node>();
    Label conceptLabel = Label.label("Concept");
    Label descLabel = Label.label("Description");
    ArrayList<Node> descNodes = new ArrayList<>();
    private static final int DEFAULT_ATTRIBUTE_RANK = 10_000;

    private static final List<String> PHARMACEUTICAL_ATTRIBUTE_ORDER = Arrays.asList(
            "has manufactured dose form",
            "has manufacture dose form",
            "has unit of presentation",
            "count of active ingredient",
            "count of base active ingredient",
            "count of base and modification pair",
            "has ingredient",
            "has active ingredient",
            "has precise active ingredient",
            "has basis of strength substance",
            "has concentration strength numerator value",
            "has concentration stregth numerator unit",
            "has concentration strength numerator unit",
            "has concentration strength denominator value",
            "has concentration strength denominator unit",
            "has presentation strength numberator value",
            "has presentation strength numerator value",
            "hass presentation strength numerator unit",
            "has presentation strength numerator unit",
            "has presentation strength denominator value",
            "has presentation strength denominator unit",
            "plays role"
    );

    private static final List<String> CLINICAL_FINDING_ATTRIBUTE_ORDER = Arrays.asList(
            "episodicity",
            "clinical course",
            "serverity",
            "severity",
            "finding method",
            "interprets",
            "has interpretation",
            "finding informer",
            "associated with",
            "due to",
            "temporally related to",
            "before",
            "after",
            "during",
            "occurrence",
            "finding site",
            "associated morphology",
            "pathological process",
            "has realization",
            "causative agent"
    );

    private static final List<String> BODY_STRUCTURE_ATTRIBUTE_ORDER = Arrays.asList(
            "all or part of",
            "proper part of",
            "constitutional part of",
            "regional part of",
            "lateral half of",
            "systemic part of",
            "laterality"
    );

    private static final List<String> EVENT_ATTRIBUTE_ORDER = Arrays.asList(
            "associated with",
            "due to",
            "temporally related to",
            "before",
            "after",
            "during",
            "causative agent",
            "occurrence"
    );

    private static final List<String> OBSERVABLE_ENTITY_ATTRIBUTE_ORDER = Arrays.asList(
            "inheres in",
            "property",
            "scale type",
            "direct site",
            "inherent location",
            "characterizes",
            "process agent",
            "process duration",
            "process output",
            "towards",
            "relative to",
            "relative to part of",
            "precondition",
            "units",
            "technique",
            "procedure device",
            "has relization",
            "has realization",
            "using device",
            "component",
            "time aspect"
    );

    private static final List<String> SITUATION_ATTRIBUTE_ORDER = Arrays.asList(
            "associated finding",
            "finding context",
            "associated procedure",
            "procedure context",
            "subject relationship context",
            "temporal context"
    );

    private static final List<String> MEDICINAL_PRODUCT_PACKAGE_ATTRIBUTE_ORDER = Arrays.asList(
            "contains clinical drug",
            "has pack size",
            "has pack size unit",
            "has supplier",
            "has product name",
            "count of clinical drug type"
    );

    private static final List<String> PHARMACEUTICAL_DOSE_FORM_ATTRIBUTE_ORDER = Arrays.asList(
            "has basic dose form",
            "has dose form intended site",
            "has dose form release characteristic",
            "has dose form transformation",
            "has dose form administration method"
    );

    private static final List<String> PROCEDURE_ATTRIBUTE_ORDER = Arrays.asList(
            "priority",
            "revision status",
            "surgical approach",
            "access",
            "scale type",
            "method",
            "procedure site",
            "procedure site - direct",
            "procedure site - indirect",
            "procedure morphology",
            "direct morphology",
            "indirect morphology",
            "procedure device",
            "direct device",
            "indirect device",
            "using device",
            "using access device",
            "has intent",
            "has focus",
            "direct substance",
            "using energy",
            "using substance",
            "route of administration",
            "recipient category",
            "time aspect",
            "property",
            "component",
            "has specimen",
            "measurement method"
    );

    private static final List<String> SPECIMEN_ATTRIBUTE_ORDER = Arrays.asList(
            "specimen procedure",
            "specimen source identity",
            "specimen source morphlogy",
            "specimen source morphology",
            "specimen source topography",
            "specimen substance"
    );

    private static final List<String> SUBSTANCE_ATTRIBUTE_ORDER = Arrays.asList(
            "has disposition",
            "is modification of"
    );

    private static final List<String> PHYSICAL_OBJECT_ATTRIBUTE_ORDER = Arrays.asList(
            "has device intended site",
            "has compositional material",
            "has filling",
            "has surface characteristic",
            "has device characteristic"
    );

    private static final Map<String, Integer> PHARMACEUTICAL_ATTRIBUTE_RANK = buildAttributeRank(PHARMACEUTICAL_ATTRIBUTE_ORDER);
    private static final Map<String, Integer> CLINICAL_FINDING_ATTRIBUTE_RANK = buildAttributeRank(CLINICAL_FINDING_ATTRIBUTE_ORDER);
    private static final Map<String, Integer> BODY_STRUCTURE_ATTRIBUTE_RANK = buildAttributeRank(BODY_STRUCTURE_ATTRIBUTE_ORDER);
    private static final Map<String, Integer> EVENT_ATTRIBUTE_RANK = buildAttributeRank(EVENT_ATTRIBUTE_ORDER);
    private static final Map<String, Integer> OBSERVABLE_ENTITY_ATTRIBUTE_RANK = buildAttributeRank(OBSERVABLE_ENTITY_ATTRIBUTE_ORDER);
    private static final Map<String, Integer> SITUATION_ATTRIBUTE_RANK = buildAttributeRank(SITUATION_ATTRIBUTE_ORDER);
    private static final Map<String, Integer> MEDICINAL_PRODUCT_PACKAGE_ATTRIBUTE_RANK = buildAttributeRank(MEDICINAL_PRODUCT_PACKAGE_ATTRIBUTE_ORDER);
    private static final Map<String, Integer> PHARMACEUTICAL_DOSE_FORM_ATTRIBUTE_RANK = buildAttributeRank(PHARMACEUTICAL_DOSE_FORM_ATTRIBUTE_ORDER);
    private static final Map<String, Integer> PROCEDURE_ATTRIBUTE_RANK = buildAttributeRank(PROCEDURE_ATTRIBUTE_ORDER);
    private static final Map<String, Integer> SPECIMEN_ATTRIBUTE_RANK = buildAttributeRank(SPECIMEN_ATTRIBUTE_ORDER);
    private static final Map<String, Integer> SUBSTANCE_ATTRIBUTE_RANK = buildAttributeRank(SUBSTANCE_ATTRIBUTE_ORDER);
    private static final Map<String, Integer> PHYSICAL_OBJECT_ATTRIBUTE_RANK = buildAttributeRank(PHYSICAL_OBJECT_ATTRIBUTE_ORDER);
    private static final Map<String, Integer> GLOBAL_ATTRIBUTE_RANK = buildAttributeRank(mergeAttributeOrders(
            PHARMACEUTICAL_ATTRIBUTE_ORDER,
            CLINICAL_FINDING_ATTRIBUTE_ORDER,
            BODY_STRUCTURE_ATTRIBUTE_ORDER,
            EVENT_ATTRIBUTE_ORDER,
            OBSERVABLE_ENTITY_ATTRIBUTE_ORDER,
            SITUATION_ATTRIBUTE_ORDER,
            MEDICINAL_PRODUCT_PACKAGE_ATTRIBUTE_ORDER,
            PHARMACEUTICAL_DOSE_FORM_ATTRIBUTE_ORDER,
            PROCEDURE_ATTRIBUTE_ORDER,
            SPECIMEN_ATTRIBUTE_ORDER,
            SUBSTANCE_ATTRIBUTE_ORDER,
            PHYSICAL_OBJECT_ATTRIBUTE_ORDER
    ));

    @Override
    public String getSctID(Node node) {
        Transaction tx = getTx();
        Node txNode = reattach(node, tx);
        return txNode.getProperty("sctid").toString();
    }

    @Override
    public String getSctFSN(Node node) {
        Transaction tx = getTx();
        Node txNode = reattach(node, tx);
        fsn = txNode.getProperty("fsn").toString();
        return fsn; 
    }

    @Override
    public int getSctStatus(Node node) {
        Transaction tx = getTx();
        Node txNode = reattach(node, tx);
        return  (int)txNode.getProperty("active");
    }

    @Override
    public Node getNodebyID(long sctid) {
        Transaction tx = getTx();
        node = tx.findNode(conceptLabel, "sctid", sctid);
        return node;   
    }
        
    
    @Override
    public Node getNodebyTermNode(Node tnode){
        Transaction tx = getTx();
        return tx.findNode(conceptLabel, "sctid", tnode.getProperty("conceptid"));
//        return tnode.getSingleRelationship(RelTypes.Has_description, Direction.INCOMING).getStartNode();
        
    }

    @Override
    public Collection<String> getTerms(Node node) {
        Collection<String> terms = new ArrayList<>();
        Transaction tx = getTx();
        Node txNode = reattach(node, tx);
        Traverser tv = getHasDescTraverser(txNode, tx);
        for(Path subpath: tv){
            terms.add(subpath.endNode().getProperty("term").toString());
        }
        return terms;
    }

    
    
    @Override
    public Collection<String> getTermsActiveType(Node node) {
        Collection<String> activeTerms = new ArrayList<>();
        Transaction tx = getTx();
        Node txNode = reattach(node, tx);
        for(Relationship r:txNode.getRelationships(Direction.OUTGOING, RelTypes.Has_description) )
//        Traverser tv = getHasDescTraverser(node);
//        for(Path subpath: tv)
            {
//            int status=(int)r.getEndNode().getProperty("active");
            String type = r.getEndNode().getProperty("desc_type").toString();
//            if(status==1){
                switch(type){
                case "900000000000003001": activeTerms.add("FSN - " + r.getEndNode().getProperty("term").toString());
                    break;

                case "900000000000013009": activeTerms.add("SYN - " + r.getEndNode().getProperty("term").toString());
                    break;
                
                case "900000000000550004": activeTerms.add("Definition - "+ r.getEndNode().getProperty("term").toString());
                    break;
                    
                default: break;
                }
            
            }
        
        return activeTerms;
    }
    
        @Override
    public Collection<String> getTermsActiveOnly(Node node) {
           Collection<String> activeTerms = new ArrayList<>();
           Transaction tx = getTx();
           Node txNode = reattach(node, tx);
           for(Relationship r: txNode.getRelationships(Direction.OUTGOING, RelTypes.Has_description))
//            Traverser tv = getHasDescTraverser(node);
//            for(Path subpath: tv)
               {
//                int status=(int)r.getEndNode().getProperty("active");
//                if(status==1){
                activeTerms.add(r.getEndNode().getProperty("term").toString());                    
                }
        
        return activeTerms;
    }
    
//    @Override    
//    public Iterator<Node> getIndexHits(String searchst){        
//        IndexHits<Node> hits= graphDB.findNode(descLabel,"term", searchst);
//        return hits;
//        }
//       
    

    @Override
    public String getDescFSN(Node node) {
        Transaction tx = getTx();
        Node txNode = reattach(node, tx);
        return txNode.getProperty("fsn").toString();
    }

    @Override
    public Collection<String> getDescPT(Node node) {
        Collection<String> activeTerms = new ArrayList<>();
        ArrayList<Node> pt_nodes = new ArrayList<>();
//        String lang=null;
        long accept;
//        Traverser tv = getHasDescTraverser(node);
//        for(Path subpath: tv){
        Transaction tx = getTx();
        Node txNode = reattach(node, tx);
        for(Relationship r: txNode.getRelationships(Direction.OUTGOING, RelTypes.Has_description))
        {   Node dNode = r.getEndNode();
            long status=Long.parseLong(dNode.getProperty("active").toString());
            long type = Long.parseLong(dNode.getProperty("desc_type").toString());          
            for(Relationship i_rel : dNode.getRelationships(Direction.OUTGOING, RelTypes.Member_of_language_refset)){
                accept = (long)i_rel.getProperty("accetabilityid");
//                lang = i_rel.getEndNode().getProperty("refset").toString();
                if(status==1 && type==900000000000013009L && accept==900000000000548007L){
//                    pt_terms.add(r.getEndNode().getProperty("term").toString());}
                    pt_nodes.add(r.getEndNode());
                }
            }
        }
//            System.out.println("Status: "+ status + " Type: " + type+ " Language " +lang );
            if(pt_nodes.get(0).getProperty("term").equals(pt_nodes.get(1).getProperty("term"))){
                activeTerms.add("P[en] "+pt_nodes.get(0).getProperty("term").toString());                    
                }            
            else{
                for(Node n : pt_nodes){
                    if(n.hasProperty("US")){
                        activeTerms.add("P[en-US] " +n.getProperty("term"));}
                    if(n.hasProperty("GB")){
                        activeTerms.add("P[en-GB] " + n.getProperty("term"));
                }
                }
                }
        return activeTerms;
    }
    
    @Override
    public Collection<String> getTextDefinition(Node node){
        Collection<String> activeTerms = new ArrayList<>();
        ArrayList<Node> pt_nodes = new ArrayList<>();
//        String lang=null;
        long accept;
//        Traverser tv = getHasDescTraverser(node);
//        for(Path subpath: tv){
        Transaction tx = getTx();
        Node txNode = reattach(node, tx);
        for(Relationship r: txNode.getRelationships(Direction.OUTGOING, RelTypes.Has_description))
        {   Node dNode = r.getEndNode();
//            long status=Long.parseLong(dNode.getProperty("active").toString());
            long type = Long.parseLong(dNode.getProperty("desc_type").toString());          
            for(Relationship i_rel : dNode.getRelationships(Direction.OUTGOING, RelTypes.Member_of_language_refset)){
                accept = (long)i_rel.getProperty("accetabilityid");
//                lang = i_rel.getEndNode().getProperty("refset").toString();
                if(type==900000000000550004L && accept==900000000000548007L){
//                    pt_terms.add(r.getEndNode().getProperty("term").toString());}
                    pt_nodes.add(dNode);
                }
            }
        }
//            System.out.println("Status: "+ status + " Type: " + type+ " Language " +lang );
            if(!pt_nodes.isEmpty()){
            if(pt_nodes.get(0).getProperty("term").equals(pt_nodes.get(1).getProperty("term"))){
                activeTerms.add("P[en] "+pt_nodes.get(0).getProperty("term").toString());                    
                }            
            else{
                for(Node n : pt_nodes){
                    if(n.hasProperty("US")){
                        activeTerms.add("P[en-US] " +n.getProperty("term"));}
                    if(n.hasProperty("GB")){
                        activeTerms.add("P[en-GB] " + n.getProperty("term"));
                }
                }
                }
            }
        return activeTerms;
    }
    
    @Override
    public Collection<String> getSimpleMapRefset(Node node) {
        Collection<String> reset = new HashSet<>();
        Transaction tx = getTx();
        Node txNode = reattach(node, tx);
        Iterable<Relationship> re_i = txNode.getRelationships(Direction.OUTGOING, RelTypes.Has_simple_map);
        
        for(Relationship r: re_i){
//            if((int)r.getProperty("active")==1){
                String refset = r.getEndNode().getProperty("refset").toString();
                String maptarget = r.getEndNode().getProperty("maptarget").toString();
                reset.add(refset + " map = " + maptarget);
//        }
    }   
        return reset;
    }
        

    @Override
    public Collection<String> getDescSyn(Node node) {
        Set<String> activeTerms = new HashSet<>();
        ArrayList<Node> syn_nodes = new ArrayList<>();
        Transaction tx = getTx();
        Node txNode = reattach(node, tx);
        for(Relationship r: txNode.getRelationships(Direction.OUTGOING, RelTypes.Has_description))
        {   Node dNode = r.getEndNode();
//            long status=Long.parseLong(dNode.getProperty("active").toString());
            long type = Long.parseLong(dNode.getProperty("desc_type").toString());          
//            String lang = r.getEndNode().getProperty("language").toString();
            for(Relationship i_rel : dNode.getRelationships(Direction.OUTGOING, RelTypes.Member_of_language_refset)){
                long accept = (long)i_rel.getProperty("accetabilityid");
                if(type==900000000000013009L && accept==900000000000549004L && dNode.hasProperty("US") && dNode.hasProperty("GB")){
                    activeTerms.add("S[en] "+dNode.getProperty("term").toString());                    
                    }
                else if(type==900000000000013009L && accept==900000000000549004L && !dNode.hasProperty("GB") ) {
                        activeTerms.add("S[en-US] " +dNode.getProperty("term"));}
                else if(type==900000000000013009L && accept==900000000000549004L && !dNode.hasProperty("US")){
                        activeTerms.add("S[en-GB] " + dNode.getProperty("term"));
                }
              }
            }
        return activeTerms;
    }
    
//    @Override
//    public Collection<String> getDesUnspec(Node node){  // change this one to definition type
//                Collection<String> activeTerms = new ArrayList<String>();
//        for(Relationship r: node.getRelationships(Direction.OUTGOING, RelTypes.Has_description))
//        {
//            long status=Long.parseLong(r.getEndNode().getProperty("active").toString());
//            long type = Long.parseLong(r.getEndNode().getProperty("desc_type").toString());          
//            String lang = r.getEndNode().getProperty("language").toString();
//            if((status==0||status==11)&&type ==0){
//                activeTerms.add("U ["+lang+"]  "+r.getEndNode().getProperty("term").toString());                    
//                }            
//            }
//        return activeTerms;
//    }

    @Override
    public long getDescType(Node node) {
        Transaction tx = getTx();
        Node txNode = reattach(node, tx);
        return Long.parseLong(txNode.getProperty("desc_type").toString());
        
    }
    
    
    @Override
    public long getIsPrimitive(Node node){
        Transaction tx = getTx();
        Node txNode = reattach(node, tx);
        return (long)(txNode.getProperty("definition_status"));
    }

    @Override
    public int getDescStatus(Node node) {
         Transaction tx = getTx();
         Node txNode = reattach(node, tx);
         return (int)txNode.getProperty("active"); 
    }

    @Override
    public String getLang(Node node) {
        Transaction tx = getTx();
        Node txNode = reattach(node, tx);
        return txNode.getProperty("langauge").toString();  
    }
    
//    @Override
//    public Collection<String> getFindingSite(Node node){
//        Collection<String> FSs = new ArrayList<String>();
//        Traverser tv = getFindingSiteTraverser(node);
//        for(Path subpath: tv){
//            FSs.add(subpath.endNode().getProperty("fsn").toString());            
//        }
//        return FSs;
//    }

//    @Override
//    public Collection<String> getRGFindingSite(Node node){
//        Collection<String> RGFSs = new ArrayList<String>();
////        Traverser tv = getFindingSiteTraverser(node);
////        for(Path subpath: tv){
////            String rg = subpath.lastRelationship().getProperty("RELATIONSHIPGROUP").toString();
////            String fsn =subpath.endNode().getProperty(FSN_KEY).toString();
////            RGFSs.add("RG-"+rg+" FINDING SITE = " +fsn);            
////        }
//        return RGFSs;
//    }
    
    @Override
    public Collection<String> getModels(Node node){
        Collection<String> models= new ArrayList<String>();
           Transaction tx = getTx();
           Node txNode = reattach(node, tx);
           String conceptFsn = txNode.hasProperty("fsn") ? txNode.getProperty("fsn").toString() : "";
           ModelOrderProfile profile = detectModelOrderProfile(conceptFsn);
           List<ModelEntry> entries = new ArrayList<>();
           for(Relationship r: txNode.getRelationships(Direction.OUTGOING)){
               RelationshipType reltype= r.getType();                    
               if(r.hasProperty("rg") && (int)r.getProperty("stated")==0 && !reltype.name().equals("Is a")){                   
                    int rg = Integer.parseInt(r.getProperty("rg").toString());                  
                    Node endNode = r.getEndNode();
                    String nodeinfo;
                    if (endNode.hasProperty("fsn")) {
                        nodeinfo = endNode.getProperty("fsn").toString();
                    } else if (endNode.hasProperty("value")) {
                        nodeinfo = endNode.getProperty("value").toString();
                        if (nodeinfo.startsWith("#")) {
                            nodeinfo = nodeinfo.substring(1);
                        }
                        if (nodeinfo.length() >= 2 && nodeinfo.startsWith("\"") && nodeinfo.endsWith("\"")) {
                            nodeinfo = nodeinfo.substring(1, nodeinfo.length() - 1);
                        }
                    } else {
                        nodeinfo = String.valueOf(endNode.getId());
                    }
                    entries.add(new ModelEntry(rg, reltype.name(), nodeinfo));
                }
        }

        Collections.sort(entries, Comparator
                .comparingInt((ModelEntry e) -> e.rg == 0 ? 0 : 1)
                .thenComparingInt(e -> getAttributeRank(e.relName, profile))
                .thenComparingInt(e -> e.rg == 0 ? 0 : e.rg)
                .thenComparing(e -> e.relName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(e -> e.nodeInfo, String.CASE_INSENSITIVE_ORDER));

        for (ModelEntry entry : entries) {
            models.add("RG [" + entry.rg + "]  " + entry.relName + " = " + entry.nodeInfo);
        }

        return models;
    }

    private static Map<String, Integer> buildAttributeRank(List<String> attributes) {
        Map<String, Integer> rank = new HashMap<>();
        int position = 0;
        for (String attribute : attributes) {
            String key = attribute.toLowerCase(Locale.ROOT).trim();
            if (!rank.containsKey(key)) {
                rank.put(key, position++);
            }
        }
        return rank;
    }

    private static List<String> mergeAttributeOrders(List<String>... lists) {
        List<String> merged = new ArrayList<>();
        for (List<String> list : lists) {
            merged.addAll(list);
        }
        return merged;
    }

    private ModelOrderProfile detectModelOrderProfile(String conceptFsn) {
        String normalized = conceptFsn == null ? "" : conceptFsn.toLowerCase(Locale.ROOT);
        if (normalized.contains("pharmaceutical")
                || normalized.contains("biologic")
                || normalized.contains("medicinal")
                || normalized.contains("clinical drug")
                || normalized.contains("product")) {
            return ModelOrderProfile.PHARMACEUTICAL;
        }
        if (normalized.contains("body structure")) {
            return ModelOrderProfile.BODY_STRUCTURE;
        }
        if (normalized.contains("event")) {
            return ModelOrderProfile.EVENT;
        }
        if (normalized.contains("observable entity")) {
            return ModelOrderProfile.OBSERVABLE_ENTITY;
        }
        if (normalized.contains("situation")) {
            return ModelOrderProfile.SITUATION;
        }
        if (normalized.contains("medicinal product package")) {
            return ModelOrderProfile.MEDICINAL_PRODUCT_PACKAGE;
        }
        if (normalized.contains("pharmaceutical dose form")) {
            return ModelOrderProfile.PHARMACEUTICAL_DOSE_FORM;
        }
        if (normalized.contains("procedure")) {
            return ModelOrderProfile.PROCEDURE;
        }
        if (normalized.contains("specimen")) {
            return ModelOrderProfile.SPECIMEN;
        }
        if (normalized.contains("substance")) {
            return ModelOrderProfile.SUBSTANCE;
        }
        if (normalized.contains("physical object")) {
            return ModelOrderProfile.PHYSICAL_OBJECT;
        }
        if (normalized.contains("clinical finding") || normalized.endsWith("(finding)")) {
            return ModelOrderProfile.CLINICAL_FINDING;
        }
        return ModelOrderProfile.DEFAULT;
    }

    private int getAttributeRank(String relationshipName, ModelOrderProfile profile) {
        String key = relationshipName == null ? "" : relationshipName.toLowerCase(Locale.ROOT).trim();
        switch (profile) {
            case PHARMACEUTICAL:
                return PHARMACEUTICAL_ATTRIBUTE_RANK.getOrDefault(key, GLOBAL_ATTRIBUTE_RANK.getOrDefault(key, DEFAULT_ATTRIBUTE_RANK));
            case CLINICAL_FINDING:
                return CLINICAL_FINDING_ATTRIBUTE_RANK.getOrDefault(key, GLOBAL_ATTRIBUTE_RANK.getOrDefault(key, DEFAULT_ATTRIBUTE_RANK));
            case BODY_STRUCTURE:
                return BODY_STRUCTURE_ATTRIBUTE_RANK.getOrDefault(key, GLOBAL_ATTRIBUTE_RANK.getOrDefault(key, DEFAULT_ATTRIBUTE_RANK));
            case EVENT:
                return EVENT_ATTRIBUTE_RANK.getOrDefault(key, GLOBAL_ATTRIBUTE_RANK.getOrDefault(key, DEFAULT_ATTRIBUTE_RANK));
            case OBSERVABLE_ENTITY:
                return OBSERVABLE_ENTITY_ATTRIBUTE_RANK.getOrDefault(key, GLOBAL_ATTRIBUTE_RANK.getOrDefault(key, DEFAULT_ATTRIBUTE_RANK));
            case SITUATION:
                return SITUATION_ATTRIBUTE_RANK.getOrDefault(key, GLOBAL_ATTRIBUTE_RANK.getOrDefault(key, DEFAULT_ATTRIBUTE_RANK));
            case MEDICINAL_PRODUCT_PACKAGE:
                return MEDICINAL_PRODUCT_PACKAGE_ATTRIBUTE_RANK.getOrDefault(key, GLOBAL_ATTRIBUTE_RANK.getOrDefault(key, DEFAULT_ATTRIBUTE_RANK));
            case PHARMACEUTICAL_DOSE_FORM:
                return PHARMACEUTICAL_DOSE_FORM_ATTRIBUTE_RANK.getOrDefault(key, GLOBAL_ATTRIBUTE_RANK.getOrDefault(key, DEFAULT_ATTRIBUTE_RANK));
            case PROCEDURE:
                return PROCEDURE_ATTRIBUTE_RANK.getOrDefault(key, GLOBAL_ATTRIBUTE_RANK.getOrDefault(key, DEFAULT_ATTRIBUTE_RANK));
            case SPECIMEN:
                return SPECIMEN_ATTRIBUTE_RANK.getOrDefault(key, GLOBAL_ATTRIBUTE_RANK.getOrDefault(key, DEFAULT_ATTRIBUTE_RANK));
            case SUBSTANCE:
                return SUBSTANCE_ATTRIBUTE_RANK.getOrDefault(key, GLOBAL_ATTRIBUTE_RANK.getOrDefault(key, DEFAULT_ATTRIBUTE_RANK));
            case PHYSICAL_OBJECT:
                return PHYSICAL_OBJECT_ATTRIBUTE_RANK.getOrDefault(key, GLOBAL_ATTRIBUTE_RANK.getOrDefault(key, DEFAULT_ATTRIBUTE_RANK));
            case DEFAULT:
            default:
                return GLOBAL_ATTRIBUTE_RANK.getOrDefault(key, DEFAULT_ATTRIBUTE_RANK);
        }
    }

    private enum ModelOrderProfile {
        PHARMACEUTICAL,
        CLINICAL_FINDING,
        BODY_STRUCTURE,
        EVENT,
        OBSERVABLE_ENTITY,
        SITUATION,
        MEDICINAL_PRODUCT_PACKAGE,
        PHARMACEUTICAL_DOSE_FORM,
        PROCEDURE,
        SPECIMEN,
        SUBSTANCE,
        PHYSICAL_OBJECT,
        DEFAULT
    }

    private static final class ModelEntry {
        private final int rg;
        private final String relName;
        private final String nodeInfo;

        private ModelEntry(int rg, String relName, String nodeInfo) {
            this.rg = rg;
            this.relName = relName;
            this.nodeInfo = nodeInfo;
        }
    }
    


/*
 * relationshiptypes by current modelling attabutes in SNOMED CT :
 * 
 */    
    
    private static enum RelTypes implements RelationshipType
    {
        Has_description,
        Member_of_language_refset,
        Has_simple_map,
        iIS_A,
        
    //the following should be list attributes under: 
    //410663007|Concept history attribute (attribute)
        SAME_AS, // 168666000
        MOVED_FROM,  // 384598002
        MOVED_TO, // 370125004
        MAY_BE_A, //149016008
        REPLACED_BY, //370124000
        WAS_A,  // 159083000
        IS_A_mapping, // 178066000
    
////410662002|Concept model attribute (attribute)     
//        IS_A, // 116680003
//        Finding_site, // 363698007
//        Part_of, // 123005000
//        Causative_agent, // 246075003
//        Associated_morphology, // 116676008
//        Method, // 260686004
//        Procedure_site, // 363704007
//        Direct_morphology, // 363700003
//        Occurrence, // 246454002
//        Interprets, // 363714003
//        Has_intent, // 363703001
//        Course, // 260908002
//        Access, // 260507000
//        Revision_status, // 246513007
//        Direct_substance, // 363701004
//        Direct_device, // 363699004
//        Associated_finding, // 246090004
//        Episodicity, // 246456000
//        Laterality, // 272741003
//        Severity, // 246112005
//        Has_definitional_manifestation, // 363705008
//        Has_focus, // 363702006
//        Priority, // 260870009
//        Has_active_ingredient, //127489000
//        Associated_procedure, // 363589002
//        Indirect_morphology, // 363709002
//        Component, // 246093002
//        Has_specimen, // 116686009
//        Has_interpretation, // 363713009
//        Subject_of_information, // 131195008
//        Indirect_device, // 363710007
//        Recipient_category, // 370131001
//        Pathological_process, // 370135005
//        Specimen_source_topography, // 118169006
//        Specimen_procedure, // 118171006
//        Specimen_substance, // 370133003
//        Specimen_source_identity, //118170007
//        Specimen_source_morphology, //118168003
//        Scale_type, // 370132008
//        Property, // 370130000
//        Time_aspect, // 370134009
//        Measurement_method, // 370129005
//        Procedure_site_Indirect, // 405814001
//        After, // 255234002
//        Associated_with, // 47429007
//        Temporal_context, // 408731000
//        Finding_context, // 408729009
//        Subject_relationship_context, // 408732007
//        Due_to, // 42752001
//        Procedure_context, // 408730004
//        Procedure_device, // 405815000
//        Procedure_site_Direct, // 405813007
//        Procedure_morphology, // 405816004
//        Has_dose_form, // 411116001
//        Finding_method, // 418775008
//        Finding_informer, // 419066007
//        Route_of_administration, // 410675002
//        Using_device, // 424226004
//        Using_substance, // 424361007
//        Clinical_course, // 263502005
//        Using_energy, // 424244007
//        Using_access_device, // 425391005
//        Surgical_approach, // 424876005
//        
//        Is_about, //704647008
//        Observes, //704347000
//        Specified_by, //704346009
//        Inheres_in, //704319004
//        Process_agent, //704322002
//        Process_duration, //704323007
//        Towards, // 704320005
//        Characterizes, //704321009
//        Precondition, //704326004
//        Technique, //246501002
//        Direct_site, //704327008
//        Process_output, //704324001
//        Relative_to, //704325000
//        Units //246514001     


    }
    
     @Override
    public void startGraphDB() {
        if(graphDB != null){
            dbManager.shutdown();
        }
        graphDB = dbManager.start(Neo4jConfig.load());
        closeSharedTx();
//        nodeIndex = graphDB.index().forNodes("nodes");
        registerShutdownHook(dbManager);
    }
     
     @Override
    public void shutdownGraphDB(){
        if(graphDB != null){
            closeSharedTx();
            dbManager.shutdown();
        }
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
        
//    @Override
//    public Index<Node> getIndex(){
////        return nodeIndex;
//    }
        
    @Override
    public GraphDatabaseService getGraphDB(){
        if (graphDB == null) {
            graphDB = dbManager.start();
        }
        return graphDB;
    }

    private static Transaction getTx() {
        if (graphDB == null) {
            graphDB = dbManager.start(Neo4jConfig.load());
        }
        Transaction tx = THREAD_TX.get();
        if (tx == null) {
            tx = graphDB.beginTx();
            THREAD_TX.set(tx);
        }
        return tx;
    }

    private static void closeSharedTx() {
        Transaction tx = THREAD_TX.get();
        if (tx != null) {
            try {
                tx.close();
            } catch (Exception e) {
                // ignore close failures
            } finally {
                THREAD_TX.remove();
            }
        }
    }
        
    @Override
    public String getFSNbyID(long sctid) {
         String fsn = null;
         Node findConcept = getNodebyID(sctid);
         if(findConcept != null){
            fsn = findConcept.getProperty("fsn").toString();
            io.getOut().println("Concept for Query: " + sctid + " |" + fsn +"|");
            io.getOut().close();
            return fsn;
         }
         else {
            String error = "Node does not exist." ;
//            System.out.println(error);
            io.getOut().println(error);
            return fsn;
         }
    }
    
    
// Traverser list:
    
    private static Traverser getHasDescTraverser(final Node node){
        Transaction tx = getTx();
        TraversalDescription td = tx.traversalDescription()
                .breadthFirst()
                .relationships(RelTypes.Has_description, Direction.OUTGOING)
                .evaluator(Evaluators.includingDepths(1, 1));
        return td.traverse(node);
    }

    private static Traverser getHasDescTraverser(final Node node, Transaction tx){
        TraversalDescription td = tx.traversalDescription()
                .breadthFirst()
                .relationships(RelTypes.Has_description, Direction.OUTGOING)
                .evaluator(Evaluators.includingDepths(1, 1));
        return td.traverse(node);
    }
    

    
    
    private static Traverser getDirectSuperTraverser( final Node node )
        {
           Transaction tx = getTx();
           TraversalDescription td = tx.traversalDescription()
                  .breadthFirst()
                  .relationships(RelationshipType.withName("Is a") , Direction.OUTGOING)
                  .evaluator(Evaluators.includingDepths(1, 1));
           return td.traverse(node);

        }
    
    private static Traverser getSuperNodesTraverser( final Node node )
    {
       Transaction tx = getTx();
       TraversalDescription td = tx.traversalDescription()
              .breadthFirst()
              .relationships(RelationshipType.withName("Is a") , Direction.OUTGOING);
       return td.traverse(node);

    }

    private static Traverser getSuperNodesTraverser(final Node node, Transaction tx)
    {
       TraversalDescription td = tx.traversalDescription()
              .breadthFirst()
              .relationships(RelationshipType.withName("Is a") , Direction.OUTGOING);
       return td.traverse(node);
    }
    
    private static Traverser getPrimitiveSuperNodesTraverser(final Node node){
        Transaction tx = getTx();
        TraversalDescription td = tx.traversalDescription()
                .breadthFirst()
                .relationships(RelationshipType.withName("Is a"), Direction.OUTGOING)
                .evaluator(Evaluators.excludeStartPosition())
                .evaluator(new Evaluator(){
                    @Override
                    public Evaluation evaluate(Path path) {
                        if(path.endNode().getProperty("definition_status").toString().equals("900000000000074008")){
                            return INCLUDE_AND_PRUNE;
                        }
                        return EXCLUDE_AND_CONTINUE;
                    }          
                });
        return td.traverse(node);
    }

    private static Traverser getPrimitiveSuperNodesTraverser(final Node node, Transaction tx){
        TraversalDescription td = tx.traversalDescription()
                .breadthFirst()
                .relationships(RelationshipType.withName("Is a"), Direction.OUTGOING)
                .evaluator(Evaluators.excludeStartPosition())
                .evaluator(new Evaluator(){
                    @Override
                    public Evaluation evaluate(Path path) {
                        if(path.endNode().getProperty("definition_status").toString().equals("900000000000074008")){
                            return INCLUDE_AND_PRUNE;
                        }
                        return EXCLUDE_AND_CONTINUE;
                    }          
                });
        return td.traverse(node);
    }
    
    private static Traverser getDirectSubTraverser(final Node node){
        Transaction tx = getTx();
        TraversalDescription td = tx.traversalDescription()
                .breadthFirst()
                .relationships(RelationshipType.withName("Is a"), Direction.INCOMING)
                .evaluator(Evaluators.includingDepths(1, 1));
        return td.traverse(node);        
    }

    private static Traverser getDirectSubTraverser(final Node node, Transaction tx){
        TraversalDescription td = tx.traversalDescription()
                .breadthFirst()
                .relationships(RelationshipType.withName("Is a"), Direction.INCOMING)
                .evaluator(Evaluators.includingDepths(1, 1));
        return td.traverse(node);        
    }
    
       
    @Override
    public Traverser getISATraverser(final Node node){
         Transaction tx = getTx();
         TraversalDescription td = tx.traversalDescription()
                 .breadthFirst()
                 .relationships(RelationshipType.withName("Is a"), Direction.INCOMING)
                 .evaluator(Evaluators.all());
         return td.traverse(node);
     }

    @Override
    public String getRelationshipSummary(Node node) {
        if (node == null) {
            return "Relationships: none (no node)";
        }
        Transaction tx = getTx();
        Node txNode = reattach(node, tx);
        int isaCount = 0;
        int descCount = 0;
        int totalOut = 0;
        for (Relationship r : txNode.getRelationships(Direction.OUTGOING)) {
            totalOut++;
            String typeName = r.getType().name();
            if ("Is a".equals(typeName)) {
                isaCount++;
            } else if (RelTypes.Has_description.name().equals(typeName)) {
                descCount++;
            }
        }
        int nonIsa = Math.max(0, totalOut - isaCount - descCount);
        return "Relationships: ISA=" + isaCount + " non-ISA=" + nonIsa + " descriptions=" + descCount;
    }

    private static Traverser getISATraverser(final Node node, Transaction tx){
         TraversalDescription td = tx.traversalDescription()
                 .breadthFirst()
                 .relationships(RelationshipType.withName("Is a"), Direction.INCOMING)
                 .evaluator(Evaluators.all());
         return td.traverse(node);
     }
    
    private static Traverser getISAExcludeStartTraverser(final Node node){
         Transaction tx = getTx();
         TraversalDescription td = tx.traversalDescription()
                 .breadthFirst()
                 .relationships(RelationshipType.withName("Is a"), Direction.INCOMING)
                 .evaluator(Evaluators.excludeStartPosition());
         return td.traverse(node);
     } 

    private static Traverser getISAExcludeStartTraverser(final Node node, Transaction tx){
         TraversalDescription td = tx.traversalDescription()
                 .breadthFirst()
                 .relationships(RelationshipType.withName("Is a"), Direction.INCOMING)
                 .evaluator(Evaluators.excludeStartPosition());
         return td.traverse(node);
     } 
    
    
//    @Override
//    public Traverser getISAiTraverser(final Node node){
//        TraversalDescription td = graphDB.traversalDescription()
//                .breadthFirst()
//                .relationships(RelationshipType.withName("Is a"), Direction.INCOMING)
//                .relationships(RelTypes.iIS_A, Direction.INCOMING)
//                .evaluator(Evaluators.all());
//        return td.traverse(node);
//    }
//    
//    private static Traverser getAllHistoryTraverserI(final Node node){
//        TraversalDescription td = graphDB.traversalDescription()
//                .breadthFirst()
//                .relationships(RelTypes.SAME_AS, Direction.INCOMING)
//                .relationships(RelTypes.MOVED_FROM, Direction.INCOMING)                
//                .relationships(RelTypes.MAY_BE_A, Direction.INCOMING)
//                .relationships(RelTypes.REPLACED_BY, Direction.INCOMING)
//                .relationships(RelTypes.WAS_A, Direction.INCOMING);
//        return td.traverse(node);
//
//    }
//
//    private static Traverser getAllHistoryTraverserO(final Node node){
//        TraversalDescription td = graphDB.traversalDescription()
//                .breadthFirst()
//                .relationships(RelTypes.SAME_AS, Direction.OUTGOING)
//                .relationships(RelTypes.MOVED_FROM, Direction.OUTGOING)                
//                .relationships(RelTypes.MAY_BE_A, Direction.OUTGOING)
//                .relationships(RelTypes.REPLACED_BY, Direction.OUTGOING)
//                .relationships(RelTypes.WAS_A, Direction.OUTGOING);
//        return td.traverse(node);
//
//    }
//    
//    
//    private static Traverser getWasATraverserI(final Node node){
//        TraversalDescription td = graphDB.traversalDescription()
//                .breadthFirst()
//                .relationships(RelTypes.WAS_A, Direction.INCOMING)
//                .evaluator(Evaluators.all());
//        return td.traverse(node);
//    }
//   
//    private static Traverser getWasATraverserO(final Node node){
//        TraversalDescription td = graphDB.traversalDescription()
//                .breadthFirst()
//                .relationships(RelTypes.WAS_A, Direction.OUTGOING)
//                .evaluator(Evaluators.all());
//        return td.traverse(node);
//    }    
//    
//    
//    private static Traverser getReplacedByTraverser(final Node node){
//        TraversalDescription td = graphDB.traversalDescription()
//                .breadthFirst()
//                .relationships(RelTypes.REPLACED_BY, Direction.INCOMING)
//                .evaluator(Evaluators.all());
//        return td.traverse(node);
//    }
//    
//    private static Traverser getReplacedByOutTraverser(final Node node){
//        TraversalDescription td = graphDB.traversalDescription()
//                .breadthFirst()
//                .relationships(RelTypes.REPLACED_BY, Direction.OUTGOING)
//                .evaluator(Evaluators.all());
//        return td.traverse(node);
//    }
//    
//    private static Traverser getMayBeReplacedTraverser(final Node node){
//        TraversalDescription td = graphDB.traversalDescription()
//                .breadthFirst()
//                .relationships(RelTypes.MAY_BE_A, Direction.INCOMING)
//                .relationships(RelTypes.REPLACED_BY, Direction.INCOMING)
//                .evaluator(Evaluators.all());
//        return td.traverse(node);
//    }
//    
//    private static Traverser getMayBeReplacedOutTraverser(final Node node){
//        TraversalDescription td = graphDB.traversalDescription()
//                .breadthFirst()
//                .relationships(RelTypes.MAY_BE_A, Direction.OUTGOING)
//                .relationships(RelTypes.REPLACED_BY, Direction.OUTGOING)
//                .evaluator(Evaluators.all());
//        return td.traverse(node);
//    }
//    
//    private static Traverser getMayBeTraverser(final Node node){
//        TraversalDescription td = graphDB.traversalDescription()
//                .breadthFirst()
//                .relationships(RelTypes.MAY_BE_A, Direction.INCOMING)
//                .evaluator(Evaluators.all());
//        return td.traverse(node);
//    }
//    
//    private static Traverser getMayBeOutTraverser(final Node node){
//        TraversalDescription td = graphDB.traversalDescription()
//                .breadthFirst()
//                .relationships(RelTypes.MAY_BE_A, Direction.OUTGOING)
//                .evaluator(Evaluators.all());
//        return td.traverse(node);
//    }
//    
//    private static Traverser getSameAsTraverser(final Node node){
//        TraversalDescription td = graphDB.traversalDescription()
//                .breadthFirst()
//                .relationships(RelTypes.SAME_AS, Direction.INCOMING)
//                .relationships(RelTypes.MOVED_FROM, Direction.INCOMING)
//                .evaluator(Evaluators.all());
//        return td.traverse(node);
//    }
//    
//    private static Traverser getSameAsOutTraverser(final Node node){
//        TraversalDescription td = graphDB.traversalDescription()
//                .breadthFirst()
//                .relationships(RelTypes.SAME_AS, Direction.OUTGOING)
//                .relationships(RelTypes.MOVED_FROM, Direction.OUTGOING)
//                .evaluator(Evaluators.all());
//        return td.traverse(node);
//    }
//     
//    private static Traverser getSameAsNonselfTraverser(final Node node){
//        TraversalDescription td = graphDB.traversalDescription()
//                .breadthFirst()
//                .relationships(RelTypes.SAME_AS, Direction.INCOMING)
//                .relationships(RelTypes.MOVED_FROM, Direction.INCOMING)
//                .evaluator(Evaluators.excludeStartPosition());
//        return td.traverse(node);
//    }
//    
//    private static Traverser getSameAsBothDirectTraverser(final Node node){
//        TraversalDescription td = graphDB.traversalDescription()
//                .breadthFirst()
//                .relationships(RelTypes.SAME_AS, Direction.BOTH)
//                .relationships(RelTypes.MOVED_FROM, Direction.BOTH)
//                .evaluator(Evaluators.excludeStartPosition());
//        return td.traverse(node);
//    }
//    
//    private static Traverser getSameAsSubTraverser(final Node node){
//         TraversalDescription td = graphDB.traversalDescription()
//                 .breadthFirst()
//                 .relationships(RelationshipType.withName("Is a"), Direction.INCOMING)
//                 .relationships(RelTypes.SAME_AS, Direction.INCOMING)
//                 .relationships(RelTypes.MOVED_FROM, Direction.INCOMING)                 
//                 .evaluator(Evaluators.all());
//         return td.traverse(node);
//     }
//    
//    private static Traverser getSameAsSubiTraverser(final Node node){
//        TraversalDescription td = graphDB.traversalDescription()
//                .breadthFirst()
//                .relationships(RelationshipType.withName("Is a"), Direction.INCOMING)
//                .relationships(RelTypes.iIS_A, Direction.INCOMING)
//                .relationships(RelTypes.SAME_AS, Direction.BOTH)
//                .relationships(RelTypes.MOVED_FROM, Direction.BOTH)
//                .evaluator(Evaluators.all());
//        return td.traverse(node);
//    }
//    
//    private static Traverser getSameAsSubnoStartiTraverser(final Node node){
//        TraversalDescription td = graphDB.traversalDescription()
//                .breadthFirst()
//                .relationships(RelationshipType.withName("Is a"), Direction.INCOMING)
//                .relationships(RelTypes.iIS_A, Direction.INCOMING)
//                .relationships(RelTypes.SAME_AS, Direction.INCOMING)
//                .relationships(RelTypes.MOVED_FROM, Direction.INCOMING)
//                .evaluator(Evaluators.excludeStartPosition());
//        return td.traverse(node);
//    }
//    
//    private static Traverser getMayBeDaisyTraverser(final Node node){
//        TraversalDescription td = graphDB.traversalDescription()
//                .breadthFirst()
//                .relationships(RelationshipType.withName("Is a"), Direction.INCOMING)
//                .relationships(RelTypes.iIS_A, Direction.INCOMING)
//                .relationships(RelTypes.SAME_AS, Direction.BOTH)
//                .relationships(RelTypes.MOVED_FROM, Direction.BOTH)
//                .relationships(RelTypes.MAY_BE_A, Direction.INCOMING)
//                .relationships(RelTypes.WAS_A, Direction.INCOMING)
//                .relationships(RelTypes.REPLACED_BY, Direction.INCOMING)
//                .evaluator(Evaluators.all());               
//             return td.traverse(node);
//    }
//    
//    private static Traverser getMayBeNoStartTraverser(final Node node){
//        TraversalDescription td = graphDB.traversalDescription()
//                .breadthFirst()
//                .relationships(RelationshipType.withName("Is a"), Direction.INCOMING)
//                .relationships(RelTypes.iIS_A, Direction.INCOMING)
//                .relationships(RelTypes.SAME_AS, Direction.INCOMING)
//                .relationships(RelTypes.MOVED_FROM, Direction.INCOMING)
//                .relationships(RelTypes.MAY_BE_A, Direction.INCOMING)
//                .relationships(RelTypes.WAS_A, Direction.INCOMING)
//                .relationships(RelTypes.REPLACED_BY, Direction.OUTGOING)
//                .evaluator(Evaluators.excludeStartPosition());               
//             return td.traverse(node);
//    }
//    
//    private static Traverser getMayBeNonselfTraverser(final Node node){
//        TraversalDescription td = graphDB.traversalDescription()
//                .breadthFirst()
//                .relationships(RelTypes.MAY_BE_A, Direction.INCOMING)
//                .relationships(RelTypes.WAS_A, Direction.INCOMING)
//                .relationships(RelTypes.REPLACED_BY, Direction.OUTGOING)
//                .evaluator(Evaluators.excludeStartPosition());
//        return td.traverse(node);
//    }
    
    

    

    @Override
    public void findDirectSuperNodes(long sctid) {
        int n = 0;
        Node startNode = getNodebyID(sctid);
        Traverser myTraverser = getDirectSuperTraverser(startNode);
        String fn = System.getProperty("user.home") + "/Parent_" + startNode.getProperty("sctid")+".txt";
        File parenttable = new File(fn);
        try{
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(parenttable), "UTF-8"));
            for (Path path : myTraverser){                  
                String id = path.endNode().getProperty("sctid").toString();
                String fsn = path.endNode().getProperty("fsn").toString();
                writer.write(id+"|"+fsn+"\n");
                  n = n+1;
              }
            writer.close();        
        }catch(IOException e){
            System.out.println("Failed to write parent concept table in method findDirectSupernodes(sctid)");
        }
        try{
            io.select();
            io.getOut().println(" =============================== ");
            io.getOut().println("Created file for parent concepts at: ");
            io.getOut().println(parenttable.getAbsolutePath(), new OpenFileHyperlinkListener());
            io.getOut().println("Totoal number of parents: " + n);
            io.getOut().println(" =============================== ");
            io.getOut().println("");
            io.getOut().close();
        }catch (IOException ex){
            
        }
    }

    @Override
    public void findSuperNodes(long sctid) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void findDirectSubNodes(long sctid) {
        int n=0;
        Node startNode = getNodebyID(sctid);
        Traverser myTraverser = getDirectSubTraverser(startNode);
        String fn = System.getProperty("user.home") + "/Children_" + startNode.getProperty("sctid")+".txt";
        File childtable = new File(fn);
        try{
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(childtable), "UTF-8"));
        for(Path path: myTraverser){
            String id = path.endNode().getProperty("sctid").toString();
            String fsn = path.endNode().getProperty("fsn").toString();
            writer.write(id+"|"+fsn+"\n");
            n=n+1;
        }
        writer.close();
            io.select();
            io.getOut().println(" =============================== ");
            io.getOut().println("Created file for children concepts at: ");
            io.getOut().println(childtable.getAbsolutePath(), new OpenFileHyperlinkListener());
            io.getOut().println("Totoal number of children: " + n);
            io.getOut().println(" =============================== ");
            io.getOut().println("");
            io.getOut().close();
        }catch(IOException e){
            System.out.println("Failed to write child concept table in method findDirectSubnodes(sctid)");
        }
    }
    
    
    @Override
    public Collection<Node> getDirectSuperNodes(Node sctnode){
        try(Transaction tx=graphDB.beginTx()){
            sctNodes.clear();
            Node txNode = reattach(sctnode, tx);
            for(Relationship r:txNode.getRelationships(Direction.OUTGOING, RelationshipType.withName("Is a"))){
                if((int)r.getProperty("stated")==0){
                sctNodes.add(r.getEndNode());
                }
            }
            return sctNodes;
        }
    }
    
    @Override
    public Collection<String> getParentNodeDescrition(Node sctnode){
        try(Transaction tx=graphDB.beginTx()){
            Collection<String> dsnDescription = new HashSet<String>();
            Node txNode = reattach(sctnode, tx);
//            Traverser directSuperTrav = getDirectSuperTraverser(sctnode);
//            for(Path subPath: directSuperTrav){
//                if((int)subPath.lastRelationship().getProperty("stated")==0){
            for(Relationship r : txNode.getRelationships(Direction.OUTGOING, RelationshipType.withName("Is a")))
                if((int)r.getProperty("stated")==0){
                    dsnDescription.add(r.getEndNode().getProperty("fsn").toString());
                }
//            if(dsnDescription.isEmpty()){
//                for(Relationship r : sctnode.getRelationships(Direction.OUTGOING, RelationshipType.withName("Is a")))
//                if((int)r.getProperty("stated")==1){
//                    dsnDescription.add(r.getEndNode().getProperty("fsn").toString() + "-stated ");
//                }
//            }
        return dsnDescription;
        }
    }
    
    @Override
    public Collection<String> getPrimitiveParentDescription(Node sctnode){
        Collection<String> primitiveParentDesc = new HashSet<>();
        Collection<Node> primitiveParentNodes = new HashSet<>();
        try(Transaction tx = graphDB.beginTx()){
            Node txNode = reattach(sctnode, tx);
            Traverser superNodeTv = getPrimitiveSuperNodesTraverser(txNode, tx);
            for(Path path:superNodeTv){
                primitiveParentNodes.add(path.endNode());
            }
//            removeSuperNodes(primitiveParentNodes);
            for(Node n:primitiveParentNodes){   
            primitiveParentDesc.add(n.getProperty("fsn").toString());
            }
        }
        return primitiveParentDesc;
        }
    
    
    private Collection<Node> removeSuperNodes(Collection<Node> nodes){
        Collection<Node> primitiveNodes = new HashSet<>();
        primitiveNodes.addAll(nodes);
        
        for(Node n:nodes){
            Traverser superNodesTv = getSuperNodesTraverser(n);
                for(Path path: superNodesTv){
                    for(Node m: nodes){
                    if(!path.endNode().getProperty("fsn").toString().equals(n.getProperty("fsn").toString())
                            && path.endNode().getProperty("fsn").toString().equals(m.getProperty("fsn").toString()))
                    {   System.out.println("Starting node = " + n.getProperty("fsn").toString());
                        System.out.println("Checking node ="+path.endNode().getProperty("fsn").toString());
                        System.out.println("Against m node ="+m.getProperty("fsn").toString());
                        primitiveNodes.remove(m);
                    }
                }
        }
        }
        
        return primitiveNodes;
    }
    
    @Override
    public int getDirectSuperNodeCount(Node sctnode){
        try(Transaction tx = graphDB.beginTx()){
            Node txNode = reattach(sctnode, tx);
            if (txNode.hasProperty(PCOUNT_KEY)) {
                return (int)txNode.getProperty(PCOUNT_KEY);
            }
            int pcount = 0;
            Traverser directSuperTrav = getSuperNodesTraverser(txNode, tx);
            for(Path subpath: directSuperTrav){
                pcount++;
            }
            return pcount;
        }
    }
   
    @Override
    public Collection<Node> getDirectSubNodes(Node sctnode) {
        try(Transaction tx = graphDB.beginTx()){
            sctNodes.clear();
            Node txNode = reattach(sctnode, tx);
            Traverser directSubTrav = getDirectSubTraverser(txNode, tx);
            for(Path subPath : directSubTrav){
                sctNodes.add(subPath.endNode());
            }
            return sctNodes;
        }        
    }
   
    
    @Override
    public int getSubConceptNo(Node sctnode){
        try(Transaction tx = graphDB.beginTx()){
            Node txNode = reattach(sctnode, tx);
            if (txNode.hasProperty(CCOUNT_KEY)) {
                return (int)txNode.getProperty(CCOUNT_KEY);
            }
            int ccount = 0;
            Traverser directSubTrav = getDirectSubTraverser(txNode, tx);
            for(Path subPath : directSubTrav){
                ccount++;
            }
            return ccount;
        }
    }
    
    @Override
    public int getDescendantNodeCount(Node sctnode) {
        try(Transaction tx = graphDB.beginTx()){
            Node txNode = reattach(sctnode, tx);
            if (txNode.hasProperty(DCOUNT_KEY)) {
                return (int)txNode.getProperty(DCOUNT_KEY);
            }
            int dcount = 0;
            Traverser tv = getISAExcludeStartTraverser(txNode, tx);
            for(Path subpath: tv){
                dcount++;
            }
            return dcount;
        }
    }

    private static Node reattach(Node node, Transaction tx) {
        if (node == null) {
            return null;
        }
        return tx.getNodeById(node.getId());
    }
    
    @Override
    public Collection<Node> getAllSctNodes(Node sctnode){
        Collection<Node> allsctnodes= new ArrayList<>();
        Transaction tx = getTx();
        Node txNode = reattach(sctnode, tx);
        Traverser allSctTrav = getISATraverser(txNode, tx);
        for(Path subPath: allSctTrav){
            allsctnodes.add(subPath.endNode());
        }
        return allsctnodes;
    }
    
    @Override
    public Iterator<Node> getAllSctNodeIterator(long sctid){
        Transaction tx = getTx();
        Node startNode = tx.findNode(conceptLabel, "sctid", sctid);
        Traverser allSctTrav = getISATraverser(startNode, tx);
        Iterable<Node> iterableNode = allSctTrav.nodes();
        Iterator<Node> iterator= iterableNode.iterator();
        return iterator;
    } 
            
            
    @Override
    public void findAllSubNodes(long sctid) {
              int n = 0;
              Transaction tx = getTx();
              Node startNode = tx.findNode(conceptLabel, "sctid", sctid);
              Traverser myTraverser = getISAExcludeStartTraverser(startNode);
              String fn = System.getProperty("user.home") + "/Descendants_" + startNode.getProperty("sctid")+".txt";
              File descendenttable = new File(fn);
//              try(BufferedWriter writer = Files.newBufferedWriter(daisyfile,charset, StandardOpenOption.CREATE)){
              try{
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fn),"UTF-8"));
//                writer.write("SubConcept" + "\t" + "SuperConcept" + "\n");
                for (Path subPath : myTraverser){                  
 //                 System.out.println(
 //                         "Has subconcepts at depth: " + subPath.length()+ " "
                    String conceptid = subPath.endNode().getProperty("sctid").toString();                          
                    String conceptfsn = subPath.endNode().getProperty("fsn").toString();
//                    String daisytc = subPath.endNode().getProperty(SCTID_KEY).toString()+ " | "
//                                    + subPath.endNode().getProperty(FSN_KEY);
//                    System.out.println(sctid + " has descendent: " + conceptid + " | " + conceptfsn);
                         
//                   try{                           
//                         writer.write(conceptid + "  " + conceptfsn + "\n");
                    writer.write(conceptid + "|" + conceptfsn + "\n");
//                    System.out.println(conceptid + "\t" + sctid + "\n");
                       n = n+1;
                    }
                writer.close();
                }catch (IOException x) {
//                System.err.format("IOException: %s%n", x);
                  io.getErr().println("IOException: %s%n" + x);
                  io.getErr().close();
                }
                
//               }catch (IOException x){
//                  System.err.println(x.getStackTrace());
            try{  
            io.select();
            io.getOut().println(" =============================== ");
            io.getOut().println("Created file for descendants at: ");
            io.getOut().println(descendenttable.getAbsolutePath(), new OpenFileHyperlinkListener());
            io.getOut().println("Totoal number of descendants: " + n);
            io.getOut().println(" =============================== ");
            io.getOut().println("");
            io.getOut().close();
            }catch(IOException e){
                e.printStackTrace();
            }
    }
    
    
  
//        @Override
//    public void findAllSubandInactiveNodes(long sctid) {
//        
//                  int n = 0;
//              Node startNode = getNodebyID(sctid);
//              Traverser myTraverser = getISAiTraverser(startNode);
//              String fn = "Descendants_a+i_" + startNode.getProperty("sctid")+".txt";
//              File descendenttable = new File(fn);
//
////              io.getOut().println("=========== subconcepts =========== ");
//            
////              try(BufferedWriter writer = Files.newBufferedWriter(daisyfile,charset, StandardOpenOption.CREATE)){
//              try{
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fn), "UTF-8"));
//                writer.write("Concept" + "\t" + "Descendants" + "\n");
//                for (Path subPath : myTraverser){                  
// //                 System.out.println(
// //                         "Has subconcepts at depth: " + subPath.length()+ " "
//                         String conceptid = subPath.endNode().getProperty("sctid").toString();                          
////                         String conceptfsn = subPath.endNode().getProperty(FSN_KEY).toString();
//                      String descendantsai = subPath.endNode().getProperty("sctid").toString()+ " | "
//                      + subPath.endNode().getProperty("fsn");
////                    System.out.println(sctid + " has descendants a + i : " + descendantsai);
//                    writer.write(sctid + "\t" + conceptid + "\n");
//                       n = n+1;
//                    }
//                writer.close();
//                }catch (IOException x) {
//                System.err.format("IOException: %s%n", x);
//                }
//           
//            io.select();
//            io.getOut().println(" =============================== ");
//            io.getOut().println("Generated file for descendants at: ");
//            io.getOut().println(descendenttable.getAbsolutePath());
//            io.getOut().println("Totoal number of descendants a + i : " + n);
//            io.getOut().println(" =============================== ");
//            io.getOut().println("");
//            io.getOut().close();        
//            
//    }
//   
//    
//    @Override
//    public void findSameAsNodes(long sctid) {
//        int n = 0;
//        Node startNode = getNodebyID(sctid);
//        String fn = "SAME_" + sctid +".txt";
//        File sameastable = new File(fn);
//        Traverser myTraverser = getSameAsTraverser(startNode); 
//
//        try{            
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fn), "UTF-8"));
//            writer.write("InactiveConcept" + "\t" + "ActiveConcept"+ "\n");
////            System.out.print("InactiveConcept" + "\t" + "ActiveConcept"+ "\n");            
//            for(Path subPath : myTraverser){
//                String sameas = subPath.endNode().getProperty("sctid").toString() + "\t"
//                                + sctid + "\n";
//                writer.write(sameas);
//
////                System.out.print(sameas);
//                n = n+1;
//            }
//            writer.close();
//        }catch (IOException e){
//            io.getErr().println("IOException in findSameAsNode method :" + e.getMessage());
//            io.getErr().close();
//        }
//            io.getOut().println(" ============================= ");
//            io.getOut().println("Generated file for SAME concepts at: ");
//            io.getOut().println(sameastable.getAbsolutePath());
//            io.getOut().println("Total number of SAME concepts: " + n);
//            io.getOut().println(" ============================= ");
//            io.getOut().println("");
//            io.getOut().close();
//       
//    }
//    
//        @Override
//    public void findSameAsForAllNodes(long sctid) {
//        int n =0;
//        Node startNode = getNodebyID(sctid);
//        int concept_status = Integer.parseInt(startNode.getProperty("active").toString());
////        System.out.println("start node status: " + concept_status);
//        String fn = "Substitutation_Table_SameAs_ReplacedBy_" + sctid + ".txt";
//        File substitfile = new File(fn);
//        Traverser isaTraverser = getISAiTraverser(startNode);
//        try{
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fn), "UTF-8"));
//            writer.write("InactiveConcept" + "\t" + "ActiveConcept" + "\n");
////            System.out.print("InactiveConcept_ID" + "\t" + "InactiveConcept_FSN" + "\t" + "ActiveConcept_ID" + "\t" + "ActiveConcept_FSN" + "\n");
//            for(Path isaPath : isaTraverser){
//                Node myStartNode = isaPath.endNode();
//                if(concept_status==0){
//                    Traverser myTraverser = getSameAsTraverser(myStartNode);
//                    for(Path myPath : myTraverser){
//                        String inactiveid = myPath.endNode().getProperty("sctid").toString();
//                        int inactive = Integer.parseInt(myPath.endNode().getProperty("active").toString());
//                        String activeid = myStartNode.getProperty("sctid").toString();
//                        int active = Integer.parseInt(myStartNode.getProperty("active").toString());                        
//                        if (!inactiveid.equals(activeid)&& (active == 0||active ==11) && inactive !=0 && inactive!=11 ){
//                        writer.write(inactiveid + "\t" + activeid + "\n");
//                        n = n+1; 
//                        }
//                    }
//                }
//                else {
//                    Traverser myTraverser = getSameAsOutTraverser(myStartNode);
//                    for(Path myPath : myTraverser){
//                        String activeid = myPath.endNode().getProperty("sctid").toString();
//                        int active = Integer.parseInt(myPath.endNode().getProperty("active").toString());                        
//                        String inactiveid = myStartNode.getProperty("sctid").toString();
//                        int inactive = Integer.parseInt(myStartNode.getProperty("active").toString());
//                        if (!inactiveid.equals(activeid)&& (active == 0||active ==11) && inactive !=0 && inactive!=11 ){
//                        writer.write(inactiveid + "\t" + activeid + "\n");
//
//    //                    System.out.print(myPath.endNode().getProperty(SCTID_KEY).toString() + "\t"
//    //                            + myPath.endNode().getProperty(FSN_KEY).toString() + "\t"
//    //                            + myStartNode.getProperty(SCTID_KEY).toString() + "\t" 
//    //                            + myStartNode.getProperty(FSN_KEY).toString() + "\n");
//
//                        n = n+1;
//                    }
//               }
//                }
//            }
//           writer.close(); 
//        }catch(IOException e){
//            io.getErr().println(e.getStackTrace().toString());
//            io.getErr().close();
//        }
//        io.select();
//        io.getOut().println(" ========================== ");
//        io.getOut().println("Generated substitution table for SameAs and ReplacedBy relationships at :");        
//        io.getOut().println(substitfile.getAbsolutePath());
//        io.getOut().println("Total number of substitutions: " + n);
//        io.getOut().println(" ========================== ");
//        io.getOut().println("");
//        io.getOut().close();        
//        
//    }
//        
//        
//        
//    @Override
//    public void findMayBeForAllNodes(long sctid) {
//        int n =0;
//        Node startNode = getNodebyID(sctid);
//        int concept_status = Integer.parseInt(startNode.getProperty("active").toString());
////        System.out.println("start node status: " + concept_status);
//        String fn = "Substitutation_Table_MayBeA_" + sctid + ".txt";
//        File substitfile = new File(fn);
//        Traverser isaTraverser = getISAiTraverser(startNode);
//        try{
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fn), "UTF-8"));
//            writer.write("InactiveConcept" + "\t" + "ActiveConcept" + "\n");
////            System.out.print("InactiveConcept_ID" + "\t" + "InactiveConcept_FSN" + "\t" + "ActiveConcept_ID" + "\t" + "ActiveConcept_FSN" + "\n");
//            for(Path isaPath : isaTraverser){
//                Node myStartNode = isaPath.endNode();
//                if(concept_status==0){
//                    Traverser myTraverser = getMayBeTraverser(myStartNode);
//                    for(Path myPath : myTraverser){
//                        String inactiveid = myPath.endNode().getProperty("sctid").toString();
//                        int inactive = Integer.parseInt(myPath.endNode().getProperty("active").toString());
//                        String activeid = myStartNode.getProperty("sctid").toString();
//                        int active = Integer.parseInt(myStartNode.getProperty("active").toString());                        
//                        if (!inactiveid.equals(activeid)&& (active == 0||active ==11) && inactive !=0 && inactive!=11 ){
//                        writer.write(inactiveid + "\t" + activeid + "\n");
//                        n = n+1; 
//                        }
//                    }
//                }
//                else {
//                    Traverser myTraverser = getMayBeOutTraverser(myStartNode);
//                    for(Path myPath : myTraverser){
//                        String activeid = myPath.endNode().getProperty("sctid").toString();
//                        int active = Integer.parseInt(myPath.endNode().getProperty("active").toString());                        
//                        String inactiveid = myStartNode.getProperty("sctid").toString();
//                        int inactive = Integer.parseInt(myStartNode.getProperty("active").toString());
//                        if (!inactiveid.equals(activeid)&& (active == 0) && inactive !=0 && inactive!=11 ){
//                        writer.write(inactiveid + "\t" + activeid + "\n");
//
//    //                    System.out.print(myPath.endNode().getProperty(SCTID_KEY).toString() + "\t"
//    //                            + myPath.endNode().getProperty(FSN_KEY).toString() + "\t"
//    //                            + myStartNode.getProperty(SCTID_KEY).toString() + "\t" 
//    //                            + myStartNode.getProperty(FSN_KEY).toString() + "\n");
//
//                        n = n+1;
//                    }
//               }
//                }
//            }
//           writer.close(); 
//        }catch(IOException e){
//            io.getErr().println(e.getStackTrace().toString());
//            io.getErr().close();
//        }
//        io.select();
//        io.getOut().println(" ========================== ");
//        io.getOut().println("Generated substitution table for MayBeA relationship at :");        
//        io.getOut().println(substitfile.getAbsolutePath());
//        io.getOut().println("Total number of substitutions: " + n);
//        io.getOut().println(" ========================== ");
//        io.getOut().println("");
//        io.getOut().close();        
//        
//    }
//    
//    @Override
//    public void findReplacedByForAllNodes(long sctid) {
//        int n =0;
//        Node startNode = getNodebyID(sctid);
//        int concept_status = Integer.parseInt(startNode.getProperty("active").toString());
////        System.out.println("start node status: " + concept_status);
//        String fn = "Substitutation_Table_ReplacedBy_" + sctid + ".txt";
//        File substitfile = new File(fn);
//        Traverser isaTraverser = getISAiTraverser(startNode);
//        try{
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fn), "UTF-8"));
//            writer.write("InactiveConcept" + "\t" + "ActiveConcept" + "\n");
////            System.out.print("InactiveConcept_ID" + "\t" + "InactiveConcept_FSN" + "\t" + "ActiveConcept_ID" + "\t" + "ActiveConcept_FSN" + "\n");
//            for(Path isaPath : isaTraverser){
//                Node myStartNode = isaPath.endNode();
//                if(concept_status==0){
//                    Traverser myTraverser = getReplacedByTraverser(myStartNode);
//                    for(Path myPath : myTraverser){
//                        String inactiveid = myPath.endNode().getProperty("sctid").toString();
//                        int inactive = Integer.parseInt(myPath.endNode().getProperty("active").toString());
//                        String activeid = myStartNode.getProperty("sctid").toString();
//                        int active = Integer.parseInt(myStartNode.getProperty("active").toString());                        
//                        if (!inactiveid.equals(activeid)&& (active == 0||active ==11) && inactive !=0 && inactive!=11 ){
//                        writer.write(inactiveid + "\t" + activeid + "\n");
//                        n = n+1; 
//                        }
//                    }
//                }
//                else {
//                    Traverser myTraverser = getReplacedByOutTraverser(myStartNode);
//                    for(Path myPath : myTraverser){
//                        String activeid = myPath.endNode().getProperty("sctid").toString();
//                        int active = Integer.parseInt(myPath.endNode().getProperty("active").toString());                        
//                        String inactiveid = myStartNode.getProperty("sctid").toString();
//                        int inactive = Integer.parseInt(myStartNode.getProperty("active").toString());
//                        if (!inactiveid.equals(activeid)&& (active == 0||active ==11) && inactive !=0 && inactive!=11 ){
//                        writer.write(inactiveid + "\t" + activeid + "\n");
//
//    //                    System.out.print(myPath.endNode().getProperty(SCTID_KEY).toString() + "\t"
//    //                            + myPath.endNode().getProperty(FSN_KEY).toString() + "\t"
//    //                            + myStartNode.getProperty(SCTID_KEY).toString() + "\t" 
//    //                            + myStartNode.getProperty(FSN_KEY).toString() + "\n");
//
//                        n = n+1;
//                    }
//               }
//                }
//            }
//           writer.close(); 
//        }catch(IOException e){
//            io.getErr().println(e.getStackTrace().toString());
//            io.getErr().close();
//        }
//        io.select();
//        io.getOut().println(" ========================== ");
//        io.getOut().println("Generated substitution table for ReplacedBy relationship at :");        
//        io.getOut().println(substitfile.getAbsolutePath());
//        io.getOut().println("Total number of substitutions: " + n);
//        io.getOut().println(" ========================== ");
//        io.getOut().println("");
//        io.getOut().close();         
//    }
//
//    @Override
//    public void findWasAForAllNodes(long sctid) {
//        int n =0;
//        Node startNode = getNodebyID(sctid);
//        int concept_status = Integer.parseInt(startNode.getProperty("active").toString());
////        System.out.println("start node status: " + concept_status);
//        String fn = "Substitutation_Table_WasA_" + sctid + ".txt";
//        File substitfile = new File(fn);
//        Traverser isaTraverser = getISAiTraverser(startNode);
//        try{
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fn), "UTF-8"));
//            writer.write("InactiveConcept" + "\t" + "ActiveConcept" + "\n");
////            System.out.print("InactiveConcept_ID" + "\t" + "InactiveConcept_FSN" + "\t" + "ActiveConcept_ID" + "\t" + "ActiveConcept_FSN" + "\n");
//            for(Path isaPath : isaTraverser){
//                Node myStartNode = isaPath.endNode();
//                if(concept_status==0){
//                    Traverser myTraverser = getWasATraverserI(myStartNode);
//                    for(Path myPath : myTraverser){
//                        String inactiveid = myPath.endNode().getProperty("sctid").toString();
//                        int inactive = Integer.parseInt(myPath.endNode().getProperty("active").toString());                        
//                        String activeid = myStartNode.getProperty("sctid").toString();
//                        int active = Integer.parseInt(myStartNode.getProperty("active").toString());                         
//                        if (!inactiveid.equals(activeid)&& (active == 0||active ==11) && inactive !=0 && inactive!=11 ){
//                        writer.write(inactiveid + "\t" + activeid + "\n");
//                        n = n+1; 
//                        }
//                    }
//                }
//                else {
//                    Traverser myTraverser = getWasATraverserO(myStartNode);
//                    for(Path myPath : myTraverser){
//                        String activeid = myPath.endNode().getProperty("sctid").toString();
//                        int active = Integer.parseInt(myPath.endNode().getProperty("active").toString());                        
//                        String inactiveid = myStartNode.getProperty("sctid").toString();
//                        int inactive = Integer.parseInt(myStartNode.getProperty("active").toString());
//                        if (!inactiveid.equals(activeid)&& (active == 0||active ==11) && inactive !=0 && inactive!=11 ){
//                        writer.write(inactiveid + "\t" + activeid + "\n");
//
//    //                    System.out.print(myPath.endNode().getProperty(SCTID_KEY).toString() + "\t"
//    //                            + myPath.endNode().getProperty(FSN_KEY).toString() + "\t"
//    //                            + myStartNode.getProperty(SCTID_KEY).toString() + "\t" 
//    //                            + myStartNode.getProperty(FSN_KEY).toString() + "\n");
//
//                        n = n+1;
//                    }
//               }
//                }
//            }
//           writer.close(); 
//        }catch(IOException e){
//            io.getErr().println(e.getStackTrace().toString());
//            io.getErr().close();
//        }
//        io.select();
//        io.getOut().println(" ========================== ");
//        io.getOut().println("Generated substitution table for WasA relationship at :");        
//        io.getOut().println(substitfile.getAbsolutePath());
//        io.getOut().println("Total number of substitutes: " + n);
//        io.getOut().println(" ========================== ");
//        io.getOut().println("");
//        io.getOut().close();  
//    }    
//
//    @Override
//    public void findMayBeReplacedForAllNodes(long sctid) {
//        int n =0;
//        Node startNode = getNodebyID(sctid);
//        int concept_status = Integer.parseInt(startNode.getProperty("active").toString());
////        System.out.println("start node status: " + concept_status);
//        String fn = "Substitutation_Table_MayBeA_ReplacedBy_" + sctid + ".txt";
//        File substitfile = new File(fn);
//        Traverser isaTraverser = getISAiTraverser(startNode);
//        try{
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fn), "UTF-8"));
//            writer.write("InactiveConcept" + "\t" + "ActiveConcept" + "\n");
////            System.out.print("InactiveConcept_ID" + "\t" + "InactiveConcept_FSN" + "\t" + "ActiveConcept_ID" + "\t" + "ActiveConcept_FSN" + "\n");
//            for(Path isaPath : isaTraverser){
//                Node myStartNode = isaPath.endNode();
//                if(concept_status==0 || concept_status==11){
//                    Traverser myTraverser = getMayBeReplacedTraverser(myStartNode);
//                    for(Path myPath : myTraverser){
//                        String inactiveid = myPath.endNode().getProperty("sctid").toString();
//                        int inactive = Integer.parseInt(myPath.endNode().getProperty("active").toString());
//                        String activeid = myStartNode.getProperty("sctid").toString();
//                        int active = Integer.parseInt(myStartNode.getProperty("active").toString());                        
//                        if (!inactiveid.equals(activeid)&& (active == 0||active ==11) && inactive !=0 && inactive!=11 ){
//                        writer.write(inactiveid + "\t" + activeid + "\n");
//                        n = n+1; 
//                        }
//                    }
//                }
//                else {
//                    Traverser myTraverser = getMayBeReplacedOutTraverser(myStartNode);
//                    for(Path myPath : myTraverser){
//                        String activeid = myPath.endNode().getProperty("sctid").toString();
//                        int active = Integer.parseInt(myPath.endNode().getProperty("active").toString());                        
//                        String inactiveid = myStartNode.getProperty("sctid").toString();
//                        int inactive = Integer.parseInt(myStartNode.getProperty("active").toString());
//                        if (!inactiveid.equals(activeid)&& (active == 0||active ==11) && inactive !=0 && inactive!=11 ){
//                        writer.write(inactiveid + "\t" + activeid + "\n");
//
//    //                    System.out.print(myPath.endNode().getProperty(SCTID_KEY).toString() + "\t"
//    //                            + myPath.endNode().getProperty(FSN_KEY).toString() + "\t"
//    //                            + myStartNode.getProperty(SCTID_KEY).toString() + "\t" 
//    //                            + myStartNode.getProperty(FSN_KEY).toString() + "\n");
//
//                        n = n+1;
//                    }
//               }
//                }
//            }
//           writer.close(); 
//        }catch(IOException e){
//            io.getErr().println(e.getStackTrace().toString());
//            io.getErr().close();
//        }
//        io.select();
//        io.getOut().println(" ========================== ");
//        io.getOut().println("Generated substitution table for MayBeA and ReplacedBy relationships at :");        
//        io.getOut().println(substitfile.getAbsolutePath());
//        io.getOut().println("Total number of substitutions: " + n);
//        io.getOut().println(" ========================== ");
//        io.getOut().println("");
//        io.getOut().close();         
//        
//    }
//
//    @Override
//    public void findAllHistoryForAllNodes(long sctid) {
//        int n =0;
//        Node startNode = getNodebyID(sctid);
//        int concept_status = Integer.parseInt(startNode.getProperty("active").toString());
////        System.out.println("start node status: " + concept_status);
//        String fn = "Substitutation_Table_AllHistorical_Relationships_" + sctid + ".txt";
//        File substitfile = new File(fn);
//        Traverser isaTraverser = getISAiTraverser(startNode);
//        try{
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fn), "UTF-8"));
//            writer.write("InactiveConcept" + "\t" + "ActiveConcept" + "\n");
////            System.out.print("InactiveConcept_ID" + "\t" + "InactiveConcept_FSN" + "\t" + "ActiveConcept_ID" + "\t" + "ActiveConcept_FSN" + "\n");
//            for(Path isaPath : isaTraverser){
//                Node myStartNode = isaPath.endNode();
//                if(concept_status==0 || concept_status==11){
//                    Traverser myTraverser = getAllHistoryTraverserI(myStartNode);
//                    for(Path myPath : myTraverser){
//                        String inactiveid = myPath.endNode().getProperty("sctid").toString();
//                        int inactive = Integer.parseInt(myPath.endNode().getProperty("active").toString());
//                        String activeid = myStartNode.getProperty("sctid").toString();
//                        int active = Integer.parseInt(myStartNode.getProperty("active").toString());                        
//                        if (!inactiveid.equals(activeid)&& (active == 0||active ==11) && inactive !=0 && inactive!=11 ){
//                        writer.write(inactiveid + "\t" + activeid + "\n");
//                        n = n+1; 
//                        }
//                    }
//                }
//                else {
//                    Traverser myTraverser = getAllHistoryTraverserO(myStartNode);
//                    for(Path myPath : myTraverser){
//                        String activeid = myPath.endNode().getProperty("sctid").toString();
//                        int active = Integer.parseInt(myPath.endNode().getProperty("active").toString());                        
//                        String inactiveid = myStartNode.getProperty("sctid").toString();
//                        int inactive = Integer.parseInt(myStartNode.getProperty("active").toString());
//                        if (!inactiveid.equals(activeid)&& (active == 0||active ==11) && inactive !=0 && inactive!=11 ){
//                        writer.write(inactiveid + "\t" + activeid + "\n");
//
//    //                    System.out.print(myPath.endNode().getProperty(SCTID_KEY).toString() + "\t"
//    //                            + myPath.endNode().getProperty(FSN_KEY).toString() + "\t"
//    //                            + myStartNode.getProperty(SCTID_KEY).toString() + "\t" 
//    //                            + myStartNode.getProperty(FSN_KEY).toString() + "\n");
//
//                        n = n+1;
//                    }
//               }
//                }
//            }
//           writer.close(); 
//        }catch(IOException e){
//            io.getErr().println(e.getStackTrace().toString());
//            io.getErr().close();
//        }
//        io.select();
//        io.getOut().println(" ========================== ");
//        io.getOut().println("Generated substitution table for all historical relationships at :");        
//        io.getOut().println(substitfile.getAbsolutePath());
//        io.getOut().println("Total number of substitutions: " + n);
//        io.getOut().println(" ========================== ");
//        io.getOut().println("");
//        io.getOut().close();         
//    }    
//    
//    
//    
//    @Override
//    public void findSameAsAllSubNodes(long sctid) {
//                int n = 0;
////         long sctid = Long.parseLong(id);
//         Node startNode = getNodebyID(sctid);
//         String fn = "Same_Descendents_" + sctid + ".txt";
//         
//         Traverser myTraverser = getSameAsSubTraverser(startNode);
//         
////           try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(fn), charset, StandardOpenOption.CREATE)){
//         try{
//             File sameasdescent = new File(fn);
//             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fn), "UTF-8"));
//             String firstline = "SubConcept"+ "\t" +"SuperConcept" + "\n"; 
//             writer.write(firstline);
////             System.out.print(firstline);
//            
//            for(Path subPath : myTraverser){
////             System.out.println("At depth: " + subPath.length()+ " "
////                     + subPath.lastRelationship().getType() + " "
////                     + subPath.endNode().getProperty(SCTID_KEY)+ " | "
////                     + subPath.endNode().getProperty(FSN_KEY));
////                   System.out.println( 
//                     String samedesoutput = subPath.endNode().getProperty("sctid").toString() + "\t"                     
//                     + sctid +"\n";                     
//                     writer.write(samedesoutput);
////                     System.out.print(samedesoutput);
//                  n = n+1;
//                 }
//                writer.close();
//                io.getOut().println(" ======================== ");
//                io.getOut().println("Generated file for SAME and Descendent concepts at :");
//                io.getOut().println(sameasdescent.getAbsolutePath());         
//                io.getOut().println("Total number of SAME and/or Descendent concepts: " + n);
//                io.getOut().println(" ======================== ");
//                io.getOut().println("");
//                io.getOut().close();
//                }catch (IOException e) {
//                       io.getErr().println(e.getStackTrace().toString());
//                  }             
//    }
//
//         
//        
//
//    @Override
//    public void findSameAsDirectSubNodes(long sctid) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    
    @Override
    public void findISATCNodes(long sctid){
         int n = 0;
         Node startNode = getNodebyID(sctid);
         String fn = System.getProperty("user.home")+"/TC_table_" + sctid + ".txt";
         File standardTCtable = new File(fn);
         Traverser TcTraverser = getISATraverser(startNode);
          try{
             
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fn), "UTF-8"));
             writer.write("supertypeId" + "\t"+ "subtypeId" + "\n");
//             System.out.println("Stanard TC Table:");
//             System.out.print("SubConcept" + "\t"+ "SuperConcept" + "\n");
             for (Path subPath : TcTraverser){
             Node myStartNode = subPath.endNode();
             Traverser myTraverser = getISATraverser(myStartNode);           
             
             for(Path myPath : myTraverser){
                 String tc = myStartNode.getProperty("sctid").toString()  + "\t"
                         + myPath.endNode().getProperty("sctid").toString()+ "\n";
                 writer.write(tc);
//                 System.out.print(tc);
                 n = n+1;
             }
             }
              writer.close();        
             }catch (IOException e){
                 io.getErr().println(e.getStackTrace().toString());
                 io.getErr().close();
             }
             try{
             io.select();
             io.getOut().println(" ============================ ");
             io.getOut().println("Generated standard Transitive Closure table at:");
             io.getOut().println(standardTCtable.getAbsolutePath(), new OpenFileHyperlinkListener());
             io.getOut().println("Total number of row in standard TC: " + n);
             io.getOut().println(" ============================ ");
             io.getOut().println("");
             io.getOut().close();
             } catch (IOException ex) {
            Logger.getLogger(QuerySCTimpl.class.getName()).log(Level.SEVERE, null, ex);
        }
                
     }
          
//    @Override
//    public void findSameAsISATCNodes(long sctid) {
//        throw new UnsupportedOperationException("Not supported yet.");
//        /*
//        int n = 0;
//        Node startNode = nodeIndex.get(SCTID_KEY, sctid).getSingle();
//        String fn = "SameAsDaisy_" + sctid + ".txt";
//        Traverser samesubTraverser = getSameAsSubTraverser(startNode);
//        try{
//            File daisyfile = new File(fn);
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fn), "UTF-8"));
//            writer.write("subconcept" + "\t" + "superconcept" + "\n");
////            System.out.print("subconcept" + "\t" + "superconcept" + "\n");
//            
//            for(Path subPath : samesubTraverser){
//                Node myStartNode = subPath.endNode();
//                Traverser myTraverser = getSameAsTraverser(myStartNode);
//                for(Path mySubPath : myTraverser){
//                    String sameasdaisy = mySubPath.endNode().getProperty(SCTID_KEY).toString() + "\t"
//                            + myStartNode.getProperty(SCTID_KEY).toString() + "\n";                    
//                    writer.write(sameasdaisy);
////                    System.out.print(sameasdaisy);
//                 n = n+1;
//                }
//               
//            }
//            
//            writer.close();
//            
//             
// 
//            io.getOut().println(" ========================== ");
//            io.getOut().println("Generated file for role inclusion transitive clousre table at :");
//            io.getOut().println(daisyfile.getAbsolutePath());
//            io.getOut().println("Total number of rows: " + n);
//            io.getOut().println(" ========================== ");
//            io.getOut().println("");
//            io.getOut().close();
//                     
//            
//        }catch (IOException e){
//            io.getErr().println(e.getStackTrace().toString());
//        }
//  */
//    }
    
//    @Override
//    public void findDaisyNodes(long sctid) {
//         int n = 0;
//         Node startNode = getNodebyID(sctid);
//         String fn = System.getProperty("user.home")+"/TC_Query_Table" + sctid + ".txt";
//         File standardTCtable = new File(fn);
//         Traverser TcTraverser = getSameAsSubiTraverser(startNode);
//          try{
//             
//             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fn), "UTF-8"));
//             writer.write("supertypeId" + "\t"+ "subtypeId" + "\n");
////             System.out.println("TC with SameAs extension table :");
////             System.out.print("SubConcept" + "\t"+ "SuperConcept" + "\n");
//             for (Path subPath : TcTraverser){
//             Node myStartNode = subPath.endNode();
//             Traverser myTraverser = getSameAsSubiTraverser(myStartNode);  
////             Traverser sameTraverser = getSameAsNonselfTraverser(myStartNode);
//             
//                for(Path myPath : myTraverser){
//                    String tc = myStartNode.getProperty("sctid").toString() + "\t"
//                         + myPath.endNode().getProperty("sctid").toString() + "\n";
//                    writer.write(tc);
////                    System.out.print(tc);
//                    n = n+1;
//                }
// 
////                for(Path samePath: sameTraverser){
////                    String same = myStartNode.getProperty(SCTID_KEY).toString() + "\t"
////                                  + samePath.endNode().getProperty(SCTID_KEY).toString() + "\n";
////                    writer.write(same);
////                    System.out.print(same);
////                    n = n+1;
////                 }
//
//                            
//             
////             Traverser samebiTraverser = getSameAsNonselfTraverser(myStartNode);
////             for(Path samePath : samebiTraverser){
////                String sameas = samePath.endNode().getProperty(SCTID_KEY).toString() + "\t"
////                         + myStartNode.getProperty(SCTID_KEY).toString() + "\n";  
////                writer.write(sameas);
////                System.out.print(sameas);
////                n = n + 1;
////             }
//             
//             }
//              writer.close();        
//             }catch (IOException e){
//                 io.getErr().println(e.getStackTrace().toString());
//                 io.getErr().close();
//             }
//             try{
//             io.select();
//             io.getOut().println(" ============================ ");
//             io.getOut().println("Generated TC Query Table at:");
//             io.getOut().println(standardTCtable.getAbsolutePath(), new OpenFileHyperlinkListener());
//             io.getOut().println("Total number of rows in table: " + n);
//             io.getOut().println(" ============================ ");
//             io.getOut().println("");
//             io.getOut().close();
//             }catch(IOException e){
//                 e.printStackTrace();
//             }
//        
//    }
    
//        @Override
//    public void findMayBeDaisyNodes(long sctid) {
//                 int n = 0;
//         Node startNode = getNodebyID(sctid);
//         String fn = "TC_MAY_extension_" + sctid + ".txt";
//         File standardTCtable = new File(fn);
//         Traverser TcTraverser = getMayBeDaisyTraverser(startNode);
//          try{
//             
//             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fn), "UTF-8"));
//             writer.write("SubConcept" + "\t"+ "SuperConcept" + "\n");
////             System.out.println("MayBe Daisy Table :");
////             System.out.print("SubConcept" + "\t"+ "SuperConcept" + "\n");
//             for (Path subPath : TcTraverser){
//             Node myStartNode = subPath.endNode();
//             Traverser myTraverser = getMayBeDaisyTraverser(myStartNode);  
////                Traverser sameTraverser = getSameAsNonselfTraverser(myStartNode);
//             
//                    for(Path myPath : myTraverser){
//                        String tc = myPath.endNode().getProperty("sctid").toString() + "\t"
//                         + myStartNode.getProperty("sctid").toString() + "\n";
//                        writer.write(tc);
////                        System.out.print(tc);
//                        n = n+1;
//                    }
//
///*                    
//                    for(Path samePath: sameTraverser){
//                        String same = myStartNode.getProperty(SCTID_KEY).toString() + "\t"
//                                    + samePath.endNode().getProperty(SCTID_KEY).toString() + "\n";
//                        writer.write(same);
//                        System.out.print(same);
//                        n = n+1;
//                    }
//                Traverser MayBeTraverser = getMayBeNonselfTraverser(myStartNode);
//           
// 
//                    for(Path samePath: MayBeTraverser){
//                        String maybe = myStartNode.getProperty(SCTID_KEY).toString() + "\t"
//                                    + samePath.endNode().getProperty(SCTID_KEY).toString() + "\n";
//                        writer.write(maybe);
//                        System.out.print(maybe);
//                                             
//                    } 
//
//*  
//*/
//             
////             Traverser samebiTraverser = getSameAsNonselfTraverser(myStartNode);
////             for(Path samePath : samebiTraverser){
////                String sameas = samePath.endNode().getProperty(SCTID_KEY).toString() + "\t"
////                         + myStartNode.getProperty(SCTID_KEY).toString() + "\n";  
////                writer.write(sameas);
////                System.out.print(sameas);
////                n = n + 1;
////             }
//             
//             }
//              writer.close();        
//             }catch (IOException e){
//                 io.getErr().println(e.getStackTrace().toString());
//                 io.getErr().close();
//             }
//             io.select();
//             io.getOut().println(" ============================ ");
//             io.getOut().println("Generated TC with MAY extension table at:");
//             io.getOut().println(standardTCtable.getAbsolutePath());
//             io.getOut().println("Total number of rows in the table: " + n);
//             io.getOut().println(" ============================ ");
//             io.getOut().println("");
//             io.getOut().close();
//    }

//    @Override
//    public void assignCounts(){
//       if(getGraphDB()!=null){
//           getGraphDB().shutdown();
//       }
//       startGraphDB();
//       Transaction tx = graphDB.beginTx();
//       System.out.println("Start assertion of number counts for concepts ...");
//       try{
//       caluateCounts();
//       tx.success();
//       }
//       finally{
//           tx.finish();
//           System.out.println (" update of counts completed. ");
//          
//       }
//       graphDB.shutdown();
//       
//        
//    }
//    
//    
//    private void caluateCounts(){
//        QuerySCT querysct = Lookup.getDefault().lookup(QuerySCT.class);
//        
//        Node node = querysct.getNodebyID(138875005);
//        for(Node onenode: querysct.getAllSctNodes(node)){
//        int parent_count=querysct.getDirectSuperNodeCount(onenode);
//        int child_count=querysct.getSubConceptNo(onenode);
//        int descendant_count=querysct.getDescendantNodeCount(onenode);
//        setCounts(onenode,parent_count,child_count,descendant_count);
//        }
//             
//    }
//    private void setCounts(Node onenode, int parent_count, int child_count, int descendant_count){
//        Node node = onenode;
//        node.setProperty(PCOUNT_KEY, parent_count);
//        node.setProperty(CCOUNT_KEY, child_count);
//        node.setProperty(DCOUNT_KEY, descendant_count);
//        nodeIndex.putIfAbsent(node, DCOUNT_KEY, descendant_count);
//        }

    private static class OpenFileHyperlinkListener implements OutputListener {

        public OpenFileHyperlinkListener() {
        }

        @Override
        public void outputLineSelected(OutputEvent ev) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void outputLineAction(OutputEvent ev) {
           File f = new File(ev.getLine());
            try {
                Desktop.getDesktop().open(f);
            } catch (IOException ex) {
                Logger.getLogger(QuerySCTimpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void outputLineCleared(OutputEvent ev) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
        
}
