package services;

import model.participant.IParticipant;
import model.participant.ParticipantType;
import repository.RepositoryJdbcParticipant;

public class ParticipantServices {

    RepositoryJdbcParticipant repo;

    public ParticipantServices(RepositoryJdbcParticipant repo) {
        this.repo = repo;
    }

    public RepositoryJdbcParticipant getRepo() {
        return repo;
    }

    public void setRepo(RepositoryJdbcParticipant repo) {
        this.repo = repo;
    }

    public void save(IParticipant participant, ParticipantType type) {
        repo.save(participant, type);
    }

    public IParticipant findOne(Integer id) {
        return  repo.findOne(id);
    }

    public Iterable<IParticipant> findAll() {
        return  repo.findAll();
    }

    public int size() {return repo.size();}
}
