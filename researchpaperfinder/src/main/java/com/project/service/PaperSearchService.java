package com.project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.project.model.Paper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.MediaType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Service
public class PaperSearchService {

    private static final Logger log = LoggerFactory.getLogger(PaperSearchService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // 🔥 API KEY (yaha paste kiya hai)
    private static final String API_KEY = "IxpZHVlr051UP1mI3Bc6R9Nz9BjIGs9v41xnnt2J";

    // Cache
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private static final long CACHE_TTL = TimeUnit.SECONDS.toMillis(30);

    static class CacheEntry {
        final List<Paper> papers;
        final long timestamp;

        CacheEntry(List<Paper> papers) {
            this.papers = papers;
            this.timestamp = System.currentTimeMillis();
        }
    }

    @Autowired
    public PaperSearchService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    private static final String API_URL = "https://api.semanticscholar.org/graph/v1/paper/search";

    public List<Paper> searchPapers(String query, int limit) {
        // Don't lowercase - API handles casing better for phrases
        query = query.trim();

        // ✅ Cache check
        CacheEntry cached = cache.get(query);
        if (cached != null && (System.currentTimeMillis() - cached.timestamp) < CACHE_TTL) {
            log.info("Cache hit for '{}'", query);
            return cached.papers;
        }

        String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("query", query)
                .queryParam("limit", Math.min(limit, 20))
                .queryParam("fields", "title,authors,venue,year,abstract,url")
                .toUriString();

        log.info("Calling Semantic Scholar API for query '{}': {}", query, url);
        log.warn("Using API key: {}", API_KEY.substring(0, 8) + "...");  // Don't log full key

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", API_KEY);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("User-Agent", "ResearchPaperFinder/1.0");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity;
        String responseBody;
        
        try {
            responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            responseBody = responseEntity.getBody();

            log.info("API Response Status: {}", responseEntity.getStatusCode());
            log.info("API Response Body Preview: {}", 
                responseBody != null && responseBody.length() > 500 ? responseBody.substring(0, 500) + "..." : responseBody);

            List<Paper> papers = parseResponse(responseBody);
            
            if (papers.isEmpty()) {
                log.warn("Empty results for query '{}'. Check API response above.", query);
            }

            // Cache result even if empty (avoid repeated fails)
            cache.put(query, new CacheEntry(papers));

            log.info("Returning {} papers for '{}'", papers.size(), query);
            return papers;

        } catch (Exception e) {
            log.error("API call failed for '{}': {}", query, e.getMessage(), e);
            return List.of();
        }
    }

    private List<Paper> parseResponse(String responseBody) {
        List<Paper> papers = new ArrayList<>();

        try {
            if (responseBody == null || responseBody.isEmpty()) {
                log.warn("Empty response body");
                return papers;
            }

            JsonNode root = objectMapper.readTree(responseBody);
            
            // Log full structure for debug
            log.debug("API JSON root keys: {}", root.fieldNames());
            
            JsonNode data = root.path("data");
            int total = root.path("total").asInt(0);
            log.info("API reports total={} papers", total);

            if (data.isArray() && data.size() > 0) {
                IntStream.range(0, Math.min(20, data.size()))
                    .forEach(i -> {
                        JsonNode node = data.get(i);
                        Paper p = new Paper();

                        p.setPaperId(node.path("paperId").asText(""));
                        p.setTitle(node.path("title").asText("No Title"));

                        JsonNode authorsNode = node.path("authors");
                        if (authorsNode.isArray() && authorsNode.size() > 0) {
                            String[] authors = new String[Math.min(5, authorsNode.size())];
                            for (int j = 0; j < authors.length; j++) {
                                authors[j] = authorsNode.get(j).path("name").asText("Unknown");
                            }
                            p.setAuthors(authors);
                        } else {
                            p.setAuthors(new String[]{"N/A"});
                        }

                        p.setVenue(node.path("venue").asText("N/A"));
                        p.setYear(node.path("year").asInt(0));
                        p.setAbstractText(node.path("abstract").asText("No abstract available."));
                        p.setUrl(node.path("url").asText("https://www.semanticscholar.org" + node.path("paperId").asText("")));
                        p.setScore((float) node.path("score").asDouble(1.0));

                        papers.add(p);
                    });
            } else {
                log.warn("No 'data' array or empty. data.size={}", data.size());
            }

        } catch (Exception e) {
            log.error("JSON parsing failed: {}", e.getMessage());
            log.debug("Failed response body: {}", responseBody);
        }

        return papers;
    }
}