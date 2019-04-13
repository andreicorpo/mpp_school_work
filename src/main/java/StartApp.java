import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import networking.server.IServer;
import networking.server.ServerRpcProxy;
import repository.RepositoryJdbcStaff;
import services.StaffServices;
import view.controllers.FXMLMainDocController;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class StartApp extends Application {

    private static int defaultChatPort = 55555;
    private static String defaultServer = "localhost";

    @Override
    public void start(Stage primaryStage) throws IOException {
        Properties clientProps = new Properties();
        try {
            clientProps.load(StartApp.class.getResourceAsStream("/chatserver.properties"));
            System.out.println("Client properties set. ");
            clientProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find chatclient.properties " + e);
            return;
        }
        String serverIP = clientProps.getProperty("chat.server.host", defaultServer);
        int serverPort = defaultChatPort;

        try {
            serverPort = Integer.parseInt(clientProps.getProperty("chat.server.port"));
        } catch (NumberFormatException ex) {
            System.err.println("Wrong port number " + ex.getMessage());
            System.out.println("Using default port: " + defaultChatPort);
        }
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);

        IServer server = new ServerRpcProxy(serverIP, serverPort);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/FXMLMainDoc.fxml"));
        Parent root = loader.load();
        FXMLMainDocController ctrl = loader.getController();
        ctrl.setServer(server);
        ctrl.initialize();
        Scene scene = new Scene(root, 600, 450);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(450);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
//        primaryStage.setOnCloseRequest(event -> {
//            ServerRpcProxy serverRpcProxy = (ServerRpcProxy) server;
//            serverRpcProxy.closeConnection();
//        });
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    static StaffServices getStaffServices() {
        Properties serverProps=new Properties();
        try {
            serverProps.load(new FileReader("sqlite_db.config"));
//            System.out.println("Properties set. ");
//            serverProps.list(System.out);
        } catch (IOException e) {
            System.out.println("Cannot find sqlite_db.config "+e);
        }
        RepositoryJdbcStaff repo = new RepositoryJdbcStaff(serverProps);
        return new StaffServices(repo);
    }
}