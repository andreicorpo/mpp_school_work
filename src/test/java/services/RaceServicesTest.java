package services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repository.RepositoryJdbcRace;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.*;

public class RaceServicesTest {

    RaceServices services;

    @Before
    public void setUp() throws Exception {
        Properties serverProps=new Properties();
        try {
            serverProps.load(new FileReader("sqlite_db.config"));
            System.out.println("Properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.out.println("Cannot find bd.config "+e);
        }
        RepositoryJdbcRace repo = new RepositoryJdbcRace(serverProps);
        this.services = new RaceServices(repo);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void numOfParticipantByRace() {
        assertEquals(2, services.numOfParticipantByRace(3));
    }
}