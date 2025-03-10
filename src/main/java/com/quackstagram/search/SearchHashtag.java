package com.quackstagram.search;

// Class for searching hashtags within user bios
public class SearchHashtag extends Search {
    // Constructor initializes the search term
    public SearchHashtag(String info) {
        this.search = info;
    }

    // Method to check if a hashtag exists in a user's bio
    public void found(String line) {
        // Extract the bio section from the input line
        String[] hashInfo1 = line.split("Bio: ");
        String[] hashinfo2 = hashInfo1[1].split(",");
        String[] bioInfo = hashinfo2[0].split(" ");
        int length = this.search.length();

        // Loop through each word in the bio to find a matching hashtag
        for(int i = 0; i < bioInfo.length; ++i) {
            String tempWord = bioInfo[i];
            // Ensure the word length does not exceed the search term length
            if (tempWord.length() > length) {
                tempWord = tempWord.substring(0, length);
            }

            // Compare the truncated word with the search term
            if (tempWord.equals(this.search)) {
                String[] tmp = line.split(": ");
                String[] addToRet = tmp[1].split(",");
                // If a match is found, extract the associated username and store it
                System.out.println(tempWord + " " + addToRet[0]);
                this.ret.add(addToRet[0]);
            }
        }

    }
}