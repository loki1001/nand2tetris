import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class CompilationEngine {
    private JackTokenizer jackTokenizer;
    private PrintStream printStream;
    private VMWriter vmWriter;
    private SymbolTable symbolTable;
    private int label;
    private String currentClass;

    public CompilationEngine(File file, File xmlFile, File vmFile) {
        jackTokenizer = new JackTokenizer(file);
        try {
            printStream = new PrintStream(xmlFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        vmWriter = new VMWriter(vmFile);
        label = 0;
    }

    public void compileAllClasses() {
        if (!compileClass()) {
            System.out.println("Could not compile " + currentClass);
        }
        printStream.close();
        vmWriter.close();
    }

    public boolean compileClass() {
        symbolTable = new SymbolTable();

        if (!compileKeyword("class", "<class>")) {
            return false;
        }

        currentClass = getIdent();
        if (!compileIdentifier() || !compileSymbol('{')) {
            return false;
        }

        while (compileClassVarDec());
        while (compileSubroutine());

        if (!compileSymbol('}')) {
            return false;
        }

        printStream.println("</class>");

        return true;
    }

    public boolean compileClassVarDec() {
        String kind = (keyword() != null && (keyword().equals("static") || keyword().equals("field"))) ? keyword() : null;

        if (!compileKeyword("static", "<classVarDec>") && !compileKeyword("field", "<classVarDec>")) {
            return false;
        }

        String type = makeType();
        if (!compileType()) {
            return false;
        }

        String name = getIdent();
        if (!compileIdentifier()) {
            return false;
        }

        int index = symbolTable.define(name, type, kind);

        while (compileSymbol(',')) {
            name = getIdent();
            if (!compileIdentifier()) {
                return false;
            }

            index = symbolTable.define(name, type, kind);
        }

        if (!compileSymbol(';')) {
            return false;
        }
        printStream.println("</classVarDec>");
        return true;
    }

    public boolean compileSubroutine() {
        String keyword = keyword();

        if (!compileKeyword("constructor", "<subroutineDec>") && !compileKeyword("function", "<subroutineDec>") && !compileKeyword("method", "<subroutineDec>")) {
            return false;
        }

        String type = makeType();

        if (!compileType()) {
            return false;
        }

        String variableName = getIdent();
        symbolTable.startSubroutine();

        if (!compileIdentifier()) {
            return false;
        }

        if (keyword == "method") {
            symbolTable.define(".", ".", "argument");
        }

        if (!compileSymbol('(') || compileParameterList() == -1 || !compileSymbol(')')) {
            return false;
        }
        
        printStream.println("<subroutineBody>");

        if (!compileSymbol('{')) {
            return false;
        }

        int totalVariableCount = 0;
        int variableCount;

        while ((variableCount = compileVarDec()) != -1) {
            totalVariableCount += variableCount;
        }

        int fields = symbolTable.varCount("field", symbolTable.classMap);

        vmWriter.writeFunction(keyword, String.format("%s.%s", currentClass, variableName), type, totalVariableCount);

        if (keyword == "constructor" && fields > 0) {
            vmWriter.writePush("constant", fields);
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop("pointer", 0);
        }

        if (keyword == "method") {
            vmWriter.writePush("argument", 0);
            vmWriter.writePop("pointer", 0);
        }

        if (!compileStatements() || !compileSymbol('}')) {
            return false;
        }
        
        printStream.println("</subroutineBody>");
        printStream.println("</subroutineDec>");

        return true;
    }

    public int compileParameterList() {
        printStream.println("<parameterList>");

        String type = makeType();
        int parameterCount = 0;

        if (compileType()) {
            String name = getIdent();

            if (!compileIdentifier()) {
                return -1;
            }

            parameterCount = 1;
            int index = symbolTable.define(name, type, "argument");

            while (compileSymbol(',')) {
                type = makeType();
                if (!compileType()) {
                    return -1;
                }

                name = getIdent();
                if (!compileIdentifier()) {
                    return -1;
                }

                index = symbolTable.define(name, type, "argument");
                parameterCount++;
            }
        }

        printStream.println("</parameterList>");

        return parameterCount;
    }

    public int compileVarDec() {
        int declarationCount = 0;

        if (!compileKeyword("var", "<varDec>")) {
            return -1;
        }

        String type = makeType();
        if (!compileType()) {
            return -1;
        }

        String name = getIdent();
        if (!compileIdentifier()) {
            return -1;
        }

        declarationCount = 1;

        int index = symbolTable.define(name, type, "local");

        while (compileSymbol(',')) {
            name = getIdent();
            if (!compileIdentifier()) {
                return -1;
            }

            index = symbolTable.define(name, type, "local");
            declarationCount += 1;
        }

        if (!compileSymbol(';')) {
            return -1;
        }

        printStream.println("</varDec>");

        return declarationCount;
    }

    public boolean compileStatement() {
        return compileLet() || compileIf() || compileWhile() || compileDo() || compileReturn();
    }


    public boolean compileStatements() {
        printStream.println("<statements>");
        while (compileStatement());
        printStream.println("</statements>");
        return true;
    }

    public boolean compileDo() {
        int numberArguments;

        if (!compileKeyword("do", "<doStatement>")) {
            return false;
        }

        String ident = getIdent();
        if (!compileIdentifier()) {
            return false;
        }

        String subMethod = null;
        if (compileSymbol('.')) {
            subMethod = getIdent();
            if (!compileIdentifier()) {
                return false;
            }
        }

        if (subMethod == null) {
            vmWriter.writePush("pointer", 0);
        } else if (!(symbolTable.getValue(ident) == null)) {
            String segment;

            if (symbolTable.kindOf(ident) == "local") {
                segment = "local";
            } else if (symbolTable.kindOf(ident) == "argument") {
                segment = "argument";
            } else if (symbolTable.kindOf(ident) == "field") {
                segment = "this";
            } else if (symbolTable.kindOf(ident) == "static") {
                segment = "static";
            } else {
                segment = null;
            }

            vmWriter.writePush(segment, symbolTable.indexOf(ident));
        }

        if (!compileSymbol('(') || (numberArguments = compileExpressionList()) == -1 || !compileSymbol(')') || !compileSymbol(';')) {
            return false;
        }

        if (subMethod == null) {
            vmWriter.writeCall(String.format("%s.%s", currentClass, ident), numberArguments + 1);
        } else if (symbolTable.getValue(ident) == null) {
            vmWriter.writeCall(String.format("%s.%s", ident, subMethod), numberArguments);
        } else {
            vmWriter.writeCall(String.format("%s.%s", symbolTable.typeOf(ident), subMethod), numberArguments + 1);
        }

        vmWriter.writePop("temp", 0);
        printStream.println("</doStatement>");

        return true;
    }

    public boolean compileLet() {
        if (!compileKeyword("let", "<letStatement>")) {
            return false;
        }

        String name = getIdent();
        String kind = symbolTable.kindOf(name);
        int index = symbolTable.indexOf(name);

        String segment;
        if (kind == "local") {
            segment = "local";
        } else if (kind == "argument") {
            segment = "argument";
        } else if (kind == "field") {
            segment = "this";
        } else if (kind == "static") {
            segment = "static";
        } else {
            segment = null;
        }

        if (!compileIdentifier()) {
            return false;
        }

        if (compileSymbol('[')) {
            vmWriter.writePush(segment, index);

            if (!compileExpression() || !compileSymbol(']')) {
                return false;
            }

            vmWriter.writeArithmetic("add");

            if (!compileSymbol('=') || !compileExpression()) {
                return false;
            }

            vmWriter.writePop("temp", 0);
            vmWriter.writePop("pointer", 1);
            vmWriter.writePush("temp", 0);
            segment = "that";
            index = 0;

            if (!compileSymbol(';')) {
                return false;
            }
        } else {
            if (!compileSymbol('=') || !compileExpression() || !compileSymbol(';')) {
                return false;
            }
        }

        vmWriter.writePop(segment, index);
        printStream.println("</letStatement>");

        return true;
    }

    public boolean compileWhile() {
        if (!compileKeyword("while", "<whileStatement>")) {
            return false;
        }

        String testLabel = String.format("label.while.test.%d", label++);
        String endLabel = String.format("label.while.end.%d", label++);
        vmWriter.writeLabel(testLabel);

        if (!compileSymbol('(') || !compileExpression() || !compileSymbol(')')) {
            return false;
        }

        vmWriter.writeArithmetic("not");
        vmWriter.writeIf(endLabel);

        if (!compileBody()) {
            return false;
        }

        vmWriter.writeGoto(testLabel);
        vmWriter.writeLabel(endLabel);
        printStream.println("</whileStatement>");

        return true;
    }

    public boolean compileReturn() {
        if (!compileKeyword("return", "<returnStatement>")) {
            return false;
        }

        if (testSymbol(';')) {
            vmWriter.writePush("constant", 0);
        } else {
            compileExpression();
        }

        vmWriter.writeReturn();

        if (!compileSymbol(';')) {
            return false;
        }
        
        printStream.println("</returnStatement>");

        return true;
    }

    public boolean compileIf() {
        if (!compileKeyword("if", "<ifStatement>")) {
            return false;
        }

        String alternativeLabel = String.format("label.if.alternative.%d", label++);
        String endLabel = String.format("label.if.end.%d", label++);

        if (!compileSymbol('(') || !compileExpression() || !compileSymbol(')')) {
            return false;
        }

        vmWriter.writeArithmetic("not");
        vmWriter.writeIf(alternativeLabel);

        if (!compileBody()) {
            return false;
        }

        vmWriter.writeGoto(endLabel);
        vmWriter.writeLabel(alternativeLabel);

        if (compileKeyword("else") && !compileBody()) {
            return false;
        }

        vmWriter.writeLabel(endLabel);
        printStream.println("</ifStatement>");

        return true;
    }

    public boolean compileExpression() {
        printStream.println("<expression>");

        if (!compileTerm()) {
            return false;
        }

        String binaryOperation = getBinOp();

        while (compileOp()) {
            if (!compileTerm()) {
                return false;
            }
            vmWriter.writeArithmetic(binaryOperation);
            binaryOperation = getBinOp();
        }

        printStream.println("</expression>");

        return true;
    }

    public boolean compileTerm() {
        printStream.println("<term>");

        int intVal = getInt();

        if (compileInt()) {
            vmWriter.writePush("constant", intVal);
            printStream.println("</term>");
            return true;
        }

        String stringValue = getString();
        if (compileStr()) {
            vmWriter.writePush("constant", stringValue.length());
            vmWriter.writeCall("String.new", 1);
            vmWriter.writePop("temp", 4);

            for (int i = 0; i < stringValue.length(); i++) {
                vmWriter.writePush("temp", 4);
                vmWriter.writePush("constant", stringValue.charAt(i));
                vmWriter.writeCall("String.appendChar", 2);
                vmWriter.writePop("temp", 5);
            }

            vmWriter.writePush("temp", 4);
            printStream.println("</term>");

            return true;
        }

        String keyword = keyword();
        if (compileKeyword("true") || compileKeyword("false") || compileKeyword("null") || compileKeyword("this")) {
            String segment = keyword == "this" ? "pointer" : "constant";
            if (keyword == "true") {
                vmWriter.writePush("constant", 0);
                vmWriter.writeArithmetic("not");
            } else {
                int index = 0;
                vmWriter.writePush(segment, index);
            }
            
            printStream.println("</term>");
            return true;
        }

        String unaryOperation = getUnOp();
        if (compileSymbol('-') || compileSymbol('~')) {
            if (compileTerm()) {
                vmWriter.writeArithmetic(unaryOperation);
                printStream.println("</term>");
                return true;
            } else {
                return false;
            }
        }

        if (compileSymbol('(') && compileExpression() && compileSymbol(')')) {
            printStream.println("</term>");
            return true;
        }

        final String ident = getIdent();
        String segment = null;
        int index = -1;

        if (!(symbolTable.getValue(ident) == null)) {
            if (symbolTable.kindOf(ident) == "local") {
                segment = "local";
            } else if (symbolTable.kindOf(ident) == "argument") {
                segment = "argument";
            } else if (symbolTable.kindOf(ident) == "field") {
                segment = "this";
            } else if (symbolTable.kindOf(ident) == "static") {
                segment = "static";
            } else {
                segment = null;
            }
            
            index = symbolTable.indexOf(ident);
        }

        if (!compileIdentifier()) {
            return false;
        }
        
        int numberArguments;
        if (jackTokenizer.hasMoreTokens() && jackTokenizer.tokenType() == "symbol") {
            switch (jackTokenizer.symbol()) {
                case '[':
                    vmWriter.writePush(segment, index);
                    
                    if (!compileSymbol('[') || !compileExpression() || !compileSymbol(']')) {
                        return false;
                    }

                    vmWriter.writeArithmetic("add");
                    vmWriter.writePop("pointer", 1);
                    vmWriter.writePush("that", 0);
                    break;
                case '(':
                    if (!compileSymbol('(') || (numberArguments = compileExpressionList()) == -1 || !compileSymbol(')')) {
                        return false;
                    }

                    vmWriter.writePush("pointer", 0);
                    vmWriter.writeCall(String.format("%s.%s", currentClass, ident), numberArguments + 1);
                    break;
                case '.':
                    if (!compileSymbol('.')) {
                        return false;
                    }

                    String subname = getIdent();
                    if (!compileIdentifier() || !compileSymbol('(') || (numberArguments = compileExpressionList()) == -1 || !compileSymbol(')')) {
                        return false;
                    }

                    if (symbolTable.getValue(ident) == null) {
                        vmWriter.writeCall(String.format("%s.%s", ident, subname), numberArguments);
                    } else {
                        String otherClass = symbolTable.typeOf(ident);
                        vmWriter.writePush(segment, index);
                        vmWriter.writeCall(String.format("%s.%s", otherClass, subname), numberArguments + 1);
                    }

                    break;
                default:
                    vmWriter.writePush(segment, index);
                    break;
            }
        }

        printStream.println("</term>");

        return true;
    }

    public int compileExpressionList() {
        int expressionCount = 0;

        printStream.println("<expressionList>");

        if (testSymbol(')')) {
            printStream.println("</expressionList>");
            return expressionCount;
        }

        if (compileExpression()) {
            expressionCount = 1;
            while (compileSymbol(',')) {
                if (!compileExpression()) {
                    return -1;
                }
                expressionCount++;
            }
        }
        
        printStream.println("</expressionList>");

        return expressionCount;
    }

    private boolean compileSymbol(char expectedSymbol) {
        if (!jackTokenizer.hasMoreTokens()) {
            return false;
        }

        if (jackTokenizer.tokenType() != "symbol") {
            return false;
        }

        if (jackTokenizer.symbol() != expectedSymbol) {
            return false;
        }

        writeXml(printStream, jackTokenizer);
        jackTokenizer.advance();

        return true;
    }

    public boolean compileInt() {
        if (!jackTokenizer.hasMoreTokens()) {
            return false;
        }

        if (jackTokenizer.tokenType() != "int_const") {
            return false;
        }

        writeXml(printStream, jackTokenizer);
        jackTokenizer.advance();

        return true;
    }

    public boolean compileStr() {
        if (!jackTokenizer.hasMoreTokens()) {
            return false;
        }

        if (jackTokenizer.tokenType() != "string_const") {
            return false;
        }

        writeXml(printStream, jackTokenizer);
        jackTokenizer.advance();

        return true;
    }

    private boolean compileKeyword(String kw, String prefixTag) {
        if (!jackTokenizer.hasMoreTokens()) {
            return false;
        }

        if (jackTokenizer.tokenType() != "keyword") {
            return false;
        }

        if (jackTokenizer.keyword() != kw) {
            return false;
        }

        printStream.println(prefixTag);
        writeXml(printStream, jackTokenizer);
        jackTokenizer.advance();

        return true;
    }

    private boolean compileKeyword(String kw) {
        if (!jackTokenizer.hasMoreTokens()) {
            return false;
        }

        if (jackTokenizer.tokenType() != "keyword") {
            return false;
        }

        if (jackTokenizer.keyword() != kw) {
            return false;
        }

        writeXml(printStream, jackTokenizer);
        jackTokenizer.advance();

        return true;
    }

    private boolean compileIdentifier() {
        if (!jackTokenizer.hasMoreTokens()) {
            return false;
        }

        if (jackTokenizer.tokenType() != "identifier") {
            return false;
        }

        writeXml(printStream, jackTokenizer);
        jackTokenizer.advance();

        return true;
    }

    private boolean testSymbol(char targetVariable) {
        return jackTokenizer.hasMoreTokens() && jackTokenizer.tokenType() == "symbol" && jackTokenizer.symbol() == targetVariable;
    }

    public boolean compileType() {
        return compileKeyword("int") || compileKeyword("boolean") || compileKeyword("char")|| compileKeyword("void") || compileIdentifier();
    }

    private String getIdent() {
        if (!jackTokenizer.hasMoreTokens() || jackTokenizer.tokenType() != "identifier") {
            return null;
        }

        return jackTokenizer.identifier();
    }

    private String keyword() {
        if (!jackTokenizer.hasMoreTokens() || jackTokenizer.tokenType() != "keyword") {
            return null;
        }

        return jackTokenizer.keyword();
    }

    private String getBinOp() {
        if (!jackTokenizer.hasMoreTokens() || jackTokenizer.tokenType() != "symbol") {
            return null;
        }

        char symbol = jackTokenizer.symbol();
        switch (symbol) {
            case '+':
                return "add";
            case '-':
                return "sub";
            case '*':
                return "call Math.multiply 2";
            case '/':
                return "call Math.divide 2";
            case '&':
                return "and";
            case '|':
                return "or";
            case '<':
                return "lt";
            case '>':
                return "gt";
            case '=':
                return "eq";
            default:
                return null;
        }
    }

    private String getUnOp() {
        if (!jackTokenizer.hasMoreTokens() || jackTokenizer.tokenType() != "symbol") {
            return null;
        }

        char symbol = jackTokenizer.symbol();
        switch (symbol) {
            case '-':
                return "neg";
            case '~':
                return "not";
            default:
                return null;
        }
    }

    public int getInt() {
        if (!jackTokenizer.hasMoreTokens()) {
            return -1;
        }

        if (jackTokenizer.tokenType() != "int_const") {
            return -1;
        }

        return jackTokenizer.intVal();
    }

    public String getString() {
        if (!jackTokenizer.hasMoreTokens()) {
            return null;
        }

        if (jackTokenizer.tokenType() != "string_const") {
            return null;
        }

        return jackTokenizer.stringVal();
    }
    
    private String makeType() {
        String identifier = getIdent();
        if (identifier != null) {
            return identifier;
        }
    
        String keyword = keyword();
        if (keyword != null) {
            if (keyword.equals("int") || keyword.equals("boolean") || keyword.equals("char") || keyword.equals("void")) {
                return keyword;
            }
        }
        
        return null;
    }

    public boolean compileBody() {
        if (!compileSymbol('{')) {
            return false;
        }

        return compileStatements() && compileSymbol('}');
    }

    public boolean compileOp() {
        return compileSymbol('+') || compileSymbol('-') || compileSymbol('*') || compileSymbol('/') || compileSymbol('&') || compileSymbol('|') || compileSymbol('<') || compileSymbol('>') || compileSymbol('=');
    }

    private void writeXml(PrintStream printStream, JackTokenizer jackTokenizer) {
        String tokenType = jackTokenizer.tokenType();
    
        if (tokenType.equals("keyword")) {
            printStream.println(String.format("<%s>%s</%s>", "keyword", JackTokenizer.keywordToString(jackTokenizer.keyword()), "keyword"));
        } else if (tokenType.equals("symbol")) {
            char symbol = jackTokenizer.symbol();
            String symbolString;
            switch (symbol) {
                case '<':
                    symbolString = "&lt;";
                    break;
                case '>':
                    symbolString = "&gt;";
                    break;
                case '"':
                    symbolString = "&quot;";
                    break;
                case '&':
                    symbolString = "&amp;";
                    break;
                default:
                    symbolString = Character.toString(symbol);
            }
            printStream.println(String.format("<%s>%s</%s>", "symbol", symbolString, "symbol"));  
        } else if (tokenType.equals("identifier")) {
            printStream.println(String.format("<%s>%s</%s>", "identifier", jackTokenizer.identifier(), "identifier"));
        } else if (tokenType.equals("int_const")) {
            printStream.println(String.format("<%s>%d</%s>", "integerConstant", jackTokenizer.intVal(), "integerConstant"));
        } else if (tokenType.equals("string_const")) {
            printStream.println(String.format("<%s>%s</%s>", "stringConstant", jackTokenizer.stringVal(), "stringConstant"));
        } else {
            printStream.println(String.format("<%s>%s</%s>", "unknown", tokenType.toString(), "unknown"));
        }
    }
}