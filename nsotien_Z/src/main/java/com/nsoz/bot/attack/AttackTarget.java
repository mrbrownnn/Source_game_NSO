
package com.nsoz.bot.attack;

import com.nsoz.model.Char;
import com.nsoz.skill.Skill;
import java.util.ArrayList;
import com.nsoz.bot.Bot;
import com.nsoz.bot.IAttack;

/**
 *
 * @author Admin
 */
public class AttackTarget implements IAttack {

    private Char target;

    public AttackTarget(Char target) {
        this.target = target;
    }

    @Override
    public void attack(Bot owner) {
        if (owner.isDead || target.isDead) {
            return;
        }
        if (owner.isDontAttack()) {
            return;
        }

        for (Skill skill : owner.vSkillFight) {
            if (!skill.isCooldown() && skill.template.type == Skill.SKILL_CLICK_USE_BUFF) {
                owner.useSkillBuff((byte) (target.x > owner.x ? 1 : -1), skill);
            }
        }
        owner.vSkillFight.sort((o1, o2) -> (new Integer(o2.level).compareTo((new Integer(o1.level)))));
        for (Skill skill : owner.vSkillFight) {
            if (!skill.isCooldown() && skill.template.type == Skill.SKILL_CLICK_USE_ATTACK) {
                int num = 0;
                if (owner.classId == 0 || owner.classId == 1 || owner.classId == 3 || owner.classId == 5) {
                    num = 40;
                }
                int num2 = owner.x - skill.dx;
                int num3 = owner.x + skill.dx;
                int num4 = owner.y - skill.dy - num;
                int num5 = owner.y + skill.dy;
                if (num2 <= target.x && target.x <= num3 && num4 <= target.y && target.y <= num5) {
                    owner.selectedSkill = skill;
                    break;
                }
            }
        }
        if (owner.isMeCanAttackOtherPlayer(target)) {
            ArrayList<Char> list = new ArrayList<>();
            list.add(target);
            owner.attackCharacter(list);
            if (owner.clone != null && owner.clone.isNhanBan && !owner.clone.isDead) {
                owner.clone.attackCharacter(list);
            }
        }
    }

}
