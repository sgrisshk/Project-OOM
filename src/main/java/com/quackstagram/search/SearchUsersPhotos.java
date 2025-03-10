package com.quackstagram.search;

// Searches for user photos based on the provided information
public class SearchUsersPhotos extends Search {
    public SearchUsersPhotos(String info) {
        this.search = info;
    }

    // Processes a line to check if the username matches the search query
    public void found(String line) {
        // Extracts the username from the given line
        String[] info1 = line.split("Username: ");
        String[] info = info1[1].split(",");
        int infoLength = this.search.length();
        String temp = null;
        if (infoLength < info[0].length()) {
            temp = info[0].substring(0, infoLength);
        }

        // Compares the extracted username prefix with the search query
        if (temp.equals(this.search)) {
            // If a match is found, add the corresponding photo data to the result list
            String[] tmp = line.split(": ");
            String[] addToRet = tmp[1].split(",");
            this.ret.add(addToRet[0]);
        }

    }
}