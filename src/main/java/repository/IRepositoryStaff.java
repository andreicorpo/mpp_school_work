package repository;

import model.staff.Staff;

public interface IRepositoryStaff extends IRepository<Integer, Staff> {

    void save(Staff entity);

}
