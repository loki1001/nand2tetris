import java.io.File;
import java.io.FileNotFoundException;

public class JackAnalyzer {
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 1) {
            System.out.println("Error: Enter file or directory of jack file(s)");
            return;
        }

        File userInput = new File(args[0]);
        if (!userInput.exists()) {
            System.out.println(args[0] + " is not available");
        }

        File directory;
        File[] jackInputFiles;
        
        if (userInput.isDirectory()) {
            jackInputFiles = userInput.listFiles((dir, name) -> name.endsWith(".jack"));
            directory = userInput.getAbsoluteFile();
        } else {
            jackInputFiles = new File[]{userInput};
            directory = userInput.getAbsoluteFile().getParentFile();
        }

        for (int i = 0; i < jackInputFiles.length; i++) {
            CompilationEngine compilationEngine = new CompilationEngine(jackInputFiles[i], new File(directory, jackInputFiles[i].getName().substring(0, jackInputFiles[i].getName().lastIndexOf('.')) + ".xml"), new File(directory, jackInputFiles[i].getName().substring(0, jackInputFiles[i].getName().lastIndexOf('.')) + ".vm"));
            compilationEngine.compileAllClasses();
        }
    }
}
