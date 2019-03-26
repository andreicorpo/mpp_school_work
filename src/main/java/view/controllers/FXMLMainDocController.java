package view.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.participant.IParticipant;
import model.participant.ParticipantSingle;
import model.participant.ParticipantTeam;
import model.participant.ParticipantType;
import model.race.Race;
import services.ParticipantServices;
import services.RaceServices;
import utils.AlertError;
import utils.RacePerson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FXMLMainDocController {

    private RaceServices raceServices;
    private ParticipantServices participantServices;

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
    private Button signUpNewBtn;

    @FXML
    private Button signUpBtn;

    @FXML
    private TextField searchTeamTF;

    @FXML
    private Button searchTeamBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    private TableView racesTable;

    @FXML
    private TableView participantsTable;

    @FXML
    private TableColumn<RacePerson, Integer> raceTableColumn;

    @FXML
    private TableColumn<RacePerson, Integer> participantsNumTableColumn;

    @FXML
    private TableColumn<String, ParticipantTeam> participantNameCol;

    @FXML
    private TableColumn<Integer, ParticipantTeam> motorCCCol;

    private ObservableList<RacePerson> data = FXCollections.observableArrayList();

    public FXMLMainDocController() {
    }

    @FXML
    public void initialize() {
        racesTable.setItems(this.data);
        participantsTable.setVisible(false);
        motorCCLabel.setVisible(false);
        nameLabel.setVisible(false);
        teamLabel.setVisible(false);
        motorCCChoiceBox.setVisible(false);
        signUpBtn.setVisible(false);
        participantNameTF.setVisible(false);
        teamTF.setVisible(false);
        raceTableColumn.setCellValueFactory(cellData -> cellData.getValue().raceMotorCCProperty().asObject());
        participantsNumTableColumn.setCellValueFactory(cellData -> cellData.getValue().numOfParticipantsProperty().asObject());

        participantNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        motorCCCol.setCellValueFactory(new PropertyValueFactory<>("motorCC"));


    }

    public void setServices(RaceServices raceServices, ParticipantServices participantServices) {
        System.out.println("services.findAll()");
        this.raceServices = raceServices;
        this.participantServices = participantServices;
        this.raceServices.findAll().forEach(race -> data.add(new RacePerson(race.getMotorCC(),
                this.raceServices.numOfParticipantByRace(race.getId()))));
    }

    public void getParticipantByTeam() {
        if (!searchTeamTF.getText().equals("")) {
            motorCCLabel.setVisible(false);
            nameLabel.setVisible(false);
            teamLabel.setVisible(false);
            motorCCChoiceBox.setVisible(false);
            signUpBtn.setVisible(false);
            participantNameTF.setVisible(false);
            teamTF.setVisible(false);
            participantsTable.getItems().clear();
            Stage currStage = (Stage) this.logoutBtn.getScene().getWindow();
            currStage.setMinWidth(890);
            participantsTable.setVisible(true);
            String teamName = searchTeamTF.getText();
            List<ParticipantTeam> participants = new ArrayList<>();
            participantServices.findAll().forEach(participant -> {
                if(participant instanceof ParticipantTeam){
                    if(((ParticipantTeam) participant).getTeamName().equals(teamName)){
                        ParticipantTeam participantTeam = (ParticipantTeam) participant;
                        participants.add(participantTeam);
                    }
                }
            });
            participants.forEach(participantTeam -> participantsTable.getItems().add(participantTeam));
        } else {
            AlertError alertErr = new AlertError();
            alertErr.alertError("Search By Team Error", "No team name entered", "You didn't enter a team name before searching");
        }
    }

    public void logout() {
        Stage currStage = (Stage) this.logoutBtn.getScene().getWindow();
        currStage.close();
    }

    public void signUpParticipant() {
        raceServices.findAll().forEach(race -> motorCCChoiceBox.getItems().add(race.getMotorCC().toString() + "cc"));
        Stage currStage = (Stage) this.logoutBtn.getScene().getWindow();
        currStage.setMinWidth(890);
        participantsTable.setVisible(false);
        motorCCLabel.setVisible(true);
        motorCCChoiceBox.setVisible(true);
        nameLabel.setVisible(true);
        teamLabel.setVisible(true);
        participantNameTF.setVisible(true);
        teamTF.setVisible(true);
        signUpBtn.setVisible(true);
    }

    public void signUp () {
        if (!participantNameTF.getText().equals("") && motorCCChoiceBox.getSelectionModel().getSelectedItem() != null) {
            Integer motorCC = Integer.valueOf(motorCCChoiceBox.getSelectionModel().getSelectedItem().replaceAll("c", ""));
            String name = participantNameTF.getText();
            String teamName = teamTF.getText();
            if (teamName.isEmpty()){
                ParticipantSingle participantSingle = new ParticipantSingle(participantServices.size() + 1, name, motorCC);
                System.out.println(participantSingle);
                participantServices.save(participantSingle, ParticipantType.Single);
            } else {
                ParticipantTeam participantTeam = new ParticipantTeam(participantServices.size() + 1, name, motorCC, teamName);
                participantServices.save(participantTeam, ParticipantType.Team);
                System.out.println(participantTeam);
            }
        } else {
            AlertError alertErr = new AlertError();
            alertErr.alertError("Sign Up Error", "Insufficient data", "You didn't enter all the necessary data");
        }
        motorCCChoiceBox.getSelectionModel().clearSelection();
        participantNameTF.clear();
        teamTF.clear();
    }
}
