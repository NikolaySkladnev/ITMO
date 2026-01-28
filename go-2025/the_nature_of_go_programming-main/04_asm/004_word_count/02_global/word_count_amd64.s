#include "textflag.h"

// Const array of whitespaces
DATA constantArray+0(SB)/4, $32
DATA constantArray+4(SB)/4, $133
DATA constantArray+8(SB)/4, $160
DATA constantArray+12(SB)/4, $5760
DATA constantArray+16(SB)/4, $8239
DATA constantArray+20(SB)/4, $8287
DATA constantArray+24(SB)/4, $12288
GLOBL constantArray(SB), RODATA, $28

// Const array of ranges
DATA constantArrayPair+28(SB)/4, $9    // from 2
DATA constantArrayPair+32(SB)/4, $13   // to 13
DATA constantArrayPair+36(SB)/4, $8192 // from
DATA constantArrayPair+40(SB)/4, $8202 // to
DATA constantArrayPair+44(SB)/4, $8232 // from
DATA constantArrayPair+48(SB)/4, $8233 // to
GLOBL constantArrayPair(SB), RODATA, $52

//func WordCount(data []rune) int32

TEXT ·WordCount(SB), NOSPLIT, $0
    MOVQ data+0(FP), SI      // Load pointer to data into SI
    MOVQ len+8(FP), CX       // Load length of slice into CX
    XORL AX, AX              // Clear AX for sum accumulation
    XORL R8, R8              // 0 -> last whitespace, 1 -> non-whitespace

loop:
    CMPQ CX, $0              // Compare length with 0
    JLE done                 // length <= 0 --> done

    MOVLQSX (SI), BX         // Load current int32 element (rune) into BX

    MOVQ $7, R15                  // set len(constantArray)
    MOVD $constantArray+0(SB), DI // load pointer to constantArray into DI
inner_loop:                       // check const not in ranges
    CMPQ R15, $0                  // Compare length with 0
    JLE next                      // it's probably not a whitespace

    MOVLQSX (DI), R14             // load nest whitespace

    CMPL BX, R14                  // Compare current rune with space
    JE ws                         //  rune == space --> update info

    ADDQ $4, DI     // Move pointer to next int32 element (space)
    DECQ R15        // Decrement length (number of spaces)
    JMP inner_loop  // goto inner_loop

next:                                   // check const in ranges
    MOVQ $3, R15                        // set len(constantArrayPair)
    MOVD $constantArrayPair+28(SB), DI  // load pointer to constantArrayPair into DI
inner_loop2:                            // serch in ranges
    CMPQ R15, $0                        // Compare length with 0
    JLE nonspace                        // it's not a whitespace

    MOVLQSX (DI), R13       // load current start of whitespaces range
    ADDQ $4, DI             // Move pointer to next int32 element (space range)

    CMPL    BX, R13            // Compare current rune with start
    JL      skip_check        // x < star --> skip range

    MOVLQSX (DI), R14       // load current end of whitespaces range
    ADDQ $4, DI             // Move pointer to next int32 element (space range)
    CMPL    BX, R14         // Compare current rune with end
    JG      skip_check      // x > end --> skip range

    JMP ws                  // start <= x <= end --> it's whitespace

skip_check:
    DECQ R15           // Decrement length (number of range of spaces)
    JMP inner_loop2    // goto inner_loop2

nonspace:
    MOVL $1, R8              // it's non-whitespace

continue:
    ADDQ $4, SI              // Move pointer to next int32 element (rune)
    DECQ CX                  // Decrement length (number of runes)
    JMP loop                 // Repeat loop

ws:
    CMPL R8, $0              // Check if last character was whitespace
    JE skipp_add             // If yes, skip adding to count
    ADDL $1, AX              // Increment word count in AX
    MOVL $0, R8              // Set last character type to whitespace
skipp_add:
    JMP continue             // Continue looping

done:
    CMPL R8, $0              // Check if last character was whitespace
    JE skipp_add2            // If yes, skip adding one more word count
    ADDL $1, AX              // Increment word count in AX if needed
skipp_add2:
    MOVQ AX, ret+24(FP)       // Store result in return value location (offset for ret)
    RET                       // Return from function
