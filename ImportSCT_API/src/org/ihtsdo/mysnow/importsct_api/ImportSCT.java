/*

 * 
 */
package org.ihtsdo.mysnow.importsct_api;

import java.io.IOException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.openide.filesystems.FileObject;

/**
 *
 * @author yoga
 */
public interface ImportSCT {
    
    public void createGraphDB();    
    public boolean deleteDir(FileObject folder);
    public void startGraphDB();
    public void shutdownGraphDB();
    public GraphDatabaseService getGraphDB();
    
    public void importConcepts(String ConceptFilePath) throws IOException;
    public void importSimpleMapRefset(String SimpleMapFilePath) throws IOException;

    public void importDescriptions(String DescFilePath) throws IOException;
    public void importTextDefinitions(String DefinitionFilePath) throws IOException;
    public void importRefsetLangauge(String LangFilePath) throws IOException;
    public void importRelationships(String relfilepath) throws IOException;
    public void assignCounts();

    public void releaseVersion(String release);
    public void setReleaseFiles(FileObject folder);
    public String getConceptPathINT();
    public String getRelPathINT();
    public String getDescPathINT();
    public String getSimpleMapPath();
    public String getLangPath();
    public String getTextDefintionPath();
    
//    public int getImportedConceptTotal();
    
    
}
