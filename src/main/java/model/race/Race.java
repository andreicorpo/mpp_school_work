package model.race;

public class Race {
    private Integer id;
    private Integer motorCC;

    public Race(Integer id, Integer motorCC) {
        this.id = id;
        this.motorCC = motorCC;
    }

    public Integer getMotorCC() {
        return motorCC;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
