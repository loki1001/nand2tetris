import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class VMWriter {
    private FileWriter fileWriter;

    public VMWriter(File file) {
        try {
            fileWriter = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writePush(String segment, int index) {
        try {
            fileWriter.write(String.format("push %s %d\n", segment, index));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writePop(String segment, int index) {
        try {
            fileWriter.write(String.format("pop %s %d\n", segment, index));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeArithmetic(String command) {
        try {
            fileWriter.write(command + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLabel(String label) {
        try {
            fileWriter.write(String.format("label %s\n", label));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeGoto(String label) {
        try {
            fileWriter.write(String.format("goto %s\n", label));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeIf(String label) {
        try {
            fileWriter.write(String.format("if-goto %s\n", label));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeCall(String name, int numberArguments) {
        try {
            fileWriter.write(String.format("call %s %d\n", name, numberArguments));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFunction(String keyword, String name, String type, int numberArguments) {
        try {
            fileWriter.write(String.format("function %s %d\n", name, numberArguments));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeReturn() {
        try {
            fileWriter.write("return\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}