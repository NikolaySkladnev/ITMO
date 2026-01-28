#include "textflag.h"


TEXT ·LowerBound(SB), NOSPLIT, $0
    MOVQ x_base+0(FP), AX      
    MOVQ x_len+8(FP), CX
    MOVQ value+24(FP), DX

    XORQ R10, R10
    MOVQ CX, R11

loop:
    CMPQ R10, R11
    JEQ  done

    MOVQ R11, R8
    SUBQ R10, R8
    SHRQ $1, R8
    MOVQ R10, R9
    ADDQ R8, R9

    MOVQ (AX)(R9*8), R12

    CMPQ R12, DX
    JLT  update

    MOVQ R9, R11
    JMP  loop

update:
    MOVQ R9, R10
    ADDQ $1, R10
    JMP  loop

done:
    MOVQ R10, ret+32(FP)
    RET
