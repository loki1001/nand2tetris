@256
D=A
@SP
M=D
@Sys.init_RETURN_0
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
D=M
@LCL
M=D
@5
D=D-A
@0
D=D-A
@ARG
M=D
@Sys.init
0;JMP
(Sys.init_RETURN_0)
(Sys.init)
@4
D=A
@SP
A=M
M=D
@SP
M=M+1
@Main.fibonacci_RETURN_2
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
D=M
@LCL
M=D
@5
D=D-A
@1
D=D-A
@ARG
M=D
@Main.fibonacci
0;JMP
(Main.fibonacci_RETURN_2)
(Sys.init_LABEL_WHILE)
@Sys.init_LABEL_WHILE
0;JMP
(Main.fibonacci)
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
@2
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
D=M-D
@lt_COMPARE_Main_8
D;JLT
@SP
A=M-1
M=0
@lt_COMPARE_END_Main_8
0;JMP
(lt_COMPARE_Main_8)
@SP
A=M-1
M=-1
(lt_COMPARE_END_Main_8)
@SP
M=M-1
A=M
D=M
@Main.fibonacci_LABEL_IF_TRUE
D;JNE
@Main.fibonacci_LABEL_IF_FALSE
0;JMP
(Main.fibonacci_LABEL_IF_TRUE)
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
@FibonacciElement_CLEAN
0;JMP
(Main.fibonacci_LABEL_IF_FALSE)
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
@2
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
@Main.fibonacci_RETURN_18
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
D=M
@LCL
M=D
@5
D=D-A
@1
D=D-A
@ARG
M=D
@Main.fibonacci
0;JMP
(Main.fibonacci_RETURN_18)
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
@Main.fibonacci_RETURN_22
D=A
@SP
A=M
M=D
@SP
M=M+1
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1
@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1
@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1
@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1
@SP
D=M
@LCL
M=D
@5
D=D-A
@1
D=D-A
@ARG
M=D
@Main.fibonacci
0;JMP
(Main.fibonacci_RETURN_22)
@SP
M=M-1
A=M
D=M
A=A-1
M=M+D
@FibonacciElement_CLEAN
0;JMP
(FibonacciElement_END)
@FibonacciElement_END
0;JMP
(FibonacciElement_CLEAN)
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
