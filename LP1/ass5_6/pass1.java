package ass5_6;

import java.io.*;
import java.util.*;

public class pass1 {

    static LinkedHashMap<String, Integer> symtab = new LinkedHashMap<>();
    static ArrayList<String> littab = new ArrayList<>();
    static ArrayList<Integer> litaddr = new ArrayList<>();
    static ArrayList<Integer> pooltab = new ArrayList<>();
    static ArrayList<String> intermediate = new ArrayList<>();

    static Map<String, String> OPTAB = new HashMap<>();
static {
    OPTAB.put("STOP", "IS,00");
    OPTAB.put("ADD", "IS,01");
    OPTAB.put("SUB", "IS,02");
    OPTAB.put("MULT", "IS,03");
    OPTAB.put("MOVER", "IS,04");
    OPTAB.put("MOVEM", "IS,05");
    OPTAB.put("COMP", "IS,06");
    OPTAB.put("BC", "IS,07");
    OPTAB.put("DIV", "IS,08");
    OPTAB.put("READ", "IS,09");
    OPTAB.put("PRINT", "IS,10");
}

static Map<String, String> ADTAB = new HashMap<>();
static {
    ADTAB.put("START", "AD,01");
    ADTAB.put("END", "AD,02");
    ADTAB.put("ORIGIN", "AD,03");
    ADTAB.put("EQU", "AD,04");
    ADTAB.put("LTORG", "AD,05");
}

static Map<String, String> DLTAB = new HashMap<>();
static {
    DLTAB.put("DC", "DL,01");
    DLTAB.put("DS", "DL,02");
}

static Map<String, String> REGTAB = new HashMap<>();
static {
    REGTAB.put("AREG", "1");
    REGTAB.put("BREG", "2");
    REGTAB.put("CREG", "3");
    REGTAB.put("DREG", "4");
}

static Map<String, String> CONDCODE = new HashMap<>();
static {
    CONDCODE.put("LT", "1");
    CONDCODE.put("LE", "2");
    CONDCODE.put("EQ", "3");
    CONDCODE.put("GT", "4");
    CONDCODE.put("GE", "5");
    CONDCODE.put("ANY", "6");
}


    static int LC = 0;

    public static void main(String[] args) throws Exception {
        List<String> lines = readAllLines("input.asm");
   
        pooltab.add(1);

        for (String line : lines) {
            processLine(line);
        }

        assignLiterals(false);

        printTables();

        writeListToFile(intermediate, "intermediate.txt");
        writeSymtabFile("symtab.txt");
        writeLittabFile("littab.txt");
        writePooltabFile("pooltab.txt");

        System.out.println("\nFiles written: intermediate.txt, symtab.txt, littab.txt, pooltab.txt");
    }

    static List<String> readAllLines(String filename) throws IOException {
        List<String> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String l;
            while ((l = br.readLine()) != null) {
                if (!l.trim().isEmpty()) {
                    out.add(l);
                }
            }
        }
        return out;
    }

    static void processLine(String raw) {
        String line = raw.trim();
        if (line.isEmpty()) {
            return;
        }

        String[] parts = line.split("\\s+");
        int idx = 0;
        String label = null;

        if (!OPTAB.containsKey(parts[0]) && !ADTAB.containsKey(parts[0]) && !DLTAB.containsKey(parts[0])) {
            label = parts[0];
            
            symtab.put(label, LC);
            idx = 1;
            if (idx >= parts.length) {
                return; 

                    }}

        String token = parts[idx];

        // Assembler Directives
        if (ADTAB.containsKey(token)) {
            String adcode = ADTAB.get(token);
            if (token.equals("START")) {
                LC = Integer.parseInt(parts[idx + 1]);
                intermediate.add("(" + adcode + ") (C," + LC + ")");
                return;
            } else if (token.equals("LTORG")) {
                intermediate.add("(" + adcode + ")");
                assignLiterals(true);
                return;
            } else if (token.equals("END")) {
                intermediate.add("(" + adcode + ")");
                assignLiterals(false);
                return;
            } else if (token.equals("ORIGIN")) {
                String expr = parts[idx + 1];
                LC = evaluateExpression(expr);
                intermediate.add("(" + adcode + ") (C," + LC + ")");
                return;
            } else if (token.equals("EQU")) {
                if (label != null && idx + 1 < parts.length) {
                    int val = evaluateExpression(parts[idx + 1]);
                    symtab.put(label, val);
                    intermediate.add("(" + adcode + ")");
                }
                return;
            }
        }

        // Declaratives: DS / DC
        if (DLTAB.containsKey(token)) {
            String dlcode = DLTAB.get(token);
            if (token.equals("DS")) {
                int size = Integer.parseInt(parts[idx + 1]);
                if (label != null) {
                    symtab.put(label, LC);
                }
                intermediate.add("(" + dlcode + ") (C," + size + ")");
                LC += size;
                return;
            } else { // DC
                int val = Integer.parseInt(parts[idx + 1]);
                if (label != null) {
                    symtab.put(label, LC);
                }
                intermediate.add("(" + dlcode + ") (C," + val + ")");
                LC++;
                return;
            }
        }

        // Imperative Statements (IS)
        if (OPTAB.containsKey(token)) {
            String iscode = OPTAB.get(token);
            StringBuilder ic = new StringBuilder("(" + iscode + ")");

            // process operands (if any)
            for (int j = idx + 1; j < parts.length; j++) {
                String op = parts[j].trim();
                if (op.isEmpty()) {
                    continue;
                }

                // register?
                if (REGTAB.containsKey(op)) {
                    ic.append(" (").append(REGTAB.get(op)).append(")");
                } // condition code for BC?
                else if (CONDCODE.containsKey(op)) {
                    ic.append(" (").append(CONDCODE.get(op)).append(")");
                } // literal?
                else if (op.startsWith("=")) {
                    int litIdx = addLiteral(op);
                    ic.append(" (L,").append(litIdx).append(")");
                } // numeric constant e.g., C,200
                else if (op.matches("\\d+")) {
                    ic.append(" (C,").append(op).append(")");
                } // symbol (variable/label)
                else {
                    // add symbol if not present (forward reference)
                    symtab.putIfAbsent(op, -1);
                    int sIdx = getSymIndex(op);
                    ic.append(" (S,").append(sIdx).append(")");
                }
            }

            intermediate.add(ic.toString());
            LC++;
            return;
        }

        // unknown token -> ignore
    }

    // Add literal to LITTAB respecting pools:
    // - If same literal exists and is unassigned in current pool -> reuse its index
    // - If same literal exists but already assigned (in earlier pool) -> create new entry (new pool)
    static int addLiteral(String lit) {
        for (int i = littab.size() - 1; i >= 0; i--) {
            if (littab.get(i).equals(lit)) {
                if (litaddr.get(i) == -1) {
                    return i + 1; // reuse unassigned same literal in current pool
                } else {
                    // assigned in previous pool -> create new entry
                    break;
                }
            }
        }
        littab.add(lit);
        litaddr.add(-1);
        return littab.size();
    }

    // Assign addresses to currently unassigned literals starting at current LC.
    // If isLTORG==true, after assigning we add next pool start index to pooltab.
    static void assignLiterals(boolean isLTORG) {
        boolean anyUnassigned = false;
        for (int i = 0; i < littab.size(); i++) {
            if (litaddr.get(i) == -1) {
                anyUnassigned = true;
                break;
            }
        }
        if (!anyUnassigned) {
            // nothing to do
            return;
        }

        // assign in order
        for (int i = 0; i < littab.size(); i++) {
            if (litaddr.get(i) == -1) {
                // extract numeric part from literal (works for ='5', =’5’, ="5" etc.)
                String lit = littab.get(i);
                String numStr = lit.replaceAll("[^0-9\\-]+", "");
                int val = Integer.parseInt(numStr);
                litaddr.set(i, LC);
                intermediate.add("(DL,01) (C," + val + ")");
                LC++;
            }
        }

        // If this was called due to LTORG, add next pool start index (littab.size()+1)
        if (isLTORG) {
            pooltab.add(littab.size() + 1);
        }
    }

    static int getSymIndex(String sym) {
        int idx = 1;
        for (String key : symtab.keySet()) {
            if (key.equals(sym)) {
                return idx;
            }
            idx++;
        }
        return -1;
    }

    // Evaluate simple expressions: SYMBOL+num, SYMBOL-num, number or symbol
    static int evaluateExpression(String expr) {
        if (expr.contains("+")) {
            String[] p = expr.split("\\+");
            String s = p[0];
            int offset = Integer.parseInt(p[1]);
            Integer base = symtab.get(s);
            if (base == null) {
                base = -1;
            }
            return base + offset;
        } else if (expr.contains("-")) {
            String[] p = expr.split("\\-");
            String s = p[0];
            int offset = Integer.parseInt(p[1]);
            Integer base = symtab.get(s);
            if (base == null) {
                base = -1;
            }
            return base - offset;
        } else if (expr.matches("\\d+")) {
            return Integer.parseInt(expr);
        } else {
            Integer v = symtab.get(expr);
            return v == null ? -1 : v;
        }
    }

    // Printing helpers
    static void printTables() {
        System.out.println("\nSymbol Table (SYMTAB):");
        System.out.println("Symbol\tAddress");
        for (Map.Entry<String, Integer> e : symtab.entrySet()) {
            String addr = (e.getValue() == null) ? "null" : Integer.toString(e.getValue());
            System.out.printf("%-6s %s%n", e.getKey(), addr);
        }

        System.out.println("\nLiteral Table (LITTAB):");
        System.out.println("Literal\tAddress");
        for (int i = 0; i < littab.size(); i++) {
            int addr = litaddr.get(i);
            System.out.printf("%-6s %d%n", littab.get(i), addr);
        }

        System.out.println("\nPoolTable (POOLTAB):");
        for (int p : pooltab) {
            System.out.println("#" + p);
        }

        System.out.println("\nIntermediate Code:");
        for (String ic : intermediate) {
            System.out.println(ic);
        }
    }

    // File writers
    static void writeListToFile(List<String> list, String fname) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fname))) {
            for (String s : list) {
                pw.println(s);
            }
        }
    }

    static void writeSymtabFile(String fname) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fname))) {
            for (Map.Entry<String, Integer> e : symtab.entrySet()) {
                pw.println(e.getKey() + " " + e.getValue());
            }
        }
    }

    static void writeLittabFile(String fname) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fname))) {
            for (int i = 0; i < littab.size(); i++) {
                pw.println(littab.get(i) + " " + litaddr.get(i));
            }
        }
    }

    static void writePooltabFile(String fname) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fname))) {
            for (int p : pooltab) {
                pw.println("#" + p);
            }
        }
    }
}