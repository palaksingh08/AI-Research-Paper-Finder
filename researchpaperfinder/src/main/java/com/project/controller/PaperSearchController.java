package com.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.project.model.Paper;
import com.project.service.PaperSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/papers")
@CrossOrigin(origins = "*")
public class PaperSearchController {

    private static final Logger log = LoggerFactory.getLogger(PaperSearchController.class);

    @Autowired
    private PaperSearchService paperSearchService;

    @GetMapping("/search")
    public Map<String, Object> searchPapers(@RequestParam String query, @RequestParam(defaultValue = "10") int limit) {
        log.info("Search request: query='{}', limit={}", query, limit);
        List<Paper> papers = paperSearchService.searchPapers(query, limit);
        log.info("Returning {} papers for '{}'", papers.size(), query);
        
        Map<String, Object> response = new HashMap<>();
        response.put("papers", papers);
        response.put("count", papers.size());
        response.put("query", query);
        return response;
    }
}
