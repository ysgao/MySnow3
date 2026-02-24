package org.ihtsdo.mysnow.querysct_ui;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ihtsdo.mysnow.querysct_api.QuerySCT;
import org.neo4j.graphdb.Node;

final class SearchResultOrderUtil {
    private SearchResultOrderUtil() {
    }

    static void sortByShortestMatchingActiveDescriptionLength(
            final QuerySCT querysct,
            final List<Node> matches,
            String searchText) {
        final String[] words = splitWords(normalize(searchText));
        final Map<String, Integer> shortestLengthBySctId = new HashMap<String, Integer>();

        Collections.sort(matches, new Comparator<Node>() {
            @Override
            public int compare(Node left, Node right) {
                int leftLength = getShortestMatchingLength(querysct, left, words, shortestLengthBySctId);
                int rightLength = getShortestMatchingLength(querysct, right, words, shortestLengthBySctId);
                if (leftLength != rightLength) {
                    return leftLength - rightLength;
                }
                return querysct.getSctID(left).compareTo(querysct.getSctID(right));
            }
        });
    }

    private static int getShortestMatchingLength(
            QuerySCT querysct,
            Node node,
            String[] words,
            Map<String, Integer> shortestLengthBySctId) {
        String sctId = querysct.getSctID(node);
        Integer cachedLength = shortestLengthBySctId.get(sctId);
        if (cachedLength != null) {
            return cachedLength.intValue();
        }

        int shortestLength = Integer.MAX_VALUE;
        Collection<String> activeTerms = querysct.getTermsActiveOnly(node);
        for (String term : activeTerms) {
            String normalizedTerm = normalize(term);
            if (!containsAllWords(normalizedTerm, words)) {
                continue;
            }
            int length = normalizedTerm.length();
            if (length < shortestLength) {
                shortestLength = length;
            }
        }

        if (shortestLength == Integer.MAX_VALUE) {
            shortestLength = 0;
        }
        shortestLengthBySctId.put(sctId, shortestLength);
        return shortestLength;
    }

    private static boolean containsAllWords(String description, String[] words) {
        for (String word : words) {
            if (word.length() > 0 && !description.contains(word)) {
                return false;
            }
        }
        return true;
    }

    private static String[] splitWords(String text) {
        if (text.length() == 0) {
            return new String[0];
        }
        return text.split("\\s+");
    }

    private static String normalize(String text) {
        return text == null ? "" : text.trim().toLowerCase();
    }
}
