package blue.endless.scarves.util;

import net.minecraft.screen.PropertyDelegate;

/**
 * ArrayPropertyDelegate does exactly what net.minecraft.screen.ArrayPropertyDelegate does, except
 * it doesn't cling desperately to its int array, the array is not a trade secret.
 */
public class ArrayPropertyDelegate implements PropertyDelegate {
	private int[] values;
	
	public ArrayPropertyDelegate(int[] values) {
		this.values = values;
	}
	
	@Override
	public int get(int index) {
		return values[index];
	}

	@Override
	public void set(int index, int value) {
		values[index] = value;
	}

	@Override
	public int size() {
		return values.length;
	}
	
}