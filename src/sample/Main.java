package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.concurrent.TimeUnit;

public class Main extends Application {
    private Socket mySocket;

    @Override
    public void start(Stage primaryStage) throws Exception{

        mySocket = new Socket(64000, Socket.SocketType.NoBroadcast);

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            System.exit(-1);
        }

        System.out.println("Welcome Yamil chat");
        MainGUI gui = new MainGUI(mySocket,primaryStage);
        mySocket.setAppObject(gui);
        Scene scene= new Scene(gui,1300,800);
        scene.getStylesheets().add("css/main.css");
        primaryStage.setScene(scene);
        gui.setScene(scene);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
