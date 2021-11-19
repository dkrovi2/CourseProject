package edu.illinois.phantom.model;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class Experience {

    private String skill;
    private Integer duration;

    public Experience(String skill, Integer duration) {
        this.skill = skill;
        this.duration = duration;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
