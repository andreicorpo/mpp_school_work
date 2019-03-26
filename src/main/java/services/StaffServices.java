package services;

import model.staff.Staff;
import repository.RepositoryJdbcStaff;

import java.util.stream.StreamSupport;

public class StaffServices {

    RepositoryJdbcStaff repo;

    public StaffServices(RepositoryJdbcStaff repo) {
        this.repo = repo;
    }

    public RepositoryJdbcStaff getRepo() {
        return repo;
    }

    public void setRepo(RepositoryJdbcStaff repo) {
        this.repo = repo;
    }

    public void save(Staff staff) {
        repo.save(staff);
    }

    public Staff findOne(Integer id){
        return repo.findOne(id);
    }

    public Iterable<Staff> findAll(){
        return repo.findAll();
    }

    public boolean checkStaff(String username, String password) {
        return StreamSupport.stream(findAll().spliterator(), false).anyMatch(staff -> staff.getUsername().equals(username) && staff.getPassword().equals(password));
    }

    public int size() {return repo.size();}

}
