
package com.nsoz.fashion;

import com.nsoz.model.Char;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 *
 * @author Admin
 */
@AllArgsConstructor
@Builder
public class FashionCustom implements FashionStrategy {

    @Builder.Default
    private short head = -1, body = -1, leg = -1, weapon = -1, glove = -1, coat = -1;
    @Builder.Default
    public short fBody = -1;
    @Builder.Default
    public short fPP = -1;
    @Builder.Default
    public short fHair = -1;
    @Builder.Default
    public short fLeg = -1;
    @Builder.Default
    public short fHorse = -1;
    @Builder.Default
    public short fName = -1;
    @Builder.Default
    public short fRank = -1;
    @Builder.Default
    public short fMask = -1;
    @Builder.Default
    public short fTransform = -1;
    @Builder.Default
    public short fWeapon = -1;

    @Override
    public void set(Char owner) {
        owner.head = this.head;
        owner.body = this.body;
        owner.leg = this.leg;
        owner.weapon = this.weapon;
        owner.glove = this.glove;
        owner.coat = this.coat;
        owner.ID_BIEN_HINH = this.fTransform;
        owner.ID_BODY = this.fBody;
        owner.ID_HAIR = this.fHair;
        owner.ID_HORSE = this.fHorse;
        owner.ID_LEG = this.fLeg;
        owner.ID_MAT_NA = this.fMask;
        owner.ID_NAME = this.fName;
        owner.ID_PP = this.fPP;
        owner.ID_RANK = this.fRank;
        owner.ID_WEA_PONE = this.fWeapon;
    }

}
