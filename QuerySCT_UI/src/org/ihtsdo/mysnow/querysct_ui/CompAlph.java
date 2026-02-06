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
public class CompAlph implements Comparator {
   private static QuerySCT querysct = Lookup.getDefault().lookup(QuerySCT.class);
   private Node node1=null;
   private Node node2=null;
   
    @Override
    public int compare(Object t, Object t1) {
        node1 = (Node)t;
        node2 = (Node)t1;
        return querysct.getDescFSN(node1).compareTo(querysct.getDescFSN(node2));
    }
    
}
