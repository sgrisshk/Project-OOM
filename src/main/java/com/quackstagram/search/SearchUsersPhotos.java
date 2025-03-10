package com.quackstagram.search;

public class SearchUsersPhotos extends Search {
    public SearchUsersPhotos(String info) {
        this.search = info;
    }

    public void found(String line) {
        if (!line.contains("Username: ")) {
            return;
        }
        
        String[] info1 = line.split("Username: ");
        if (info1.length < 2) {
            return;
        }
        
        String[] info = info1[1].split(",");
        if (info.length == 0) {
            return;
        }
        
        String username = info[0].trim();
        if (username.toLowerCase().contains(this.search.toLowerCase())) {
            String[] tmp = line.split(": ");
            if (tmp.length > 1) {
                String[] addToRet = tmp[1].split(",");
                if (addToRet.length > 0) {
                    this.ret.add(addToRet[0]);
                }
            }
        }
    }
}