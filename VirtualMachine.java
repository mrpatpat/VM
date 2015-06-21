/**
 * Virtuelle Maschine mit 16 Registern, 4096 Speicherzellen
 * 
 * @author Oksana Semeshkina, Adrian Endrich
 *
 */
public class VirtualMachine {

	/**
	 * Registergröße
	 */
	private static short REGS = 16;

	/**
	 * Speichergröße
	 */
	private static short MEMS = 4096;

	/**
	 * Register
	 */
	private short[] reg = new short[REGS];

	/**
	 * Profilerarray
	 */
	private short[] profiler = new short[MEMS];

	/**
	 * Speicher
	 */
	private short[] mem = new short[MEMS];

	/**
	 * Stack für die Register
	 */
	private VMStack<Short> registerStack = new VMStack<>();

	/**
	 * Rücksprungadressenstack
	 */
	private VMStack<Short> subroutineStack = new VMStack<>();

	/**
	 * Programmzähler
	 */
	private short pc = 0;

	/**
	 * Terminiert Flag
	 */
	private boolean terminated;

	/**
	 * Einstiegspunkt
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
			VirtualMachine vm = new VirtualMachine("fibo.txt");
			vm.run();

	}

	/**
	 * Konstruktor mit Angabe der Datei
	 * 
	 * @param file
	 *            Assemblerdatei
	 */
	public VirtualMachine(String file) {

		this(Assembler.getOpCode(file));

	}

	/**
	 * Konstruktor mit Angabe des Programms in Opcode.
	 * 
	 * @param prog Opcode
	 */
	public VirtualMachine(short[] prog) {

		// write ops to mem
		for (int i = 0; i < prog.length; i++) {
			mem[i] = prog[i];
		}

		// initial state
		printMemory(false);
		printRegs();
		printStacks();

	}

	/**
	 * Führt die VM aus
	 */
	public void run() {
		terminated = false;
		while (pc < MEMS && !terminated) {
			step();
		}
	}

	/**
	 * Holt sich eine Operation aus dem Speicher
	 * @return Operation
	 */
	private short fetchOp() {
		return mem[pc];
	}

	/**
	 * führt einen Schritt aus
	 */
	private void step() {

		// fetch
		OpCode op = new OpCode(fetchOp());

		// decode
		short ry = op.getRy();
		short rx = op.getRx();
		short cmd = op.getOp();
		short val = op.getValue();
		short tm = op.getTm();
		short fm = op.getFm();

		// execute

		boolean printMem = false;
		boolean printStacks = false;

		switch (cmd) {

		case 0: // NOP
			profiler[pc++]++;
			return;

		case 1: { // LOAD
			reg[0] = val;
			System.out.print("LOADING " + val + " into R0.");
			profiler[pc++]++;
		}
			break;

		case 2: { // MOV
			if (tm == 1) {
				mem[reg[rx]] = reg[ry];
				System.out.print("MOVING R" + ry + " into @" + reg[rx] + ".");
				printMem = true;
			} else if (fm == 1) {
				reg[rx] = mem[reg[ry]];
				System.out.print("MOVING @" + reg[ry] + " into R" + rx + ".");
				printMem = true;
			} else {
				reg[rx] = reg[ry];
				System.out.print("MOVING R" + ry + " into R" + rx + ".");
			}
			profiler[pc++]++;

		}
			break;

		case 3: { // ADD
			reg[rx] = (short) (reg[rx] + reg[ry]);
			System.out.print("ADDING R" + ry + " to R" + rx + ".");
			profiler[pc++]++;
		}
			break;

		case 4: { // SUB
			reg[rx] = (short) (reg[rx] - reg[ry]);
			System.out.print("SUBTRACTING R" + ry + " from R" + rx + ".");
			profiler[pc++]++;
		}
			break;

		case 5: { // MUL
			reg[rx] = (short) (reg[rx] * reg[ry]);
			System.out.print("MULTIPLYING R" + rx + " with R" + ry + ".");
			profiler[pc++]++;
		}
			break;

		case 6: { // DIV
			reg[rx] = (short) (reg[rx] / reg[ry]);
			System.out.print("DIVIDING R" + rx + " by R" + ry + ".");
			profiler[pc++]++;
		}
			break;

		case 7: { // PUSH
			registerStack.push(reg[rx]);
			System.out.print("PUSHING R" + rx + " to the stack.");
			printStacks = true;
			profiler[pc++]++;
		}
			break;

		case 8: { // POP
			reg[rx] = registerStack.pop();
			System.out.print("POPPING from stack to R" + rx);
			printStacks = true;
			profiler[pc++]++;
		}
			break;

		case 9: { // JUMP
			pc = val;
			System.out.print("JUMPING to @" + val);
			profiler[pc]++;
		}
			break;

		case 10: { // JUMPIF
			System.out.print("JUMPING to @" + val + " IF R0 = 0. Result: " + (reg[0] == 0));
			if (reg[0] == 0)
				pc = val;
			else
				pc++;
			profiler[pc]++;
		}
			break;

		case 11: { // JUMPIFHIGHER
			System.out.print("JUMPING to @" + val + " IF R0 > 0. Result: " + (reg[0] > 0));
			if (reg[0] > 0)
				pc = val;
			else
				pc++;
			profiler[pc]++;
		}
			break;

		case 12: { // JSR
			System.out.print("JUMPING to Subroutine @" + (val + 1) + ". Putting jump back address " + pc + " on subroutine Stack.");
			subroutineStack.push((short) (pc + 1));
			printStacks = true;
			profiler[pc]++;
			pc = val;
			
		}
			break;

		case 13: { // RTS
			Short rt = subroutineStack.pop();

			if (rt != null) {
				System.out.print("RETURNING to @" + rt);
				printStacks = true;
				pc = rt;
				profiler[pc]++;
			} else {
				terminated = true;
				System.out.println("Program terminated @pc=" + pc);
				printMemory(true);
				printRegs();
				printStacks();
				return;
			}

		}
			break;

		}
		System.out.println(" - PC: " + (pc - 1));
		if (printMem)
			printMemory(false);
		printRegs();
		if (printStacks)
			printStacks();

	}
	
	/**
	 * Druckt Stacks
	 */
	public void printStacks() {
		System.out.println("registerStack: [ " + registerStack.toString() + "]\n");
		System.out.println("subroutineStack: [ " + subroutineStack.toString() + "]\n");
	}

	/**
	 * Druckt Register
	 */
	public void printRegs() {
		String header = "  R0|  R1|  R2|  R3|  R4|  R5|  R6|  R7|  R8|  R9| R10| R11| R12| R13| R14| R15";
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

	/**
	 * Druckt den Speicher
	 * @param cpu Auslastung
	 */
	public void printMemory(boolean cpu) {

		String header = (cpu ? "CPU %|" : "") + "    @|  tM|  fM|  ry|  rx|  cmd| value| realVal";
		String sep = (cpu ? "-----+" : "") + "-----+----+----+----+----+-----+------+--------";
		String row = (cpu ? "%5s|" : "") + "%5s|%4s|%4s|%4s|%4s|%5s|%6s|%8s\n";

		System.out.println("");
		System.out.println(" Memory");
		System.out.println(sep);
		System.out.println(header);
		System.out.println(sep);

		int opsDone = 0;

		if (cpu) {

			for (short s : profiler) {
				opsDone += s;
			}

		}

		for (int i = 0; i < mem.length; i++) {

			// fetch
			OpCode op = new OpCode(mem[i]);

			// decode
			short ry = op.getRy();
			short rx = op.getRx();
			short cmd = op.getOp();
			short val = op.getValue();
			short tm = op.getTm();
			short fm = op.getFm();

			if (cmd != 0) {
				if (!cpu)
					System.out.printf(row, i, tm, fm, ry, rx, cmd, val, mem[i]);
				else {
					float percent = 100f / opsDone * profiler[i];
					String p = Float.toString(percent).length() > 4 ? Float.toString(percent).substring(0, 4) : Float.toString(percent);
					System.out.printf(row, p, i, tm, fm, ry, rx, cmd, val, mem[i]);
				}
			}

		}

		System.out.println(sep);
		System.out.println("");

	}

}
