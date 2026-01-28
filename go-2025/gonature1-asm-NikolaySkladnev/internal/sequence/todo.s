#include "textflag.h"


TEXT ·Fibonacci(SB), NOSPLIT, $0
    MOVQ x_base+0(FP), AX

    CMPQ AX, $1
    JLS le
    JMP gt

le:
    MOVQ AX, ret+8(FP)
    RET

gt:
    SUBQ $1, AX
    MOVQ $0, R8
    MOVQ $1, R9
    MOVQ R8, R11

loop:
    CMPQ AX, $0
    JE done

    MOVQ R9, R10
    ADDQ R8, R9
    MOVQ R10, R8

    MOVQ R9, R11

    SUBQ $1, AX
    JMP loop

done:
    MOVQ R11, ret+8(FP)
    RET

