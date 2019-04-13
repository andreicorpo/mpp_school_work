package networking.server;

import repository.RepositoryJdbcParticipant;
import repository.RepositoryJdbcRace;
import repository.RepositoryJdbcStaff;
import services.ParticipantServices;
import services.RaceServices;
import services.StaffServices;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class StartServer {
    private static int defaultPort = 50000;

    public static void main(String[] args) {
        Properties serverProps = new Properties();
        try {
            serverProps.load(StartServer.class.getResourceAsStream("/chatserver.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Properties dbProps = new Properties();
        try {
            dbProps.load(new FileReader("sqlite_db.config"));
        } catch (IOException e) {
            System.out.println("Cannot find sqlite_db.config " + e);
        }
        RepositoryJdbcStaff staffRepo = new RepositoryJdbcStaff(dbProps);
        StaffServices staffServices = new StaffServices(staffRepo);
        RepositoryJdbcParticipant participantRepo = new RepositoryJdbcParticipant(dbProps);
        ParticipantServices participantServices = new ParticipantServices(participantRepo);
        RepositoryJdbcRace raceRepo = new RepositoryJdbcRace(dbProps);
        RaceServices raceServices = new RaceServices(raceRepo);
        IServer serverImpl = new Server(staffServices, participantServices, raceServices);
        int serverPort = defaultPort;
        try {
            serverPort = Integer.parseInt(serverProps.getProperty("chat.server.port"));
        } catch (NumberFormatException nef) {
            nef.printStackTrace();
        }
        System.out.println(serverPort);
        AbstractServer server = new RpcConcurrentServer(serverPort, serverImpl);
        server.start();
    }
}
