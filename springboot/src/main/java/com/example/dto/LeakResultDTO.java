package com.example.dto;

public class LeakResultDTO {
    private String label;
    private double score;
    private String imageUrl;

    public LeakResultDTO(String label, double score, String imageUrl) {
        this.label = label;
        this.score = score;
        this.imageUrl = imageUrl;
    }

    // 原来的构造函数
    public LeakResultDTO(String label, double score) {
        this(label, score, null);  // 默认 imageUrl 为 null
    }

    // getters 和 setters
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
