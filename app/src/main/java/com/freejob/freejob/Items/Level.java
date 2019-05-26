package com.freejob.freejob.Items;

import java.util.ArrayList;
import java.util.List;

public class Level {
    private int level;
    private List<String> level_codeNames = new ArrayList<>();

    public Level() {

    }


    public void IncreaseLevel(){
        level++;
    }
    public int Level(){
        return level;
    }
    public void GenerateCodeNames(){
        level_codeNames.add("Nenhum");
        level_codeNames.add("Iniciante");
        level_codeNames.add("Nenhum");
    }
}
