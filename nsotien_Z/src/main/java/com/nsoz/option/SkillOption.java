
package com.nsoz.option;

import com.nsoz.server.GameData;
import com.nsoz.skill.SkillOptionTemplate;

public class SkillOption {

    public SkillOption(int templateId, int param) {
        this.param = param;
        this.optionTemplate = GameData.getInstance().getOptionTemplates().get(templateId);
    }

    public int param;
    public SkillOptionTemplate optionTemplate;
}
