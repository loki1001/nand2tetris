import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;

public class Assembler {
    private static final HashMap<String, String> SymbolTable = new HashMap<>();
    private static final HashMap<String, String> DestTable = new HashMap<>();
    private static final HashMap<String, String> JumpTable = new HashMap<>();

    static {
        // Put predefined symbols into HashMap
        SymbolTable.put("SP", "0");
        SymbolTable.put("LCL", "1");
        SymbolTable.put("ARG", "2");
        SymbolTable.put("THIS", "3");
        SymbolTable.put("THAT", "4");
        SymbolTable.put("R0", "0");
        SymbolTable.put("R1", "1");
        SymbolTable.put("R2", "2");
        SymbolTable.put("R3", "3");
        SymbolTable.put("R4", "4");
        SymbolTable.put("R5", "5");
        SymbolTable.put("R6", "6");
        SymbolTable.put("R7", "7");
        SymbolTable.put("R8", "8");
        SymbolTable.put("R9", "9");
        SymbolTable.put("R10", "10");
        SymbolTable.put("R11", "11");
        SymbolTable.put("R12", "12");
        SymbolTable.put("R13", "13");
        SymbolTable.put("R14", "14");
        SymbolTable.put("R15", "15");
        SymbolTable.put("SCREEN", "16384");
        SymbolTable.put("KBD", "24576");

        // Put comp a=0
        SymbolTable.put("0", "101010");
        SymbolTable.put("1", "111111");
        SymbolTable.put("-1", "111010");
        SymbolTable.put("D", "001100");
        SymbolTable.put("A", "110000");
        SymbolTable.put("!D", "001101");
        SymbolTable.put("!A", "1100011");
        SymbolTable.put("-D", "001111");
        SymbolTable.put("-A", "110011");
        SymbolTable.put("D+1", "011111");
        SymbolTable.put("A+1", "110111");
        SymbolTable.put("D-1", "001110");
        SymbolTable.put("A-1", "110010");
        SymbolTable.put("D+A", "000010");
        SymbolTable.put("D-A", "010011");
        SymbolTable.put("A-D", "000111");
        SymbolTable.put("D&A", "000000");
        SymbolTable.put("D|A", "010101");

        // Put comp a=1
        SymbolTable.put("M", "110000");
        SymbolTable.put("!M", "110001");
        SymbolTable.put("-M", "110011");
        SymbolTable.put("M+1", "110111");
        SymbolTable.put("M-1", "110010");
        SymbolTable.put("D+M", "000010");
        SymbolTable.put("D-M", "010011");
        SymbolTable.put("M-D", "000111");
        SymbolTable.put("D&M", "000000");
        SymbolTable.put("D|M", "010101");

        // Put dest
        DestTable.put("null", "000");
        DestTable.put("M", "001");
        DestTable.put("D", "010");
        DestTable.put("MD", "011");
        DestTable.put("A", "100");
        DestTable.put("AM", "101");
        DestTable.put("AD", "110");
        DestTable.put("AMD", "111");

        // Put jump
        JumpTable.put("null", "000");
        JumpTable.put("JGT", "001");
        JumpTable.put("JEQ", "010");
        JumpTable.put("JGE", "011");
        JumpTable.put("JLT", "100");
        JumpTable.put("JNE", "101");
        JumpTable.put("JLE", "110");
        JumpTable.put("JMP", "111");
    }

    public void assemble(String fileName) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            int lineNumber = 0;
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                line = line.split("//")[0].trim();
                if (line.isEmpty()) {
                    continue;
                }
                
                if (line.startsWith("(") && line.endsWith(")")) {
                    String label = line.substring(1, line.length() - 1);
                    SymbolTable.put(label, Integer.toString(lineNumber));
                } else {
                    lineNumber++;
                }
            }

            bufferedReader.close();

            try (BufferedReader bufferedReader2 = new BufferedReader(new FileReader(fileName))) {
                PrintWriter printWriter = new PrintWriter(new FileWriter(fileName.replace(".asm", ".hack")));

                int startingAddress = 16;

                while ((line = bufferedReader2.readLine()) != null) {
                    line = line.split("//")[0].trim();
                    if (line.isEmpty() || line.startsWith("//") || (line.startsWith("(") && line.endsWith(")"))) {
                        continue;
                    }
                
                    if (line.startsWith("@")) {
                        String symbol = line.substring(1);
                        int address;
                
                        if (Character.isDigit(symbol.charAt(0))) {
                            address = Integer.parseInt(symbol);
                        } else {
                            if (SymbolTable.containsKey(symbol)) {
                                address = Integer.parseInt(SymbolTable.get(symbol));
                            } else {
                                if (!symbol.startsWith("@")) {
                                    SymbolTable.put(symbol, Integer.toString(startingAddress));
                                    address = startingAddress;
                                    startingAddress++;
                                } else {
                                    throw new IllegalArgumentException("Invalid symbol: " + symbol);
                                }
                            }
                        }
                
                        String binaryAddress = String.format("%16s", Integer.toBinaryString(address)).replace(' ', '0');
                        printWriter.println(binaryAddress);
                    } else {
                        String dest = "null";
                        String jump = "null";
                        String comp = line;

                        if (line.contains("=")) {
                            String[] destComp = line.split("=");
                            dest = destComp[0];
                            comp = destComp[1];
                        }

                        if (line.contains(";")) {
                            String[] compJump = comp.split(";");
                            comp = compJump[0];
                            jump = compJump[1];
                        }

                        int aBit = comp.contains("M") ? 1 : 0;
                        String binaryComp = SymbolTable.get(comp);
                        String binaryDest = DestTable.get(dest);
                        String binaryJump = JumpTable.get(jump);

                        printWriter.println("111" + aBit + binaryComp + binaryDest + binaryJump);
                    }
                }

                printWriter.close();
                bufferedReader2.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Error: Enter in the format java Assembler fileName");
            return;
        }

        Assembler assembler = new Assembler();
        assembler.assemble(args[0]);
        System.out.println("Output saved to " + args[0].replace(".asm", ".hack"));
    }
}