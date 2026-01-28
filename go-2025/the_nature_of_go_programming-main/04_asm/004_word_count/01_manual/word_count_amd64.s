#include "textflag.h"

// from `package unicode`
// in `unicode.White_Space.R16`
//
// var _White_Space = &RangeTable{
// 	R16: []Range16{
// 		{0x0009, 0x000d, 1}, // lo-hi, stride(step)
// 		{0x0020, 0x0085, 101},
// 		{0x00a0, 0x1680, 5600},
// 		{0x2000, 0x2004, 1}, // there was 0x2000 to 0x200a
// 		{0x2005, 0x200a, 1}, // splitted it
// 		{0x2028, 0x2029, 1},
// 		{0x202f, 0x205f, 48},
// 		{0x3000, 0x3000, 1},
// 	},
// 	LatinOffset: 2,
// }
//
// Do binary search in this table
// it'll take 4 comparisons on average

// func WordCount(data []rune) int32
TEXT ·WordCount(SB), NOSPLIT, $0
    XORL AX, AX
    MOVQ data+0(FP), BX
    MOVQ data+8(FP), CX
    INCQ CX
    MOVL $1, DX

loop:
    DECQ CX
    JZ end
    MOVL (BX), DI
    ADDQ $4, BX
    CMPQ DI, $0x2005
    JLT lower_half
    JMP upper_half

lower_half: // rune <= 0x2004
    CMPQ DI, $0x0085
    JLE lower_quarter
    JMP lower_middle_quarter

lower_quarter: // rune <= 0x0085
    CMPQ DI, $0x000d
    JLE first_range
    JMP second_range

first_range: // rune <= 0x000d
    CMPQ DI, $0x0009
    JGE ws
    JMP word

second_range: // 0x000d < rune <= 0x0085
    CMPQ DI, $0x0020
    JE ws
    CMPQ DI, $0x0085
    JE ws
    JMP word

lower_middle_quarter: //  0x0085 < rune <= 0x2004
    CMPQ DI, $0x2000
    JLT third_range
    JMP ws // fourth_range

third_range: // 0x0085 < rune < 0x2000
    CMPQ DI, $0x00a0
    JE ws
    CMPQ DI, $0x1680
    JE ws
    JMP word

upper_half: // 0x2005 <= rune
    CMPQ DI, $0x2029
    JLE upper_middle_quarter
    JMP upper_quarter

upper_middle_quarter: // 0x2005 <= rune <= 0x2029
    CMPQ DI, $0x2028
    JLT fifth_range
    JMP ws // sixth_range

fifth_range: // 0x2005 <= rune < 0x2028
    CMPQ DI, $0x200a
    JLE ws
    JMP word

upper_quarter: // 0x2029 < rune
    CMPQ DI, $0x205f
    JE ws
    JLT seventh_range
    JMP eighth_range

seventh_range: // 0x2029 < rune < 0x205f
    CMPQ DI, $0x202f
    JE ws
    JMP word

eighth_range: // 0x205f < rune
    CMPQ DI, $0x3000
    JE ws
    JMP word

word:
    ADDL DX, AX
    XORL DX, DX
    JMP loop
ws:
    MOVL $1, DX
    JMP loop

end:
    MOVL AX, return+24(FP)
    RET
