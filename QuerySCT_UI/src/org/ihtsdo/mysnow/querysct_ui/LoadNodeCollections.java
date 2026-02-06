/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.mysnow.querysct_ui;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.ihtsdo.mysnow.querysct_api.QuerySCT;
import org.neo4j.graphdb.Node;
import org.openide.util.Lookup;

/**
 *
 * @author yoga
 */
public class LoadNodeCollections {
    public static QuerySCT querysct = Lookup.getDefault().lookup(QuerySCT.class);    
    public static Collection<Node> allfindingnodes;
    public static Collection<Node> allprocnodes;
    
    public static void loadCollections(){
        setFindingNodeCollection();
        setProcNodeCollection();
    }
    
    public static void setFindingNodeCollection(){
    Node findingnode= querysct.getNodebyID(404684003);
    allfindingnodes = querysct.getAllSctNodes(findingnode);
    Collections.sort((List<Node>)allfindingnodes, new comp());
//    System.out.println("Number of findings: "+allfindingnodes.size());
    }
    
    public static void setProcNodeCollection(){
    Node procnode= querysct.getNodebyID(71388002);
    allprocnodes = querysct.getAllSctNodes(procnode);
    Collections.sort((List<Node>) allprocnodes, new comp());
//    System.out.println("Number of procedures: " +allprocnodes.size());
    }
}
