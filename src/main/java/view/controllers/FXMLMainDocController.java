package view.controllers;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.participant.ParticipantSingle;
import model.participant.ParticipantTeam;
import model.staff.Staff;
import networking.server.IClientObserver;
import networking.server.IServer;
import utils.AlertError;
import utils.ErrorType;
import utils.RacePerson;

import java.util.ArrayList;

public class FXMLMainDocController implements IClientObserver {

    private IServer server;
    private Staff currStaff;
    private Integer numOfParticipants;
    @FXML
    private Label motorCCLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label teamLabel;
    @FXML
    private TextField participantNameTF;
    @FXML
    private TextField teamTF;
    @FXML
    private ChoiceBox<String> motorCCChoiceBox;
    @FXML
    private Button signUpBtn;
    @FXML
    private TextField searchTeamTF;
    @FXML
    private Button logoutBtn;
    @FXML
    private TableView racesTable;
    @FXML
    private TableView participantsTable;
    @FXML
    private TableColumn<Integer, RacePerson> raceColumn;
    @FXML
    private TableColumn<Integer, RacePerson> participantsNumColumn;
    @FXML
    private TableColumn<String, ParticipantTeam> participantNameCol;
    @FXML
    private TableColumn<Integer, ParticipantTeam> motorCCCol;
    @FXML
    private Group loginGroup;
    @FXML
    private Group mainGroup;
    @FXML
    private TextField userField;
    @FXML
    private PasswordField passField;
    @FXML
    private Button loginBtn;

    public FXMLMainDocController() {
    }

    @FXML
    public void initialize() {
        elementsMainPage(false);
        elementsLoginPage(true);
        participantNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        motorCCCol.setCellValueFactory(new PropertyValueFactory<>("motorCC"));
        raceColumn.setCellValueFactory(new PropertyValueFactory<>("raceMotorCC"));
        participantsNumColumn.setCellValueFactory(new PropertyValueFactory<>("numOfParticipants"));
    }

    public void setServer(IServer server) {
        this.server = server;
    }

    public void elementsParticipantsByTeam(boolean show) {
        participantsTable.setVisible(show);
    }

    public void elementsSignUpParticipants(boolean show) {
        motorCCLabel.setVisible(show);
        nameLabel.setVisible(show);
        teamLabel.setVisible(show);
        motorCCChoiceBox.setVisible(show);
        signUpBtn.setVisible(show);
        participantNameTF.setVisible(show);
        teamTF.setVisible(show);
    }

    public void elementsMainPage(boolean show) {
        mainGroup.setVisible(show);
        elementsSignUpParticipants(false);
        elementsParticipantsByTeam(false);
    }

    public void elementsLoginPage(boolean show) {
        loginGroup.setVisible(show);
    }

    public void getParticipantByTeam() {
        if (!searchTeamTF.getText().equals("")) {
            elementsSignUpParticipants(false);
            participantsTable.getItems().clear();
            Stage currStage = (Stage) this.logoutBtn.getScene().getWindow();
            currStage.setMinHeight(450);
            currStage.setHeight(450);
            currStage.setMinWidth(890);
            currStage.setWidth(890);
            elementsParticipantsByTeam(true);
            String teamName = searchTeamTF.getText();
            server.searchByTeam(teamName, this);
        } else {
            AlertError alertErr = new AlertError();
            alertErr.alertError("Search By Team Error", "No team name entered", "You didn't enter a team name before searching");
        }
    }

    public void login() {
        String user = userField.getText();
        String pass = passField.getText();
        System.out.println(server == null ? "Server is null" : "Server is ok");
        server.login(user + "," + pass, this);

    }

    public void logout() {
        elementsMainPage(false);
        elementsLoginPage(true);
        Stage currStage = (Stage) this.logoutBtn.getScene().getWindow();
        currStage.setMinHeight(450);
        currStage.setHeight(450);
        currStage.setMinWidth(600);
        currStage.setWidth(600);
        server.logout(currStaff, this);
    }

    public void signUpParticipant() {
        server.getRaces(this);
        server.numOfParticipants();
        Stage currStage = (Stage) this.logoutBtn.getScene().getWindow();
        currStage.setMinHeight(450);
        currStage.setHeight(450);
        currStage.setMinWidth(890);
        currStage.setWidth(890);
        elementsParticipantsByTeam(false);
        elementsSignUpParticipants(true);
    }

    public void signUp () {
        if (!participantNameTF.getText().equals("") && motorCCChoiceBox.getSelectionModel().getSelectedItem() != null) {
            Integer motorCC = Integer.valueOf(motorCCChoiceBox.getSelectionModel().getSelectedItem().replaceAll("c", ""));
            String name = participantNameTF.getText();
            String teamName = teamTF.getText();
            if (teamName.isEmpty()){
                ParticipantSingle participantSingle = new ParticipantSingle(numOfParticipants + 1, name, motorCC);
                server.addParticipant(participantSingle);
                server.numOfParticipants();
            } else {
                ParticipantTeam participantTeam = new ParticipantTeam(numOfParticipants + 1, name, motorCC, teamName);
                server.addParticipant(participantTeam);
                server.numOfParticipants();
            }
        } else {
            AlertError alertErr = new AlertError();
            alertErr.alertError("Sign Up Error", "Insufficient data", "You didn't enter all the necessary data");
        }
        motorCCChoiceBox.getSelectionModel().clearSelection();
        participantNameTF.clear();
        teamTF.clear();
        server.updateParticipants();
    }


    @Override
    public void updateParticipantsNum(ArrayList<RacePerson> data) {
        racesTable.getItems().clear();
        if (data != null) {
            data.forEach(racePerson -> racesTable.getItems().add(racePerson));
        }
    }

    @Override
    public void participantsByTeam(ArrayList<ParticipantTeam> data) {
        participantsTable.getItems().clear();
        data.forEach(participantTeam -> participantsTable.getItems().add(participantTeam));
    }

    @Override
    public void currentRaces(ArrayList<String> data) {
        data.forEach(race -> motorCCChoiceBox.getItems().add(race));
    }

    @Override
    public void numOfParticipants(Integer num) {
        numOfParticipants = num;
    }

    @Override
    public void setStaff(Staff staff) {
        currStaff = staff;
        Stage currStage = (Stage) this.loginBtn.getScene().getWindow();
        currStage.setTitle("Welcome, " + currStaff.getUsername());
        elementsMainPage(true);
        elementsLoginPage(false);
        server.updateParticipants();
    }

    @Override
    public void error(ErrorType errorType) {
        AlertError alertErr = new AlertError();
        switch (errorType) {
            case FAILED:
                alertErr.alertError("Login Error", "User not found", "User not found, try again");
                userField.clear();
                passField.clear();
                break;
            case ALREADY_LOGGED_IN:
                alertErr.alertError("Login Error", "User logged in", "This user is already logged in, try again");
                userField.clear();
                passField.clear();
                break;
        }
    }
}