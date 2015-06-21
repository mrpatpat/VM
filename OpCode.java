

public class OpCode {

	private short value = 0;
	
	public OpCode(short code){
		this.set(code);
	}

	public OpCode(String line) {

		String[] tokens = line.split(" |,");

		switch (tokens.length) {
		case 1:
			resolveZeroParamLine(tokens);
			break;
		case 2:
			resolveOneParamLine(tokens);
			break;
		case 3:
			resolveTwoParamLine(tokens);
			break;
		}

	}

	private void resolveTwoParamLine(String[] tokens) {

		if (tokens[0].equals("MOV")) {

			this.setOp((short) 2);
			if (tokens[1].matches("\\(R[0-9]+\\)")) {
				
				this.setRx((short) Integer.parseInt(tokens[1].replace("(", "").replace(")", "").replace("R", "")));
				this.setTm();
				
				if (tokens[2].matches("R[0-9]+")) {
					this.setRy((short) Integer.parseInt(tokens[2].replace("R", "")));
				} else {
					// error
				}
				
			} else {
				
				if (tokens[2].matches("\\(R[0-9]+\\)")) {
					
					this.setRy((short) Integer.parseInt(tokens[2].replace("(", "").replace(")", "").replace("R", "")));
					this.setFm();
					
					if (tokens[1].matches("R[0-9]+")) {
						this.setRx((short) Integer.parseInt(tokens[1].replace("R", "")));
					} else {
						// error
					}
					
				} else {
					
					if (tokens[1].matches("R[0-9]+")) {
						this.setRx((short) Integer.parseInt(tokens[1].replace("R", "")));
					} else {
						// error
					}

					if (tokens[2].matches("R[0-9]+")) {
						this.setRy((short) Integer.parseInt(tokens[2].replace("R", "")));
					} else {
						// error
					}
					
				}
	
			}

		} else {
			switch (tokens[0]) {
			case "ADD":
				this.setOp((short) 3);
				break;
			case "SUB":
				this.setOp((short) 4);
				break;
			case "MUL":
				this.setOp((short) 5);
				break;
			case "DIV":
				this.setOp((short) 6);
				break;
			default:
				// Error
				break;
			}

			if (tokens[1].matches("R[0-9]+")) {
				this.setRx((short) Integer.parseInt(tokens[1].replace("R", "")));
			} else {
				// error
			}

			if (tokens[2].matches("R[0-9]+")) {
				this.setRy((short) Integer.parseInt(tokens[2].replace("R", "")));
			} else {
				// error
			}
		}

	}

	private void resolveOneParamLine(String[] tokens) {

		switch (tokens[0]) {
		case "LOAD":
			this.setOp((short) 1);
			break;
		case "PUSH":
			this.setOp((short) 7);
			if (tokens[1].matches("R[0-9]+")) {
				this.setRx((short) Integer.parseInt(tokens[1].replace("R", "")));
				return;
			} else {
				// error
			}
			break;
		case "POP":
			this.setOp((short) 8);
			if (tokens[1].matches("R[0-9]+")) {
				this.setRx((short) Integer.parseInt(tokens[1].replace("R", "")));
				return;
			} else {
				// error
			}
			break;
		case "JMP":
			this.setOp((short) 9);
			break;
		case "JIZ":
			this.setOp((short) 10);
			break;
		case "JIH":
			this.setOp((short) 11);
			break;
		case "JSR":
			this.setOp((short) 12);
			break;
		default:
			// Error
			break;
		}

		if (tokens[1].matches("[0-9]+")) {
			this.setValue((short) Integer.parseInt(tokens[1]));
		} else {
			// error
		}

	}

	private void resolveZeroParamLine(String[] tokens) {

		switch (tokens[0]) {
		case "RTS":
			this.setOp((short) 13);
			break;
		case "NOP":
			this.set((short) 0);
			break;
		default:
			// Error
			break;
		}

	}

	private void set(short val) {
		this.value = val;
	}

	public short get() {
		return this.value;
	}

	private void setRy(short ry) {
		short op = getOp();
		short rx = getRx();
		short res = (short) ((value >> 12) << 4);
		res += ry;
		res = (short) (res << 4);
		res += rx;
		res = (short) (res << 4);
		res += op;
		this.value = res;
	}

	private void setRx(short rx) {
		short op = getOp();
		short res = (short) ((value >> 8) << 4);
		res += rx;
		res = (short) (res << 4);
		res += op;
		this.value = res;
	}

	private void setFm() {
		this.value = (short) (this.value | 0x1000);
	}

	private void unsetFm() {
		this.value = (short) (this.value & 0x2FFF);
	}

	private void unsetTm() {
		this.value = (short) (this.value & 0x1FFF);
	}

	private void setTm() {
		this.value = (short) (this.value | 0x2000);
	}

	private void setValue(short value) {
		short op = getOp();
		short res = (short) (value << 4);
		this.value = (short) (res + op);
	}

	private void setOp(short op) {
		value = (short) ((value >> 4) << 4);
		value += op;
	}

	public short getOp() {
		return (short) (value & 15);
	}

	public short getRy() {
		return (short) ((value >> 8) & 15);
	}

	public short getRx() {
		return (short) ((value >> 4) & 15);
	}

	public short getValue() {
		return (short) (value >> 4);
	}

	public short getTm() {
		return (short) ((value >> 13) & 1);
	}

	public short getFm() {
		return (short) ((value >> 12) & 1);
	}

	public String toString() {
		return "tm:" + getTm() + " fm:" + getFm() + " ry:" + getRy() + " rx:" + getRx() + " op:" + getOp() + " va:" + getValue();
	}

}
