/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.mysnow.querysct_ui;

import java.util.Comparator;
import org.ihtsdo.mysnow.querysct_api.QuerySCT;
import org.neo4j.graphdb.Node;
import org.openide.util.Lookup;

/**
 *
 * @author yoga
 */
class comp implements Comparator {
   private static QuerySCT querysct = Lookup.getDefault().lookup(QuerySCT.class);
   private Node node1=null;
   private Node node2=null;

    @Override
    public int compare(Object t, Object t1) {
        node1 = (Node)t;
        node2 = (Node)t1;
        if(querysct.getDescFSN(node1).length()>querysct.getDescFSN(node2).length()){
            return 1;
        }else if (querysct.getDescFSN(node1).length()<querysct.getDescFSN(node2).length()){
            return -1;
        }else{
            return 0;
        }
    }
    
}
