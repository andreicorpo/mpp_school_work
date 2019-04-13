package networking.server;

import model.participant.ParticipantTeam;
import model.staff.Staff;
import utils.ErrorType;
import utils.RacePerson;

import java.util.ArrayList;

public interface IClientObserver {
    void updateParticipantsNum(ArrayList<RacePerson> data);

    void participantsByTeam(ArrayList<ParticipantTeam> data);

    void currentRaces(ArrayList<String> data);

    void numOfParticipants(Integer num);

    void setStaff(Staff staff);

    void error(ErrorType errorType);
}
