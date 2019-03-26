package repository;

import model.participant.IParticipant;
import model.participant.ParticipantSingle;
import model.participant.ParticipantType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.Assert.*;

public class RepositoryJdbcParticipantTest {

    RepositoryJdbcParticipant repo;

    @Before
    public void setUp() throws Exception {
        Properties serverProps=new Properties();
        try {
            serverProps.load(new FileReader("sqlite_db.config"));
            //System.setProperties(serverProps);

            System.out.println("Properties set. ");
//            System.getProperties().list(System.out);
            serverProps.list(System.out);
        } catch (IOException e) {
            System.out.println("Cannot find bd.config "+e);
        }
        this.repo = new RepositoryJdbcParticipant(serverProps);
    }

    @Test
    public void save() {
        IParticipant participant = new ParticipantSingle(4, "Marcel", 125);
        repo.save(participant, ParticipantType.Single);
    }

    @Test
    public void findOne() {
        Properties serverProps=new Properties();
        try {
            serverProps.load(new FileReader("sqlite_db.config"));
            //System.setProperties(serverProps);

            System.out.println("Properties set. ");
//            System.getProperties().list(System.out);
            serverProps.list(System.out);
        } catch (IOException e) {
            System.out.println("Cannot find bd.config "+e);
        }
        RepositoryJdbcParticipant repo = new RepositoryJdbcParticipant(serverProps);
        IParticipant participant = repo.findOne(1);
        System.out.println(participant);
    }

    @Test
    public void findAll() {
        Properties serverProps=new Properties();
        try {
            serverProps.load(new FileReader("sqlite_db.config"));
            //System.setProperties(serverProps);

            System.out.println("Properties set. ");
//            System.getProperties().list(System.out);
            serverProps.list(System.out);
        } catch (IOException e) {
            System.out.println("Cannot find bd.config "+e);
        }
        RepositoryJdbcParticipant repo = new RepositoryJdbcParticipant(serverProps);
        System.out.println(repo.findAll());
    }

    @After
    public void tearDown() throws Exception {
        Properties serverProps=new Properties();
        try {
            serverProps.load(new FileReader("sqlite_db.config"));
            //System.setProperties(serverProps);

            System.out.println("Properties set. ");
//            System.getProperties().list(System.out);
            serverProps.list(System.out);
        } catch (IOException e) {
            System.out.println("Cannot find bd.config "+e);
        }
        JdbcUtils dbUtils = new JdbcUtils(serverProps);
        Connection con= dbUtils.getConnection();
        try(PreparedStatement preStmt=con.prepareStatement("delete from SortingTasks where id=?")){
            preStmt.setInt(1,4);
            int result=preStmt.executeUpdate();
        }catch (SQLException ex){
            System.out.println("Error DB "+ex);
        }
    }
}