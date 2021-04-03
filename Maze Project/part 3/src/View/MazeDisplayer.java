package View;

import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.nio.file.Paths;



public class MazeDisplayer extends Canvas {

    private int[][] maze;
    private int characterPositionRow;
    private int characterPositionColumn;
    public static MediaPlayer mediaPlayer;
    public static boolean Sound;

    public void setMaze(int[][] maze) {
        this.maze = maze;
        this.characterPositionRow = 1;
        this.characterPositionColumn = 1;
        this.Sound = false;

        redraw();
    }

    public static void playMusic(int musicSwitch) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.pause();
            Sound = false;
        }
        if (musicSwitch == 1) {
            Sound = true;
            Media player = new Media(Paths.get("resources/Music/game.mp3").toUri().toString());
            mediaPlayer = new MediaPlayer(player);
            mediaPlayer.play();
        }
        if (musicSwitch == 2) {
            mediaPlayer.stop();
            mediaPlayer.pause();
            Sound = false;
        }
    }

    public int[][] getMaze()
    {
        return maze;
    }

    public void setCharacterPosition(int row, int column) {
        characterPositionRow = row;
        characterPositionColumn = column;
        redraw();
    }



    public void redraw() {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.length;
            double cellWidth = canvasWidth / maze[0].length;

            try {
                Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
                Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));
                Image wall1 = new Image(new FileInputStream(ImageFileNamewall1.get()));
                Image Path = new Image(new FileInputStream(ImageFileNamePath.get()));
                Image himiko = new Image(new FileInputStream(ImageFileNamehimiko.get()));

                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());

                //Draw Maze
                for (int i = 0; i < maze.length; i++) {
                    for (int j = 0; j < maze[i].length; j++) {
                        if (maze[i][j] == 1) {
                            gc.drawImage( wall1, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        }
                        if (maze[i][j] == 0) {
                            gc.drawImage(wallImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        }
                        if (maze[i][j] == 5) {
                            gc.drawImage(Path, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        }
                        if (maze[i][j] == 4) {
                            gc.drawImage(himiko, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        }
                    }
                }

                gc.drawImage(characterImage, characterPositionColumn * cellWidth, characterPositionRow * cellHeight, cellWidth, cellHeight);
            } catch (FileNotFoundException e) {
            }
        }
    }

    //region Properties
    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();
    private StringProperty ImageFileNameEnd = new SimpleStringProperty();
    private StringProperty ImageFileNamePath = new SimpleStringProperty();
    private StringProperty ImageFileNamewall1 = new SimpleStringProperty();
   private StringProperty ImageFileNamehimiko = new SimpleStringProperty();

    public String getImageFileNamewall1() {
        return ImageFileNamewall1.get();
    }

    public String getImageFileNameWall() {
        return ImageFileNameWall.get();
    }
    public String getImageFileNamehimiko() {
        return ImageFileNamehimiko.get();
    }

    public void setImageFileNamehimiko(String imageFileNamehimiko) {
        this.ImageFileNamehimiko.set(imageFileNamehimiko);
    }
    public void setImageFileNamewall1(String imageFileNamewall1) {
        this.ImageFileNamewall1.set(imageFileNamewall1);
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }

    public void setImageFileNamePath(String imageFileNamePath) {
        this.ImageFileNamePath.set(imageFileNamePath);
    }

    public String getImageFileNamePath() {
        return ImageFileNamePath.get();
    }

    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }

    public void setImageFileNameCharacter(String imageFileNameCharacter) {
        this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }

    public String getImageFileNameEnd() {
        return ImageFileNameEnd.get();
    }

    public void setImageFileNameEnd(String imageFileNameEnd) {
        this.ImageFileNameEnd.set(imageFileNameEnd);
    }

}
