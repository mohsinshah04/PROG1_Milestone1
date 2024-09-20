//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it
        //hi
        //again
        int opcode = 8; // 0000 0000 0000 0000 0000 0000 0000 1000
        // 0010 00xx xxxx xxxx xxxx xxxx xxxx xxxx

        int rs = 29;// 0000 0000 0000 0000 0000 0000 0001 1101
        // xxxx xx11 101x xxxx xxxx xxxx xxxx xxxx

        int rt = 29;// 0000 0000 0000 0000 0000 0000 0001 1101
        // xxxx xxxx xxx1 1101 xxxx xxxx xxxx xxxx

        int imm = 4;// 0000 0000 0000 0000 0000 0000 0000 0100
        // xxxx xxxx xxxx xxxx 0000 0000 0001 1101

        int inst = 0;   // 0000 0000 0000 0000 0000 0000 0000 0000
        // 0000 0000 0000 0000 0000 0000 0000 0100
        // 0000 0000 0000 0000 0000 0000 0001 1101
        // 0000 0000 0000 0000 0000 0000 0001 1101
        // 0000 0000 0000 0000 0000 0000 0000 1000

        inst = inst | (imm << 0);
        inst = inst | (rt << 16);
        inst = inst | (rs << 21);
        inst = inst | (opcode << 26);
        System.out.println(String.format("%08x", inst));
    }
}