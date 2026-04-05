// Implement smart query processing with keyword extraction and fallback search logic

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class PaperSearchService {

    public List<Paper> searchPapers(String query) {
        // First, attempt to execute the full search
        List<Paper> results = performSearch(query);
        if (!results.isEmpty()) {
            return results;
        }

        // If the full query returns no results, extract keywords and fallback to a broader search
        List<String> keywords = extractKeywords(query);
        return performFallbackSearch(keywords);
    }

    private List<Paper> performSearch(String query) {
        // Implementation for searching papers using the full query
        // This method interacts with the database or API to find papers
        return new ArrayList<>(); // Placeholder for actual implementation
    }

    private List<String> extractKeywords(String query) {
        // Simplistic keyword extraction logic (can be improved)
        // Split the query into terms and filter out common words
        String[] terms = query.split(" ");
        List<String> keywords = new ArrayList<>();
        for (String term : terms) {
            if (!isCommonWord(term)) {
                keywords.add(term);
            }
        }
        return keywords;
    }

    private boolean isCommonWord(String term) {
        // Here you can implement logic to identify common words
        // For simplicity, we can consider words like "the", "is", "at" as common
        String[] commonWords = {"the", "is", "at", "which", "on", "and"};
        for (String common : commonWords) {
            if (common.equalsIgnoreCase(term)) {
                return true;
            }
        }
        return false;
    }

    private List<Paper> performFallbackSearch(List<String> keywords) {
        // Implementation for searching papers using the extracted keywords
        // This should broaden the search scope using keywords
        return new ArrayList<>(); // Placeholder for actual implementation
    }
}