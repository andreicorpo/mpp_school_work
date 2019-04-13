package networking.server;

import model.participant.IParticipant;
import model.staff.Staff;

public interface IServer {
    void login(String userAndPass, IClientObserver client);

    void logout(Staff staff, IClientObserver client);

    void updateParticipants();

    void numOfParticipants();

    void getRaces(IClientObserver client);

    void searchByTeam(String teamName, IClientObserver client);

    void addParticipant(IParticipant participant);

    void checkStaff(String userAndPass, IClientObserver clientObserver);
}
