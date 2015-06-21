import java.util.ArrayList;
import java.util.List;

public class VMStack<T> {

	List<T> stack;

	public <T> VMStack() {
		stack = new ArrayList<>();
	}

	public void push(T element) {
		stack.add(element);
	}

	public T pop() {
		
		if (stack.size() == 0)
			return null;
		
		T element = stack.get(stack.size() - 1);
		stack.remove(stack.size() - 1);
		return element;
	}

	public String toString() {
		String result = "";
		for (T element : stack) {
			result += element + " ";
		}
		return result;
	}

}
