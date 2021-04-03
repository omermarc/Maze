package Model;

import Server.IServerStrategy;
import View.MazeDisplayer;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.io.IOException;


public interface IModel {
    void generateMaze(int width, int height);
    Solution SolveMaze();
    int[][] getMaze();
    void moveCharacter(KeyCode movement);
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
    boolean missionaccomplished();
    public void pushedSave(File file)throws IOException;
    public void pushedLoad(File file) throws IOException, ClassNotFoundException;
    void close();


}
