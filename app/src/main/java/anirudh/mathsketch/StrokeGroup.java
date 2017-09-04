package anirudh.mathsketch;

import android.graphics.Path;

import java.util.ArrayList;

import static android.R.id.list;

/**
 * Created by user on 9/4/2017.
 */

public class StrokeGroup {
    ArrayList<Path> pathset=new ArrayList<>();
    int color=0;
    public StrokeGroup(ArrayList<Path> list, int color){
        this.color=color;
        pathset.addAll(list);
    }
    public ArrayList<Path> getPathset(){
        return pathset;
    }
    public int getColor(){
        return color;
    }


}
