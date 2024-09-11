package com.nsoz.model;

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
public class PartFrame {

    public short idSmallImg;
    public short dx;
    public short dy;
    public byte flip;
    public byte onTop;
}
