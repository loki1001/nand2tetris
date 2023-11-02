import java.io.*;
import java.util.Scanner;

public class VMTranslator {
    private PrintWriter outPrinter;
    private int arthJumpFlag = 0;

    public VMTranslator(String outputFileName) {
        try {
            this.outPrinter = new PrintWriter(new BufferedWriter(new FileWriter(outputFileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeArithmetic(String command) {
        if (command.equals("add")) {
            outPrinter.print(arithmeticTemplate1() + "M=M+D\n");
        } else if (command.equals("sub")) {
            outPrinter.print(arithmeticTemplate1() + "M=M-D\n");
        } else if (command.equals("and")) {
            outPrinter.print(arithmeticTemplate1() + "M=M&D\n");
        } else if (command.equals("or")) {
            outPrinter.print(arithmeticTemplate1() + "M=M|D\n");
        } else if (command.equals("gt")) {
            outPrinter.print(arithmeticTemplate2("JLE"));
            arthJumpFlag++;
        } else if (command.equals("lt")) {
            outPrinter.print(arithmeticTemplate2("JGE"));
            arthJumpFlag++;
        } else if (command.equals("eq")) {
            outPrinter.print(arithmeticTemplate2("JNE"));
            arthJumpFlag++;
        } else if (command.equals("not")) {
            outPrinter.print("@SP\nA=M-1\nM=!M\n");
        } else if (command.equals("neg")) {
            outPrinter.print("D=0\n@SP\nA=M-1\nM=D-M\n");
        } else {
            throw new IllegalArgumentException("Call writeArithmetic() for a non-arithmetic command");
        }
    }

    public void writePushPop(int command, String segment, int index) {
        if (command == 1) {
            if (segment.equals("constant")) {
                outPrinter.print("@" + index + "\n" + "D=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
            } else if (segment.equals("local") || segment.equals("argument") || segment.equals("this") || segment.equals("that")) {
                outPrinter.print(pushTemplate1(segment, index, false));
            } else if (segment.equals("temp") || segment.equals("pointer") || segment.equals("static")) {
                outPrinter.print(pushTemplate1(segment, index, true));
            }
        } else if (command == 2) {
            if (segment.equals("local") || segment.equals("argument") || segment.equals("this") || segment.equals("that")) {
                outPrinter.print(popTemplate1(segment, index, false));
            } else if (segment.equals("temp") || segment.equals("pointer") || segment.equals("static")) {
                outPrinter.print(popTemplate1(segment, index, true));
            }
        } else {
            throw new IllegalArgumentException("Call writePushPop() for a non-pushpop command");
        }
    }

    private String arithmeticTemplate1() {
        return "@SP\nAM=M-1\nD=M\nA=A-1\n";
    }

    private String arithmeticTemplate2(String type) {
        return "@SP\nAM=M-1\nD=M\nA=A-1\nD=M-D\n@FALSE" + arthJumpFlag + "\nD;" + type + "\n@SP\nA=M-1\nM=-1\n@CONTINUE" + arthJumpFlag + "\n0;JMP\n(FALSE" + arthJumpFlag + ")\n@SP\nA=M-1\nM=0\n(CONTINUE" + arthJumpFlag + ")\n";
    }

    private String pushTemplate1(String segment, int index, boolean isDirect) {
        String segmentSymbol = getSegmentSymbol(segment, index);
        String noPointerCode = (isDirect) ? "" : "@" + index + "\n" + "A=D+A\nD=M\n";
        return "@" + segmentSymbol + "\nD=M\n" + noPointerCode + "@SP\nA=M\nM=D\n@SP\nM=M+1\n";
    }
    
    private String popTemplate1(String segment, int index, boolean isDirect) {
        String segmentSymbol = getSegmentSymbol(segment, index);
        String noPointerCode = (isDirect) ? "D=A\n" : "D=M\n@" + index + "\nD=D+A\n";
        return "@" + segmentSymbol + "\n" + noPointerCode + "@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n";
    }
    
    private String getSegmentSymbol(String segment, int index) {
        switch (segment) {
            case "local":
                return "LCL";
            case "argument":
                return "ARG";
            case "this":
                return (index == 0) ? "THIS" : "THAT";
            case "that":
                return (index == 0) ? "THIS" : "THAT";
            case "temp":
                return "R5";
            case "pointer":
                return (index == 0) ? "THIS" : "THAT";
            case "static":
                return String.valueOf(16 + index); // Adjust this if needed based on your static segment base address
            default:
                throw new IllegalArgumentException("Invalid segment: " + segment);
        }
    }    

    public void close() {
        outPrinter.close();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Error: Enter in the format java VMTranslator fileName");
            return;
        }

        String outputFileName = args[0].replace(".vm", ".asm");
        VMTranslator translator = new VMTranslator(outputFileName);
        File inputFile = new File(args[0]);

        try (Scanner scanner = new Scanner(inputFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty() && !line.startsWith("//")) {
                    String[] segments = line.split("\\s+");
                    if (segments.length == 1) {
                        translator.writeArithmetic(segments[0]);
                    } else if (segments.length == 3) {
                        String command = segments[0];
                        String segment = segments[1];
                        int index = Integer.parseInt(segments[2]);
                        int commandType = command.equalsIgnoreCase("push") ? 1 : 2;
                        translator.writePushPop(commandType, segment, index);
                    } else {
                        throw new IllegalArgumentException("Invalid VM command: " + line);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        translator.close();
        System.out.println("Output saved to " + outputFileName);
    }
}