/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.mysnow.querysct_ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.ihtsdo.mysnow.querysct_api.QuerySCT;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;


/**
 *
 * @author yoga
 */
class SctParentFactory extends ChildFactory<org.neo4j.graphdb.Node> {
    Collection<org.neo4j.graphdb.Node> nodes = new ArrayList<>();
    QuerySCT querysct = Lookup.getDefault().lookup(QuerySCT.class);
    private org.neo4j.graphdb.Node node = null;
   
    
    public SctParentFactory(org.neo4j.graphdb.Node node) {
          this.node = node;
    }
    

    @Override
    protected boolean createKeys(List<org.neo4j.graphdb.Node> toPopulate) {
        nodes=querysct.getDirectSuperNodes(node);
        boolean addAll = toPopulate.addAll(nodes);
        return addAll;
    }

    @Override
    protected Node createNodeForKey (org.neo4j.graphdb.Node key){
        Node result;
        result= new RootNode(key, Children.create(new SctChildFactory(key), true));        
        return result;
    }
    
}
