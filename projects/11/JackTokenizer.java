import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PushbackReader;

public class JackTokenizer {
    private PushbackReader inputPushbackReader;
    private String stringVal;
    private int intVal;
    private char symbol;
    private boolean doneParsing;
    private boolean validToken;
    private String tokenType;
    private String keyword;

    public JackTokenizer(File inputFile) {
        try {
            inputPushbackReader = new PushbackReader(new FileReader(inputFile));
            doneParsing = false;
            validToken = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String stringToKeyword(String string) {
        switch (string) {
            case "class":
                return "class";
            case "method":
                return "method";
            case "function":
                return "function";
            case "constructor":
                return "constructor";
            case "int":
            case "integer":
                return "int";
            case "boolean":
                return "boolean";
            case "char":
                return "char";
            case "void":
                return "void";
            case "var":
                return "var";
            case "static":
                return "static";
            case "field":
                return "field";
            case "let":
                return "let";
            case "do":
                return "do";
            case "if":
                return "if";
            case "else":
                return "else";
            case "while":
                return "while";
            case "return":
                return "return";
            case "true":
                return "true";
            case "false":
                return "false";
            case "null":
                return "null";
            case "this":
                return "this";
            default:
                return null;
        }
    }

    public static String keywordToString(String keyword) {
        if (keyword != null) {
            if (keyword.equals("class")) {
                return "class";
            } else if (keyword.equals("method")) {
                return "method";
            } else if (keyword.equals("function")) {
                return "function";
            } else if (keyword.equals("constructor")) {
                return "constructor";
            } else if (keyword.equals("int")) {
                return "int";
            } else if (keyword.equals("boolean")) {
                return "boolean";
            } else if (keyword.equals("char")) {
                return "char";
            } else if (keyword.equals("void")) {
                return "void";
            } else if (keyword.equals("var")) {
                return "var";
            } else if (keyword.equals("static")) {
                return "static";
            } else if (keyword.equals("field")) {
                return "field";
            } else if (keyword.equals("let")) {
                return "let";
            } else if (keyword.equals("do")) {
                return "do";
            } else if (keyword.equals("if")) {
                return "if";
            } else if (keyword.equals("else")) {
                return "else";
            } else if (keyword.equals("while")) {
                return "while";
            } else if (keyword.equals("return")) {
                return "return";
            } else if (keyword.equals("true")) {
                return "true";
            } else if (keyword.equals("false")) {
                return "false";
            } else if (keyword.equals("null")) {
                return "null";
            } else if (keyword.equals("this")) {
                return "this";
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public boolean hasMoreTokens() {
        if (doneParsing) {
            return false;
        }

        if (validToken) {
            return true;
        }

        validToken = readNext();
        return validToken;
    }

    private boolean readNext() {
        if (doneParsing) {
            return validToken;
        }
        try {
            int character = inputPushbackReader.read();
            while (!doneParsing) {
                if (character == '/') {
                    character = inputPushbackReader.read();
                    switch (character) {
                        case -1:
                            doneParsing = true;
                            return false;
                        case '/':
                            int intChar = inputPushbackReader.read();
                            while (intChar != -1 && intChar != '\n') {
                                intChar = inputPushbackReader.read();
                            }
                            if (intChar == -1) {
                                doneParsing = true;
                            }
                            break;
                        case '*':
                            int commentChar = inputPushbackReader.read();
                            while (commentChar != -1) {
                                if (commentChar == '*') {
                                    commentChar = inputPushbackReader.read();
                                    if (commentChar == -1) {
                                        doneParsing = true;
                                        break;
                                    }
                                    if (commentChar == '/') {
                                        break;
                                    }
                                    inputPushbackReader.unread(commentChar);
                                }
                                commentChar = inputPushbackReader.read();
                            }
                            if (commentChar == -1) {
                                doneParsing = true;
                            }
                            break;
                        default:
                            inputPushbackReader.unread(character);
                            tokenType = "symbol";
                            symbol = '/';
                            return true;
                    }
                } else if (character == '"') {
                    StringBuilder content = new StringBuilder();
                    int doubleQuoteChar = inputPushbackReader.read();
                    while (doubleQuoteChar != -1 && doubleQuoteChar != '"') {
                        content.append((char) doubleQuoteChar);
                        doubleQuoteChar = inputPushbackReader.read();
                    }
                    if (doubleQuoteChar == -1) {
                        doneParsing = true;
                    }
                    stringVal = content.toString();
                    tokenType = "string_const";
                    return true;
                } else if (Character.isLetter(character)) {
                    int letterChar = character;
                    StringBuilder content = new StringBuilder();
                    content.append((char) letterChar);
                    letterChar = inputPushbackReader.read();
                    while (letterChar != -1 && Character.isLetterOrDigit(letterChar)) {
                        content.append((char) letterChar);
                        letterChar = inputPushbackReader.read();
                    }
                    if (letterChar == -1) {
                        doneParsing = true;
                    } else {
                        inputPushbackReader.unread(letterChar);
                    }
                    String contentString = content.toString();

                    keyword = stringToKeyword(contentString);
                    if (keyword != null) {
                        tokenType = "keyword";
                    } else {
                        tokenType = "identifier";
                        stringVal = contentString;
                    }
                    return true;
                } else if (Character.isDigit(character)) {
                    tokenType = "int_const";
                    int integerChar = character;
                    int result = integerChar - '0';
                    integerChar = inputPushbackReader.read();
                    while (integerChar != -1 && Character.isDigit(integerChar)) {
                        result *= 10;
                        result += (integerChar - '0');
                        integerChar = inputPushbackReader.read();
                    }
                    if (integerChar == -1) {
                        doneParsing = true;
                    } else {
                        inputPushbackReader.unread(integerChar);
                    }
                    intVal = result;
                    return true;
                } else if (character == -1) {
                    doneParsing = true;
                    return false;
                } else if (!Character.isWhitespace(character)) {
                    tokenType = "symbol";
                    symbol = (char) character;
                    return true;
                } 
                if (!doneParsing) {
                    character = inputPushbackReader.read();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void advance() {
        validToken = false;
    }

    public String tokenType() {
        return tokenType;
    }

    public String keyword() {
        return keyword;
    }

    public char symbol() {
        return symbol;
    }

    public String identifier() {
        return stringVal;
    }

    public int intVal() {
        return intVal;
    }

    public String stringVal() {
        return stringVal;
    }

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
            PrintStream printStream = new PrintStream(new File(directory, jackInputFiles[i].getName().substring(0, jackInputFiles[i].getName().lastIndexOf('.')) + "T.xml"));
            JackTokenizer jackTokenizer = new JackTokenizer(jackInputFiles[i]);
            printStream.println("<tokens>");
            while (jackTokenizer.hasMoreTokens()) {
                jackTokenizer.advance();
    
                String tokenType = jackTokenizer.tokenType();
                if (tokenType.equals("keyword")) {
                    printStream.println(String.format("<%s>%s</%s>", "keyword", keywordToString(jackTokenizer.keyword()), "keyword"));
                } else if (tokenType.equals("symbol")) {
                    char symbol = jackTokenizer.symbol();
                    String symbolString = switch (symbol) {
                        case '<' -> "&lt;";
                        case '>' -> "&gt;";
                        case '"' -> "&quot;";
                        case '&' -> "&amp;";
                        default -> Character.toString(symbol);
                    };
                    printStream.println(String.format("<%s>%s</%s>", "symbol", symbolString, "symbol"));
                } else if (tokenType.equals("identifier")) {
                    printStream.println(String.format("<%s>%s</%s>", "identifier", jackTokenizer.identifier(), "identifier"));
                } else if (tokenType.equals("int_const")) {
                    printStream.println(String.format("<%s>%d</%s>", "integerConstant", jackTokenizer.intVal(), "integerConstant"));
                } else if (tokenType.equals("string_const")) {
                    printStream.println(String.format("<%s>%s</%s>", "stringConstant", jackTokenizer.stringVal(), "stringConstant"));
                } else {
                    printStream.println(String.format("<%s>%s</%s>", "unsupported", tokenType.toString(), "unsupported"));
                }
            }
            printStream.println("</tokens>");
            printStream.close();
        }
    }
}
