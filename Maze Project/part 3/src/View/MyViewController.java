package View;

import ViewModel.ViewModel;
import algorithms.search.Solution;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Observable;
import java.util.Observer;


public class MyViewController<scroll>  implements Observer, IView {
    //Controls
    public MazeDisplayer mazeDisplayer;
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Label Label_rowsNum;
    public javafx.scene.control.Label Label_columnsNum;
    public javafx.scene.control.Button Button_generateMaze;
    public javafx.scene.control.Button Button_solveMaze;

    public StringProperty characterPositionRow = new SimpleStringProperty("1");
    public StringProperty characterPositionColumn = new SimpleStringProperty("1");

    @FXML
    private ViewModel viewModel;
    private Scene mainScene;
    private Stage mainStage;
    private Scene WinScene;
    MainScreenController EndController;
    private Timeline timeline = new Timeline(60);

    public void initialize(ViewModel viewModel, Stage mainStage, Scene mainScene, Scene winScene, MainScreenController WinController) {
        this.viewModel = viewModel;
        this.mainScene = mainScene;
        this.mainStage = mainStage;
        this.WinScene = winScene;
        this.EndController = WinController;
        bindProperties();
        setResizeEvent();
    }

    private void bindProperties() {
        Label_rowsNum.textProperty().bind(this.characterPositionRow);
        Label_columnsNum.textProperty().bind(this.characterPositionColumn);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == viewModel) {
            displayMaze(viewModel.getMaze());
            Button_generateMaze.setDisable(false);
        }
    }

    @Override
    public void displayMaze(int[][] maze) {
        mazeDisplayer.setMaze(maze);
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
        Button_solveMaze.setDisable(false);
    }

    boolean numberOrNot(String input)
    {
        try
        { Integer.parseInt(input); }
        catch(NumberFormatException ex)
        { return false; }
        return true;
    }

    public void generateMaze() {
        if(numberOrNot(txtfld_rowsNum.getText())==false||numberOrNot(txtfld_columnsNum.getText())==false){
            String AlertMessage = "Please type a number!";
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(AlertMessage);
            alert.show();
        }
        else {
            int heigth = Integer.valueOf(txtfld_rowsNum.getText());
            int width = Integer.valueOf(txtfld_columnsNum.getText());
            if (heigth <= 0 || width <= 0) {
                String AlertMessage = "Maze size is not good !";
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(AlertMessage);
                alert.show();
            } else {
                Button_generateMaze.setDisable(true);
                Button_solveMaze.setDisable(true);
                viewModel.generateMaze(width, heigth);
                MazeDisplayer.playMusic(1);
            }
        }
    }

    public void SolveMaze(ActionEvent actionEvent) {
        Solution MazeSolution = viewModel.SolveMaze();
        mazeDisplayer.redraw();
    }


    public void KeyPressed(KeyEvent keyEvent) {
        viewModel.moveCharacter(keyEvent.getCode());
        keyEvent.consume();
        missionaccomplished();

    }

    public void missionaccomplished(){
        if (viewModel.missionaccomplished()) {
            MazeDisplayer.playMusic(2);
            EndController.playMusic(1);
            mainStage.setScene(WinScene);
        }

    }

    public void setResizeEvent() {
        this.mainScene.widthProperty().addListener((observable, oldValue, newValue) -> {
        });

        this.mainScene.heightProperty().addListener((observable, oldValue, newValue) -> {
        });
    }

    public void About(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("About");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
            Scene scene = new Scene(root, 400, 350);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //lock the window
            stage.show();
        } catch (Exception e) {
        }
    }

    public void Help(ActionEvent actionEvent) {
        Pane pane = new Pane();
        Stage newStage = new Stage();
        String path = "resources/Images/help.jpg";
        Image help = new Image(Paths.get(path).toUri().toString());
        pane.getChildren().add(new ImageView(help));
        Scene scene = new Scene(pane);
        newStage.setScene(scene);
        newStage.show();
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        this.mazeDisplayer.requestFocus();
    }

    public void Zoom(ScrollEvent event) {
        double m_zoom;
        if (event.isControlDown()) {
            m_zoom = 1.5;
            if (event.getDeltaY() > 0) {
                m_zoom = 1.1*m_zoom;

            } else if (event.getDeltaY() < 0) {
                 m_zoom = 1.1/ m_zoom;
            }
            zoom(mazeDisplayer, m_zoom, event.getSceneX(), event.getSceneY());
            mazeDisplayer.setScaleX(mazeDisplayer.getScaleX() * m_zoom);
            mazeDisplayer.setScaleY(mazeDisplayer.getScaleY() * m_zoom);
            event.consume();// event handling from the root
        }
    }

    //https://stackoverflow.com/questions/29506156/javafx-8-zooming-relative-to-mouse-pointer
    public void zoom(Node node, double factor, double x, double y) {
        // determine scale
        double oldScale = node.getScaleX();
        double scale = oldScale * factor;
        double f = (scale / oldScale) - 1;
        // determine offset that we will have to move the node
        Bounds bounds = node.localToScene(node.getBoundsInLocal());
        double dx = (x - (bounds.getWidth() / 2 + bounds.getMinX()));
        double dy = (y - (bounds.getHeight() / 2 + bounds.getMinY()));
        // timeline that scales and moves the node
        timeline.getKeyFrames().clear();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(100), new KeyValue(node.translateXProperty(), node.getTranslateX() - f * dx)),
                new KeyFrame(Duration.millis(100), new KeyValue(node.translateYProperty(), node.getTranslateY() - f * dy)),
                new KeyFrame(Duration.millis(100), new KeyValue(node.scaleXProperty(), scale)),
                new KeyFrame(Duration.millis(100), new KeyValue(node.scaleYProperty(), scale))
        );
        timeline.play();
    }
    public void showAbout() throws Exception {
        Pane tempPane = new Pane();
        Stage TempStage = new Stage();
        String PathToImage = "resources/Images/ins.png";
        Image help = new Image(Paths.get(PathToImage).toUri().toString());
        tempPane.getChildren().add(new ImageView(help));
        Scene scene = new Scene(tempPane);
        TempStage.setScene(scene);
        TempStage.show();

    }
    public void showProperties() throws Exception {
        Pane tempPane = new Pane();
        Stage TempStage = new Stage();
        String PathToImage = "resources/Images/proprtis1.png";
        Image help = new Image(Paths.get(PathToImage).toUri().toString());
        tempPane.getChildren().add(new ImageView(help));
        Scene scene = new Scene(tempPane);
        TempStage.setScene(scene);
        TempStage.show();
    }

    public void pushedPlayGame(ActionEvent event) throws IOException {
        Parent gamePlay = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));
        Scene gameP = new Scene(gamePlay);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(gameP);
        window.show();
    }

    public void pushedSave() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Mazes", "*.tomMaze"));
        fileChooser.setInitialFileName("Tomb Raider");
        File SaveMe = fileChooser.showSaveDialog(mainStage);
        if (SaveMe != null) {
            viewModel.pushedSave(SaveMe);
        }
    }

    public void pushedLoad()throws IOException, ClassNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Maze", "*.tomMaze"));
        File LoadMe = fileChooser.showOpenDialog(mainStage);
        if (LoadMe != null) {
            viewModel.pushedLoad(LoadMe);
        } else {
            System.out.println("Error trying to load");
        }
        mazeDisplayer.setMaze(viewModel.getMaze());
        mazeDisplayer.setCharacterPosition(viewModel.getCharacterPositionRow(), viewModel.getCharacterPositionColumn());
        displayMaze(viewModel.getMaze());
    }

}