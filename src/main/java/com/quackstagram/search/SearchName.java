package com.quackstagram.search;

public class SearchName extends Search {
    public SearchName(String info) {
        this.search = info;
    }

    public void found(String line) {
        String[] info = line.split(":");
        if (info[0].equals(this.search)) {
            this.ret.add(this.search);
        }

    }
}