package model.participant;

public class ParticipantTeam implements  IParticipant{
    private Integer id;
    private String name;
    private Integer motorCC;
    private String teamName;

    public ParticipantTeam(Integer id, String name, Integer motorCC, String teamName) {
        this.id = id;
        this.name = name;
        this.motorCC = motorCC;
        this.teamName = teamName;
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

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ParticipantTeam{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", motorCC=" + motorCC +
                ", teamName='" + teamName + '\'' +
                '}' + '\n';
    }
}
