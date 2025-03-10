package com.quackstagram.search;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

// Abstract base class for search functionality
public abstract class Search {
    // Stores the search query
    protected String search;
    // Stores search results
    public ArrayList<String> ret = new ArrayList();

    // Default constructor
    public Search() {
    }

    // Reads a file and processes each line through found() method
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

    // Checks if searchText already exists in ret list
    public boolean checkToAdd(String searchText) {
        for(int i = 0; i < this.ret.size(); ++i) {
            if (((String)this.ret.get(i)).equals(searchText)) {
                return true;
            }
        }

        return false;
    }

    // Abstract method to handle found search results
    public abstract void found(String var1);
}