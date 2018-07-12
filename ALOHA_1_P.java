import java.util.ArrayList;
import java.io.*;
import java.util.LinkedList;

public class ALOHA_1_P {

	public static void main(String[] args) {

		int message = 10000;
		int subsciber = 16;
		double probability = 1.0 / subsciber;
		double probability_1 = 2.0 / subsciber;// 0.367;
		double lamdba_er = Math.pow((1.0 - 1.0 / (double) subsciber), ((double) subsciber - 1.0));
		double lambda = 0.01;
		LinkedList<Double> la = new LinkedList<>();
		LinkedList<Double> d_opt = new LinkedList<>();
		LinkedList<Double> d_1_p = new LinkedList<>();
		LinkedList<Double> d_1_p2 = new LinkedList<>();

		while (lambda < lamdba_er) {
			la.add(lambda);
			d_1_p.add(aloha(lambda, subsciber, message, probability, false));
			d_opt.add(opt_aloha(lambda, subsciber, message, false));
			d_1_p2.add(aloha(lambda, subsciber, message, probability_1, false));
			lambda += 0.01;
		}
		lambda = lamdba_er;
		opt_aloha(lambda, subsciber, message, false);
		aloha(lambda, subsciber, message, probability, false);
		aloha(lambda, subsciber, message, probability * 2.0, false);
		// opt_aloha(0.7, 3, 6, true);

		try (FileWriter writer = new FileWriter("C:\\Users\\�����\\YandexDisk\\_��������_(������ + �������)\\file__1.csv",
				false)) {
			// ������ ���� ������

			String text = "Lambda;Optimal;P;P*2\n";
			writer.write(text);
			for (int i = 0; i < d_opt.size(); i++) {
				text = la.get(i).toString() + ";";
				writer.write(text);
				text = d_opt.get(i).toString() + ";";
				writer.write(text);
				text = d_1_p.get(i).toString() + ";";
				writer.write(text);
				text = d_1_p2.get(i).toString() + "\n";

				writer.write(text);
			}
			writer.flush();
		} catch (IOException ex) {

			System.out.println(ex.getMessage());
		}

		System.out.println("Good works");
	}

	private static double aloha(double lambda, int subs, int mess, double prob, boolean show) {
		// lambda - ������������� �������� ������
		// subs - ����� ��������� � �������
		// mess - ����� ��������� � �������
		// prob - ����������� �������� ���������
		// show - ������������� ����� � �������
		int m = mess;
		// System.out.println("Aloha(1,P)\nlambda -> " + lambda + "\nSubscribers -> " +
		// subs + "\nMessage -> " + mess
		// + "\nProbability -> " + prob);
		ArrayList<Double> Probability = new ArrayList<>();
		// ����������� �������� � ���������
		ArrayList<ArrayList<Double>> time = new ArrayList<>();
		// ����� ��������� ��������� � ���������
		ArrayList<Integer> count = new ArrayList<Integer>();
		for (int i = 0; i < subs; i++) {
			time.add(new ArrayList<Double>());
			count.add(0);
			Probability.add(prob);
		}
		double fullTime = 0;
		double d0 = 0;

		if (show)
			System.out.print("Time ->\t");
		double x = 0;
		// ��������� �������������� ������
		for (int i = 0; i < mess; i++) {
			x = -Math.log(Math.random()) / lambda;
			fullTime += x;
			if (show)
				System.out.printf("%.3f;\t", fullTime);
			x = Math.random() * subs;
			ArrayList<Double> curr = time.get((int) x);
			curr.add(fullTime);
			time.set((int) x, curr);
			count.set((int) x, count.get((int) x) + 1);
		}
		if (show) {
			System.out.println("\nSubscribers ->" + time);
			System.out.println("Count ->" + count);
		}

		ArrayList<ArrayList<Double>> queue = new ArrayList<>();
		// queue - ������� ������� ��������, ��������� �� ���� ��������� ����
		ArrayList<Boolean> state_queue = new ArrayList<>();
		// state_queue : True - ������� �������� �����,
		// False - ���� ��������� � ������� � ��������
		for (int i = 0; i < subs; i++) {
			queue.add(new ArrayList<>());
			state_queue.add(true);
		}

		long windows = 0; // ����� ���� �� ����� ������ �������
		long N = 0; // ������� ����� ��������� � �������
		int state = 0; // (0-�����; 1-�����; 2-��������) ��������� ������� ����� ������� ��������

		do {
			if (show)
				System.out.println("---------------------\nNew Window -> " + windows);
			for (int i = 0; i < subs; i++) {
				while (!time.get(i).isEmpty() && Math.floor(time.get(i).get(0)) <= windows) {
					// ����� ��� ��������� �� �������� ������� � ��������
					if (show)
						System.out.println((i + 1) + "_sub -> " + time.get(i).get(0));
					queue.get(i).add(time.get(i).get(0));
					time.get(i).remove(0);
				}
			}
			state = 0;
			ArrayList<Boolean> channel = new ArrayList<>(subs);
			// ������ �� �������� ���������
			// True - ����������� � �����
			// False - ������������� � �����
			if (show)
				System.out.print("Probabilty -> ");
			for (int i = 0; i < subs; i++) {
				channel.add(new Boolean(null));
				x = Math.random();
				N += queue.get(i).size();

				if (!queue.get(i).isEmpty()) {
					if (state_queue.get(i)) { // �������� ������� ���������� � ��� = 1
						channel.add(i, true);
						state++;
						if (show)
							System.out.printf("  1 : ");
						if (state > 1) {
							if (show)
								System.out.println("\nConflict");
							break;
						}
						continue;
					}
					if (x < prob) { // ������� ������������ ��� ���������
						channel.add(i, true);
						state++;
					} else if (x >= prob) {
						channel.add(false);
					}
					if (show)
						System.out.printf("%.3f : ", x);
				} else {
					if (show)
						System.out.printf("----- : ");
				}

				if (state > 1) {
					if (show)
						System.out.println("\nConflict");
					break;
				}
			}
			if (state == 0) {
				if (show)
					System.out.println("\nEmpty");
			} else if (state == 1) {
				d0 += windows - queue.get(channel.indexOf(true)).get(0) + 1;
				queue.get(channel.indexOf(true)).remove(0);
				if (show)
					System.out.println("\nAdvance -> " + (channel.indexOf(true) + 1));
				mess--;
			}

			for (int i = 0; i < subs; i++) {
				if (queue.get(i).size() > 0)
					state_queue.set(i, false);
				else if (queue.get(i).size() == 0)
					state_queue.set(i, true);
			}

			if (show) {
				System.out.println("N -> " + N);
				System.out.println("Queue -> " + queue);
				System.out.println("End of the window -> " + windows);
			}
			if (windows > 0 && windows % 100000000 == 0)
				System.out.println(
						"Window(%100|000|000) -> " + windows / 10000000 + "\tN -> " + (double) N / (double) windows);

			windows++;

		} while (mess > 0);

		/*
		 * System.out.println("--------------------------------------------"); //
		 * System.out.println("Windows -> " + windows); //
		 * System.out.printf("N average -> %.5f\n", (double) N / (double) windows);
		 * System.out.printf("D average -> %.5f\n", (double) d0 / (double) windows); //
		 * System.out.printf("Lambda exit-> %.5f\n", (double) m / (double) windows);
		 * System.out.println("____________________________________________\n"); //
		 */
		return (double) d0 / (double) windows;
	}

	private static double opt_aloha(double lambda, int subs, int mess, boolean show) {
		// lambda - ������������� �������� ������
		// subs - ����� ��������� � �������
		// mess - ����� ��������� � �������
		// prob - ����������� �������� ���������
		// show - ������������� ����� � �������
		int m = mess;
		/// System.out.println("Optimal Aloha\nlambda -> " + lambda + "\nSubscribers ->
		/// " + subs + "\nMessage -> " + mess);
		ArrayList<ArrayList<Double>> time = new ArrayList<>();
		// ����� ��������� ��������� � ���������
		ArrayList<Integer> count = new ArrayList<Integer>();
		for (int i = 0; i < subs; i++) {
			time.add(new ArrayList<Double>());
			count.add(0);
		}
		double fullTime = 0;
		double d0 = 0;

		if (show)
			System.out.print("Time ->\t");
		double x = 0;
		// ��������� �������������� ������
		for (int i = 0; i < mess; i++) {
			x = -Math.log(Math.random()) / lambda;
			fullTime += x;
			if (show)
				System.out.printf("%.3f;\t", fullTime);
			x = Math.random() * subs;
			ArrayList<Double> curr = time.get((int) x);
			curr.add(fullTime);
			time.set((int) x, curr);
			count.set((int) x, count.get((int) x) + 1);
		}
		if (show) {
			System.out.println("\nSubscribers ->" + time);
			System.out.println("Count ->" + count);
		}

		ArrayList<ArrayList<Double>> queue = new ArrayList<>();
		// queue - ������� ������� ��������, ��������� �� ���� ��������� ����
		ArrayList<Boolean> state_queue = new ArrayList<>();
		// state_queue : True - ������� �������� �����,
		// False - ���� ��������� � ������� � ��������
		for (int i = 0; i < subs; i++) {
			queue.add(new ArrayList<>());
			state_queue.add(true);
		}

		long windows = 0; // ����� ���� �� ����� ������ �������
		long N = 0; // ������� ����� ��������� � �������
		int state = 0; // (0-�����; 1-�����; 2-��������) ��������� ������� ����� ������� ��������

		do {
			if (show)
				System.out.println("---------------------\nNew Window -> " + windows);
			for (int i = 0; i < subs; i++) {
				while (!time.get(i).isEmpty() && Math.floor(time.get(i).get(0)) <= windows) {
					// ����� ��� ��������� �� �������� ������� � ��������
					if (show)
						System.out.println((i + 1) + "_sub -> " + time.get(i).get(0));
					queue.get(i).add(time.get(i).get(0));
					time.get(i).remove(0);
				}
			}

			double freq = 0;
			for (int i = 0; i < subs; i++) {
				if (queue.get(i).size() > 0)
					freq++;
			}
			double prob = 1 / freq;

			state = 0;
			ArrayList<Boolean> channel = new ArrayList<>(subs);
			// ������ �� �������� ���������
			// True - ����������� � �����
			// False - ������������� � �����
			if (show)
				System.out.print("Probabilty -> ");
			for (int i = 0; i < subs; i++) {
				channel.add(new Boolean(null));
				x = Math.random();
				N += queue.get(i).size();

				if (!queue.get(i).isEmpty()) {
					if (x < prob) { // ������� ������������ ��� ���������
						channel.add(i, true);
						state++;
					} else if (x >= prob) {
						channel.add(false);
					}
					if (show)
						System.out.printf("%.3f : ", x);
				} else {
					if (show)
						System.out.printf("----- : ");
				}

				if (state > 1) {
					if (show)
						System.out.println("\nConflict");
					break;
				}
			}
			if (state == 0) {
				if (show)
					System.out.println("\nEmpty");
			} else if (state == 1) {
				d0 += windows - queue.get(channel.indexOf(true)).get(0) + 1;
				queue.get(channel.indexOf(true)).remove(0);
				if (show)
					System.out.println("\nAdvance -> " + (channel.indexOf(true) + 1));
				mess--;
			}

			for (int i = 0; i < subs; i++) {
				if (queue.get(i).size() > 0)
					state_queue.set(i, false);
				else if (queue.get(i).size() == 0)
					state_queue.set(i, true);
			}

			if (show) {
				System.out.println("N -> " + N);
				System.out.println("Queue -> " + queue);
				System.out.println("End of the window -> " + windows);
			}
			if (windows > 0 && windows % 100000000 == 0)
				System.out.println(
						"Window(%100|000|000) -> " + windows / 10000000 + "\tN -> " + (double) N / (double) windows);

			windows++;

		} while (mess > 0);

		/*
		 * System.out.println("--------------------------------------------"); //
		 * System.out.println("Windows -> " + windows); //
		 * System.out.printf("N average -> %.5f\n", (double) N / (double) windows); //
		 * System.out.printf("D average -> %.5f\n", (double) d0 / (double) windows);
		 * System.out.printf("Lambda exit-> %.5f\n", (double) m / (double) windows);
		 * System.out.println("____________________________________________\n");
		 */
		return (double) d0 / (double) windows;
	}

}