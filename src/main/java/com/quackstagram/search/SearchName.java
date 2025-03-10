package com.quackstagram.search;

// Searches for a name in the provided data
public class SearchName extends Search {
    public SearchName(String info) {
        this.search = info;
    }

    // Checks if the given line contains the search term
    public void found(String line) {
        String[] info = line.split(":");
        if (info[0].equals(this.search)) {
            this.ret.add(this.search);
        }

    }
}