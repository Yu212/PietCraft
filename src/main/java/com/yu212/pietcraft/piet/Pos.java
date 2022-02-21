package com.yu212.pietcraft.piet;

public record Pos(int x, int y) {
    public Pos moved(Direction dir) {
        return new Pos(x + dir.dx, y + dir.dy);
    }
}
