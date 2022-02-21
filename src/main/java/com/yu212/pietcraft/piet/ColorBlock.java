package com.yu212.pietcraft.piet;

public record ColorBlock(PietCodel color, int size, Pos[] corners) {
    public Pos corner(Direction dp, Direction cc) {
        // RL RR DL DR LL LR UL UR
        int ord = dp.ordinal() * 2 + 1 - cc.ordinal() / 2;
        return corners[ord];
    }
}
