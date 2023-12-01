import java.util.HashMap;

public class SymbolTable {
    private static class SymbolTableValue {
        String type;
        String kind;
        int index;
    }

    private SymbolTableValue newValue(String type, String kind, int index) {
        SymbolTableValue value = new SymbolTableValue();
        value.type = type;
        value.kind = kind;
        value.index = index;
        return value;
    }

    public SymbolTableValue getValue(String key) {
        return subroutineMap.computeIfAbsent(key, k -> classMap.get(k));
    }
        
    public HashMap<String, SymbolTableValue> classMap = null;
    public HashMap<String, SymbolTableValue> subroutineMap = null;

    public SymbolTable() {
        classMap = new HashMap<String, SymbolTableValue>();
    }

    public void startSubroutine() {
        subroutineMap = new HashMap<String, SymbolTableValue>();
    }

    public int define(String name, String type, String kind) {
        int variableIndex = -1;
    
        if (kind.equals("static") || kind.equals("field")) {
            variableIndex = varCount(kind, classMap);
            classMap.put(name, newValue(type, kind, variableIndex));
        } else if (kind.equals("local") || kind.equals("argument")) {
            variableIndex = varCount(kind, subroutineMap);
            subroutineMap.put(name, newValue(type, kind, variableIndex));
        }
    
        return variableIndex;
    }

    public int varCount(String kind, HashMap<String, SymbolTableValue> map) {
        int variableCount = 0;
        for (SymbolTableValue value : map.values()) {
            if (kind.equals(value.kind)) {
                variableCount++;
            }
        }
        return variableCount;
    }

    public String kindOf(String name) {
        return getValue(name).kind;
    }

    public String typeOf(String name) {
        return getValue(name).type;
    }

    public int indexOf(String name) {
        return getValue(name).index;
    }
}