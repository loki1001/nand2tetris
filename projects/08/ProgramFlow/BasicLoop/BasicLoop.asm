@0
D=A
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
A=M
D=M
@R13
M=D
@LCL
D=M
@0
D=A+D
@R14
M=D
@R13
D=M
@R14
A=M
M=D
(Sys.init_LABEL_LOOP_START)
@ARG
D=M
@0
A=A+D
D=M
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=M
@0
A=A+D
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
A=M
D=M
A=A-1
M=M+D
@SP
M=M-1
A=M
D=M
@R13
M=D
@LCL
D=M
@0
D=A+D
@R14
M=D
@R13
D=M
@R14
A=M
M=D
@ARG
D=M
@0
A=A+D
D=M
@SP
A=M
M=D
@SP
M=M+1
@1
D=A
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
A=M
D=M
A=A-1
M=M-D
@SP
M=M-1
A=M
D=M
@R13
M=D
@ARG
D=M
@0
D=A+D
@R14
M=D
@R13
D=M
@R14
A=M
M=D
@ARG
D=M
@0
A=A+D
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
M=M-1
A=M
D=M
@Sys.init_LABEL_LOOP_START
D;JNE
@LCL
D=M
@0
A=A+D
D=M
@SP
A=M
M=D
@SP
M=M+1
(BasicLoop_END)
@BasicLoop_END
0;JMP
(BasicLoop_CLEAN)
@ARG
D=M
@R14
M=D
@LCL
D=M-1
@R13
AM=D
D=M
@THAT
M=D
@R13
D=M-1
AM=D
D=M
@THIS
M=D
@R13
D=M-1
AM=D
D=M
@ARG
M=D
@R13
D=M-1
AM=D
D=M
@LCL
M=D
@R13
A=M-1
D=M
@R13
M=D
@SP
A=M-1
D=M
@R14
A=M
M=D
@R14
D=M+1
@SP
M=D
@R13
A=M
0;JMP
