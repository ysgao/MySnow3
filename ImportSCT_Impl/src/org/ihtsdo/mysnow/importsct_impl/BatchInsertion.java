/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.mysnow.importsct_impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ihtsdo.mysnow.importsct_api.ImportSCT;
import org.ihtsdo.mysnow.neo4j.EmbeddedDbManager;
import org.ihtsdo.mysnow.neo4j.Neo4jConfig;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author yoga
 */

@ServiceProvider(service = ImportSCT.class)
public class BatchInsertion implements ImportSCT {

    private static final int BATCH_SIZE = 10000;
    BatchInserterCompat inserter;
// String filepath = "/Users/yoga/neo4j-sct2-store";
    private final String filepath;
    File file_store;
    private final FileObject File_Store_FO;
    private final EmbeddedDbManager dbManager;

    Label conceptLabel;
    Label descLabel;
    Label definitionLabel;
    Label USlangLabel;
    Label GBlangLabel;
    Label simpleMapLabel;
    Label CTV3Label;
    Label GMDNLabel;
    Label ICDOLabel;
    Label ICNPdiagnosesLabel;
    Label ICNPinterventionLabel;
    Label SNOMEDRTLabel;

    String PCOUNT_KEY;
    String CCOUNT_KEY;
    String DCOUNT_KEY;

HashMap<Long, Long> idmap = new HashMap<>();
HashMap<Long, Long> descidmap = new HashMap<>();
HashMap<String, String> attmap = new HashMap<>();
int pCount=0;
int cCount=0;
int dCount=0;
int langrefCount;
GraphDatabaseService graphDB;

InputOutput io = IOProvider.getDefault().getIO("Import Status", true);
private String release = null;
private String relv;
private String ukreldate = null;
private String intreldate = null;

private String conceptPathINT = null;
private String relPathINT = null;
private String statedRelPath = null;
private String descPathINT = null;
private String textDefinitionPath = null;
private String simpleMapPath = null;
private String langPath = null;
    private String fsn = null;
    private static final String SCTID_KEY = "sctid";

        
    public BatchInsertion(){
        this.DCOUNT_KEY = "descendent_count";
        this.CCOUNT_KEY = "child_count";
        this.PCOUNT_KEY = "parent_count";
        this.SNOMEDRTLabel = Label.label("SNOMED RT");
        this.ICNPinterventionLabel = Label.label("ICNP Interventioins");
        this.ICNPdiagnosesLabel = Label.label("ICNP Diagnoses");
        this.ICDOLabel = Label.label("ICD-O");
        this.GMDNLabel = Label.label("GMDN");
        this.CTV3Label = Label.label("CTV3");
        this.simpleMapLabel = Label.label("Simple Map");
        this.GBlangLabel = Label.label("GB");
        this.USlangLabel = Label.label("US");
        this.definitionLabel = Label.label("Definition");
        this.descLabel = Label.label("Description");
        this.conceptLabel = Label.label("Concept");

        this.filepath = System.getProperty("user.home")+"/neo4j-sct2-store";
        this.file_store = new File(filepath);
        this.File_Store_FO = FileUtil.toFileObject(file_store);
        this.dbManager = new EmbeddedDbManager(file_store);
        this.inserter = null;

    }

    private Long resolveConceptNodeId(long sctid) {
        Long nodeId = idmap.get(sctid);
        if (nodeId != null) {
            return nodeId;
        }
        if (inserter == null) {
            return null;
        }
        return inserter.findNodeId(conceptLabel, SCTID_KEY, sctid);
    }

    private Long resolveDescNodeId(long descid) {
        Long nodeId = descidmap.get(descid);
        if (nodeId != null) {
            return nodeId;
        }
        if (inserter == null) {
            return null;
        }
        return inserter.findNodeId(descLabel, "descid", descid);
    }

    private String stripSemanticTag(String fsnValue) {
        if (fsnValue == null) {
            return null;
        }
        String fsn = fsnValue.trim();
        int idx = fsn.lastIndexOf(" (");
        if (idx > 0) {
            return fsn.substring(0, idx);
        }
        return fsn;
    }

@Override    
public void importConcepts(String ConceptFilePath) throws IOException{
    // Validate input
    if (ConceptFilePath == null || ConceptFilePath.trim().isEmpty()) {
        throw new IOException("Concept file path is null or empty");
    }
    
    File conceptFile = new File(ConceptFilePath);
    if (!conceptFile.exists()) {
        throw new IOException("Concept file does not exist: " + ConceptFilePath);
    }
    if (!conceptFile.canRead()) {
        throw new IOException("Cannot read concept file: " + ConceptFilePath);
    }
    
    io.getOut().println("Starting concept import from: " + ConceptFilePath);
    
    try{
        // Ensure database directory exists and is writable
        if (!file_store.getParentFile().exists()) {
            file_store.getParentFile().mkdirs();
        }
        
        dbManager.shutdown();
        graphDB = null;
        deleteDirectory(file_store.toPath());
        
        // Enable memory configuration for large imports
        graphDB = dbManager.start(Neo4jConfig.load());
        inserter = new BatchInserterCompat(graphDB, BATCH_SIZE);
        inserter.createSchemaIndex(conceptLabel, SCTID_KEY);
        cCount=0;
        Map<String, Object> c_properties = new HashMap<>();
        
        BufferedReader reader = null;       
        try{
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(ConceptFilePath), "UTF-8"));
            String readline;
            int curLineNr = 1;
            int skipLines = 1;
            int errorCount = 0;
            
            while((readline = reader.readLine()) != null){
                if(curLineNr++ <= skipLines){
                    continue;
                }
                
                try {
                    String datavalue[] = readline.split("\t");
                    
                    // Validate line format
                    if (datavalue.length < 5) {
                        io.getOut().println("Warning: Invalid line format at line " + curLineNr + ": " + readline);
                        errorCount++;
                        if (errorCount > 100) {
                            throw new IOException("Too many format errors. Import aborted.");
                        }
                        continue;
                    }
                    
                    long id = Long.parseLong(datavalue[0]);
                    int effectiveTime = Integer.parseInt(datavalue[1]);
                    int active = Integer.parseInt(datavalue[2]);
                    long moduleid = Long.parseLong(datavalue[3]);
                    long definition_status = Long.parseLong(datavalue[4]);
                    
                    c_properties.clear();
                    c_properties.put(SCTID_KEY, id);
                    c_properties.put("effectiveTime", effectiveTime);
                    c_properties.put("active", active);
                    c_properties.put("modulieid", moduleid);
                    c_properties.put("definition_status", definition_status);
                    
                    long cNode = inserter.createNode(c_properties, conceptLabel);
                    idmap.put(id, cNode);
                    cCount++;
                    
                    // Progress reporting
                    if (cCount % 10000 == 0) {
                        io.getOut().println("Imported " + cCount + " concepts...");
                    }
                    
                } catch (NumberFormatException e) {
                    io.getOut().println("Warning: Number format error at line " + curLineNr + ": " + e.getMessage());
                    errorCount++;
                    if (errorCount > 100) {
                        throw new IOException("Too many format errors. Import aborted.");
                    }
                }
            }
            
            io.getOut().println("Concept import completed. Total concepts: " + cCount);
            if (errorCount > 0) {
                io.getOut().println("Import completed with " + errorCount + " errors.");
            }
            
        } catch(IOException e){
            io.getOut().println("Error reading concept file: " + e.getMessage());
            throw e;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    io.getOut().println("Warning: Error closing file reader: " + e.getMessage());
                }
            }
        }
        
    } catch (Exception e) {
        io.getOut().println("Fatal error during concept import: " + e.getMessage());
        throw new IOException("Concept import failed", e);
    } finally{
        if(inserter != null) {
            try {
                inserter.shutdown();
            } catch (Exception e) {
                io.getOut().println("Warning: Error shutting down inserter: " + e.getMessage());
            }
        }
        io.getOut().close();
    }
}

@Override
public void importSimpleMapRefset(String SimpleMapFilePath) throws IOException{
    // Validate input
    if (SimpleMapFilePath == null || SimpleMapFilePath.trim().isEmpty()) {
        throw new IOException("Simple map file path is null or empty");
    }
    
    File mapFile = new File(SimpleMapFilePath);
    if (!mapFile.exists()) {
        throw new IOException("Simple map file does not exist: " + SimpleMapFilePath);
    }
    
    io.getOut().println("Starting simple map import from: " + SimpleMapFilePath);
    
    try {
        graphDB = dbManager.start();
        inserter = new BatchInserterCompat(graphDB, BATCH_SIZE);
        inserter.createSchemaIndex(simpleMapLabel, "maptarget");
        langrefCount=0;
      
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(SimpleMapFilePath), "UTF-8"));
            
            String readline;
            int curLineNr = 1;
            int skipLines =1;
            while((readline = reader.readLine()) != null){
                if(curLineNr++ <= skipLines){
                    continue;
                }
                String datavalue[] = readline.split("\t");
                
                String simplemapid = datavalue[0];
                int effectiveTime = Integer.parseInt(datavalue[1]);
                int active = Integer.parseInt(datavalue[2]);
                long moduleid=Long.parseLong(datavalue[3]);
                long refsetid = Long.parseLong(datavalue[4]);
                long compid = Long.parseLong(datavalue[5]);
                String maptarget = datavalue[6];           
             
            Map<String, Object> hasmap_properties = new HashMap<>();    
                hasmap_properties.put("mapid", simplemapid);
                hasmap_properties.put("effectiveTime", effectiveTime);
                hasmap_properties.put("active", active);
                hasmap_properties.put("modulieid", moduleid);
                
            Map<String, Object> map_properties = new HashMap<>();    
                map_properties.put("refsetid", refsetid);                
                map_properties.put("maptarget", maptarget);

    /**
     *  900000000000496009|Simple map type reference set (foundation metadata concept)|
     *  900000000000497000|CTV3 simple map reference set (foundation metadata concept)|
     *  467614008|GMDN simple map reference set (foundation metadata concept)|
     *  446608001|ICD-O simple map reference set (foundation metadata concept)|
     *  711112009|ICNP diagnoses simple map reference set (foundation metadata concept)|
     *  712505008|ICNP interventions simple map reference set (foundation metadata concept)|
     *  900000000000498005|SNOMED RT identifier simple map (foundation metadata concept)|
     */     
            Long c_nodeid = resolveConceptNodeId(compid);
            switch (String.valueOf(refsetid)){
                case "900000000000497000":                    
                    long ctv3_node = inserter.createNode(map_properties, CTV3Label);
                    inserter.setNodeProperty(ctv3_node, "refset", "CTV3 refset");
                    if(c_nodeid!=null){
                        inserter.createRelationship(c_nodeid, ctv3_node, RelTypes.Has_simple_map, hasmap_properties);}
                    break;
                case "467614008":
                    long gmdn_node = inserter.createNode(map_properties, GMDNLabel);
                    inserter.setNodeProperty(gmdn_node, "refset","GMDN reset");;
                    if(c_nodeid!=null){
                        inserter.createRelationship(c_nodeid, gmdn_node, RelTypes.Has_simple_map, hasmap_properties);}
                    break;                   
                case "446608001":
                    long icdo_node = inserter.createNode(map_properties, ICDOLabel); 
                    inserter.setNodeProperty(icdo_node, "refset", "ICD-O refset");
                    if(c_nodeid!=null){
                        inserter.createRelationship(c_nodeid, icdo_node, RelTypes.Has_simple_map, hasmap_properties);}
//                    System.out.println("ICD-O map = " + maptarget + "for concept " +compid);
//                    if(compid==86049000L) {System.out.println("ICD-O map = " + maptarget);}
                    break;
                case "711112009":
                    long icnp_d_node = inserter.createNode(map_properties, ICNPdiagnosesLabel); 
                    inserter.setNodeProperty(icnp_d_node, "refset", "ICNP Diagnoses refset");
                    if(c_nodeid!=null){
                        inserter.createRelationship(c_nodeid, icnp_d_node, RelTypes.Has_simple_map, hasmap_properties);}
                    break;
                case "712505008":
                    long icnp_i_node = inserter.createNode(map_properties, ICNPinterventionLabel);
                    inserter.setNodeProperty(icnp_i_node, "refset", "ICNP Interventions refset");
                    if(c_nodeid!=null){
                        inserter.createRelationship(c_nodeid, icnp_i_node, RelTypes.Has_simple_map, hasmap_properties);}
                    break; 
                case "900000000000498005":
                    long snomedrt_node = inserter.createNode(map_properties, SNOMEDRTLabel);
                    inserter.setNodeProperty(snomedrt_node, "refset", "SNOMED RT refset");
                    if(c_nodeid!=null){
                        inserter.createRelationship(c_nodeid, snomedrt_node, RelTypes.Has_simple_map, hasmap_properties);}
                    break;
                    
                default: break;
            }        
        }
       }
        finally{
        if(inserter !=null) {inserter.shutdown();}  
            }
    } finally {
        dbManager.shutdown();
    }

}

//    inserter = BatchInserters.inserter(inserter_store, config);
@Override
public void importDescriptions(String DescFilePath) throws IOException{
    // Validate input
    if (DescFilePath == null || DescFilePath.trim().isEmpty()) {
        throw new IOException("Description file path is null or empty");
    }
    
    File descFile = new File(DescFilePath);
    if (!descFile.exists()) {
        throw new IOException("Description file does not exist: " + DescFilePath);
    }
    
    io.getOut().println("Starting description import from: " + DescFilePath);
    
    try {
        graphDB = dbManager.start();
        inserter = new BatchInserterCompat(graphDB, BATCH_SIZE);
        inserter.createSchemaIndex(descLabel, "term");
        dCount=0;
    
        Map<String, Object> desc_properties = new HashMap<>();
        
               
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(DescFilePath), "UTF-8"));
            
            String readline;
            int curLineNr = 1;
            int skipLines =1;
            while((readline = reader.readLine()) != null){
                if(curLineNr++ <= skipLines){
                    continue;
                }
                String datavalue[] = readline.split("\t");
                
                long descid = Long.parseLong(datavalue[0]);
                int effectiveTime = Integer.parseInt(datavalue[1]);
                int active = Integer.parseInt(datavalue[2]);
                long moduleid=Long.parseLong(datavalue[3]);
                long conceptid = Long.parseLong(datavalue[4]);
                String language = datavalue[5];
                long desctype = Long.parseLong(datavalue[6]);           
                String term = datavalue[7];
                long casesignificance = Long.parseLong(datavalue[8]);

                
                desc_properties.put("descid", descid);
                desc_properties.put("effectiveTime", effectiveTime);
                desc_properties.put("active", active);
                desc_properties.put("modulieid", moduleid);
                desc_properties.put("conceptid", conceptid);
                desc_properties.put("language", language);
                desc_properties.put("desc_type", desctype);
                desc_properties.put("term", term);
                desc_properties.put("casesignificance", casesignificance);

                long dNode=inserter.createNode(desc_properties, descLabel);
                descidmap.put(descid, dNode);
                dCount++;
                
                if(active==1){
                Long c_nodeid = resolveConceptNodeId(conceptid);
                if (c_nodeid != null) {
                    inserter.createRelationship(c_nodeid, dNode, RelTypes.Has_description, null);
                }
                
                // adding FSNs to Concepts
                if(desctype==900000000000003001L){
                    if (c_nodeid != null) {
                        inserter.setNodeProperty(c_nodeid, "fsn", term);
                    }
                }
                }
            }
            System.out.println("Inserted description count = " + dCount);
            System.out.println("description node maps in descidmap = " + descidmap.size());
       }
        finally{
        if(inserter !=null) {inserter.shutdown();}  
            }
    } finally {
        dbManager.shutdown();
    }
}

@Override
public void importTextDefinitions(String DefinitionFilePath) throws IOException {
    graphDB = dbManager.start(Neo4jConfig.load());
    inserter = new BatchInserterCompat(graphDB, BATCH_SIZE);
    inserter.createSchemaIndex(definitionLabel, "term");
    dCount=0;
    
        Map<String, Object> desc_properties = new HashMap<>();
        
               
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(DefinitionFilePath), "UTF-8"));
            
            String readline;
            int curLineNr = 1;
            int skipLines =1;
            while((readline = reader.readLine()) != null){
                if(curLineNr++ <= skipLines){
                    continue;
                }
                String datavalue[] = readline.split("\t");
                
                long descid = Long.parseLong(datavalue[0]);
                int effectiveTime = Integer.parseInt(datavalue[1]);
                int active = Integer.parseInt(datavalue[2]);
                long moduleid=Long.parseLong(datavalue[3]);
                long conceptid = Long.parseLong(datavalue[4]);
                String language = datavalue[5];
                long desctype = Long.parseLong(datavalue[6]);           
                String term = datavalue[7];
                long casesignificance = Long.parseLong(datavalue[8]);
                
                desc_properties.put("descid", descid);
                desc_properties.put("effectiveTime", effectiveTime);
                desc_properties.put("active", active);
                desc_properties.put("modulieid", moduleid);
                desc_properties.put("conceptid", conceptid);
                desc_properties.put("language", language);
                desc_properties.put("desc_type", desctype);
                desc_properties.put("term", term);
                desc_properties.put("casesignificance", casesignificance);
                
                
                long dNode=inserter.createNode(desc_properties, descLabel);
                descidmap.put(descid, dNode);
                dCount++;
                if(active==1){
                long c_nodeid = idmap.get(conceptid);            
                inserter.createRelationship(c_nodeid, dNode, RelTypes.Has_description, null);
                }
            }
            System.out.println("Inserted text defintion count = " + dCount);
            System.out.println("description node maps in descidmap = " + descidmap.size());
       }
        finally{
        if(inserter !=null) {inserter.shutdown();}  
            }
}

@Override
public void importRefsetLangauge(String LangFilePath) throws IOException{
    graphDB = dbManager.start();
    inserter = new BatchInserterCompat(graphDB, BATCH_SIZE);
//    inserter.createDeferredSchemaIndex(descLabel).on("refsetid").create();
    langrefCount=0;
    
        Map<String, Object> USnode_property = new HashMap<>();
        USnode_property.put("refset", "US");
        long USnode = inserter.createNode(USnode_property, USlangLabel);
        
        Map<String, Object> GBnode_property = new HashMap<>();
        GBnode_property.put("refset", "GB");
        long GBnode = inserter.createNode(GBnode_property, GBlangLabel);
        
        Map<String, Object> lang_properties = new HashMap<>();
           
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(LangFilePath), "UTF-8"));
            
            String readline;
            int curLineNr = 1;
            int skipLines =1;
            while((readline = reader.readLine()) != null){
                if(curLineNr++ <= skipLines){
                    continue;
                }
                String datavalue[] = readline.split("\t");
                
                String langid = datavalue[0];
                int effectiveTime = Integer.parseInt(datavalue[1]);
                int active = Integer.parseInt(datavalue[2]);
                long moduleid=Long.parseLong(datavalue[3]);
                long refsetid = Long.parseLong(datavalue[4]);
                long compid = Long.parseLong(datavalue[5]);
                long acceptabilityid = Long.parseLong(datavalue[6]);           
                
                lang_properties.put("langid", langid);
                lang_properties.put("effectiveTime", effectiveTime);
                lang_properties.put("active", active);
                lang_properties.put("modulieid", moduleid);
                lang_properties.put("refsetid", refsetid);
                lang_properties.put("accetabilityid", acceptabilityid);
                //acceptabilityid 900000000000548007 preferred   900000000000549004 acceptable
                //900000000000509007 | US English |  900000000000508004 | GB English |
                //not all compid are description, QA issue in data that 88 descriptions missed in lang refset
                if(active==1){
                    Long d_nodeid = resolveDescNodeId(compid);
                    if(d_nodeid != null){ 
                            if(refsetid==900000000000508004L){
                            inserter.createRelationship(d_nodeid, GBnode, RelTypes.Member_of_language_refset, lang_properties);
                            inserter.setNodeProperty(d_nodeid, "GB", 1);
//                            if(compid==28383015L || compid==743848012L || compid==28383015L){ 
//                            System.out.println("description id = " +compid + "  - en-GB");}
                            
                            }
                            else if(refsetid==900000000000509007L){
                                inserter.createRelationship(d_nodeid, USnode, RelTypes.Member_of_language_refset, lang_properties);
                                inserter.setNodeProperty(d_nodeid,"US",1);
                                if(compid==28380017L || compid==743848012L || compid==28383015L){
//                                System.out.println("description id = " +compid + "  - en-US");
                                }
                            }
//                                
//                                inserter.setNodeProperty(d_nodeid, "language", "en-US");}
//                                else {inserter.setNodeProperty(d_nodeid, "language", "en-US");}

                        langrefCount++;
//                        System.out.println("descript id:" + compid + " language refest:" + refsetid + " count:" + langrefCount);
//                        }
//                        
//                        inserter.createRelationship(d_nodeid, USnode, RelTypes.In_language_refset, lang_properties);
//                        langrefCount++;
//                        System.out.println("descript id:" + descid + " language refest:" + refsetid + " count:" + langrefCount);
//                        }                        
//                    }
                    }
                }
            }
            
       }
        finally{
        if(inserter !=null) {inserter.shutdown();}
        if(!descidmap.isEmpty()){descidmap.clear();}
            }



}


        
@Override
public void importRelationships(String relfilepath) throws IOException{
    // Validate input
    if (relfilepath == null || relfilepath.trim().isEmpty()) {
        throw new IOException("Relationship file path is null or empty");
    }
    
    File relFile = new File(relfilepath);
    if (!relFile.exists()) {
        throw new IOException("Relationship file does not exist: " + relfilepath);
    }
    
    io.getOut().println("Starting relationship import from: " + relfilepath);
    
    try {
        graphDB = dbManager.start(Neo4jConfig.load());
        inserter = new BatchInserterCompat(graphDB, BATCH_SIZE);
        Map<String, Object> rel_properties = new HashMap<>();

        // Build attribute map before loading relationships so we can name non-ISA edges
        attmap.clear();
        try (Transaction tx = graphDB.beginTx()) {
    Node attnode = tx.findNode(conceptLabel, SCTID_KEY, 410662002);
            if (attnode != null) {
                Traverser allattnodes = getISAExcludeStartTraverser(attnode, tx);
                for(org.neo4j.graphdb.Path subpath : allattnodes){
        String attid = subpath.endNode().getProperty(SCTID_KEY).toString();
                    fsn = subpath.endNode().getProperty("fsn").toString();
                    String term = fsn.substring(0, fsn.length()-11);
                    attmap.put(attid, term);
                }
            }
            tx.commit();
        }
        
               
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(relfilepath), "UTF-8"));
            String readline;
            int curLineNr = 1;
            int skipLines =1;
            while((readline = reader.readLine()) != null){
                if(curLineNr++ <= skipLines){
                    continue;
                }
                String datavalue[] = readline.split("\t");
                
                long relid = Long.parseLong(datavalue[0]);
                int effectiveTime = Integer.parseInt(datavalue[1]);
                int active=Integer.parseInt(datavalue[2]);
                long moduleid = Long.parseLong(datavalue[3]);
                long sourceid = Long.parseLong(datavalue[4]);
                long destinationid = Long.parseLong(datavalue[5]);
                int rg = Integer.parseInt(datavalue[6]);
                long reltype = Long.parseLong(datavalue[7]);
                long character = Long.parseLong(datavalue[8]);
                long modifier = Long.parseLong(datavalue[9]);
                int stated  = 0;
                
                rel_properties.put("relid", relid);
                rel_properties.put("effectiveTime", effectiveTime);
                rel_properties.put("active", active);
                rel_properties.put("modulieid", moduleid);
                rel_properties.put("rg", rg);
                rel_properties.put("reltype", reltype);
                rel_properties.put("character", character);
                rel_properties.put("modifier", modifier);
                rel_properties.put("stated", stated);

                Long node1 = resolveConceptNodeId(sourceid);
                Long node2 = resolveConceptNodeId(destinationid);
                if (node1 == null || node2 == null) {
                    continue;
                }

                if(active==1 && String.valueOf(reltype).equals("116680003")){
                    RelationshipType inferredreltype  = RelationshipType.withName("Is a");
                    inserter.createRelationship(node1, node2, inferredreltype, rel_properties);
//                if(String.valueOf(sourceid).equals("7246002")){
//                    System.out.println("Adding ISA relationships for Kidney biopsy : " );
//                    System.out.println("Attribute: " + inferredreltype.name() + " " +destinationid + " RG:" +rg); 
//                }
                    
                }
                else if(active==1) {
                    String relKey = String.valueOf(reltype);
                    String relName = attmap.get(relKey);
                    if(relName == null) {
                        Long relTypeNodeId = resolveConceptNodeId(reltype);
                        if (relTypeNodeId != null) {
                            Object fsnValue = inserter.getNodeProperty(relTypeNodeId, "fsn");
                            relName = stripSemanticTag(fsnValue == null ? null : fsnValue.toString());
                            if (relName != null) {
                                attmap.put(relKey, relName);
                            }
                        }
                    }
                    if(relName == null) {
                        relName = "RelType_" + relKey;
                    }
                    RelationshipType inferredreltype  = RelationshipType.withName(relName);
                    inserter.createRelationship(node1, node2, inferredreltype, rel_properties);
                }


            }
       }
        finally{
        if(inserter !=null) {inserter.shutdown();}  
            }
    } finally {
        dbManager.shutdown();
    }

}

public void importInferredRelaitonships(String relfilepath) throws IOException{
        graphDB = dbManager.start();
        inserter = new BatchInserterCompat(graphDB, BATCH_SIZE);
//    inserter = BatchInserters.inserter(inserter_store, config);
//    inserter.createDeferredSchemaIndex(descLabel).on("term").create(); 
   
        Map<String, Object> inferredrel_properties = new HashMap<>();
             
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(relfilepath), "UTF-8"));
            String readline;
            int curLineNr = 1;
            int skipLines =1;
            while((readline = reader.readLine()) != null){
                if(curLineNr++ <= skipLines){
                    continue;
                }
                String datavalue[] = readline.split("\t");
                
                long relid = Long.parseLong(datavalue[0]);
                int effectiveTime = Integer.parseInt(datavalue[1]);
                int active=Integer.parseInt(datavalue[2]);
                long moduleid = Long.parseLong(datavalue[3]);
                long sourceid = Long.parseLong(datavalue[4]);
                long destinationid = Long.parseLong(datavalue[5]);
                int rg = Integer.parseInt(datavalue[6]);
                long reltype = Long.parseLong(datavalue[7]);
                long character = Long.parseLong(datavalue[8]);
                long modifier = Long.parseLong(datavalue[9]);
                int stated = 0;
                if(active==1){
                inferredrel_properties.put("relid", relid);
                inferredrel_properties.put("effectiveTime", effectiveTime);
                inferredrel_properties.put("active", active);
                inferredrel_properties.put("modulieid", moduleid);
                inferredrel_properties.put("rg", rg);
//                rel_properties.put("reltype", reltype);
                inferredrel_properties.put("character", character);
                inferredrel_properties.put("modifier", modifier);
                inferredrel_properties.put("stated", stated);

                Long node1 = resolveConceptNodeId(sourceid);
                Long node2 = resolveConceptNodeId(destinationid);
                if (node1 == null || node2 == null) {
                    continue;
                }

     String relKey = String.valueOf(reltype);
     String relName = attmap.get(relKey);
     if (relName == null) {
        Long relTypeNodeId = resolveConceptNodeId(reltype);
        if (relTypeNodeId != null) {
            Object fsnValue = inserter.getNodeProperty(relTypeNodeId, "fsn");
            relName = stripSemanticTag(fsnValue == null ? null : fsnValue.toString());
            if (relName != null) {
                attmap.put(relKey, relName);
            }
        }
     }
     if(relName == null) {
        relName = "RelType_" + relKey;
     }
     if(!String.valueOf(reltype).equals("116680003")) {        
        RelationshipType inferredreltype  = RelationshipType.withName(relName);
     //batch inserter create relationship 
        inserter.createRelationship(node1, node2, inferredreltype, inferredrel_properties);
//        if(String.valueOf(sourceid).equals("7246002")){
//        System.out.println("Relaitonships for Kidney biopsy : " );
//        System.out.println("Attribute: " + inferredreltype.name() + " " +destinationid + "RG:" +rg);    
//     }
            }
            }
            }
    }
     finally{
        if(inserter !=null) {inserter.shutdown();}  
            }
}

public void importStatedRelationships(String statedRelPath) throws IOException {
    //import stated is a relationship only 

    //hashmap for conceptid and fsn for attributes
   
     //query graphDB and get transitive closure table for concept model attribtues //410662002|Concept model attribute (attribute)

    
    graphDB = dbManager.start(Neo4jConfig.load());
    inserter = new BatchInserterCompat(graphDB, BATCH_SIZE);
//    inserter = BatchInserters.inserter(inserter_store, config);
//    inserter.createDeferredSchemaIndex(descLabel).on("term").create(); 
   
        Map<String, Object> statedrel_properties = new HashMap<>();
             
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(statedRelPath), "UTF-8"));
            String readline;
            int curLineNr = 1;
            int skipLines =1;
            while((readline = reader.readLine()) != null){
                if(curLineNr++ <= skipLines){
                    continue;
                }
                String datavalue[] = readline.split("\t");
                
                long relid = Long.parseLong(datavalue[0]);
                int effectiveTime = Integer.parseInt(datavalue[1]);
                int active=Integer.parseInt(datavalue[2]);
                long moduleid = Long.parseLong(datavalue[3]);
                long sourceid = Long.parseLong(datavalue[4]);
                long destinationid = Long.parseLong(datavalue[5]);
                int rg = Integer.parseInt(datavalue[6]);
                long reltype = Long.parseLong(datavalue[7]);
                long character = Long.parseLong(datavalue[8]);
                long modifier = Long.parseLong(datavalue[9]);
                int stated = 1;
                
                if(active==1){
                statedrel_properties.put("relid", relid);
                statedrel_properties.put("effectiveTime", effectiveTime);
                statedrel_properties.put("active", active);
                statedrel_properties.put("modulieid", moduleid);
                statedrel_properties.put("rg", rg);
//                rel_properties.put("reltype", reltype);
                statedrel_properties.put("character", character);
                statedrel_properties.put("modifier", modifier);
                statedrel_properties.put("stated", stated);

                Long node1 = resolveConceptNodeId(sourceid);
                Long node2 = resolveConceptNodeId(destinationid);
                if (node1 == null || node2 == null) {
                    continue;
                }
//     System.out.println("Relationship type id = " +reltype);
     if(String.valueOf(reltype).equals("116680003")){
        RelationshipType statedreltype  = RelationshipType.withName("Is a(s)");
     //batch inserter create relationship 
        inserter.createRelationship(node1, node2, statedreltype, statedrel_properties);
//        System.out.println("inserted stated isa relationship " + statedreltype.name());
     }
     //create relationship types  
//     System.out.println("no is a, Attribute = " + attmap.get(String.valueOf(reltype)));
     String relKey = String.valueOf(reltype);
     String relName = attmap.get(relKey);
     if (relName == null) {
        Long relTypeNodeId = resolveConceptNodeId(reltype);
        if (relTypeNodeId != null) {
            Object fsnValue = inserter.getNodeProperty(relTypeNodeId, "fsn");
            relName = stripSemanticTag(fsnValue == null ? null : fsnValue.toString());
            if (relName != null) {
                attmap.put(relKey, relName);
            }
        }
     }
     if(relName == null) {
        relName = "RelType_" + relKey;
     }
     if(!String.valueOf(reltype).equals("116680003")) {        
        RelationshipType statedreltype  = RelationshipType.withName(relName + "(s)");
     //batch inserter create relationship 
        inserter.createRelationship(node1, node2, statedreltype, statedrel_properties);
//        System.out.println("RelType inserted : " + statedreltype.name());
     }    
                }
     }
    }
     finally{
        if(inserter !=null) {inserter.shutdown();}  
            }   
    }

public static enum RelTypes implements RelationshipType{

        Has_description,
        Member_of_language_refset,
        Has_simple_map,
        Member_of_simple_map_refset,
        iISA,        
//    //the following should be list attributes under: 
//    //410663007|Concept history attribute (attribute)
//        SAME_AS, // 168666000
//        MOVED_FROM,  // 384598002
//        MOVED_TO, // 370125004
//        MAY_BE_A, //149016008
//        REPLACED_BY, //370124000
//        WAS_A,  // 159083000
//        ISA_mapping, // 178066000
//    
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
//        Units, //246514001 
//        Property_type, //704318007
//        Role_group //609096000
    }
        
private int getDirectSuperNodeCount(Node sctnode, Transaction tx){
        int pcount = 0;
        Traverser directSuperTrav = getDirectSuperTraverser(sctnode, tx);
        for(org.neo4j.graphdb.Path subpath: directSuperTrav){
            pcount = pcount+1;
        }
        return pcount;
    }
        
private int getSubConceptNo(Node sctnode, Transaction tx){
        int ccount = 0;
        Traverser directSubTrav = getDirectSubTraverser(sctnode, tx);
        for(org.neo4j.graphdb.Path subPath : directSubTrav){
          ccount = ccount+1;
        }
        return ccount;
    }
    
private int getDescendantNodeCount(Node sctnode, Transaction tx) {
//        dncount = Integer.parseInt(node.getProperty(DCOUNT_KEY).toString());
        int dcount = 0;
        Traverser tv = getISAExcludeStartTraverser(sctnode, tx);
        for(org.neo4j.graphdb.Path subpath: tv){
            dcount=dcount+1;
        }
        return dcount;
            }  


@Override
public void createGraphDB() {
        io.getOut().println("Start to create database ...");
        if(graphDB != null){
            dbManager.shutdown();
        }
        if (file_store.exists()) {
//            FileUtils.deleteFile(File_Store);
            deleteDir(File_Store_FO);
            System.out.println("Delete existing DB at: " + File_Store_FO.getPath().toString());
        }
        graphDB = dbManager.start(Neo4jConfig.load());
        registerShutdownHook(dbManager);
        io.getOut().println("GraphDB is ready for data import ... ");
        io.getOut().println("New database created at: ");
        io.getOut().println(file_store);
        io.getOut().close();  
    }

@Override
public void startGraphDB() {
        if(graphDB != null){
            dbManager.shutdown();
            }
        graphDB = dbManager.start(Neo4jConfig.load());
        registerShutdownHook(dbManager);
    }

@Override
    public boolean deleteDir(FileObject folder) {
        try{
        FileObject neoDB[] = folder.getChildren();
        for (FileObject fileObject : neoDB) {
            boolean success = deleteDir(fileObject);
            if(!success){
                return false;
            }
            }    
            folder.delete();
            return true;  

    }catch(IOException e){
        System.out.println("Failed deletion of existing SCT-STORE.");
        return false;
        
    }
    }

    private static void deleteDirectory(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.deleteIfExists(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null) {
                    throw exc;
                }
                Files.deleteIfExists(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

@Override
public void shutdownGraphDB() {
            if(graphDB != null){
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

    @Override
    public GraphDatabaseService getGraphDB() {
        if (graphDB == null) {
            graphDB = dbManager.start();
        }
        return graphDB;
    }    
@Override    
public void assignCounts(){
        graphDB = dbManager.start();
        try(Transaction tx = graphDB.beginTx()){
//       io.getOut().println("Start assertion of number counts for concepts ...");
//       io.getOut().close();
        Node node = tx.findNode(conceptLabel, SCTID_KEY, 138875005);           
            for(Node onenode: getAllSctNodes(node, tx)){
            int parent_count=getDirectSuperNodeCount(onenode, tx);
            int child_count=getSubConceptNo(onenode, tx);
            int descendant_count=getDescendantNodeCount(onenode, tx);
            setCounts(onenode,parent_count,child_count,descendant_count);
            
            }
            tx.commit();
            
       }
          finally{
           
//           io.getOut().println (" update of counts completed. ");
//           io.getOut().close();
             dbManager.shutdown(); 
       }
    
   
    }
    
        
private void setCounts(Node onenode, int parent_count, int child_count, int descendant_count){
        Node node = onenode;
        node.setProperty(PCOUNT_KEY, parent_count);
        node.setProperty(CCOUNT_KEY, child_count);
        node.setProperty(DCOUNT_KEY, descendant_count);
//        nodeIndex.putIfAbsent(node, DCOUNT_KEY, descendant_count);
        }
    
@Override
public void setReleaseFiles(FileObject folder) {
        List<FileObject> releaseFiles = new ArrayList<>();
        collectFilesRecursively(folder, releaseFiles);
        io.getOut().println("Scanning directory for SNOMED CT files...");

        for (FileObject fileObject : releaseFiles) {
            if (fileObject.isFolder()) {
                continue;
            }
            String fileName = fileObject.getNameExt().toLowerCase();
            String ext = fileObject.getExt().toLowerCase();
            if (!"txt".equals(ext)) {
                continue;
            }
            String fullPath = fileObject.getPath();
            
            // More robust file detection using contains and file extension check
            if (fileName.contains("concept") && fileName.contains("snapshot") &&
                fileName.contains("int") && fileName.endsWith(".txt")) {
                conceptPathINT = fullPath;
                io.getOut().println("Found concept file: " + conceptPathINT);
            }
            else if (fileName.contains("description") && fileName.contains("snapshot") && 
                     fileName.contains("int") && fileName.contains("en") && fileName.endsWith(".txt")) {
                descPathINT = fullPath;
                io.getOut().println("Found description file: " + descPathINT);
            }
            else if (fileName.contains("relationship") && fileName.contains("snapshot") &&
                     fileName.contains("int") && fileName.endsWith(".txt")) {
                if (fileName.contains("concrete")) {
                    io.getOut().println("Skipping concrete values relationship file: " + fullPath);
                } else if (relPathINT == null) {
                    relPathINT = fullPath;
                    io.getOut().println("Found relationship file: " + relPathINT);
                }
            }
            else if (fileName.contains("textdefinition") && fileName.contains("snapshot") && 
                     fileName.contains("int") && fileName.contains("en") && fileName.endsWith(".txt")) {
                textDefinitionPath = fullPath;
                io.getOut().println("Found text definition file: " + textDefinitionPath);
            }
            else if (fileName.contains("simplemap") && fileName.contains("snapshot") && 
                     fileName.contains("int") && fileName.endsWith(".txt")) {
                simpleMapPath = fullPath;
                io.getOut().println("Found simple map file: " + simpleMapPath);
            }
            else if (fileName.contains("language") && fileName.contains("snapshot") && 
                     fileName.contains("int") && fileName.contains("en") && fileName.endsWith(".txt")) {
                langPath = fullPath;
                io.getOut().println("Found language refset file: " + langPath);
            }
        }
        
        // Validate that all required files were found
        validateRequiredFiles();
    }

    private void collectFilesRecursively(FileObject folder, List<FileObject> out) {
        for (FileObject child : folder.getChildren()) {
            if (child.isFolder()) {
                collectFilesRecursively(child, out);
            } else {
                out.add(child);
            }
        }
    }
    
    private void validateRequiredFiles() {
        StringBuilder missingFiles = new StringBuilder();
        
        if (conceptPathINT == null) missingFiles.append("- Concept file\n");
        if (descPathINT == null) missingFiles.append("- Description file\n");
        if (relPathINT == null) missingFiles.append("- Relationship file\n");
        if (langPath == null) missingFiles.append("- Language refset file\n");
        
        if (missingFiles.length() > 0) {
            io.getOut().println("ERROR: Missing required files:");
            io.getOut().println(missingFiles.toString());
            io.getOut().println("Please ensure all SNOMED CT files are present in the selected directory.");
        } else {
            io.getOut().println("All required files found successfully.");
        }
        io.getOut().close();
    }


    public Collection<Node> getAllSctNodes(Node sctnode, Transaction tx){
    Collection<Node> allsctnodes= new ArrayList<>();
        Traverser allSctTrav = getISATraverser(sctnode, tx);
        for(org.neo4j.graphdb.Path subPath: allSctTrav){
            allsctnodes.add(subPath.endNode());
    
    }
        return allsctnodes;
}
           
    private Traverser getISATraverser(final Node node, Transaction tx){
         TraversalDescription td = tx.traversalDescription()
                 .breadthFirst()
                 .relationships(RelationshipType.withName("Is a"), Direction.INCOMING);
//                 .evaluator(Evaluators.excludeStartPosition());
         return td.traverse(node);
     }
    
        private Traverser getISAExcludeStartTraverser(final Node node, Transaction tx){
         TraversalDescription td = tx.traversalDescription()
                 .breadthFirst()
                 .relationships(RelationshipType.withName("Is a"), Direction.INCOMING)
                 .evaluator(Evaluators.excludeStartPosition());
         return td.traverse(node);
     } 
     
    private Traverser getDirectSuperTraverser( final Node node, Transaction tx )
        {
           TraversalDescription td = tx.traversalDescription()
                  .breadthFirst()
                  .relationships(RelationshipType.withName("Is a"), Direction.OUTGOING)
 //               .evaluator(Evaluators.excludeStartPosition())
                  .evaluator(Evaluators.includingDepths(1, 1));
           
           return td.traverse(node);

        }
          
    private Traverser getDirectSubTraverser(final Node node, Transaction tx){
        TraversalDescription td = tx.traversalDescription()
                .breadthFirst()
                .relationships(RelationshipType.withName("Is a"), Direction.INCOMING)
//               .evaluator(Evaluators.excludeStartPosition())
                .evaluator(Evaluators.includingDepths(1, 1));
        return td.traverse(node);
        
    }


@Override
public void releaseVersion(String release) {
       String[] releasev = release.split("_");
       relv = releasev[1];
       System.out.println(relv);
       
       setUKDate(releasev[2]);
       System.out.println("UK release date: " + ukreldate);
       System.out.println("UK release month: " +ukreldate.substring(4, 6));
       if((releasev[2].substring(4, 6)).equals("10"))
        {
        intreldate = releasev[2].substring(0,4)+"07"+"31";
        setINTDate(intreldate);
        System.out.println("INT release date: " + intreldate);
               }
            
       else{
           intreldate = releasev[2].substring(0,4)+"01"+"31";
           setINTDate(intreldate);
           System.out.println(intreldate);
       }          
    }
    
public void setINTDate(String intdate){
        intreldate = intdate;
    }
    
public String getINTDate(){
        return intreldate;
    }
    
public void setUKDate(String ukdate){
        ukreldate = ukdate;
    }
    
public String getUKDate(){
        return ukreldate;
    }
    
            
@Override
public String getConceptPathINT(){    
        return conceptPathINT;
    }
            
@Override
public String getRelPathINT(){
        return relPathINT;
    }
    
@Override
public String getDescPathINT() {
        return descPathINT;
    }    

    @Override
    public String getSimpleMapPath() {
        return simpleMapPath;
    }

    @Override
    public String getLangPath() {
        return langPath;
    }

    @Override
    public String getTextDefintionPath() {
        return textDefinitionPath;
    }
    
}
