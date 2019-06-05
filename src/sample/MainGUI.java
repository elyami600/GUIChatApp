package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.DatagramPacket;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

//we extend stack pane just because we want
public class MainGUI extends StackPane {

    private Socket socket;
    //the stage points to the stage of my javafx app
    private Stage myPrimaryStage;
    //the scene is the maain gui element scene
    //provide width and height
    public Scene myScene;
    //the main pane will be a borderpane since we care about top left bottom and center position
    private BorderPane borderPane;
    private HBox topNav;
    private Button newChat;
    private Label ipLabel;
    private Label prtLabel;
    private ScrollPane scroll;
    private GridPane grid;
   //lets keep track of small chats
    public TreeMap<String,tabs> tab;

    //column attr
    private int row=0,col=0;
    public MainGUI(Socket mSocket,Stage ps){
        this.myPrimaryStage = ps;
        tab = new TreeMap<>();
        this.socket = mSocket;

        initChatWindow();
    }

    //setter
    public void setScene(Scene scene){
        this.myScene =  scene;
    }

    public void initChatWindow(){
        borderPane = new BorderPane();
        this.getStyleClass().add("MainBackground");

        //top header nav
        scroll = new ScrollPane();
        grid = new GridPane();
        grid.setPrefWidth(1297);
        grid.getStyleClass().add("MainBackground");
        grid.setHgap(150);
        grid.setVgap(50);

        topNav = new HBox();
        topNav.setAlignment(Pos.CENTER);
        topNav.setSpacing(10.0);
        topNav.getStyleClass().add("top");
        ipLabel = new Label("Your Ip :"+socket.getAddress().getHostAddress());
        ipLabel.getStyleClass().add("text");
        prtLabel = new Label("Your Port :"+socket.getPortNumber());
        prtLabel.getStyleClass().add("text");

        //the button to start a new chat
        newChat = new Button("New Chat");
        newChat.getStyleClass().add("green");
        newChat.setOnAction(event -> {
            //AskUser();
            //newWindow("123.234.22.1",45000,"");
            AskUser();
        });

        //add the children pane to the vertical box
        topNav.getChildren().addAll(ipLabel,prtLabel,newChat);

        //now set the navigation bar to the top of your border pane
        this.borderPane.setTop(topNav);
        this.borderPane.setAlignment(topNav, Pos.TOP_CENTER);
        //scroll attributes
        scroll.setContent(grid);
        this.borderPane.setCenter(scroll);
        this.borderPane.setAlignment(scroll,Pos.CENTER);


        this.getChildren().add(borderPane);
    }


    public void AskUser(){
         Stage secondStage = new Stage();
         BorderPane childRoot = new BorderPane();
         Scene newScene = new Scene(childRoot,300,100);
         secondStage.setScene(newScene);
         secondStage.show();
         //lets create the needed objects to get the new user
        Label ipL = new Label("IP : ");
        Label ptL = new Label("PORT : ");
        TextField ip = new TextField();
        TextField pt = new TextField();

        GridPane child = new GridPane();
        child.setPrefSize(300,100);
        child.add(ipL,0,0,1,1);
        child.add(ip,1,0,1,1);

        child.add(ptL,0,1,1,1);
        child.add(pt,1,1,1,1);

        Button add = new Button("Add");
        add.setOnAction(event -> {
                boolean validIp = !ip.getText().isEmpty()? true : false;
                boolean validPort = !pt.getText().isEmpty()? true : false;

                if(validIp && validPort){
                    newWindow(ip.getText().toString(),Integer.valueOf((Integer.parseInt(pt.getText()))), "");
                    secondStage.hide();
                }
        });

        child.add(add, 1,2, 1, 1);

        childRoot.setCenter(child);



    }

    public void newWindow(String ip, int port, String msg){
        System.out.println("col = " + col+" row = "+ row);
         if(col%1==0 && col > 0){
             col = 0;
             row++;
         }else{
             col++;
         }
        tabs t = new tabs(socket,this,ip,port);
         if(!msg.isEmpty()){
             t.getQueue().add(msg);
             t.refreshArea();
         }
        //lets keep track of this windows
        String key =  ip+port;
        tab.put(key,t);
        grid.add(t,col,row,1,1);
        //now lets add this tab to our center vertical frames
        scroll.setContent(grid);
    }


    //create window or populate chat
    public void CreateWindowOrPopulateChat(DatagramPacket inPacket){
        String ip =  inPacket.getAddress().getHostAddress();
        int port  =  inPacket.getPort();
        String msg =  new String(inPacket.getData());
        String  key =  ip+port;
        if(tab.containsKey(key)){
            tabs obj =  tab.get(key);
            obj.area.setText("");
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            obj.getQueue().add("From : "+ip+" msg "+msg.trim()+" time : "+ formatter.format(date));
            obj.refreshArea();
        }else{
            String data = new String(inPacket.getData());
            newWindow(ip,port,"From ip = "+ip+" Msg = "+data.trim());
        }

    }
}
