package com.quackstagram.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.quackstagram.model.User;

public abstract class NavigateUI extends BaseUI {

    public NavigateUI(String title) {
        super(title);
    }

    protected String readUserNameFromFile() {
        String username = "";
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "users.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                username = line.split(":")[0].trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return username;
    }

    protected void openProfileUI() {
        this.dispose();
        User user = new User(readUserNameFromFile());
        InstagramProfileUI profileUI = new InstagramProfileUI(user);
        profileUI.setVisible(true);
    }
}

