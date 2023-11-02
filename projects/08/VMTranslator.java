import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class VMTranslator {
    private String outputFileName;
    private PrintWriter outputPrinter;
    private String currentFileName;
    private String currentFunctionName = "Sys.init";
    private int arithmeticJumpFlag = 0;

    public VMTranslator(File file) {
        try {
            outputPrinter = new PrintWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeInit() {
        outputPrinter.println("@256");
        outputPrinter.println("D=A");
        outputPrinter.println("@SP");
        outputPrinter.println("M=D");
        writeCall("Sys.init", 0);
    }

    public void writeArithmetic(String command) {
        if ("neg".equals(command) || "not".equals(command)) {
            outputPrinter.println("@SP");
            outputPrinter.println("A=M-1");
            outputPrinter.println("M=" + (command.equals("neg") ? "-M" : "!M"));
        } else {
            outputPrinter.println("@SP");
            outputPrinter.println("M=M-1");
            outputPrinter.println("A=M");
            outputPrinter.println("D=M");
            outputPrinter.println("A=A-1");
            if ("eq".equals(command) || "gt".equals(command) || "lt".equals(command)) {
                outputPrinter.println("D=M-D");
                outputPrinter.println("@" + command + "_COMPARE_" + currentFileName + "_" + arithmeticJumpFlag);
                outputPrinter.println("D;J" + command.toUpperCase());
                outputPrinter.println("@SP");
                outputPrinter.println("A=M-1");
                outputPrinter.println("M=0");
                outputPrinter.println("@" + command + "_COMPARE_END_" + currentFileName + "_" + arithmeticJumpFlag);
                outputPrinter.println("0;JMP");
                outputPrinter.println("(" + command + "_COMPARE_" + currentFileName + "_" + arithmeticJumpFlag + ")");
                outputPrinter.println("@SP");
                outputPrinter.println("A=M-1");
                outputPrinter.println("M=-1");
                outputPrinter.println("(" + command + "_COMPARE_END_" + currentFileName + "_" + arithmeticJumpFlag + ")");
            } else if ("add".equals(command)) {
                outputPrinter.println("M=M+D");
            } else if ("sub".equals(command)) {
                outputPrinter.println("M=M-D");
            } else if ("and".equals(command)) {
                outputPrinter.println("M=M&D");
            } else if ("or".equals(command)) {
                outputPrinter.println("M=M|D");
            }
        }
    }

    private void writePushPrecomputed(String letter) {
        outputPrinter.println("@SP");
        outputPrinter.println("A=M");
        outputPrinter.println("M=" + letter);
        outputPrinter.println("@SP");
        outputPrinter.println("M=M+1");
    }

    public void writePush(String segment, int index) {
        if ("temp".equals(segment)) {
            outputPrinter.println("@5");
            outputPrinter.println("D=A");
            outputPrinter.println("@" + index);
            outputPrinter.println("A=A+D");
            outputPrinter.println("D=M");
        } else if ("constant".equals(segment)) {
            outputPrinter.println("@" + index);
            outputPrinter.println("D=A");
        } else if ("static".equals(segment)) {
            outputPrinter.println("@" + currentFileName + "." + index);
            outputPrinter.println("D=M");
        } else if ("pointer".equals(segment)) {
            outputPrinter.println(index == 0 ? "@THIS" : "@THAT");
            outputPrinter.println("D=M");
        } else {
            if ("local".equals(segment)) {
                outputPrinter.println("@LCL");
            } else if ("argument".equals(segment)) {
                outputPrinter.println("@ARG");
            } else if ("this".equals(segment)) {
                outputPrinter.println("@THIS");
            } else if ("that".equals(segment)) {
                outputPrinter.println("@THAT");
            } else {
                outputPrinter.println("@null");
            }
            outputPrinter.println("D=M");
            outputPrinter.println("@" + index);
            outputPrinter.println("A=A+D");
            outputPrinter.println("D=M");
        }
        writePushPrecomputed("D");
    }

    public void writePop(String segment, int index) {
        outputPrinter.println("@SP");
        outputPrinter.println("M=M-1");
        outputPrinter.println("A=M");
        outputPrinter.println("D=M");
        if ("static".equals(segment)) {
            outputPrinter.println("@" + currentFileName + "." + index);
            outputPrinter.println("M=D");
        } else if ("pointer".equals(segment)) {
            outputPrinter.println(index == 0 ? "@THIS" : "@THAT");
            outputPrinter.println("M=D");
        } else {
            outputPrinter.println("@R13");
            outputPrinter.println("M=D");
            if ("temp".equals(segment)) {
                outputPrinter.println("@5");
                outputPrinter.println("D=A");
            } else {
                if ("local".equals(segment)) {
                    outputPrinter.println("@LCL");
                } else if ("argument".equals(segment)) {
                    outputPrinter.println("@ARG");
                } else if ("this".equals(segment)) {
                    outputPrinter.println("@THIS");
                } else if ("that".equals(segment)) {
                    outputPrinter.println("@THAT");
                } else {
                    outputPrinter.println("@null");
                }
                outputPrinter.println("D=M");
            }
            outputPrinter.println("@" + index);
            outputPrinter.println("D=A+D");
            outputPrinter.println("@R14");
            outputPrinter.println("M=D");
            outputPrinter.println("@R13");
            outputPrinter.println("D=M");
            outputPrinter.println("@R14");
            outputPrinter.println("A=M");
            outputPrinter.println("M=D");
        }
    }

    public void writeGoTo(String label) {
        outputPrinter.println("@" + currentFunctionName + "_LABEL_" + label);
        outputPrinter.println("0;JMP");
    }

    public void writeLabel(String label) {
        outputPrinter.println("(" + currentFunctionName + "_LABEL_" + label + ")");
    }

    public void writeIfGoTo(String label) {
        outputPrinter.println("@SP");
        outputPrinter.println("M=M-1");
        outputPrinter.println("A=M");
        outputPrinter.println("D=M");
        outputPrinter.println("@" + currentFunctionName + "_LABEL_" + label);
        outputPrinter.println("D;JNE");
    }

    public void writeReturn() {
        outputPrinter.println("@" + outputFileName + "_CLEAN");
        outputPrinter.println("0;JMP");
    }

    public void writeCall(String functionName, int numArguments) {
        String newLabel = functionName + "_RETURN_" + arithmeticJumpFlag;

        outputPrinter.println("@" + newLabel);
        outputPrinter.println("D=A");
        writePushPrecomputed("D");

        outputPrinter.println("@LCL");
        outputPrinter.println("D=M");
        writePushPrecomputed("D");

        outputPrinter.println("@ARG");
        outputPrinter.println("D=M");
        writePushPrecomputed("D");

        outputPrinter.println("@THIS");
        outputPrinter.println("D=M");
        writePushPrecomputed("D");

        outputPrinter.println("@THAT");
        outputPrinter.println("D=M");
        writePushPrecomputed("D");

        outputPrinter.println("@SP");
        outputPrinter.println("D=M");
        outputPrinter.println("@LCL");
        outputPrinter.println("M=D");

        outputPrinter.println("@5");
        outputPrinter.println("D=D-A");
        outputPrinter.println("@" + numArguments);
        outputPrinter.println("D=D-A");
        outputPrinter.println("@ARG");
        outputPrinter.println("M=D");

        outputPrinter.println("@" + functionName);
        outputPrinter.println("0;JMP");

        outputPrinter.println("(" + newLabel + ")");
    }

    public void writeEnd() {
        outputPrinter.println("(" + outputFileName + "_END)");
        outputPrinter.println("@" + outputFileName + "_END");
        outputPrinter.println("0;JMP");
    }

    public void writeClean() {
        outputPrinter.println("(" + outputFileName + "_CLEAN)");

        outputPrinter.println("@ARG");
        outputPrinter.println("D=M");
        outputPrinter.println("@R14");
        outputPrinter.println("M=D");

        outputPrinter.println("@LCL");
        outputPrinter.println("D=M-1");
        outputPrinter.println("@R13");
        outputPrinter.println("AM=D");
        outputPrinter.println("D=M");
        outputPrinter.println("@THAT");
        outputPrinter.println("M=D");

        outputPrinter.println("@R13");
        outputPrinter.println("D=M-1");
        outputPrinter.println("AM=D");
        outputPrinter.println("D=M");
        outputPrinter.println("@THIS");
        outputPrinter.println("M=D");

        outputPrinter.println("@R13");
        outputPrinter.println("D=M-1");
        outputPrinter.println("AM=D");
        outputPrinter.println("D=M");
        outputPrinter.println("@ARG");
        outputPrinter.println("M=D");

        outputPrinter.println("@R13");
        outputPrinter.println("D=M-1");
        outputPrinter.println("AM=D");
        outputPrinter.println("D=M");
        outputPrinter.println("@LCL");
        outputPrinter.println("M=D");

        outputPrinter.println("@R13");
        outputPrinter.println("A=M-1");
        outputPrinter.println("D=M");
        outputPrinter.println("@R13");
        outputPrinter.println("M=D");

        outputPrinter.println("@SP");
        outputPrinter.println("A=M-1");
        outputPrinter.println("D=M");
        outputPrinter.println("@R14");
        outputPrinter.println("A=M");
        outputPrinter.println("M=D");

        outputPrinter.println("@R14");
        outputPrinter.println("D=M+1");
        outputPrinter.println("@SP");
        outputPrinter.println("M=D");

        outputPrinter.println("@R13");
        outputPrinter.println("A=M");
        outputPrinter.println("0;JMP");
    }

    public void writeFunction(String functionName, int numArguments) {
        currentFunctionName = functionName;
        outputPrinter.println("(" + functionName + ")");

        for (int i = 0; i < numArguments; i++) {
            writePushPrecomputed("0");
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Error: Enter in the format java VMTranslator fileName or directoryName");
            return;
        }

        File userInput = new File(args[0]);
        ArrayList<File> files = new ArrayList<>();
        boolean checkSys = false;

        if (userInput.isDirectory() && userInput.exists()) {
            File sysFile = new File(userInput, "Sys.vm");

            if (sysFile.isFile()) {
                files.add(sysFile);
                checkSys = true;
            }

            File[] filesInDirectory = userInput.listFiles();
            for (int i = 0; i < filesInDirectory.length; i++) {
                File file = filesInDirectory[i];

                if (file.getName().endsWith(".vm") && !file.getName().equals("Sys.vm")) {
                    files.add(file);
                }
            }
        } else if (userInput.isFile() && userInput.getName().endsWith(".vm")) {
            files.add(userInput);
        }

        File outFile = new File(userInput, userInput.getName() + ".asm");
        VMTranslator translator = new VMTranslator(outFile);
        translator.outputFileName = outFile.getName().substring(0, outFile.getName().lastIndexOf("."));

        if (checkSys) {
            translator.writeInit();
        }

        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);

            translator.currentFileName = file.getName().substring(0, file.getName().lastIndexOf("."));
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty() && !line.startsWith("//")) {
                    if (line.contains("//")) {
                        line = line.substring(0, line.indexOf("//")).trim();
                    }

                    String[] commandParts = line.split(" ");

                    if ("push".equals(commandParts[0])) {
                        translator.writePush(commandParts[1], Integer.parseInt(commandParts[2]));
                    } else if ("pop".equals(commandParts[0])) {
                        translator.writePop(commandParts[1], Integer.parseInt(commandParts[2]));
                    } else if ("label".equals(commandParts[0])) {
                        translator.writeLabel(commandParts[1]);
                    } else if ("goto".equals(commandParts[0])) {
                        translator.writeGoTo(commandParts[1]);
                    } else if ("if-goto".equals(commandParts[0])) {
                        translator.writeIfGoTo(commandParts[1]);
                    } else if ("return".equals(commandParts[0])) {
                        translator.writeReturn();
                    } else if ("call".equals(commandParts[0])) {
                        translator.writeCall(commandParts[1], Integer.parseInt(commandParts[2]));
                    } else if ("function".equals(commandParts[0])) {
                        translator.writeFunction(commandParts[1], Integer.parseInt(commandParts[2]));
                    } else {
                        translator.writeArithmetic(commandParts[0]);
                    }
                    translator.arithmeticJumpFlag++;
                }
            }

            scanner.close();
        }

        translator.writeEnd();
        translator.writeClean();
        translator.outputPrinter.close();
    }
}