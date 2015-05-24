public class VirtualMachine {

	public static int REGS = 16;
	public static int MEMS = 4096;

	short[] reg = new short[REGS];

	short[] mem = new short[MEMS];

	int pc = 0;

	public static void main(String[] args) {

		short[] p = { 0, 0, 0, 0x0131, 0, 0, 0, 0x0111 };

		VirtualMachine vm = new VirtualMachine(p);

	}

	public VirtualMachine(short[] prog) {

		// write ops to mem
		for (int i = 0; i < prog.length; i++) {
			mem[i] = prog[i];
		}

		// initial state
		printMemory();
		printRegs();

		// execute
		run();

	}

	private void run() {
		while (pc < MEMS) {
			step();
		}
	}

	public short fetchOp() {
		return mem[pc++];
	}

	public void step() {

		// fetch
		short op = fetchOp();

		// decode
		short ry = (short) ((op >> 8) & 15);
		short rx = (short) ((op >> 4) & 15);
		short cmd = (short) (op & 15);
		short val = (short) (op >> 4);
		short tmfm = (short) ((op >> 12) & 15);

		// execute
		
		
		
		switch (cmd) {

		case 0: // NOP
			return;

		case 1: { // LOAD
			reg[0] = val;
			System.out.print("LOADING " + val + " into r0.");
		}

		}
		System.out.println(" - PC: "+(pc-1));
		printRegs();

	}

	public void printRegs() {
		String header = "  r0|  r1|  r2|  r3|  r4|  r5|  r6|  r7|  r8|  r9| r10| r11| r12| r13| r14| r15";
		String sep = "----+----+----+----+----+----+----+----+----+----+----+----+----+----+----+----";
		String row = "%4s|%4s|%4s|%4s|%4s|%4s|%4s|%4s|%4s|%4s|%4s|%4s|%4s|%4s|%4s|%4s\n";

		System.out.println("");
		System.out.println(" Register");
		System.out.println(sep);
		System.out.println(header);
		System.out.println(sep);
		System.out.printf(row, reg[0], reg[1], reg[2], reg[3], reg[4], reg[5], reg[6], reg[7], reg[8], reg[9], reg[10], reg[11], reg[12], reg[13], reg[14],
				reg[15]);
		System.out.println(sep);
		System.out.println("");
	}

	public void printMemory() {

		String header = "    @|  tM/fM|  ry|  rx|  cmd| value";
		String sep = "-----+-------+----+----+-----+------";
		String row = "%5s|%7s|%4s|%4s|%5s|%6s\n";

		System.out.println("");
		System.out.println(" Memory");
		System.out.println(sep);
		System.out.println(header);
		System.out.println(sep);

		for (int i = 0; i < mem.length; i++) {

			short ry = (short) ((mem[i] >> 8) & 15);
			short rx = (short) ((mem[i] >> 4) & 15);
			short cmd = (short) (mem[i] & 15);
			short val = (short) (mem[i] >> 4);
			short tmfm = (short) ((mem[i] >> 12) & 15);

			if (cmd != 0) {
				System.out.printf(row, i, tmfm, ry, rx, cmd, val);
			}

		}

		System.out.println(sep);
		System.out.println("");

	}

}
