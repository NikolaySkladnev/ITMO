#include "textflag.h"

TEXT ·IntSum(SB), NOSPLIT, $0
    MOVD first+0(FP), R0
    MOVD second+8(FP), R1
    ADD R0, R1
    MOVD R1, ret+16(FP)
    RET
