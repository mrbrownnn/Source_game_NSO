package com.nsoz.skill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Administrator
 */
@NoArgsConstructor
@Setter
@Getter
@Builder
@AllArgsConstructor
public class SkillInfoPaint {

    public int status;
    public int effS0Id;
    public int e0dx;
    public int e0dy;
    public int effS1Id;
    public int e1dx;
    public int e1dy;
    public int effS2Id;
    public int e2dx;
    public int e2dy;
    public int arrowId;
    public int adx;
    public int ady;
}
