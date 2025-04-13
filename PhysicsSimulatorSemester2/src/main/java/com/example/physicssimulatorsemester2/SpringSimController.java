package com.example.physicssimulatorsemester2;

import java.io.IOException;

public class SpringSimController {



    public void actionMainMenu(){
        try {
            HelloApplication.loadScene("MainMenu.fxml", "Physics Simulator");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }








}
