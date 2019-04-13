package utils;

import java.io.Serializable;

public class RacePerson implements Serializable {

    private Integer raceMotorCC;
    private Integer numOfParticipants;

    public RacePerson(Integer raceMotorCC, Integer numOfParticipants) {
        this.raceMotorCC = raceMotorCC;
        this.numOfParticipants = numOfParticipants;
    }

    public Integer getRaceMotorCC() {
        return raceMotorCC;
    }

    public void setRaceMotorCC(Integer raceMotorCC) {
        this.raceMotorCC = raceMotorCC;
    }

    public Integer getNumOfParticipants() {
        return numOfParticipants;
    }

    public void setNumOfParticipants(Integer numOfParticipants) {
        this.numOfParticipants = numOfParticipants;
    }

    @Override
    public String toString() {
        return "RacePerson{" +
                "raceMotorCC=" + raceMotorCC +
                ", numOfParticipants=" + numOfParticipants +
                '}';
    }
}
