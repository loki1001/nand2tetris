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
@6
D=A
@SP
A=M
M=D
@SP
M=M+1
@8
D=A
@SP
A=M
M=D
@SP
M=M+1
@Class1.set_RETURN_3
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
@2
D=D-A
@ARG
M=D
@Class1.set
0;JMP
(Class1.set_RETURN_3)
@SP
M=M-1
A=M
D=M
@R13
M=D
@5
D=A
@0
D=A+D
@R14
M=D
@R13
D=M
@R14
A=M
M=D
@23
D=A
@SP
A=M
M=D
@SP
M=M+1
@15
D=A
@SP
A=M
M=D
@SP
M=M+1
@Class2.set_RETURN_7
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
@2
D=D-A
@ARG
M=D
@Class2.set
0;JMP
(Class2.set_RETURN_7)
@SP
M=M-1
A=M
D=M
@R13
M=D
@5
D=A
@0
D=A+D
@R14
M=D
@R13
D=M
@R14
A=M
M=D
@Class1.get_RETURN_9
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
@Class1.get
0;JMP
(Class1.get_RETURN_9)
@Class2.get_RETURN_10
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
@Class2.get
0;JMP
(Class2.get_RETURN_10)
(Sys.init_LABEL_WHILE)
@Sys.init_LABEL_WHILE
0;JMP
(Class1.set)
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
@Class1.0
M=D
@ARG
D=M
@1
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
@Class1.1
M=D
@0
D=A
@SP
A=M
M=D
@SP
M=M+1
@StaticsTest_CLEAN
0;JMP
(Class1.get)
@Class1.0
D=M
@SP
A=M
M=D
@SP
M=M+1
@Class1.1
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
M=M-D
@StaticsTest_CLEAN
0;JMP
(Class2.set)
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
@Class2.0
M=D
@ARG
D=M
@1
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
@Class2.1
M=D
@0
D=A
@SP
A=M
M=D
@SP
M=M+1
@StaticsTest_CLEAN
0;JMP
(Class2.get)
@Class2.0
D=M
@SP
A=M
M=D
@SP
M=M+1
@Class2.1
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
M=M-D
@StaticsTest_CLEAN
0;JMP
(StaticsTest_END)
@StaticsTest_END
0;JMP
(StaticsTest_CLEAN)
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
