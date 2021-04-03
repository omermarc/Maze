package View;

import ViewModel.ViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Paths;

public class MainScreenController {

    @FXML
    private ViewModel viewModel;
    private Scene mainScene;
    private Stage mainStage;
    private Scene NextScene;
    public static MediaPlayer mediaPlayer;
    public static boolean Sound = false;
    private MyViewController myV;

    public void initialize(Scene nextScene, Stage mainStage, Scene mainScene) {
        this.viewModel = viewModel;
        this.mainStage = mainStage;
        this.NextScene = nextScene;

        String openingMusic = "";
        openingMusic = "resources/Music/Open.mp3";
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.pause();
            Sound = false;
        }
        Sound = true;
        Media player = new Media(Paths.get(openingMusic).toUri().toString());
        mediaPlayer = new MediaPlayer(player);
        mediaPlayer.play();

    }

    public static void playMusic(int musicSwitch) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.pause();
            Sound = false;
        }
        if (musicSwitch == 1) {
            Sound = true;
            Media player = new Media(Paths.get("resources/Music/end.mp3").toUri().toString());
            mediaPlayer = new MediaPlayer(player);
            mediaPlayer.play();
        }
        if (musicSwitch == 2) {
            mediaPlayer.stop();
            mediaPlayer.pause();
            Sound = false;
        }
    }
    public void pushedPlayGame(ActionEvent event) throws IOException {
        mediaPlayer.stop();
        mediaPlayer.pause();
        Sound = false;
        if (mediaPlayer != null) {
            this.playMusic(2);
        }
        mainStage.setScene(NextScene);
    }
}
