package repository;

import model.participant.IParticipant;
import model.participant.ParticipantType;

public interface IRepositoryParticipant extends IRepository<Integer, IParticipant> {

    void save(IParticipant entity, ParticipantType type);
}
