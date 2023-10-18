// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.

// Initialize rows with 8192 (32 bits * 256 rows)
(INITIALIZE)
	@8192
	D=A
	@pixels
	M=D

(INFINITE_LOOP)
    // Decrement pixels by 1
	@pixels
	M=M-1
	D=M
    // Go back to INITIALIZE if pixels is less than 0
	@INITIALIZE
	D;JLT
    // Get keyboard input, if 0 (no key) go to WHITE, if 1 (key) go to BLACK
	@KBD
	D=M
	@TURN_WHITE
	D;JEQ
	@TURN_BLACK
	0;JMP

(TURN_WHITE)
    // Store current memory address to SCREEN, then add it to pixels, change pixel to white
	@SCREEN
	D=A                
	@pixels        
	A=D+M
	M=0
    // Go back to INFINITE_LOOP to keep listening for keystroke
	@INFINITE_LOOP
	0;JMP

(TURN_BLACK)
    // Store current memory address to SCREEN, then add it to pixels, change pixel to black     
	@SCREEN
	D=A
	@pixels
	A=D+M
	M=-1
    // Go back to INFINITE_LOOP to keep listening for keystroke
	@INFINITE_LOOP
	0;JMP