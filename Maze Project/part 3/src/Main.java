
import Model.*;
import View.MyViewController;
import View.MainScreenController;
import ViewModel.ViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        MyModel model = new MyModel();
        ViewModel viewModel = new ViewModel(model);
        model.addObserver(viewModel);
        primaryStage.setTitle("Tomb Raider-HIMIKO");
        primaryStage.setWidth(1100);
        primaryStage.setHeight(695);

        FXMLLoader FXMenu = new FXMLLoader(getClass().getResource("View/mainScreen.fxml"));
        FXMLLoader FXGame = new FXMLLoader(getClass().getResource("View/MyView.fxml"));
        FXMLLoader FXEnd = new FXMLLoader(getClass().getResource("View/EndMenu.fxml"));
        //load fxml
        Parent PMenu = FXMenu.load();
        Parent PPlaying = FXGame.load();
        Parent PWinning = FXEnd.load();

        Scene scene_Menu = new Scene(PMenu, 1100   , 695);
        Scene scene_Game = new Scene(PPlaying, 1100   , 695);
        Scene scene_End = new Scene(PWinning, 1100   , 695);

        scene_Menu.getStylesheets().add("View/MainScreen.css");
        scene_Game.getStylesheets().add("View/View.css");
        scene_End.getStylesheets().add("View/EndMenu.css");

        MainScreenController MenuController = FXMenu.getController();
        MyViewController GamegController = FXGame.getController();
        MainScreenController EndController = FXEnd.getController();

        MenuController.initialize(scene_Game,primaryStage,scene_Menu);
        GamegController.initialize(viewModel,primaryStage,scene_Game, scene_End,EndController);
        EndController.initialize(scene_Game,primaryStage,scene_End);
        //addObserver
        viewModel.addObserver(GamegController);
        setStageCloseEvent(primaryStage, model);

        primaryStage.setScene(scene_Menu);

        primaryStage.show();
    }


    private void setStageCloseEvent(Stage primaryStage, MyModel model) {
        primaryStage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to exit?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                model.close();
            } else {
                event.consume();
            }
        });
    }


}
