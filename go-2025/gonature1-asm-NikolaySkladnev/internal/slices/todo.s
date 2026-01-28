#include "textflag.h"


TEXT ·Sum(SB), NOSPLIT, $0
    MOVQ x_base+0(FP), AX
    MOVQ x_len+8(FP), DX
    XORQ R8, R8

loop:
    CMPQ DX, $0
    JE done

    MOVLQSX (AX), R9
    ADDQ R9, R8

    ADDQ $4, AX
    SUBQ $1, DX
    JMP loop

done:
    MOVQ R8, ret+24(FP)
    RET
