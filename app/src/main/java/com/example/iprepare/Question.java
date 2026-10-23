package com.example.iprepare;

import java.util.Map; // for choices
import java.util.HashMap;

public class Question {
    private String text;
    private Map<String, String> choices;
    private String answer;
    private int order;
    private int points;

    // 🔹 Empty constructor required for Firestore deserialization
    public Question() {}

    public Question(String text, Map<String, String> choices, String answer, int order, int points) {
        this.text = text;
        this.choices = choices;
        this.answer = answer;
        this.order = order;
        this.points = points;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, String> getChoices() {
        if (choices == null) return new HashMap<>();
        return choices;
    }

    public void setChoices(Map<String, String> choices) {
        this.choices = choices;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
