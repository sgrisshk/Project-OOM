package com.quackstagram.search;

public class SearchUsersPhotos extends Search {
    public SearchUsersPhotos(String info) {
        this.search = info;
    }

    public void found(String line) {
        String[] info1 = line.split("Username: ");
        String[] info = info1[1].split(",");
        int infoLength = this.search.length();
        String temp = null;
        if (infoLength < info[0].length()) {
            temp = info[0].substring(0, infoLength);
        }

        if (temp.equals(this.search)) {
            String[] tmp = line.split(": ");
            String[] addToRet = tmp[1].split(",");
            this.ret.add(addToRet[0]);
        }

    }
}