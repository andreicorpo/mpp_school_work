package view.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import repository.RepositoryJdbcParticipant;
import repository.RepositoryJdbcRace;
import repository.RepositoryJdbcStaff;
import services.ParticipantServices;
import services.RaceServices;
import services.StaffServices;
import utils.AlertError;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class FXMLStartDocController {

    @FXML
    // The reference of inputText will be injected by the FXML loader
    private TextField userTF;

    // The reference of outputText will be injected by the FXML loader
    @FXML
    private TextField passTF;

    @FXML
    private Button loginBtn;

    private StaffServices services;

    public FXMLStartDocController() {
    }

    public void setServices(StaffServices services) {
        this.services = services;
    }

    public void login() throws IOException {
        String username = userTF.getText();
        String password = passTF.getText();
        userTF.clear();
        passTF.clear();
        boolean userExists = services.checkStaff(username, password);
        if (userExists) {
            try {
                openMainWindow();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            AlertError alertErr = new AlertError();
            alertErr.alertError("Login Error", "User not found", "User not found, try again or GTFO");
        }
    }

    private void openMainWindow() throws IOException, ClassNotFoundException {
        Class cls = Class.forName("StartApp");
        FXMLLoader loader = new FXMLLoader(cls.getResource("fxml/FXMLMainDoc.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        FXMLMainDocController ctrl = loader.getController();
        ctrl.setServices(getRacesServices(), getParticipantServices());
        ctrl.initialize();
        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Welcome");
        stage.setScene(scene);
        stage.show();
    }

    private static RaceServices getRacesServices() {
        Properties serverProps=new Properties();
        try {
            serverProps.load(new FileReader("sqlite_db.config"));
//            System.out.println("Properties set. ");
//            serverProps.list(System.out);
        } catch (IOException e) {
            System.out.println("Cannot find sqlite_db.config "+e);
        }
        RepositoryJdbcRace repo = new RepositoryJdbcRace(serverProps);
        return new RaceServices(repo);
    }

    private static ParticipantServices getParticipantServices() {
        Properties serverProps=new Properties();
        try {
            serverProps.load(new FileReader("sqlite_db.config"));
//            System.out.println("Properties set. ");
//            serverProps.list(System.out);
        } catch (IOException e) {
            System.out.println("Cannot find sqlite_db.config "+e);
        }
        RepositoryJdbcParticipant repo = new RepositoryJdbcParticipant(serverProps);
        return new ParticipantServices(repo);
    }
}
