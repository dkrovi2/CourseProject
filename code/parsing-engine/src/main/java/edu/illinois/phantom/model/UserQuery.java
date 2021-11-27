package edu.illinois.phantom.model;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class UserQuery {
    private String skill;
    private int minExperience;
    boolean mandatorySkill;

    public UserQuery(String skill, int minExperience, boolean mandatorySkill) {
        this.skill = skill.toUpperCase()+"_FIELD";
        this.minExperience = minExperience;
        this.mandatorySkill = mandatorySkill;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill.toUpperCase()+"_FIELD";;
    }

    public int getMinExperience() {
        return minExperience;
    }

    public void setMinExperience(int minExperience) {
        this.minExperience = minExperience;
    }

    public boolean isMandatorySkill() {
        return mandatorySkill;
    }

    public void setMandatorySkill(boolean mandatorySkill) {
        this.mandatorySkill = mandatorySkill;
    }
}
