package utils;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class RacePerson {

    private final IntegerProperty raceMotorCC;
    private final IntegerProperty numOfParticipants;

    public RacePerson(Integer raceMotorCC, Integer numOfParticipants) {
        this.raceMotorCC = new SimpleIntegerProperty(raceMotorCC);
        this.numOfParticipants = new SimpleIntegerProperty(numOfParticipants);
    }

    public int getRaceMotorCC() {
        return raceMotorCC.get();
    }

    public IntegerProperty raceMotorCCProperty() {
        return raceMotorCC;
    }

    public void setRaceMotorCC(int raceMotorCC) {
        this.raceMotorCC.set(raceMotorCC);
    }

    public int getNumOfParticipants() {
        return numOfParticipants.get();
    }

    public IntegerProperty numOfParticipantsProperty() {
        return numOfParticipants;
    }

    public void setNumOfParticipants(int numOfParticipants) {
        this.numOfParticipants.set(numOfParticipants);
    }
}
