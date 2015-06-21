import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
/**
 * Ein Einfacher Assembler für die VM
 * 
 * @author Oksana Semeshkina, Adrian Endrich
 *
 */
public class Assembler {

	/**
	 * Übersetzt eine Datei in ein Array aus shorts (OpCodes)
	 * @param file Die Datei
	 * @return Opcodes
	 */
	public static short[] getOpCode(String file) {

		List<String> lines;
		short[] op = null;
		try {

			lines = readFile(file);
			op = new short[lines.size()];

			for (int i = 0; i < lines.size(); i++) {
				op[i] = resolveLine(lines.get(i));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return op;

	}

	/**
	 * Übersetzt eine Zeile.
	 * @param line Zeile
	 * @return
	 */
	private static short resolveLine(String line) {

		return new OpCode(line).get();

	}

	private static List<String> readFile(String file) throws IOException {
		Path path = Paths.get(file);
		return Files.readAllLines(path);
	}

}
