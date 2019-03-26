package repository;

import model.race.Race;

public interface IRepositoryRace extends IRepository<Integer, Race> {

    void save(Race entity);

}
