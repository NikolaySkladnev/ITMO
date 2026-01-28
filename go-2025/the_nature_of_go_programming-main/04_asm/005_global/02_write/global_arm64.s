#include "textflag.h"

DATA ·name+0(SB)/5,  $"hello"
DATA ·name+5(SB)/11, $"@igoroutine"
GLOBL ·name(SB), NOPTR, $16


// MOVD $name<>+0(SB), R0