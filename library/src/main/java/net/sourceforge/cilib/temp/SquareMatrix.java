/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */

package net.sourceforge.cilib.temp;

import java.util.ArrayList;
import java.util.List;

public class SquareMatrix<E> {

	private List<List<E>> matrix;

	public SquareMatrix(int size, E defaultEntry) {

		matrix = new ArrayList<List<E>>(size);
		List<E> rowList;
		for (int row = 0; row < size; ++row) {
			rowList = new ArrayList<E>(size);
			matrix.add(rowList);
			for (int col = 0; col < size; ++col) {
				rowList.add(defaultEntry);
			}
		}
	}

	public void removeIndex(int index) {

		// remove row (index)
		matrix.remove(index);
		// remove column (index)
		for (List<E> column : matrix) {
			column.remove(index);
		}
	}

	public E get(int i, int j) {

		return matrix.get(i).get(j);
	}

	public void set(E e, int i, int j) {

		matrix.get(i).set(j, e);
	}

	public E symetricGet(int i, int j) {

		if (i > j) {
			return matrix.get(j).get(i);
		}

		return matrix.get(i).get(j);
	}

	public void symetricSet(E e, int i, int j) {

		if (i > j) {
			matrix.get(j).set(i, e);
		}

		matrix.get(i).set(j, e);
	}

	public int size() {

		return matrix.size();
	}
}
