package com.yu212.pietcraft.piet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalLong;
import java.util.function.Consumer;

public enum PietCommand {
    PUSH(interpreter -> {
        interpreter.stack.push((long)interpreter.getCurrentBlockSize());
    }),
    POP(interpreter -> {
        if (interpreter.stack.size() >= 1) {
            interpreter.stack.pop();
        }
    }),
    ADD(interpreter -> {
        if (interpreter.stack.size() >= 2) {
            long y = interpreter.stack.pop();
            long x = interpreter.stack.pop();
            interpreter.stack.push(x + y);
        }
    }),
    SUBSTRACT(interpreter -> {
        if (interpreter.stack.size() >= 2) {
            long y = interpreter.stack.pop();
            long x = interpreter.stack.pop();
            interpreter.stack.push(x - y);
        }
    }),
    MULTIPLY(interpreter -> {
        if (interpreter.stack.size() >= 2) {
            long y = interpreter.stack.pop();
            long x = interpreter.stack.pop();
            interpreter.stack.push(x * y);
        }
    }),
    DIVIDE(interpreter -> {
        if (interpreter.stack.size() >= 2) {
            long y = interpreter.stack.pop();
            long x = interpreter.stack.pop();
            interpreter.stack.push(x / y);
        }
    }),
    MOD(interpreter -> {
        if (interpreter.stack.size() >= 2) {
            long y = interpreter.stack.pop();
            long x = interpreter.stack.pop();
            interpreter.stack.push(x < 0 ? x % y + y : x % y);
        }
    }),
    NOT(interpreter -> {
        if (interpreter.stack.size() >= 1) {
            long x = interpreter.stack.pop();
            interpreter.stack.push(x == 0 ? 1L : 0L);
        }
    }),
    GREATER(interpreter -> {
        if (interpreter.stack.size() >= 2) {
            long y = interpreter.stack.pop();
            long x = interpreter.stack.pop();
            interpreter.stack.push(x > y ? 1L : 0L);
        }
    }),
    POINTER(interpreter -> {
        if (interpreter.stack.size() >= 1) {
            long x = interpreter.stack.pop();
            interpreter.dp = interpreter.dp.rotate((int)(x % 4 + 4));
        }
    }),
    SWITCH(interpreter -> {
        if (interpreter.stack.size() >= 1) {
            long x = interpreter.stack.pop();
            interpreter.cc = interpreter.cc.rotate((int)(x % 2 * 2 + 4));
        }
    }),
    DUPLICATE(interpreter -> {
        if (interpreter.stack.size() >= 1) {
            long x = interpreter.stack.peek();
            interpreter.stack.push(x);
        }
    }),
    ROLL(interpreter -> {
        if (interpreter.stack.size() >= 2) {
            long y = interpreter.stack.pop();
            long x = interpreter.stack.pop();
            if (0 < x && x <= interpreter.stack.size()) {
                long c = (y % x + x) % x;
                List<Long> copy = new ArrayList<>();
                for (long i = 0; i < x; i++) {
                    copy.add(interpreter.stack.pop());
                }
                for (long i = 0; i < x; i++) {
                    interpreter.stack.push(copy.get((int)((c - 1 - i + x) % x)));
                }
            }
        }
    }),
    IN_NUMBER(interpreter -> {
        OptionalLong num = interpreter.inNumber();
        if (num.isPresent()) {
            interpreter.stack.push(num.getAsLong());
        }
    }),
    IN_CHAR(interpreter -> {
        OptionalLong ch = interpreter.inChar();
        if (ch.isPresent()) {
            interpreter.stack.push(ch.getAsLong());
        }
    }),
    OUT_NUMBER(interpreter -> {
        if (interpreter.stack.size() >= 1) {
            long x = interpreter.stack.pop();
            interpreter.outNumber(x);
        }
    }),
    OUT_CHAR(interpreter -> {
        if (interpreter.stack.size() >= 1) {
            long x = interpreter.stack.pop();
            interpreter.outChar((int)x);
        }
    });

    private final Consumer<PietInterpreter> command;

    PietCommand(Consumer<PietInterpreter> command) {
        this.command = command;
    }

    public static PietCommand of(PietCodel codel1, PietCodel codel2) {
        int dh = (codel2.h - codel1.h + 6) % 6;
        int dv = (codel2.v - codel1.v + 3) % 3;
        return values()[dh * 3 + dv - 1];
    }

    public void execute(PietInterpreter interpreter) {
        command.accept(interpreter);
    }
}
