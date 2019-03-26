package repository;

import model.participant.IParticipant;
import model.participant.ParticipantSingle;
import model.participant.ParticipantTeam;
import model.participant.ParticipantType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RepositoryJdbcParticipant implements IRepositoryParticipant {
    private JdbcUtils dbUtils;

    private static final Logger logger= LogManager.getLogger();

    public RepositoryJdbcParticipant(Properties props) {
        logger.info("Initializing Participants Repo with properties: {} ",props);
        this.dbUtils = new JdbcUtils(props);
    }

    @Override
    public void save(IParticipant entity, ParticipantType participantType) {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        switch (participantType){
            case Single:
                ParticipantSingle participantSingle = (ParticipantSingle) entity;
                try(PreparedStatement preStmt=connection.prepareStatement("insert into Participants(ParticipantID, Name, MotorCC) values (?,?,?)")){
                    preStmt.setInt(1,participantSingle.getId());
                    preStmt.setString(2,participantSingle.getName());
                    preStmt.setInt(3,participantSingle.getMotorCC());
                    int result=preStmt.executeUpdate();
                }catch (SQLException ex){
                    logger.error(ex);
                    System.out.println("Error DB "+ex);
                }
                try(PreparedStatement preStmt=connection.prepareStatement("select RaceID from Races where MotorCC=?")){
                    preStmt.setInt(1,participantSingle.getMotorCC());
                    try(ResultSet result=preStmt.executeQuery()) {
                        if (result.next()) {
                            Integer raceID = result.getInt("RaceID");
                            try(PreparedStatement preStmt2=connection.prepareStatement("insert into RacesParticipants values (?,?)")){
                                preStmt2.setInt(1,participantSingle.getId());
                                preStmt2.setInt(2,raceID);
                                int result2=preStmt2.executeUpdate();
                            }catch (SQLException ex){
                                logger.error(ex);
                                System.out.println("Error DB "+ex);
                            }
                        }
                    }
                }catch (SQLException ex){
                    logger.error(ex);
                    System.out.println("Error DB "+ex);
                }
                break;
            case Team:
                ParticipantTeam participantTeam = (ParticipantTeam) entity;
                try(PreparedStatement preStmt=connection.prepareStatement("insert into Participants(ParticipantID, Name, MotorCC, Team) values (?,?,?,?)")){
                    preStmt.setInt(1,participantTeam.getId());
                    preStmt.setString(2,participantTeam.getName());
                    preStmt.setInt(3,participantTeam.getMotorCC());
                    preStmt.setString(4,participantTeam.getTeamName());
                    int result=preStmt.executeUpdate();
                }catch (SQLException ex){
                    logger.error(ex);
                    System.out.println("Error DB "+ex);
                }
                try(PreparedStatement preStmt=connection.prepareStatement("select RaceID from Races where MotorCC=?")){
                    preStmt.setInt(1,participantTeam.getMotorCC());
                    try(ResultSet result=preStmt.executeQuery()) {
                        if (result.next()) {
                            Integer raceID = result.getInt("RaceID");
                            try(PreparedStatement preStmt2=connection.prepareStatement("insert into RacesParticipants values (?,?)")){
                                preStmt2.setInt(1,raceID);
                                preStmt2.setInt(2,participantTeam.getId());
                                int result2=preStmt2.executeUpdate();
                            }catch (SQLException ex){
                                logger.error(ex);
                                System.out.println("Error DB "+ex);
                            }
                        }
                    }
                }catch (SQLException ex){
                    logger.error(ex);
                    System.out.println("Error DB "+ex);
                }
                break;
            default:
                break;
        }

        logger.traceExit();

    }

    @Override
    public IParticipant findOne(Integer integer) {
        logger.traceEntry("finding participant with id {} ",integer);
        Connection con=dbUtils.getConnection();

        try(PreparedStatement preStmt=con.prepareStatement("select * from Participants where ParticipantID=?")){
            preStmt.setInt(1,integer);
            try(ResultSet result=preStmt.executeQuery()) {
                if (result.next()) {
                    Integer id = result.getInt("ParticipantID");
                    String name = result.getString("Name");
                    Integer motorCC = result.getInt("MotorCC");
                    String team = result.getString("Team");
                    IParticipant participant;
                    if (team != null){
                        participant = new ParticipantTeam(id, name, motorCC, team);
                    }
                    else {
                        participant = new ParticipantSingle(id, name, motorCC);
                    }
                    logger.traceExit(participant);
                    return participant;
                }
            }
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        logger.traceExit("No participant found with id {}", integer);

        return null;
    }

    @Override
    public Iterable<IParticipant> findAll() {
        logger.traceEntry();
        Connection con=dbUtils.getConnection();
        List<IParticipant> participants=new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select * from Participants")) {
            try(ResultSet result=preStmt.executeQuery()) {
                while (result.next()) {
                    Integer id = result.getInt("ParticipantID");
                    String name = result.getString("Name");
                    Integer motorCC = result.getInt("MotorCC");
                    String team = result.getString("Team");
                    IParticipant participant;
                    if (team != null){
                        participant = new ParticipantTeam(id, name, motorCC, team);
                    }
                    else {
                        participant = new ParticipantSingle(id, name, motorCC);
                    }
                    logger.traceExit(participant);
                    participants.add(participant);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        logger.traceExit(participants);
        return participants;
    }

    @Override
    public int size() {
        logger.traceEntry();
        Connection con=dbUtils.getConnection();
        List<IParticipant> participants=new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select count(*) as NumRows from Participants")) {
            try(ResultSet result=preStmt.executeQuery()) {
                if (result.next()) {
                    return result.getInt("NumRows");
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB "+e);
        }
        logger.traceExit(participants);
        return -1;
    }
}
