package com.yu212.pietcraft.piet;

import java.util.*;

import static java.util.Comparator.comparingInt;

public class PietInterpreter {
    private static final int STEP_LIMIT = 1000000;
    private final PietCodel[][] code;
    private final int width;
    private final int height;
    private final int[][] blockIds;
    private final List<ColorBlock> blocks;
    private Pos pp;
    private Scanner in;
    private StringBuilder out;
    final Deque<Long> stack;
    Direction dp;
    Direction cc;

    public PietInterpreter(PietCodel[][] code) {
        this.code = code;
        this.width = code[0].length;
        this.height = code.length;
        this.blockIds = new int[height][width];
        this.blocks = new ArrayList<>();
        this.stack = new ArrayDeque<>();
        precomputation();
    }

    private void precomputation() {
        boolean[][] visited = new boolean[height][width];
        int blockId = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (visited[i][j]) {
                    continue;
                }
                int size = 0;
                Comparator<Pos> rightmost = comparingInt(Pos::x);
                Comparator<Pos> lowermost = comparingInt(Pos::y);
                Comparator<Pos> leftmost = comparingInt(Pos::x).reversed();
                Comparator<Pos> uppermost = comparingInt(Pos::y).reversed();
                List<Comparator<Pos>> comparators = new ArrayList<>() {{
                    add(rightmost.thenComparing(uppermost));
                    add(rightmost.thenComparing(lowermost));
                    add(lowermost.thenComparing(rightmost));
                    add(lowermost.thenComparing(leftmost));
                    add(leftmost.thenComparing(lowermost));
                    add(leftmost.thenComparing(uppermost));
                    add(uppermost.thenComparing(leftmost));
                    add(uppermost.thenComparing(rightmost));
                }};
                Pos[] corners = new Pos[8];
                Arrays.fill(corners, new Pos(j, i));
                Deque<Pos> deque = new ArrayDeque<>();
                deque.add(new Pos(j, i));
                visited[i][j] = true;
                while (!deque.isEmpty()) {
                    Pos pos = deque.removeFirst();
                    size++;
                    blockIds[pos.y()][pos.x()] = blockId;
                    for (int k = 0; k < 8; k++) {
                        if (comparators.get(k).compare(pos, corners[k]) > 0) {
                            corners[k] = pos;
                        }
                    }
                    for (Direction dir : Direction.values()) {
                        Pos moved = pos.moved(dir);
                        if (moved.x() < 0 || width <= moved.x() || moved.y() < 0 || height <= moved.y()) {
                            continue;
                        }
                        if (code[pos.y()][pos.x()] != code[moved.y()][moved.x()]) {
                            continue;
                        }
                        if (visited[moved.y()][moved.x()]) {
                            continue;
                        }
                        visited[moved.y()][moved.x()] = true;
                        deque.add(moved);
                    }
                }
                blocks.add(new ColorBlock(code[i][j], size, corners));
                blockId++;
            }
        }
    }

    public String execute(String input) throws PietException {
        in = new Scanner(input);
        out = new StringBuilder();
        stack.clear();
        pp = new Pos(1, 1);
        dp = Direction.RIGHT;
        cc = Direction.LEFT;
        if (getBlock(pp).color() == PietCodel.BLACK) {
            throw new PietException();
        }
        for (int i = 0; i < STEP_LIMIT; i++) {
            if (advance()) {
                return out.toString();
            }
        }
        throw new PietException();
    }

    private boolean advance() {
        if (getBlock(pp).color() == PietCodel.WHITE) {
            return slide();
        }
        Pos dest = getBlock(pp).corner(dp, cc).moved(dp);
        if (getBlock(dest).color() == PietCodel.BLACK) {
            return toggle();
        }
        if (getBlock(dest).color() != PietCodel.WHITE) {
            PietCommand command = PietCommand.of(getBlock(pp).color(), getBlock(dest).color());
            command.execute(this);
        }
        pp = dest;
        return false;
    }

    private boolean slide() {
        Pos last = null;
        Set<Pos> slided = new HashSet<>();
        while (true) {
            Pos next = pp.moved(dp);
            if (getBlock(next).color() == PietCodel.BLACK) {
                if (!pp.equals(last)) {
                    if (slided.contains(pp)) {
                        return true;
                    }
                    slided.add(pp);
                }
                last = pp;
                cc = cc.rotate(2);
                dp = dp.rotate(1);
            } else if (getBlock(pp).color() != PietCodel.WHITE) {
                return false;
            } else {
                pp = next;
            }
        }
    }

    private boolean toggle() {
        for (int i = 0; i < 8; i++) {
            Pos dest = getBlock(pp).corner(dp, cc).moved(dp);
            if (getBlock(dest).color() != PietCodel.BLACK) {
                return false;
            }
            if (i % 2 == 0) {
                cc = cc.rotate(2);
            } else {
                dp = dp.rotate(1);
            }
        }
        return true;
    }

    private ColorBlock getBlock(Pos pos) {
        return blocks.get(blockIds[pos.y()][pos.x()]);
    }

    int getCurrentBlockSize() {
        return getBlock(pp).size();
    }

    OptionalLong inNumber() {
        if (!in.hasNextLine()) {
            return OptionalLong.empty();
        }
        try {
            return OptionalLong.of(in.nextLong());
        } catch (NumberFormatException e) {
            return OptionalLong.empty();
        }
    }

    OptionalLong inChar() {
        String ch = in.findInLine(".");
        if (ch == null) {
            if (!in.hasNextLine()) {
                return OptionalLong.empty();
            }
            in.nextLine();
        }
        return OptionalLong.of(ch == null ? '\n' : ch.charAt(0));
    }

    void outNumber(long num) {
        out.append(num);
    }

    void outChar(int ch) {
        out.appendCodePoint(ch);
    }
}
