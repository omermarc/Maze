package ViewModel;

import Model.IModel;
import View.MazeDisplayer;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;


public class ViewModel extends Observable implements Observer {

    private IModel model;

    public ViewModel(IModel model){
        this.model = model;
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (observable==model){
            setChanged();
            notifyObservers();
        }
    }

    public void generateMaze(int width, int height){
        model.generateMaze(width, height);
    }


    public Solution SolveMaze(){
        return model.SolveMaze();
    }
    public void moveCharacter(KeyCode movement){
        model.moveCharacter(movement);
    }

    public int[][] getMaze() {
        return model.getMaze();
    }

    public boolean missionaccomplished(){
        return model.missionaccomplished();
    }

    public void pushedSave(File SaveMe) throws IOException {
        model.pushedSave(SaveMe);
    }

    public void pushedLoad(File LoadMe) throws IOException, ClassNotFoundException {
        model.pushedLoad(LoadMe);
    }

    public int getCharacterPositionRow() {
        return model.getCharacterPositionRow();
    }

    public int getCharacterPositionColumn() {
        return model.getCharacterPositionColumn();
    }

}
