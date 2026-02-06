/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.mysnow.querysct_ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.ihtsdo.mysnow.querysct_api.QuerySCT;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.netbeans.spi.quicksearch.SearchProvider;
import org.netbeans.spi.quicksearch.SearchRequest;
import org.netbeans.spi.quicksearch.SearchResponse;
import org.openide.util.Lookup;

public class SctSearchProvider implements SearchProvider {
    private static QuerySCT querysct = Lookup.getDefault().lookup(QuerySCT.class);
    private static final long ROOT_ID = 138875005L;
//    Collection<Node> termNodes = new ArrayList<Node>();
//    Iterator<Node> iterator;
//////    Collection<Node> allsctnodes1 = new ArrayList<Node>();
//    Collection<Node> allsctnodes2 = new ArrayList<Node>();
    
    @Override
    public void evaluate(SearchRequest request, SearchResponse response) {
//        if(request.getText().length()>2){
         org.neo4j.graphdb.Node rootNode = querysct.getNodebyID(ROOT_ID);
         if (rootNode == null) {
             return;
         }
        Collection<Node> allsctnodes = querysct.getAllSctNodes(rootNode);
//        for (Node sctnode:allsctnodes){
          for (Iterator<Node> iterator =querysct.getAllSctNodeIterator(138875005); iterator.hasNext();){
              Node sctnode = iterator.next();
              if(isTermConditionSatisfied(sctnode, request.getText())){
//            for(String term: querysct.getTermsActiveOnly(sctnode)){
//                if(term.contains(request.getText())){
                   if(!response.addResult(new AddtoSearchList(sctnode), querysct.getDescFSN(sctnode))){
                    return;
                }
               }
            }
        }


//        }
    
//            tnode.getSingleRelationship(RelTypes., Direction.BOTH)
//        }
        
//        String[] words = request.toString().toLowerCase().split("\\s");
// //       int wordno = words.length;
////        if(wordno==1){
// //           for (Node sctnode:allsctnodes){
////            if(isTermConditionSatisfied(sctnode, 1, request.getText())){
////                allsctnodes1.add(sctnode);
//                if(!response.addResult(new AddtoSearchList(sctnode), querysct.getSctFSN(sctnode))){
//                    return;
//                }
//            }
//            }
//        }
 //       else if(wordno==2){
//            for(Node sctnode:allsctnodes1){
//            if(isTermConditionSatisfied(sctnode, 2, request.getText())){
//            if(!response.addResult(new AddtoSearchList(sctnode), querysct.getSctFSN(sctnode))){
//                    return;
//                }
//            }
//        }
//        }
//        else {
//            for (Node sctnode:allsctnodes2){
//            if(isTermConditionSatisfied(sctnode, 3, request.getText())){
//            if(!response.addResult(new AddtoSearchList(sctnode), querysct.getSctFSN(sctnode))){
//                    return;
//                }
//            }
//        }
//       }
    
//}
    
//    private boolean isIDConditionSatisfied(Node sctnode, String text) {
//    boolean check=false;
//    String sct_id = querysct.getSctID(sctnode).toString();
//    if(sct_id.equals(text)){
//        check=true;
//    }
//    return check;
//    }

    private boolean isTermConditionSatisfied(Node sctnode, int wordno, String text) {
        Collection<String> activeTerms = querysct.getTermsActiveOnly(sctnode);
        String sct_id = querysct.getSctID(sctnode);
        String[] words = text.toLowerCase().split("\\s");
//        int wordno = words.length;
        boolean check=false;
                
        switch(wordno){
            case 1: 
                
                if(words[0].length()>5&&CheckString.isLong(words[0])){
                        if(sct_id.equals(words[0]))
                            check=true;}
                    else{
                        for(String anyterm: activeTerms){
                            String term = anyterm.toLowerCase();
                    if(term.contains(words[0]))
                            check=true;                
                            break;
                        }
                    }
                break;
            case 2: for(String anyterm: activeTerms){
                            String term = anyterm.toLowerCase();
                            if(term.contains(words[1]))
//                            if(term.contains(words[0]) && term.contains(words[1]))
                            check=true;
                            break;
                    }
                break;
            case 3: for(String anyterm: activeTerms){
                            String term = anyterm.toLowerCase();
                            if(term.contains(words[2]))
//                            if(term.contains(words[0]) && term.contains(words[1]) && term.contains(words[2]))
                            check=true;
                            break;}
                break;  

                
            default:
                break;
            }
        
        return check;
          
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
        public AddtoSearchList(Node tnode) {
            this.sctid = Long.parseLong(querysct.getSctID(tnode));
//            this.node= querysct.getNodebyTermNode(tnode);
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
