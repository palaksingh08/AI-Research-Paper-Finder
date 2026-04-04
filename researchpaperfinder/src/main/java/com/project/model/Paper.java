package com.project.model;

public class Paper {
    private String paperId;
    private String title;
    private String[] authors;
    private String venue;
    private int year;
    private String abstractText;
    private String url;
    private float score; // For AI ranking

    // Default constructor
    public Paper() {}

    // Constructor
    public Paper(String paperId, String title, String[] authors, String venue, int year, 
                 String abstractText, String url) {
        this.paperId = paperId;
        this.title = title;
        this.authors = authors;
        this.venue = venue;
        this.year = year;
        this.abstractText = abstractText;
        this.url = url;
    }

    // Getters and Setters
    public String getPaperId() { return paperId; }
    public void setPaperId(String paperId) { this.paperId = paperId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String[] getAuthors() { return authors; }
    public void setAuthors(String[] authors) { this.authors = authors; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getAbstractText() { return abstractText; }
    public void setAbstractText(String abstractText) { this.abstractText = abstractText; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public float getScore() { return score; }
    public void setScore(float score) { this.score = score; }

    @Override
    public String toString() {
        return "Paper{" +
                "title='" + title + '\'' +
                ", authors=" + java.util.Arrays.toString(authors) +
                ", venue='" + venue + '\'' +
                ", year=" + year +
                '}';
    }
}

