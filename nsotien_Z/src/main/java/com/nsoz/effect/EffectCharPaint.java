
package com.nsoz.effect;

/**
 *
 * @author Administrator
 */
public class EffectCharPaint {

    public int idEf;
    public EffectInfoPaint[] arrEfInfo;

    public EffectCharPaint clone() {
        EffectCharPaint eff = new EffectCharPaint();
        eff.idEf = this.idEf;
        eff.arrEfInfo = new EffectInfoPaint[arrEfInfo.length];
        for (int i = 0; i < this.arrEfInfo.length; i++) {
            eff.arrEfInfo[i] = new EffectInfoPaint();
            eff.arrEfInfo[i].idImg = this.arrEfInfo[i].idImg;
            eff.arrEfInfo[i].dx = this.arrEfInfo[i].dx;
            eff.arrEfInfo[i].dy = this.arrEfInfo[i].dy;
        }
        return eff;
    }
}
