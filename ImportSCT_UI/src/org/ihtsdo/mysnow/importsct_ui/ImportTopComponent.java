/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.mysnow.importsct_ui;

import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import org.ihtsdo.mysnow.importsct_api.ImportSCT;
import org.ihtsdo.mysnow.querysct_api.QuerySCT;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.LifecycleManager;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.ihtsdo.mysnow.importsct_ui//Import//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "ImportTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "org.ihtsdo.mysnow.importsct_ui.ImportTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_ImportAction",
        preferredID = "ImportTopComponent")
@Messages({
    "CTL_ImportAction=Import",
    "CTL_ImportTopComponent=Import Window",
    "HINT_ImportTopComponent=Import window"
})
public final class ImportTopComponent extends TopComponent {
    ImportSCT importsct = Lookup.getDefault().lookup(ImportSCT.class);
    QuerySCT querysct = Lookup.getDefault().lookup(QuerySCT.class);
    InputOutput io = IOProvider.getDefault().getIO("Import Status", false);
    File home = new File(System.getProperty("user.home"));

    public ImportTopComponent() {
        initComponents();
        setName(Bundle.CTL_ImportTopComponent());
        setToolTipText(Bundle.HINT_ImportTopComponent());

    }
    
    private void importINTConcept() throws IOException{
        String conceptPathINT = importsct.getConceptPathINT();
        String simpleMapPath = importsct.getSimpleMapPath();
        
        // Validate paths before attempting import
        if (conceptPathINT == null || conceptPathINT.trim().isEmpty()) {
            io.getOut().println("ERROR: Concept file path is not set. Please select a valid directory first.");
            io.getOut().close();
            return;
        }
        
        io.getOut().println("Start to import INT concept table, please wait ... ");
        io.getOut().println("Concept file: " + conceptPathINT);
        
        try {
            importsct.importConcepts(conceptPathINT);
            
            if (simpleMapPath != null && !simpleMapPath.trim().isEmpty()) {
                io.getOut().println("Simple map file: " + simpleMapPath);
                importsct.importSimpleMapRefset(simpleMapPath);
            } else {
                io.getOut().println("Warning: Simple map file not found - skipping simple map import");
            }
        } catch (IOException e) {
            io.getOut().println("ERROR during concept import: " + e.getMessage());
            throw e;
        } finally {
            io.getOut().close();
        }
    }
    
    private void importINTRel() throws IOException{
        String relPathINT = importsct.getRelPathINT();
        
        if (relPathINT == null || relPathINT.trim().isEmpty()) {
            io.getOut().println("ERROR: Relationship file path is not set. Please select a valid directory first.");
            io.getOut().close();
            return;
        }
        
        io.getOut().println("Start to import INT Relationship table, please wait ... ");
        io.getOut().println("Relationship file: " + relPathINT);
        
        try {
            importsct.importRelationships(relPathINT);
        } catch (IOException e) {
            io.getOut().println("ERROR during relationship import: " + e.getMessage());
            throw e;
        } finally {
            io.getOut().close();
        }
    }
    
    private void importINTTerm() throws IOException{
        String descPathINT = importsct.getDescPathINT();
        String textDefinitionPath = importsct.getTextDefintionPath();
        String langPath = importsct.getLangPath();
        
        if (descPathINT == null || descPathINT.trim().isEmpty()) {
            io.getOut().println("ERROR: Description file path is not set. Please select a valid directory first.");
            io.getOut().close();
            return;
        }
        
        io.getOut().println("Start to import INT Description table, please wait ...");
        io.getOut().println("Description file: " + descPathINT);
        
        try {
            importsct.importDescriptions(descPathINT);
            
            if (textDefinitionPath != null && !textDefinitionPath.trim().isEmpty()) {
                io.getOut().println("Text definition file: " + textDefinitionPath);
                importsct.importTextDefinitions(textDefinitionPath);
            } else {
                io.getOut().println("Warning: Text definition file not found - skipping text definition import");
            }
            
            if (langPath != null && !langPath.trim().isEmpty()) {
                io.getOut().println("Language refset file: " + langPath);
                importsct.importRefsetLangauge(langPath);
            } else {
                io.getOut().println("ERROR: Language refset file is required but not found");
                throw new IOException("Language refset file is missing");
            }
        } catch (IOException e) {
            io.getOut().println("ERROR during description/language import: " + e.getMessage());
            throw e;
        } finally {
            io.getOut().close();
        }
    }
      
    
    private void assignCounts(){
        importsct.assignCounts();
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        jTextField1.setText(org.openide.util.NbBundle.getMessage(ImportTopComponent.class, "ImportTopComponent.jTextField1.text")); // NOI18N
        jTextField1.setToolTipText(org.openide.util.NbBundle.getMessage(ImportTopComponent.class, "ImportTopComponent.jTextField1.toolTipText")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(ImportTopComponent.class, "ImportTopComponent.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(ImportTopComponent.class, "ImportTopComponent.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 571, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 159, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap(8, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        String foldername = jTextField1.getText();
        //the default dir to use if no value is stored
        
        FileChooserBuilder fcbuilder = new FileChooserBuilder("release-dir").setTitle("Select the folder which contains both latest INT and UK release text file").setDefaultWorkingDirectory(home);
        JFileChooser chooser = fcbuilder.createFileChooser();
//        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(this);
        switch(result){
            case JFileChooser.APPROVE_OPTION:
                File selectedFolder = chooser.getSelectedFile();
                FileObject folder = FileUtil.toFileObject(selectedFolder);
                importsct.setReleaseFiles(folder);
                jTextField1.setText(selectedFolder.getAbsolutePath());
//                String release = selectedFolder.getName();
//                importsct.releaseVersion(release);
                io.select();
//                io.getOut().println("Selected release folder contains INT and UK data files: ");
                io.getOut().println("File path for concept: " + importsct.getConceptPathINT());
                io.getOut().println("File path for description: "+importsct.getDescPathINT());
                io.getOut().println(jTextField1.getText());
                io.getOut().close();
                break;
            case JFileChooser.CANCEL_OPTION:
                break;
            case JFileChooser.ERROR_OPTION:
                break;                
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        importsct.shutdownGraphDB();
        querysct.shutdownGraphDB();
        io.getOut().println("Start create graphDB ...... ");
        io.getOut().close();
//        importsct.createGraphDB();
        Runnable importRunnable = new Runnable(){
        @Override
        public void run() {
        ProgressHandle p = ProgressHandleFactory.createSystemHandle("Import SNOMED Task");        
        p.start(100);
        p.progress("Start importing ", 2);        
            try {
                importINTConcept();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        p.progress("Import INT concepts", 30);
            try {
                importINTTerm();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        p.progress("Import INT descriptions", 70);
            try {     
                importINTRel();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        p.progress("Import INT relationships", 80);
        
        assignCounts();
        p.progress(100);
        p.finish();
        io.getOut().println("Import SNOMED CT completed.");
        io.getOut().close();
        LifecycleManager.getDefault().markForRestart();
        LifecycleManager.getDefault().exit();
           }
        };  

        Thread mythread = new Thread(importRunnable);
        mythread.start();
    }//GEN-LAST:event_jButton2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        importsct.shutdownGraphDB();
    }

    @Override
    public void componentClosed() {
        //need to shutdown graph db here !!!
      importsct.shutdownGraphDB();
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
}
