package anirudh.mathsketch;

import android.graphics.Path;

import java.util.ArrayList;

import static android.R.id.list;

/**
 * Created by user on 9/4/2017.
 */

public class StrokeGroup implements Comparable<StrokeGroup> {
    ArrayList<Path> pathset=new ArrayList<>();
    int color=0;
    long time=0;
    public StrokeGroup(ArrayList<Path> list, int color, long time){
        this.color=color;
        this.time=time;
        pathset.addAll(list);
    }
    public boolean combine(StrokeGroup other){
//        if(other.color!=color)
//            return false;
        if(Math.abs(other.time-time)<1000){
        pathset.addAll(other.pathset);
        time=Math.max(time,other.time);
        return true;}
        else
            return false;
    }
    public ArrayList<Path> getPathset(){
        return pathset;
    }
    public int getColor(){
        return color;
    }


    @Override
    public int compareTo(StrokeGroup other) {
        return (int)(this.time-other.time);
    }
}
