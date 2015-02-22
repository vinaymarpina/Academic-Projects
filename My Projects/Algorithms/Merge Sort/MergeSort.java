import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MergeSort {

	public static void main(String[] args) {

		System.out
				.println("-------------------------------------------------------------------------------------------------------------------");
		System.out
				.format("%85s",
						"Comparison of merge sort implementations for integers 1-2^20          ");
		System.out.println();
		System.out
				.println("--------------------------------------------------------------------------------------------------------------------");
		System.out.format("%10s%35s%35s%35s", "N",
				"SortOne-Time(secs),Memory(MB)",
				"SortTwo-Time(secs),Memory(MB)",
				"SortThree-Time(secs),Memory(MB)");
		System.out.println();
		System.out
				.println("--------------------------------------------------------------------------------------------------------------------");

		int n = 16384;
		for (int j = 1; j <= 10; j++) {
			// Sort one--------------------------------------------------------
			List<Integer> list = new ArrayList<Integer>();
			n *= 2;
			for (int i = 1; i <= n; i++) {
				list.add(i);
			}
			Collections.shuffle(list);
			Integer[] a = list.toArray(new Integer[] {});

			long startM = Runtime.getRuntime().freeMemory();
			long start = System.currentTimeMillis();
			MergeSort.sort(a, 1);
			long end = System.currentTimeMillis();
			long endM = Runtime.getRuntime().freeMemory();
			System.out.format("%10s%35s", n, ((double) (end - start) / 1000)
					+ " , " + ((long) ((double) (startM - endM) / 1048576)));
			// Sort two---------------------------------------------------------
			list = new ArrayList<Integer>();
			for (int i = 1; i <= n; i++) {
				list.add(i);
			}
			Collections.shuffle(list);
			a = list.toArray(new Integer[] {});

			startM = Runtime.getRuntime().freeMemory();
			start = System.currentTimeMillis();
			MergeSort.sort(a, 2);
			end = System.currentTimeMillis();
			endM = Runtime.getRuntime().freeMemory();
			System.out.format("%35s", ((double) (end - start) / 1000) + " , "
					+ ((long) ((double) (startM - endM) / 1048576)));
			// Sort three-------------------------------------------------------
			list = new ArrayList<Integer>();
			for (int i = 1; i <= n; i++) {
				list.add(i);
			}
			Collections.shuffle(list);
			a = list.toArray(new Integer[] {});

			startM = Runtime.getRuntime().freeMemory();
			start = System.currentTimeMillis();
			MergeSort.sort(a, 3);
			end = System.currentTimeMillis();
			endM = Runtime.getRuntime().freeMemory();
			System.out.format("%35s", ((double) (end - start) / 1000) + " , "
					+ ((long) ((double) (startM - endM) / 1048576)));
			System.out.println();
			// ----------------------------------------------------------------
		}
		System.out
				.println("--------------------------------------------------------------------------------------------------------------------");
	}

	/*
	 * Sorts given array using merge sort algorithm Arguments : 1) array of type
	 * T 2) Integer code(1,2,3) to specify method of merge
	 */
	public static <T extends Comparable<? super T>> void sort(T[] a, int type) {

		if (a == null || a.length == 0)
			throw new IllegalArgumentException("Empty or Null array passed");

		if (type == 1) {
			sortOne(a, 0, a.length - 1);
		} else if (type == 2) {
			@SuppressWarnings("unchecked")
			T[] b = (T[]) new Comparable[a.length];
			sortTwo(a, b, 0, a.length - 1);
		} else if (type == 3) {
			@SuppressWarnings("unchecked")
			T[] tmp = (T[]) new Comparable[a.length];
			int flip = sortThree(a, tmp);
			if (flip == 1) {
				if (flip == 1) {
					for (int k = 0; k <= a.length - 1; k++) {
						a[k] = tmp[k];
					}
				}
			}
		}
	}

	/*
	 * Allocate dynamic memory in each call to Merge for L and R
	 */
	public static <T extends Comparable<? super T>> void mergeOne(T[] a, int l,
			int m, int r) {

		@SuppressWarnings("unchecked")
		T[] b = (T[]) new Comparable[r - l + 1];

		int i = l, j = m + 1;
		for (int k = l; k <= r; k++) {
			if (i > m)
				b[k - l] = a[j++];
			else if (j > r)
				b[k - l] = a[i++];
			else if (a[i].compareTo(a[j]) >= 0)
				b[k - l] = a[j++];
			else
				b[k - l] = a[i++];
		}

		for (int k = 0; k < r - l + 1; k++) {
			a[l + k] = b[k];
		}
	}

	/*
	 * Iterative merge sort implementation,uses mergeOne();
	 */
	private static <T extends Comparable<? super T>> void sortOne(T[] a,
			int left, int right) {
		for (int n = 1; n < a.length; n = n * 2) {
			for (int i = 0; i <= a.length - n + 1; i += n * 2) {
				int l = i;
				int m = i + n - 1;
				int h = i + n * 2 - 1;
				if (h >= a.length)
					h = a.length - 1;
				mergeOne(a, l, m, h);
			}
		}
	}

	/*
	 * Use an auxiliary array B[ ] (same size as A). In each call to Merge, copy
	 * contents from A to B and then merge them back to A, in sorted order.
	 */
	private static <T extends Comparable<? super T>> void mergeTwo(T[] a,
			T[] b, int l, int m, int r) {

		int i = l, j = m + 1;

		for (int k = l; k <= r; k++) {
			if (i > m)
				b[k] = a[j++];
			else if (j > r)
				b[k] = a[i++];
			else if (a[i].compareTo(a[j]) >= 0)
				b[k] = a[j++];
			else
				b[k] = a[i++];
		}
		for (int k = l; k <= r; k++) {
			a[k] = b[k];
		}
	}

	/*
	 * Iterative merge sort implementation,uses mergeTwo();
	 */
	private static <T extends Comparable<? super T>> void sortTwo(T[] a, T[] b,
			int l, int r) {

		for (int n = 1; n < a.length; n = n * 2) {
			for (int i = 0; i <= a.length - n + 1; i += n * 2) {
				int lo = i;
				int m = i + n - 1;
				int hi = i + n * 2 - 1;
				if (hi >= a.length)
					hi = a.length - 1;
				mergeTwo(a, b, lo, m, hi);
			}
		}
	}

	/*
	 * Use an auxiliary array B[ ] (same size as A). When data is in A[p..q..r],
	 * it is merged into B[p..r]. When data is in B[p..q..r], it is merged into
	 * A[p..r].
	 */
	private static <T extends Comparable<? super T>> void mergeThree(T[] a,
			T[] b, int l, int m, int r) {

		int i = l, j = m + 1;

		for (int k = l; k <= r; k++) {
			if (i > m)
				b[k] = a[j++];
			else if (j > r)
				b[k] = a[i++];
			else if (a[i].compareTo(a[j]) >= 0)
				b[k] = a[j++];
			else
				b[k] = a[i++];
		}
	}

	/*
	 * Iterative merge sort implementation,uses mergeThree();
	 * 
	 * Return : Integer indicates the array containing sorted elements 0- if 'a'
	 * has data 1- if 'b' has data
	 */
	private static <T extends Comparable<? super T>> int sortThree(T[] a, T[] b) {

		int flip = 0;
		for (int n = 1; n < a.length; n = n * 2) {

			T[] atmp, btmp;
			if (flip == 0) {
				atmp = a;
				btmp = b;
			} else {
				atmp = b;
				btmp = a;
			}
			for (int i = 0; i <= a.length - n + 1; i += n * 2) {
				int l = i;
				int m = i + n - 1;
				int h = i + n * 2 - 1;
				if (h >= a.length)
					h = a.length - 1;
				mergeThree(atmp, btmp, l, m, h);
			}
			flip = 1 - flip;
		}
		return flip;
	}
}
