package com.nsoz.skill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Builder
@AllArgsConstructor
public class SkillPaint {

    public int id;
    public int effId;
    public int numEff;
    public SkillInfoPaint[] skillStand;
    public SkillInfoPaint[] skillfly;

    public SkillPaint clone() {
        SkillPaint skillPaint = new SkillPaint();
        skillPaint.id = this.id;
        skillPaint.effId = this.effId;
        skillPaint.numEff = this.numEff;
        skillPaint.skillStand = new SkillInfoPaint[this.skillStand.length];
        skillPaint.skillfly = new SkillInfoPaint[this.skillfly.length];
        for (int i = 0; i < this.skillStand.length; i++) {
            skillPaint.skillStand[i] = new SkillInfoPaint();
            skillPaint.skillStand[i].adx = skillStand[i].adx;
            skillPaint.skillStand[i].ady = skillStand[i].ady;
            skillPaint.skillStand[i].arrowId = skillStand[i].arrowId;
            skillPaint.skillStand[i].e0dx = skillStand[i].e0dx;
            skillPaint.skillStand[i].e0dy = skillStand[i].e0dy;
            skillPaint.skillStand[i].e1dx = skillStand[i].e1dx;
            skillPaint.skillStand[i].e1dy = skillStand[i].e1dy;
            skillPaint.skillStand[i].e2dx = skillStand[i].e2dx;
            skillPaint.skillStand[i].e2dy = skillStand[i].e2dy;
            skillPaint.skillStand[i].effS0Id = skillStand[i].effS0Id;
            skillPaint.skillStand[i].effS1Id = skillStand[i].effS1Id;
            skillPaint.skillStand[i].effS2Id = skillStand[i].effS2Id;
            skillPaint.skillStand[i].status = skillStand[i].status;
        }
        for (int i = 0; i < this.skillfly.length; i++) {
            skillPaint.skillfly[i] = new SkillInfoPaint();
            skillPaint.skillfly[i].adx = skillfly[i].adx;
            skillPaint.skillfly[i].ady = skillfly[i].ady;
            skillPaint.skillfly[i].arrowId = skillfly[i].arrowId;
            skillPaint.skillfly[i].e0dx = skillfly[i].e0dx;
            skillPaint.skillfly[i].e0dy = skillfly[i].e0dy;
            skillPaint.skillfly[i].e1dx = skillfly[i].e1dx;
            skillPaint.skillfly[i].e1dy = skillfly[i].e1dy;
            skillPaint.skillfly[i].e2dx = skillfly[i].e2dx;
            skillPaint.skillfly[i].e2dy = skillfly[i].e2dy;
            skillPaint.skillfly[i].effS0Id = skillfly[i].effS0Id;
            skillPaint.skillfly[i].effS1Id = skillfly[i].effS1Id;
            skillPaint.skillfly[i].effS2Id = skillfly[i].effS2Id;
            skillPaint.skillfly[i].status = skillfly[i].status;
        }
        return skillPaint;
    }
}
