package repository;

import model.participant.IParticipant;
import model.participant.ParticipantSingle;
import model.participant.ParticipantTeam;
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

public class RepositoryJdbcStaff implements IRepositoryStaff{

    private JdbcUtils dbUtils;

    private static final Logger logger= LogManager.getLogger();

    public RepositoryJdbcStaff (Properties props) {
        logger.info("Initializing SortingTaskRepository with properties: {} ",props);
        this.dbUtils = new JdbcUtils(props);
    }

    @Override
    public void save(Staff entity) {
        logger.traceEntry();
        Connection connection = dbUtils.getConnection();
        try(PreparedStatement preStmt=connection.prepareStatement("insert into Staffs(StaffID, Username, Password) values (?,?,?)")){
            preStmt.setInt(1,entity.getId());
            preStmt.setString(2,entity.getUsername());
            preStmt.setString(3,entity.getPassword());
            int result=preStmt.executeUpdate();
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }

        logger.traceExit();
    }

    @Override
    public Staff findOne(Integer integer) {
        logger.traceEntry("finding task with id {} ",integer);
        Connection con=dbUtils.getConnection();

        try(PreparedStatement preStmt=con.prepareStatement("select * from Staffs where StaffID=?")){
            preStmt.setInt(1,integer);
            try(ResultSet result=preStmt.executeQuery()) {
                if (result.next()) {
                    Integer id = result.getInt("StaffID");
                    String username = result.getString("Username");
                    String password= result.getString("Password");
                    return new Staff(id, username, password);
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
    public Iterable<Staff> findAll() {
        Connection con=dbUtils.getConnection();
        List<Staff> staffs = new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select * from Staffs")){
            try(ResultSet result=preStmt.executeQuery()) {
                if (result.next()) {
                    Integer id = result.getInt("StaffID");
                    String username = result.getString("Username");
                    String password= result.getString("Password");
                    Staff staff = new Staff(id, username, password);
                    staffs.add(staff);
                }
            }
        }catch (SQLException ex){
            logger.error(ex);
            System.out.println("Error DB "+ex);
        }
        return staffs;
    }

    @Override
    public int size() {
        logger.traceEntry();
        Connection con=dbUtils.getConnection();
        List<IParticipant> participants=new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select count(*) as NumRows from Staffs")) {
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
