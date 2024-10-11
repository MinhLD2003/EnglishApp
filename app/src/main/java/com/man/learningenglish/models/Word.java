package com.man.learningenglish.models;

public class Word {
    private final String original;
    private final String shuffled;

    public Word(String original, String shuffled) {
        this.original = original;
        this.shuffled = shuffled;
    }

    public String getOriginal() {
        return original;
    }

    public String getShuffled() {
        return shuffled;
    }
}
