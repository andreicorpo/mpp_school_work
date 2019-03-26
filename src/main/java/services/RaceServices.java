package services;

import model.race.Race;
import repository.RepositoryJdbcRace;

public class RaceServices {

    RepositoryJdbcRace repo;

    public RaceServices(RepositoryJdbcRace repo) {
        this.repo = repo;
    }

    public RepositoryJdbcRace getRepo() {
        return repo;
    }

    public void setRepo(RepositoryJdbcRace repo) {
        this.repo = repo;
    }

    void save(Race race) {
        repo.save(race);
    }

    public Race findOne(Integer id) {
        return repo.findOne(id);
    }

    public Iterable<Race> findAll() {
        return repo.findAll();
    }

    public int numOfParticipantByRace(Integer id){
        return repo.numOfParticipantsByRace(id);
    }

    public int size() {return repo.size();}

}
