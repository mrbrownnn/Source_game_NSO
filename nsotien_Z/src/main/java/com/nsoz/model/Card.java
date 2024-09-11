
package com.nsoz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class Card {

    private int id;
    @Builder.Default
    private int quantity = 1;
    @Builder.Default
    private long expire = -1;
    private double rate;
}
