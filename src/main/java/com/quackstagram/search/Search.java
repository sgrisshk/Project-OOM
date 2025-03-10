package com.quackstagram.search;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public abstract class Search {
    protected String search;
    public ArrayList<String> ret = new ArrayList();

    public Search() {
    }

    public void findSearchText(String searchText, String file) {
        try {
            String line;
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                while((line = reader.readLine()) != null) {
                    this.found(line);
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkToAdd(String searchText) {
        for(int i = 0; i < this.ret.size(); ++i) {
            if (((String)this.ret.get(i)).equals(searchText)) {
                return true;
            }
        }

        return false;
    }

    public abstract void found(String var1);
}