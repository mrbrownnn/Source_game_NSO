
package com.nsoz.bot.attack;

import com.nsoz.bot.move.MoveToTarget;
import com.nsoz.model.Char;
import com.nsoz.skill.Skill;
import java.util.ArrayList;
import com.nsoz.bot.Bot;
import com.nsoz.map.zones.Zone;
import com.nsoz.util.NinjaUtils;
import java.util.List;
import com.nsoz.bot.IMove;
import com.nsoz.bot.IAttack;

/**
 *
 * @author Admin
 */
public class AttackAround implements IAttack {

    private final ArrayList<Skill> skills = new ArrayList<>();

    private Char target;

    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    private Char detect(Char owner) {
        Zone z = owner.zone;
        List<Char> list = z.getChars();
        for (Char c : list) {
            if (c == owner || c.isDead || c.isNhanBan || c instanceof Bot) {
                continue;
            }
            int d = NinjaUtils.getDistance(owner.x, owner.y, c.x, c.y);
            if (d < 100) {
                return c;
            }
        }
        return null;
    }

    @Override
    public void attack(Bot owner) {
        if (owner.isDead) {
            return;
        }
        if (owner.isDontAttack()) {
            return;
        }
        if (target == null || target.isDead || target.zone != owner.zone) {
            target = detect(owner);
            IMove move = owner.getMove();
            if (move != null) {
                if (move instanceof MoveToTarget) {
                    MoveToTarget m = (MoveToTarget) move;
                    m.setTarget(target);
                }
            }
        }
        if (target == null || target.isDead) {
            return;
        }

        for (Skill skill : skills) {
            if (!skill.isCooldown() && skill.template.type == Skill.SKILL_CLICK_USE_BUFF) {
                owner.useSkillBuff((byte) (target.x > owner.x ? 1 : -1), skill);
            }
        }
        int index = NinjaUtils.nextInt(skills.size());
        Skill skill = skills.get(index);
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

    }
}
