package com.quackstagram.search;

public class SearchHashtag extends Search {
    public SearchHashtag(String info) {
        this.search = info;
    }

    public void found(String line) {
        String[] hashInfo1 = line.split("Bio: ");
        String[] hashinfo2 = hashInfo1[1].split(",");
        String[] bioInfo = hashinfo2[0].split(" ");
        int length = this.search.length();

        for(int i = 0; i < bioInfo.length; ++i) {
            String tempWord = bioInfo[i];
            if (tempWord.length() > length) {
                tempWord = tempWord.substring(0, length);
            }

            if (tempWord.equals(this.search)) {
                String[] tmp = line.split(": ");
                String[] addToRet = tmp[1].split(",");
                System.out.println(tempWord + " " + addToRet[0]);
                this.ret.add(addToRet[0]);
            }
        }

    }
}