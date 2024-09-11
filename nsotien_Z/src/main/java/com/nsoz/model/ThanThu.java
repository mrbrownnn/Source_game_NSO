/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nsoz.model;

import com.nsoz.constants.ItemName;
import com.nsoz.option.ItemOption;
import com.nsoz.util.NinjaUtils;
import java.util.ArrayList;
import lombok.Data;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@Data
public class ThanThu {

    public static int ST_QUAI_ID = 102;
    public static int ST_NGUOI_ID = 103;
    public static final byte HAI_MA = 0;
    public static final byte DI_LONG = 1;

    public static final int MAX_LEVEL = 100;
    public static final int MAX_STAR = 3;

    private int eggHatchingTime;
    private int currentExp;
    private byte type;
    private int level;
    private byte stars;
    private ArrayList<ItemOption> options;
    private int damageOnMob, damageOnPlayer;

    public void load(JSONObject obj) {
        this.eggHatchingTime = Integer.parseInt(obj.get("egg_hatching_time").toString());
        this.currentExp = Integer.parseInt(obj.get("exp").toString());
        this.type = Byte.parseByte(obj.get("type").toString());
        this.level = Integer.parseInt(obj.get("level").toString());
        this.stars = Byte.parseByte(obj.get("stars").toString());
        this.options = new ArrayList<>();
        JSONArray arr = (JSONArray) obj.get("options");
        for (int i = 0; i < arr.size(); i++) {
            JSONArray o = (JSONArray) arr.get(i);
            int optionID = Integer.parseInt(o.get(0).toString());
            int param = Integer.parseInt(o.get(1).toString());
            addOption(new ItemOption(optionID, param));
        }
    }

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("egg_hatching_time", eggHatchingTime);
        obj.put("exp", currentExp);
        obj.put("type", type);
        obj.put("level", level);
        obj.put("stars", stars);
        JSONArray arr = new JSONArray();
        for (ItemOption o : options) {
            JSONArray a = new JSONArray();
            a.add(o.optionTemplate.id);
            a.add(o.param);
            arr.add(a);
        }
        obj.put("options", arr);
        return obj;
    }

    public void addOption(ItemOption o) {
        options.add(o);
        setAttribute(o);
    }

    public int getMaxExp() {
        if (stars == 3) {
            return 20000;
        }
        if (stars == 4) {
            return 30000;
        }
        if (stars == 5) {
            return 40000;
        }
        return 10000;
    }

    public void hatchedEgg() {
        currentExp = 0;
        level = 1;
        stars = 1;
        options.clear();
        addOption(new ItemOption(ST_QUAI_ID, 1000));
        addOption(new ItemOption(ST_NGUOI_ID, 1000));
    }

    public void addExp(int exp) {
        this.currentExp += exp;
        int max = getMaxExp();
        if (currentExp > max) {
            currentExp = max;
            levelUp();
        }
    }

    public void setAttribute(ItemOption o) {
        int optionID = o.optionTemplate.id;
        if (optionID == ST_NGUOI_ID) {
            damageOnPlayer = o.param;
        }
        if (optionID == ST_QUAI_ID) {
            damageOnMob = o.param;
        }
    }

    public void levelUp() {
        this.level++;
        this.currentExp = 0;
        if (this.level <= 100) {
            int levelPer10 = level / 10;
            if (levelPer10 == 0) {
                levelPer10 = 1;
            }
            for (ItemOption o : options) {
                int add = stars * (int)NinjaUtils.nextInt(20, 30) * levelPer10;
                o.param += add;
                setAttribute(o);
            }
        }
        if (level > MAX_LEVEL) {
            level = MAX_LEVEL;
        }
    }

    public int getItemID() {
        if (type == HAI_MA) {
            if (stars == 1) {
                return ItemName.HAI_MA_CAP_1;
            } else if (stars == 2) {
                return ItemName.HAI_MA_CAP_2;
            } else {
                return ItemName.HAI_MA_CAP_3;
            }
        } else {
            if (stars == 1) {
                return ItemName.DI_LONG_CAP_1;
            } else if (stars == 2) {
                return ItemName.DI_LONG_CAP_2;
            } else {
                return ItemName.DI_LONG_CAP_3;
            }
        }
    }

    public int getIcon() {
        if (type == HAI_MA) {
            if (eggHatchingTime > 0) {
                return 2494;
            } else {
                if (stars == 1) {
                    return 2484;
                }
                if (stars == 2) {
                    return 2485;
                } else {
                    return 2486;
                }
            }
        } else {
            if (eggHatchingTime > 0) {
                return 2493;
            } else {
                if (stars == 1) {
                    return 2487;
                }
                if (stars == 2) {
                    return 2488;
                } else {
                    return 2489;
                }
            }
        }
    }

    public void evolution() {
        this.stars++;
        this.level = 1;
        this.currentExp = 0;
        if (this.stars > MAX_STAR) {
            this.stars = MAX_STAR;
        }
    }

    public String getName() {
        if (eggHatchingTime > 0) {
            if (type == HAI_MA) {
                return "Trứng Hải Mã";
            } else {
                return "Trứng Dị Long";
            }
        }
        String text;
        if (type == HAI_MA) {
            text = "Hải mã";
        } else {
            text = "Dị long";
        }
        return String.format(text, level);
    }

    public int getId() {
        if (type == HAI_MA) {
            if (eggHatchingTime > 0) {
                return 2494;
            } else {
                if (stars == 1) {
                    return 2502;
                } else if (stars == 2) {
                    return 2503;
                } else {
                    return 2504;
                }
            }
        } else {
            if (eggHatchingTime > 0) {
                return 2493;
            } else {
                if (stars == 1) {
                    return 2506;
                }
                if (stars == 2) {
                    return 2507;
                } else {
                    return 2508;
                }
            }
        }
    }
}
