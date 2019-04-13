package view.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.staff.Staff;
import networking.server.IServer;
import services.StaffServices;
import utils.AlertError;

import java.io.IOException;

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

    private IServer server;

    private Staff currStaff = null;

    private Stage startStage;

    public void close() {
        startStage.hide();
    }

    public FXMLStartDocController() {
    }

    public void setServices(StaffServices services) {
        this.services = services;
    }

    public void setServer(IServer server) {
        this.server = server;
    }

    public void login() throws IOException {
        String username = userTF.getText();
        String password = passTF.getText();
        userTF.clear();
        passTF.clear();
        currStaff = services.checkStaff(username, password);
        if (currStaff != null) {
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
        startStage = (Stage) loginBtn.getScene().getWindow();
        Class cls = Class.forName("StartApp");
        FXMLLoader loader = new FXMLLoader(cls.getResource("fxml/FXMLMainDoc.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        FXMLMainDocController ctrl = loader.getController();
        ctrl.setServer(server);
        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Welcome, " + currStaff.getUsername());
        stage.setScene(scene);
        stage.show();
        this.close();
    }
}