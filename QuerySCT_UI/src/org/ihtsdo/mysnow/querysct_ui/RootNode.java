/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.mysnow.querysct_ui;


import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.ihtsdo.mysnow.querysct_api.QuerySCT;
import org.neo4j.graphdb.Node;
import org.openide.actions.CopyAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.ExTransferable;

/**
 *
 * @author yoga
 */
public class RootNode extends AbstractNode {
    private String definedIcon = "org/ihtsdo/mysnow/querysct_ui/icons/defined.png";
    private String primIcon = "org/ihtsdo/mysnow/querysct_ui/icons/primitive.png";
//    private String inactiveIcon = "org/ihtsdo/mysnow/querysct_ui/icons/history.png";
    private org.neo4j.graphdb.Node sctnode;
    private Children children=null;


    QuerySCT querysct = Lookup.getDefault().lookup(QuerySCT.class);
    
    
    public RootNode(org.neo4j.graphdb.Node node,Children ch){
        super(ch);
        sctnode = node;
        this.children = ch;
        setDisplayName(querysct.getSctFSN(node));
        }
   public RootNode(org.neo4j.graphdb.Node node){
       super(Children.LEAF);
       sctnode = node;
       setDisplayName(querysct.getSctFSN(node));
   }
   
   public RootNode(Children ch){
       super(ch);
   }
   
   
   
    @Override
   public Action getPreferredAction(){
        return new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                ExplorerTopComponent.setExplorerRoot(sctnode);
//                System.out.println("Double clicked .." + querysct.getSctFSN(sctnode));
            }
        };
       
   }
    

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
            new ExpandNodeAction(),
            CopyAction.get(CopyAction.class),
//            new CopyIDfsnAction(),
            new CopyChildrenAction(),
            new CopyDescendantAction(),
            new CopyParentAction(),
            new GenerateTCTable(),
//            new GenerateQueryTable()
        };
    }
    
       
    @Override
    public Image getIcon(int type){
        Image image;
        if(getIsPrimitive()==900000000000074008L){
        image = ImageUtilities.loadImage(primIcon);}
        else{
            image = ImageUtilities.loadImage(definedIcon);}
        return image;
    }

    @Override
    public Image getOpenedIcon(int type){
        Image image;
        if(getIsPrimitive()==900000000000074008L){
        image = ImageUtilities.loadImage(primIcon);}
        else{
            image = ImageUtilities.loadImage(definedIcon);}
        return image;
    
    }
        
    @Override
    public boolean canCopy() {
        return true;
    }    
    
    public org.neo4j.graphdb.Node getSctNode(){
        return sctnode;
    }
    
    public String getSctIdFsn(){
        
        return querysct.getSctID(sctnode) + "|" + querysct.getSctFSN(sctnode)+"|";
        
    }

    public long getSctId(){
        
        return Long.parseLong(querysct.getSctID(sctnode));
        
    }

    @Override
    public String toString(){
        
        return querysct.getSctFSN(sctnode);
        
    }
    
    public String getFSN(){
        
        return querysct.getSctFSN(sctnode);
        
    }

    public int getStatus(){
        
        return querysct.getSctStatus(sctnode);
        
    }

    public long getIsPrimitive(){
        
        return querysct.getIsPrimitive(sctnode);
        
    }

    public String getParentCount(){
        
        return String.valueOf(querysct.getDirectSuperNodeCount(sctnode));
        
    }

    public String getChildrenCount(){
        
        return String.valueOf(querysct.getSubConceptNo(sctnode));
        
    }

    public String getDescendantCount(){
        
        return String.valueOf(querysct.getDescendantNodeCount(sctnode));
        
    }

    public Collection<String> getTerms(){
        return querysct.getTerms(sctnode);
        
    }

    public Collection<String> getTermsActiveType(){
        return querysct.getTermsActiveType(sctnode);
        
    }

    public Collection<String> getPTActive(){
        return querysct.getDescPT(sctnode);
    }

    public Collection<String> getSynActive(){       
        return querysct.getDescSyn(sctnode);
    }
    
//    public Collection<String> getUnspecActive(){
//        return querysct.getDesUnspec(sctnode);
//    }

    public Collection<String> getParetDescription(){
        
        return querysct.getParentNodeDescrition(sctnode);
        
    }
    
    public Collection<String> getPrimitiveParentDescription(){
        return querysct.getPrimitiveParentDescription(sctnode);
    }
    
    public Collection<String> getTextDefinition(){
        return querysct.getTextDefinition(sctnode);
    }
    
    public Collection<String> getSimpleMapRefset(){
        return querysct.getSimpleMapRefset(sctnode);
    }

//    public Collection<String> getRGFindingSite(){
//        return  querysct.getRGFindingSite(sctnode);
//        
//    }

//    public Collection<String> getRGs(){
////        Collection<String> RGs = querysct.getRGs(sctnode);
//        return querysct.getRGs(sctnode);
//    }
    
    public Collection<String> getModels(){
        return querysct.getModels(sctnode);
    }

    @Override
    public Transferable clipboardCopy() throws IOException {
    Transferable deflt = super.clipboardCopy();
    ExTransferable added = ExTransferable.create(deflt);
    added.put(new ExTransferable.Single(DataFlavor.stringFlavor) {
        @Override
        protected Object getData() {
            return getLookup().lookup(RootNode.class).getSctIdFsn();
        }
    });
    return added;
    }
//        @Override
//        public Transferable clipboardCopy () throws IOException {
//        Transferable deflt = super.clipboardCopy ();
//        ExTransferable added = ExTransferable.create (deflt);
//        added.put (new ExTransferable.Single (DataFlavor.stringFlavor) {
//            
//        @Override
//        protected Object getData () {
//                    return getSctIdFsn();
//                    }
//                });
//            return added;
//        }

//    private class GenerateQueryTable extends AbstractAction {
//
//        public GenerateQueryTable() {
//            putValue(NAME, "Generate TC Query Table");
//        }

//        @Override
//        public void actionPerformed(ActionEvent ae) {
//            long sctid = getSctId();
//            querysct.findDaisyNodes(sctid);
//        }
//    }

    private class GenerateTCTable extends AbstractAction {

        public GenerateTCTable() {
            putValue(NAME, "Generate Transitive Closure Table");
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            long sctid = getSctId();
            querysct.findISATCNodes(sctid);
        }
    }

//    private class CopyIDfsnAction extends AbstractAction {
//        
//        public CopyIDfsnAction(){
//            putValue(NAME, "Copy Concept ID & FSN");          
//        }
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            try {
//                clipboardCopy();
////                JOptionPane.showMessageDialog(null, "Copy id" + copySt);
////                JOptionPane.showMessageDialog(null, "Copy id" + copySt);
//            } catch (IOException ex) {
//                Exceptions.printStackTrace(ex);
//            }
//        }

    private class CopyParentAction extends AbstractAction {

        public CopyParentAction() {
            putValue(NAME, "Generate Parent List");
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            long sctid = getSctId();
            querysct.findDirectSuperNodes(sctid);
            
        }
        
       
    }
        
        
//        Need to override the following if implements Presenter.popup for this Action.
//        @Override
//        public JMenuItem getPopupPresenter() {
//                JMenuItem item;
//                item = new JMenuItem(this);
//                item.setOpaque(true);
//                item.setBackground(Color.BLUE);
//                item.setForeground(Color.YELLOW);
//                item.setSize(20, 15);
//                return item;
//        }            
//    }

    private class CopyDescendantAction extends AbstractAction {

        public CopyDescendantAction() {
            putValue(NAME, "Generate Descendant List");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            long sctid = getSctId();
            querysct.findAllSubNodes(sctid);
            
        }
    }

    private class ExpandNodeAction extends AbstractAction {

        public ExpandNodeAction() {
            putValue(NAME, "Focus Node");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            org.neo4j.graphdb.Node rnode=getSctNode();
            ExplorerTopComponent.setExplorerRoot(rnode);    
        }
    }

    private class CopyChildrenAction extends AbstractAction {

        public CopyChildrenAction() {
            putValue(NAME, "Generate Children List");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            long sctid = getSctId();
            querysct.findDirectSubNodes(sctid);
        }
    }

}
    
    
