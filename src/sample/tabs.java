package sample;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class tabs extends VBox {
    private Socket socket;
    private Label Ip;
    private Label Port;

    private InetAddress inetIp;
    private int port;
    public TextArea area;
    private ConcurrentLinkedQueue<String> queue;
    public  TextField input;
    private Button sendChat;
    private MainGUI main;


    public tabs(Socket socket,MainGUI m,String ip, int port){
        this.socket =  socket;
        this.main = m;
        this.port  = port;
        try {
            this.inetIp = InetAddress.getByName(ip);
        }catch (UnknownHostException e){
            e.printStackTrace();
        }
        //init our queue
        queue = new ConcurrentLinkedQueue<>();
        this.getStyleClass().add("smallchat");
        Ip = new Label("Ip : "+ip);
        Ip.getStyleClass().add("text");
        Port = new Label("Port : " + port);
        Port.getStylesheets().add("text");

        area = new TextArea();
        area.getStyleClass().add("textbox");

        HBox sm = new HBox();
        sm.setAlignment(Pos.CENTER);
        input = new TextField();
        sendChat = new Button("Send");
        sendChat.getStyleClass().add("sendB");
        sm.getChildren().addAll(input,sendChat);
        //sction event to get the text input on the small window
        sendChat.setOnAction(event -> {
               String msg = input.getText().toString();
               boolean valid = !msg.isEmpty()? true : false;
               if(valid) {
                   input.setText("");
                   queue.add(msg);
                   //send the packet
                   socket.send(msg,this.inetIp,this.port);
                   refreshArea();
               }

        });

        this.getChildren().addAll(Ip,Port,area,sm);

    }


    public void AddMsgToQueue(String str){
        queue.add(str);
    }

    public ConcurrentLinkedQueue<String> getQueue(){
        return queue;
    }

    public void refreshArea(){
        area.setText("");
        Iterator it =  queue.iterator();
        while(it.hasNext()){
            String val =  (String)it.next();
            area.appendText(val);
            area.appendText("\n");
        }

    }

}
