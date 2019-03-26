package model.participant;

public class ParticipantSingle implements IParticipant {
    private Integer id;
    private String name;
    private  Integer motorCC;

    public ParticipantSingle(Integer id, String name, Integer motorCC) {
        this.id = id;
        this.name = name;
        this.motorCC = motorCC;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMotorCC() {
        return motorCC;
    }

    public void setMotorCC(Integer motorCC) {
        this.motorCC = motorCC;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ParticipantSingle{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", motorCC=" + motorCC +
                '}' + '\n';
    }
}

