package ass5_6;

import java.io.*;
import java.util.*;

public class pass2 {

    static Map<Integer, Integer> symtab = new HashMap<>(); 
    static Map<Integer, Integer> littab = new HashMap<>(); 

    public static void main(String[] args) throws Exception {
        loadSymtab("symtab.txt");
        loadLittab("littab.txt");

        List<String> machineCode = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("intermediate.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String mc = translateLine(line);
                if (mc != null && !mc.isEmpty()) {
                    machineCode.add(mc);
                }
            }
        }


        try (PrintWriter pw = new PrintWriter(new FileWriter("machinecode.txt"))) {
            for (String s : machineCode) {
                pw.println(s);
            }
        }

        System.out.println("✅ Machine code generated in machinecode.txt");
    }


    static String translateLine(String line) {

        if (line.startsWith("(AD")) {
            return ""; 
        }


        if (line.startsWith("(DL,01)")) { 
            String val = extractConstant(line);
            return "00 0 " + val; 
        }
        if (line.startsWith("(DL,02)")) { 
            
            return "";
        }

      
        if (line.startsWith("(IS")) {
            String[] parts = line.split("\\)"); 

        
            String opcode = parts[0].replace("(", "").replace("IS,", "").trim();

            String reg = "0";
            String addr = "000";

            if (parts.length > 1 && parts[1].contains("(")) {
                reg = parts[1].replace("(", "").replace(")", "").trim();
            }

            if (parts.length > 2 && parts[2].contains("(")) {
                String op2 = parts[2].replace("(", "").replace(")", "").trim();

                if (op2.startsWith("S,")) {
                    int idx = Integer.parseInt(op2.split(",")[1]);
                    addr = String.valueOf(symtab.get(idx));
                } else if (op2.startsWith("L,")) {
                    int idx = Integer.parseInt(op2.split(",")[1]);
                    addr = String.valueOf(littab.get(idx));
                } else if (op2.startsWith("C,")) {
                    addr = op2.split(",")[1];
                }
            }

            return opcode + " " + reg + " " + addr;
        }

        return "";
    }

    static String extractConstant(String line) {
        int start = line.indexOf("(C,") + 3;
        int end = line.indexOf(")", start);
        return line.substring(start, end);
    }


    static void loadSymtab(String fname) throws IOException {
        int index = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(fname))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.trim().split("\\s+");
                if (p.length >= 2) {
                    symtab.put(index, Integer.parseInt(p[1]));
                    index++;
                }
            }
        }
    }

    // Load LITTAB into indexed map (index → address)
    static void loadLittab(String fname) throws IOException {
        int index = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(fname))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.trim().split("\\s+");
                if (p.length >= 2) {
                    littab.put(index, Integer.parseInt(p[1]));
                    index++;
                }
            }
        }
    }
}