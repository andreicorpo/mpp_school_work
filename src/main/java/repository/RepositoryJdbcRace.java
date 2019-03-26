package repository;

import model.participant.IParticipant;
import model.race.Race;
import model.staff.Staff;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RepositoryJdbcRace implements IRepositoryRace {

    private JdbcUtils dbUtils;

    private static final Logger logger= LogManager.getLogger();

    public RepositoryJdbcRace (Properties props) {
        logger.info("Initializing SortingTaskRepository with properties: {} ",props);
        this.dbUtils = new JdbcUtils(props);
    }

    @Override
    public void save(Race entity) {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preStmt=connection.prepareStatement("insert into Races(StaffID, Username, Password) values (?,?,?)")){
            preStmt.setInt(1,entity.getId());
            preStmt.setInt(2,entity.getMotorCC());
            int result=preStmt.executeUpdate();
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }

        logger.traceExit();
    }

    @Override
    public Race findOne(Integer integer) {
        logger.traceEntry("finding task with id {} ",integer);
        Connection con=dbUtils.getConnection();

        try(PreparedStatement preStmt=con.prepareStatement("select * from Races where RaceID=?")){
            preStmt.setInt(1,integer);
            try(ResultSet result=preStmt.executeQuery()) {
                if (result.next()) {
                    Integer id = result.getInt("RaceID");
                    Integer motorCC = result.getInt("MotorCC");
                    return new Race(id, motorCC);
                }
            }
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        logger.traceExit("No task found with id {}", integer);

        return null;
    }


    @Override
    public Iterable<Race> findAll() {
        Connection con=dbUtils.getConnection();
        List<Race> races = new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select * from Races")){
            try(ResultSet result=preStmt.executeQuery()) {
                while (result.next()) {
                    Integer id = result.getInt("RaceID");
                    Integer motorCC = result.getInt("MotorCC");
                    Race race= new Race(id, motorCC);
                    races.add(race);
                }
            }
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        return races;
    }

    public int numOfParticipantsByRace(Integer id) {
        boolean raceExists = findOne(id) != null;
        int num = 0;
        if (raceExists) {
            Connection con = dbUtils.getConnection();
            try(PreparedStatement preStmt = con.prepareStatement("select count(ParticipantID) as 'NumParticipants' from RacesParticipants group by RaceID having RaceID = ?")) {
                preStmt.setInt(1, id);
                try(ResultSet resultSet = preStmt.executeQuery()){
                    if (resultSet.next()){
                        num = resultSet.getInt("NumParticipants");
                    }
                }
            }catch (SQLException ex){
                logger.error(ex);
                System.out.println("Error DB "+ex);
                num = -1;
            }
        }
        return num;
    }

    @Override
    public int size() {
        logger.traceEntry();
        Connection con=dbUtils.getConnection();
        List<IParticipant> participants=new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select count(*) as NumRows from Races")) {
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
