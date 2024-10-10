import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static final Map<String, Integer> FUNCTION_CODES = new HashMap<>();
    private static final Map<String, Integer> I_TYPE_OPCODES = new HashMap<>();

    static {
        FUNCTION_CODES.put("add", 32);
        FUNCTION_CODES.put("sub", 34);
        FUNCTION_CODES.put("and", 36);
        FUNCTION_CODES.put("or", 37);
        FUNCTION_CODES.put("slt", 42);

        I_TYPE_OPCODES.put("addiu", 9);
        I_TYPE_OPCODES.put("andi", 12);
        I_TYPE_OPCODES.put("beq", 4);
        I_TYPE_OPCODES.put("bne", 5);
        I_TYPE_OPCODES.put("lw", 35);
        I_TYPE_OPCODES.put("sw", 43);
        I_TYPE_OPCODES.put("ori", 13);
    }

    public static void main(String[] args) {
        String input = String.join(" ", args);
        String cleanInput = input.split("#")[0].trim();
        if (cleanInput.isEmpty()) {
            return;
        }
        String[] parts = cleanInput.replace(",", " ").split("\\s+");
        List<String> tokens = Arrays.asList(parts);
        String opcode = tokens.get(0).toLowerCase();

        try {
            switch (opcode) {
                case "add":
                case "and":
                case "or":
                case "slt":
                case "sub":
                    processRType(tokens);
                    break;
                case "addiu":
                case "andi":
                case "beq":
                case "bne":
                    processIType(tokens);
                    break;
                case "lw":
                    processLw(tokens);
                    break;
                case "sw":
                    processSw(tokens);
                    break;
                case "ori":
                    processIType(tokens);
                    break;
                case "j":
                    processJType(tokens);
                    break;
                case "lui":
                    processLui(tokens);
                    break;
                case "syscall":
                    processSyscall();
                    break;
                default:
                    print("Unknown instruction: " + opcode + "\n");
                    break;
            }
        } catch (IllegalArgumentException e) {
            print(e.getMessage() + "\n");
        }
    }

    private static void processRType(List<String> tokens) {
        if (tokens.size() == 4) {
            int rd = getRegister(tokens.get(1));
            int rs = getRegister(tokens.get(2));
            int rt = getRegister(tokens.get(3));
            int machineCode = encodeRType(tokens.get(0), rd, rs, rt);
            print(String.format("%08x\n", machineCode));
        } else {
            throw new IllegalArgumentException("Invalid format for '" + tokens.get(0) + "'. Expected: " + tokens.get(0) + " $rd, $rs, $rt");
        }
    }

    private static void processIType(List<String> tokens) {
        if (tokens.size() >= 4) {
            int rt = getRegister(tokens.get(1));
            int rs = getRegister(tokens.get(2));
            String immediateStr = tokens.get(3);
            int immediate = parseImmediate(immediateStr);
            immediate = immediate / 4;
            int machineCode = encodeIType(tokens.get(0), rt, rs, immediate);
            print(String.format("%08x\n", machineCode));
        } else {
            throw new IllegalArgumentException("Invalid format for '" + tokens.get(0) + "'. Expected: " + tokens.get(0) + " $rt, $rs, immediate");
        }
    }

    private static void processLw(List<String> tokens) {
        if (tokens.size() == 3) {
            int rt = getRegister(tokens.get(1));
            String addressToken = tokens.get(2);
            int[] addressComponents = parseAddress(addressToken);
            int baseReg = addressComponents[0];
            int offset = addressComponents[1];
            int machineCode = encodeLw(rt, baseReg, offset);
            print(String.format("%08x\n", machineCode));
        } else {
            throw new IllegalArgumentException("Invalid format for 'lw'. Expected: lw $rt, offset($base)");
        }
    }

    private static void processSw(List<String> tokens) {
        if (tokens.size() == 3) {
            int rt = getRegister(tokens.get(1));
            String addressToken = tokens.get(2);
            int[] addressComponents = parseAddress(addressToken);
            int baseReg = addressComponents[0];
            int offset = addressComponents[1];
            int machineCode = encodeSw(rt, baseReg, offset);
            print(String.format("%08x\n", machineCode));
        } else {
            throw new IllegalArgumentException("Invalid format for 'sw'. Expected: sw $rt, offset($base)");
        }
    }

    private static int[] parseAddress(String addressToken) {
        String[] parts = addressToken.split("\\(");
        int offset = 0;
        int baseReg = getRegister(parts[1].replace(")", "").trim());

        if (parts[0].trim().isEmpty()) {
            offset = 0;
        } else {
            offset = parseImmediate(parts[0].trim());
        }
        return new int[]{baseReg, offset};
    }

    private static void processJType(List<String> tokens) {
        if (tokens.size() == 2) {
            String addressStr = tokens.get(1);
            int address = parseImmediate(addressStr);
            int machineCode = encodeJType(address);
            print(String.format("%08x\n", machineCode));
        } else {
            throw new IllegalArgumentException("Invalid format for '" + tokens.get(0) + "'. Expected: " + tokens.get(0) + " label");
        }
    }

    private static void processLui(List<String> tokens) {
        if (tokens.size() == 3) {
            int rt = getRegister(tokens.get(1));
            String immediateStr = tokens.get(2);
            int immediate = parseImmediate(immediateStr);
            int machineCode = encodeLui(rt, immediate);
            print(String.format("%08x\n", machineCode));
        } else {
            throw new IllegalArgumentException("Invalid format for 'lui'. Expected: lui $rt, immediate");
        }
    }

    private static void processSyscall() {
        int machineCode = encodeSyscall();
        print(String.format("%08x\n", machineCode));
    }

    private static int getRegister(String reg) {
        switch (reg) {
            case "$zero": return 0;
            case "$at": return 1;
            case "$v0": return 2;
            case "$v1": return 3;
            case "$a0": return 4;
            case "$a1": return 5;
            case "$a2": return 6;
            case "$a3": return 7;
            case "$t0": return 8;
            case "$t1": return 9;
            case "$t2": return 10;
            case "$t3": return 11;
            case "$t4": return 12;
            case "$t5": return 13;
            case "$t6": return 14;
            case "$t7": return 15;
            case "$s0": return 16;
            case "$s1": return 17;
            case "$s2": return 18;
            case "$s3": return 19;
            case "$s4": return 20;
            case "$s5": return 21;
            case "$s6": return 22;
            case "$s7": return 23;
            case "$t8": return 24;
            case "$t9": return 25;
            case "$k0": return 26;
            case "$k1": return 27;
            case "$gp": return 28;
            case "$sp": return 29;
            case "$fp": return 30;
            case "$ra": return 31;
            default:
                throw new IllegalArgumentException("Unknown register: " + reg);
        }
    }

    private static int parseImmediate(String immediateStr) {
        if (immediateStr.startsWith("0x")) {
            return Integer.parseInt(immediateStr.substring(2), 16);
        } else {
            return Integer.parseInt(immediateStr);
        }
    }

    private static int encodeRType(String opcode, int rd, int rs, int rt) {
        int instruction = 0;
        instruction |= (rd << 11);
        instruction |= (rs << 21);
        instruction |= (rt << 16);
        instruction |= getFunctionCode(opcode);
        instruction |= (0 << 26);
        return instruction;
    }

    private static int encodeIType(String opcode, int rt, int rs, int immediate) {
        int instruction = 0;
        int opcodeValue = I_TYPE_OPCODES.get(opcode);
        instruction |= (opcodeValue << 26);
        instruction |= (rs << 21);
        instruction |= (rt << 16);
        short offset = (short) immediate;
        instruction |= offset & 0xFFFF;
        return instruction;
    }

    private static int encodeLw(int rt, int baseReg, int offset) {
        return encodeIType("lw", rt, baseReg, offset);
    }

    private static int encodeSw(int rt, int baseReg, int offset) {
        return encodeIType("sw", rt, baseReg, offset);
    }

    private static int encodeJType(int address) {
        int instruction = 0;
        instruction |= (address & 0x3FFFFFF);
        instruction |= (2 << 26);
        return instruction;
    }

    private static int encodeLui(int rt, int immediate) {
        int instruction = 0;
        instruction |= (15 << 26); // opcode for LUI
        instruction |= (rt << 16);
        short offset = (short) immediate;
        instruction |= offset & 0xFFFF;
        return instruction;
    }

    private static int encodeSyscall() {
        return 0xC;
    }

    private static int getFunctionCode(String function) {
        return FUNCTION_CODES.get(function);
    }

    private static void print(String message) {
        System.out.print(message);
    }
}