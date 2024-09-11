
package com.nsoz.map;

import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 *
 * @author PC
 */
@Builder
@AllArgsConstructor
public class ItemTree {

    public ItemTree(int x, int y) {
        this.xTree = x;
        this.yTree = y;
    }

    public int idTree;
    public int xTree, yTree;
}
