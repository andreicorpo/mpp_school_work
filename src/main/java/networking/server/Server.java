package networking.server;

import model.participant.IParticipant;
import model.participant.ParticipantSingle;
import model.participant.ParticipantTeam;
import model.participant.ParticipantType;
import model.race.Race;
import model.staff.Staff;
import services.ParticipantServices;
import services.RaceServices;
import services.StaffServices;
import utils.ErrorType;
import utils.RacePerson;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements IServer {

    private final int threadsNum = 5;
    private StaffServices staffServices;
    private ParticipantServices participantServices;
    private RaceServices raceServices;
    private Map<Integer, IClientObserver> loggedClients;

    public Server(StaffServices staffServices, ParticipantServices participantServices, RaceServices raceServices) {
        this.staffServices = staffServices;
        this.participantServices = participantServices;
        this.raceServices = raceServices;
        loggedClients = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized void login(String userAndPass, IClientObserver client) {
        Staff staff = staffServices.checkStaff(userAndPass.split(",")[0], userAndPass.split(",")[1]);
        if (staff != null) {
            if (loggedClients.get(staff.getId()) != null) {
                client.error(ErrorType.ALREADY_LOGGED_IN);
            } else {
                loggedClients.put(staff.getId(), client);
                client.setStaff(staff);
            }
        } else {
            client.error(ErrorType.FAILED);

        }
    }

    @Override
    public synchronized void logout(Staff staff, IClientObserver client) {
        loggedClients.remove(staff.getId());
    }

    @Override
    public synchronized void updateParticipants() {
        System.out.println("Update start.");
        Iterable<Staff> staffs = staffServices.findAll();
        ArrayList<RacePerson> data = new ArrayList<>();
        for (Race r : raceServices.findAll()) {
            RacePerson racePerson = new RacePerson(r.getMotorCC(), raceServices.numOfParticipantByRace(r.getId()));
            data.add(racePerson);
        }
        ExecutorService executorService = Executors.newFixedThreadPool(threadsNum);
        for (Staff s : staffs) {
            IClientObserver client = loggedClients.get(s.getId());
            System.out.println(client);
            if (client != null) {
                executorService.execute(() -> {
                    try {
                        System.out.println("Updating participants list...");
                        client.updateParticipantsNum(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        executorService.shutdown();
        System.out.println("Update Done.");
    }

    @Override
    public synchronized void numOfParticipants() {
        ExecutorService executorService = Executors.newFixedThreadPool(threadsNum);
        Iterable<Staff> staffs = staffServices.findAll();
        for (Staff s : staffs) {
            IClientObserver client = loggedClients.get(s.getId());
            System.out.println(client);
            if (client != null) {
                executorService.execute(() -> {
                    try {
                        client.numOfParticipants(participantServices.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        executorService.shutdown();
    }

    @Override
    public synchronized void getRaces(IClientObserver client) {
        ArrayList<String> races = new ArrayList<>();
        raceServices.findAll().forEach(race -> races.add(race.getMotorCC().toString() + "cc"));
        client.currentRaces(races);
    }

    @Override
    public synchronized void searchByTeam(String teamName, IClientObserver client) {
        ArrayList<ParticipantTeam> participantTeams = new ArrayList<>();
        participantServices.findAll().forEach(participant -> {
            if (participant instanceof ParticipantTeam) {
                if (((ParticipantTeam) participant).getTeamName().equals(teamName)) {
                    ParticipantTeam participantTeam = (ParticipantTeam) participant;
                    participantTeams.add(participantTeam);
                }
            }
        });
        client.participantsByTeam(participantTeams);
    }

    @Override
    public synchronized void addParticipant(IParticipant participant) {
        if (participant instanceof ParticipantSingle) {
            ParticipantSingle participantSingle = (ParticipantSingle) participant;
            participantServices.save(participantSingle, ParticipantType.Single);
        } else {
            ParticipantTeam participantTeam = (ParticipantTeam) participant;
            participantServices.save(participantTeam, ParticipantType.Team);
        }
    }

    @Override
    public synchronized void checkStaff(String userAndPass, IClientObserver clientObserver) {
        String user = userAndPass.split(",")[0];
        String pass = userAndPass.split(",")[1];
        Staff staff = staffServices.checkStaff(user, pass);
        clientObserver.setStaff(staff);
    }
}
