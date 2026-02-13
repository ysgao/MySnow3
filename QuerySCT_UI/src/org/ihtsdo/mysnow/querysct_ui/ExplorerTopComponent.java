/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.mysnow.querysct_ui;

import java.beans.PropertyVetoException;
import java.io.File;
import javax.swing.ActionMap;
import javax.swing.text.DefaultEditorKit;
import org.ihtsdo.mysnow.querysct_api.QuerySCT;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.WindowManager;



/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.ihtsdo.mysnow.querysct_ui//Explorer//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "ExplorerTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "explorer", openAtStartup = true)
@ActionID(category = "Window", id = "org.ihtsdo.mysnow.querysct_ui.ExplorerTopComponent")
@ActionReference(path = "Menu/Window", position = 200)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_ExplorerAction",
        preferredID = "ExplorerTopComponent")
@Messages({
    "CTL_ExplorerAction=Explorer",
    "CTL_ExplorerTopComponent=Explorer Window",
    "HINT_ExplorerTopComponent=Taxonomy View"
})
public final class ExplorerTopComponent extends TopComponent implements ExplorerManager.Provider {
    private static ExplorerManager em = new ExplorerManager(); //CommonExplorers.SHARED_MANAGER;
    private ActionMap map = this.getActionMap();
    static QuerySCT querysct=Lookup.getDefault().lookup(QuerySCT.class);
    private static BeanTreeView btv;
    private static final String DB_PATH = System.getProperty("user.home")+"/neo4j-sct3-store"; 
//    private static final String DB_PATH ="/graphstore/neo4j-sct-store";    
    private static final File File_Store = new File(DB_PATH);
    
    
    long sctid=138875005;
//  long sctid=241601008;
//  long sctid=71388002;
    org.neo4j.graphdb.Node drnode;
    private Node node;
    Node[] nodes;

    public ExplorerTopComponent() {
        initComponents();
        initActions();
        btv = new BeanTreeView();       
        setLayout(new java.awt.BorderLayout());
        add(btv, java.awt.BorderLayout.CENTER);
        setName(Bundle.CTL_ExplorerTopComponent());
        setToolTipText(Bundle.HINT_ExplorerTopComponent());
        if (!File_Store.exists()) {
            String msg = "Cannot find neo4j-sct3-store at "+System.getProperty("user.home")+"\n"+
                    "Please import SNOMED CT release files by clicking Window and selecting Import from the menu of this application.";
            final NotifyDescriptor d = new NotifyDescriptor.Message(msg);
            WindowManager.getDefault().invokeWhenUIReady(new Runnable(){
                @Override
                public void run() {
                    DialogDisplayer.getDefault().notify(d);
                }
                
            });
            
        }else {
            querysct.startGraphDB();       
            drnode = querysct.getNodebyID(sctid);
            if (drnode == null) {
                String msg = "Root concept (138875005) was not found in the database.\n" +
                        "The import may be incomplete or the database may be empty.\n" +
                        "Please re-import the RF2 release and try again.";
                final NotifyDescriptor d = new NotifyDescriptor.Message(msg);
                WindowManager.getDefault().invokeWhenUIReady(new Runnable(){
                    @Override
                    public void run() {
                        DialogDisplayer.getDefault().notify(d);
                    }
                });
                return;
            }
            associateLookup(ExplorerUtils.createLookup(em, map));
            setExplorerRoot(drnode);
            int rootChildren = querysct.getSubConceptNo(drnode);
            btv.setRootVisible(rootChildren == 0);
            
            WindowManager.getDefault().invokeWhenUIReady(new Runnable(){                
                  @Override
                  public void run(){
                    LoadNodeCollections.loadCollections();
              }  
            });
            }
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    
    @Override
    public void componentActivated(){
        
    }
    
    @Override
    public void componentDeactivated(){
    }
    
    @Override
    public void componentClosed() {
        querysct.shutdownGraphDB();
    }
    
    @Override
    public void componentOpened(){
        
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return em;
    }

    private void initActions() {
        map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(em));
        map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(em));
        map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(em));
        map.put("delete", ExplorerUtils.actionDelete(em, true));
    }

    
    public static void setExplorerRoot(org.neo4j.graphdb.Node sctnode){
            if(querysct.getSctID(sctnode).equals("138875005")){
            em.setRootContext(new RootNode(sctnode, Children.create(new SctChildFactory(sctnode), true)));
            
                Node rootnode = em.getRootContext();
                long sctid = rootnode.getLookup().lookup(RootNode.class).getSctId();
//                System.out.println("Default true root: "+ sctid + " "+rootnode.getDisplayName());
                try {
                        em.setExploredContextAndSelection(rootnode, new Node[]{rootnode});
                    }catch (PropertyVetoException ex) {
                                      Exceptions.printStackTrace(ex);
                     }         
            
                }else{
                        em.setRootContext(new RootNode(sctnode, Children.create(new SctParentFactory(sctnode), true)));
            
                        Node rootnode = em.getRootContext();
                        long sctid = rootnode.getLookup().lookup(RootNode.class).getSctId();
//                System.out.println("Default true root: "+ sctid + " "+rootnode.getDisplayName());

                Node childnodes[] = rootnode.getChildren().getNodes(true);
                    int i = childnodes.length-1;
                 Node childnode = childnodes[i];
                 Node selectNodes[] = childnode.getChildren().getNodes(true);
                 for(int j=0; j<selectNodes.length; j++){
                     Node anyOneNode = selectNodes[j];
                     long anyOneNodeId = anyOneNode.getLookup().lookup(RootNode.class).getSctId();
                     if(anyOneNodeId==sctid){   
                     int indexOfNode = j;
                     Node nextNode = childnode.getChildren().getNodeAt(indexOfNode);
//                     System.out.println("Node to be expanded: " +nextNode.getDisplayName());                                            
                     try {
                        em.setExploredContextAndSelection(rootnode, new Node[]{nextNode});
                        btv.expandNode(nextNode);
                    }catch (PropertyVetoException ex) {
                                    Exceptions.printStackTrace(ex);
                     }
                }
            }    
          }
    }



 }



