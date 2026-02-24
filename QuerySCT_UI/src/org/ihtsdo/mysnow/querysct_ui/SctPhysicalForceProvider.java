/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.mysnow.querysct_ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import org.ihtsdo.mysnow.querysct_api.QuerySCT;
import org.neo4j.graphdb.Node;
import org.netbeans.spi.quicksearch.SearchProvider;
import org.netbeans.spi.quicksearch.SearchRequest;
import org.netbeans.spi.quicksearch.SearchResponse;
import org.openide.util.Lookup;

public class SctPhysicalForceProvider implements SearchProvider {
    private static QuerySCT querysct = Lookup.getDefault().lookup(QuerySCT.class);
    private static final long ROOT_ID = 78621006L;

    @Override
    public void evaluate(SearchRequest request, SearchResponse response) {
         if(request.getText().length()>2){
         org.neo4j.graphdb.Node rootNode = querysct.getNodebyID(ROOT_ID);
         if (rootNode == null) {
             return;
         }
        Collection<Node> allsctnodes = querysct.getAllSctNodes(rootNode);
                List<Node> matches = new ArrayList<Node>();
        for (Node sctnode:allsctnodes){
//          for (Iterator<Node> iterator =querysct.getAllSctNodeIterator(78621006); iterator.hasNext();){
//              Node sctnode = iterator.next();
                        if(isTermConditionSatisfied(sctnode, request.getText())){
//            for(String term: querysct.getTermsActiveOnly(sctnode)){
//                if(term.contains(request.getText())){
                                      matches.add(sctnode);
               }
            }
        SearchResultOrderUtil.sortByShortestMatchingActiveDescriptionLength(querysct, matches, request.getText());
        for (Node sctnode : matches) {
            if(!response.addResult(new AddtoSearchList(sctnode), querysct.getSctFSN(sctnode))){
                return;
            }
        }
        }
    }
    
    private boolean isTermConditionSatisfied(Node sctnode, String text) {
        Collection<String> activeTerms = querysct.getTermsActiveOnly(sctnode);
        String sct_id = querysct.getSctID(sctnode);
        String[] words = text.toLowerCase().split("\\s");
        int wordno = words.length;
        boolean check=false;

        switch(wordno){
            case 1: 
                
                if(words[0].length()>5&&CheckString.isLong(words[0])){
                        if(sct_id.equals(words[0]))
                        {check=true;
                        break;}
                    }
                    else{
                        for(String anyterm: activeTerms){
                            String term = anyterm.toLowerCase();
                    if(term.contains(words[0]))
                        {check=true;
                        break;}                
                        }
                    }
                break;
            case 2: for(String anyterm: activeTerms){
                            String term = anyterm.toLowerCase();
                            if(term.contains(words[0]) && term.contains(words[1]))
                            {check=true;
                            break;}
                    }
                break;
            case 3: for(String anyterm: activeTerms){
                            String term = anyterm.toLowerCase();
                            if(term.contains(words[0]) && term.contains(words[1]) && term.contains(words[2]))
                            {check=true;
                            break;}
                            }
                break;
            case 4: for(String anyterm: activeTerms){
                            String term = anyterm.toLowerCase();
                            if(term.contains(words[0]) && term.contains(words[1]) && term.contains(words[2]) && term.contains(words[3]))    
                            {check=true;
                            break;}
                            
            }
                break;
            case 5: for(String anyterm: activeTerms){
                            String term = anyterm.toLowerCase();
                if(term.contains(words[0]) && term.contains(words[1]) && term.contains(words[2]) && term.contains(words[3]) && term.contains(words[4]))
                {check=true;}
            }
                break;                
            case 6:for(String anyterm: activeTerms){
                            String term = anyterm.toLowerCase(); 
                if(term.contains(words[0]) && term.contains(words[1]) && term.contains(words[2]) && term.contains(words[3]) && term.contains(words[4]) && term.contains(words[5]))
                {check=true;
                break;}
                    }
                break;
            case 7: for(String anyterm: activeTerms){
                            String term = anyterm.toLowerCase();
                if(term.contains(words[0]) && term.contains(words[1]) && term.contains(words[2]) && term.contains(words[3]) && term.contains(words[4]) && term.contains(words[5]) && term.contains(words[6]))
                {check=true;
                break;}
                }
                break;   
            case 8:for(String anyterm: activeTerms){
                            String term = anyterm.toLowerCase(); 
                if(term.contains(words[0]) && term.contains(words[1]) && term.contains(words[2]) && term.contains(words[3]) && term.contains(words[4]) && term.contains(words[5]) && term.contains(words[6]) && term.contains(words[7]))
                {check=true;
                break;}
                }
                break;
            case 9: for(String anyterm: activeTerms){
                            String term = anyterm.toLowerCase();
                if(term.contains(words[0]) && term.contains(words[1]) && term.contains(words[2]) && term.contains(words[3]) && term.contains(words[4]) && term.contains(words[5]) && term.contains(words[6]) && term.contains(words[7]) && term.contains(words[8]))
                {check=true;
                break;}
                }
                break;                
            case 10: for(String anyterm: activeTerms){
                            String term = anyterm.toLowerCase();
                if(term.contains(words[0]) && term.contains(words[1]) && term.contains(words[2]) && term.contains(words[3]) && term.contains(words[4]) && term.contains(words[5]) && term.contains(words[6]) && term.contains(words[7]) && term.contains(words[8]) && term.contains(words[9]))
                {check=true;
                break;}
                }
                break;                 
            default:
                break;
            }
        
        return check;
          
    }

    private static class AddtoSearchList implements Runnable {
        private long sctid;
        public AddtoSearchList(Node sctnode) {
            this.sctid = Long.parseLong(querysct.getSctID(sctnode));
        }
    
        
        @Override
    public void run(){
//        StatusDisplayer.getDefault().setStatusText("Selected search result: " + querysct.getSctFSN(node));
        
          org.neo4j.graphdb.Node node = querysct.getNodebyID(sctid);
          if (node != null) {
              ExplorerTopComponent.setExplorerRoot(node);
          }
        }
    }
}
