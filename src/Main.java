//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
//public class Main {
//    public static void main(String[] args) {
//        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
//        // to see how IntelliJ IDEA suggests fixing it
//        //hi
//        //again
//        int opcode = 8; // 0000 0000 0000 0000 0000 0000 0000 1000
//        // 0010 00xx xxxx xxxx xxxx xxxx xxxx xxxx
//
//        int rs = 29;// 0000 0000 0000 0000 0000 0000 0001 1101
//        // xxxx xx11 101x xxxx xxxx xxxx xxxx xxxx
//
//        int rt = 29;// 0000 0000 0000 0000 0000 0000 0001 1101
//        // xxxx xxxx xxx1 1101 xxxx xxxx xxxx xxxx
//
//        int imm = 4;// 0000 0000 0000 0000 0000 0000 0000 0100
//        // xxxx xxxx xxxx xxxx 0000 0000 0001 1101
//
//        int inst = 0;   // 0000 0000 0000 0000 0000 0000 0000 0000
//        // 0000 0000 0000 0000 0000 0000 0000 0100
//        // 0000 0000 0000 0000 0000 0000 0001 1101
//        // 0000 0000 0000 0000 0000 0000 0001 1101
//        // 0000 0000 0000 0000 0000 0000 0000 1000
//
//        inst = inst | (imm << 0);
//        inst = inst | (rt << 16);
//        inst = inst | (rs << 21);
//        inst = inst | (opcode << 26);
//        System.out.println(String.format("%08x", inst));
//    }
//}
public class Main {
    public static void main(String[] args) {


        // Join all arguments to form the full instruction
        String inputinstruction = String.join(" ", args);;
        System.out.println(inputinstruction);
        String input = String.join(" ", args);
        // Replace commas with spaces for easier parsing and split the instruction
        String[] parts = input.replace(",", " ").split("\\s+");
        String instruction = parts[0].toLowerCase(); // Get the instruction (add, addi, j, etc.)

        // Debug: Print the parts array
        System.out.println("Parsed parts: " + String.join(", ", parts));

        try {
            switch (instruction) {
                case "add":
                    if (parts.length == 4) {
                        // R-type: add $t0 $t1 $t2
                        int rd = getRegister(parts[1]);
                        int rs = getRegister(parts[2]);
                        int rt = getRegister(parts[3]);
                        int machineCode = encodeRType("add", rd, rs, rt);
                        System.out.println(String.format("Machine code (R-type): %08x", machineCode));
                    } else {
                        System.out.println("Invalid format for 'add'. Expected: add $rd, $rs, $rt");
                    }
                    break;

                case "addi":
                    if (parts.length == 4) {
                        // I-type: addi $t0, $t1, 4
                        int rtI = getRegister(parts[1]);
                        int rsI = getRegister(parts[2]);
                        try {
                            int immediate = Integer.parseInt(parts[3]); // Can throw NumberFormatException
                            int machineCode = encodeIType("addi", rtI, rsI, immediate);
                            System.out.println(String.format("Machine code (I-type): %08x", machineCode));
                        } catch (NumberFormatException e) {
                            System.out.println("Immediate value must be an integer.");
                        }
                    } else {
                        System.out.println("Invalid format for 'addi'. Expected: addi $rt, $rs, immediate");
                    }
                    break;

                case "j":
                    if (parts.length == 2) {
                        // J-type: j loop
                        String label = parts[1];
                        int machineCode = encodeJType(label);
                        System.out.println(String.format("Machine code (J-type): %08x", machineCode));
                    } else {
                        System.out.println("Invalid format for 'j'. Expected: j label");
                    }
                    break;

                default:
                    System.out.println("Unknown instruction: " + instruction);
                    break;
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static int getRegister(String reg) {
        switch (reg) {
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
            case "$sp": return 29; // Stack pointer
            case "$ra": return 31; // Return address
            default: throw new IllegalArgumentException("Unknown register: " + reg);
        }
    }

    private static int encodeRType(String opcode, int rd, int rs, int rt) {
        int instruction = 0;
        instruction |= (rd << 11); // rd
        instruction |= (rs << 21); // rs
        instruction |= (rt << 16); // rt
        instruction |= getFunctionCode(opcode); // function code
        instruction |= (0 << 26); // opcode (for R-type, it's 0)
        return instruction;
    }

    private static int encodeIType(String opcode, int rt, int rs, int immediate) {
        int instruction = 0;
        instruction |= (immediate & 0xFFFF); // immediate (lower 16 bits)
        instruction |= (rt << 16); // rt
        instruction |= (rs << 21); // rs
        instruction |= getITypeOpcode(opcode); // opcode
        return instruction;
    }

    private static int encodeJType(String label) {
        int instruction = 0;
        int address = label.equals("loop") ? 0x0040000C : 0; // Example address
        instruction |= (address >> 2); // address (upper 26 bits)
        instruction |= (2 << 26); // opcode for J
        return instruction;
    }

    private static int getFunctionCode(String opcode) {
        switch (opcode) {
            case "add": return 32; // Function code for ADD
            // Add other R-type function codes as needed
            default: throw new IllegalArgumentException("Unknown R-type opcode: " + opcode);
        }
    }

    private static int getITypeOpcode(String opcode) {
        switch (opcode) {
            case "addi": return 8; // Opcode for ADDI
            // Add other I-type opcodes as needed
            default: throw new IllegalArgumentException("Unknown I-type opcode: " + opcode);
        }
    }
}