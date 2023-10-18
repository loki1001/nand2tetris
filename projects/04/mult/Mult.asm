// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
//
// This program only needs to handle arguments that satisfy
// R0 >= 0, R1 >= 0, and R0*R1 < 32768.

// Put your code here.
// R2 = R0 * R1

// R2 = 0
// while (R1 > 0) {
//     R2 = R0 + R2
//     R1 = R1 - 1
// }

// Make product = 0
@R2
M=0

// Check if R0 is 0, skip loop if it is
@R0
D=M
@END
D;JEQ

// Check if R1 is 0, skip loop if it is
@R1
D=M
@END
D;JEQ

(LOOP)
    // Load R1 into register, check if 0 exit
    @R1
    D=M
    @END
    D;JEQ

    // Load R0 into register
    @R0
    D=M

    // Add R0 to existing R2 value
    @R2
    M=M+D

    // Decrease R1
    @R1
    M=M-1

    // Go back to start of loop
    @LOOP
    0;JMP
(END)
@END
0;JMP
