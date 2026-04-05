import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PaperSearchService {

    // Assuming this is the existing method for searching papers
    public List<Paper> searchPapers(String query) {
        // First, try to search with the full query
        List<Paper> results = performSearch(query);
        if (results.isEmpty()) {
            // If no results found, break the query into keywords
            List<String> keywords = extractKeywords(query);
            Set<Paper> allResults = new HashSet<>();
            for (String keyword : keywords) {
                List<Paper> keywordResults = performSearch(keyword);
                allResults.addAll(keywordResults);
            }
            results.addAll(allResults);
        }
        return results;
    }

    private List<String> extractKeywords(String query) {
        // Logic to extract meaningful terms and remove stop words
        Set<String> stopWords = new HashSet<>(Arrays.asList("a", "the", "is", "in", "at", "of", "and", "to", "for", "with"));
        List<String> keywords = new ArrayList<>();
        String[] words = query.split("\s+");
        for (String word : words) {
            word = word.toLowerCase();
            if (!stopWords.contains(word)) {
                keywords.add(word);
            }
        }
        return keywords;
    }

    private List<Paper> performSearch(String query) {
        // Implementation of the actual search logic
        // This is a placeholder for the actual search operation
        return new ArrayList<>(); // Should return search results based on the query
    }
}