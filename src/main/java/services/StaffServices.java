package services;

import model.staff.Staff;
import repository.RepositoryJdbcStaff;

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

    public Staff checkStaff(String username, String password) {
        Staff found = null;
        for (Staff s : findAll()) {
            if (s.getUsername().equals(username) && s.getPassword().equals(password)) {
                found = s;
            }
        }
        return found;
    }

    public int size() {return repo.size();}

}
