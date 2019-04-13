package services;

import org.junit.Before;
import org.junit.Test;
import repository.RepositoryJdbcStaff;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class StaffServicesTest {

    StaffServices services;

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
        RepositoryJdbcStaff repo = new RepositoryJdbcStaff(serverProps);
        this.services  = new StaffServices(repo);
    }

    @Test
    public void save() {
    }

//    @Test
//    public void checkStaff() {
//        assertTrue(services.checkStaff("AndreiBossu", "thisisnotreal"));
//        assertFalse(services.checkStaff("NuExist", "cacumzici"));
//    }
}