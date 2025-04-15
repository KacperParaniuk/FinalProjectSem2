package com.example.physicssimulatorsemester2;

import java.io.IOException;

public class PendulumSimController {
    private boolean isEducationalMode = false;



    public void setEducationalMode(boolean val){
        isEducationalMode = val;
    }


    public void actionMainMenu(){
        try {
            HelloApplication.loadScene("MainMenu.fxml", "Physics Simulator", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
