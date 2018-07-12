import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class LoRa {

	// SimpleModel(0.000001, 200, 5100, airtime, false); // эталон
	public static void main(String[] args) {
		ArrayList<Double> test1 = new ArrayList<>();
		ArrayList<Double> test2 = new ArrayList<>();
		ArrayList<Double> test3 = new ArrayList<>();
		final double lambda = 0.000001;
		final int mess = 5100;
		final double airtime1 = 1712.13;
		final double airtime2 = 7.07;
		final double airtime3 = 100.0;
		final int TP = 14;

		long timer = new Date().getTime();

		for (int subscriber = 100; subscriber < 1601; subscriber += 100) {
			/*
			* test1.add(SimpleModel(lambda, subscriber, mess, airtime1, false));
			* test2.add(SimpleModel(lambda, subscriber, mess, airtime2, false));
			* test3.add(SimpleModel(lambda, subscriber, mess, airtime3, false));
			*/
			test1.add(SN(lambda, subscriber, mess, airtime1, TP, false));
			test2.add(SN(lambda, subscriber, mess, airtime2, TP, false));
			test3.add(SN(lambda, subscriber, mess, airtime3, TP, false));

		}
		System.out.println("\n Time -> " + (new Date().getTime() - timer));
		try (FileWriter writer = new FileWriter("C:\\Users\\Роман\\YandexDisk\\_ТЮРЛИКОВ_(КУРСАЧ + ЭКЗАМЕН)\\LoRa.csv",
				false)) {
			String text;
			// text = ";Aloha; SN1; SN2; SN3;SIMPLE MODEL\n";
			text = ";Aloha; SN1; SN2; SN3;SIMPLE MODEL + Parameter P\n";
			writer.write(text);
			for (int i = 0; i < test1.size(); i++) {
				text = (i + 1) * 100 + ";" + Math.exp(-2 * (i + 1) * 100 * airtime1 * lambda) + ";"
						+ test1.get(i).toString() + ";" + test2.get(i).toString() + ";" + test3.get(i).toString()
						+ "\n";
				writer.write(text);
			}
			writer.flush();
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}

		System.out.println("Good works");
	}

	private static double SimpleModel(double lambda, int subs, int mess, double airtime, boolean show) {
		ArrayList<ArrayList<Double>> time = new ArrayList<>();
		// время начала передачи сообщения от абонента к серверу
		ArrayList<Double> server_time = new ArrayList<>();
		// время начала передачи на сервере
		ArrayList<Boolean> received_mess = new ArrayList<>();
		// решение о том, принято сообщение или потеряно в результате коллизии.
		for (int i = 0; i < subs; i++)
			time.add(new ArrayList<Double>());
		for (int i = 0; i < subs * mess; i++)
			received_mess.add(true);

		// Генерация потока
		double x = 0;
		if (show)
			System.out.println("Timer in min : ");
		for (int kx = 0; kx < subs; kx++) {
			double xfull = 0;
			ArrayList<Double> curr = new ArrayList<>();
			for (int i = 0; i < mess; i++) {
				x = -Math.log(Math.random()) / lambda; // время появления нового сообщения
				xfull += x; // всё время в мс
				if (show)
					System.out.printf("%f;\t", xfull / 60000); // вывод в минутах
				curr.add(xfull);
				server_time.add(xfull);
			}
			time.set(kx, curr);
		}
		Collections.sort(server_time);

		if (show) {
			System.out.println("\ntime(ms) -> " + time);
			System.out.println("test(ms) -> " + server_time);
		}

		for (int i = 1; i < server_time.size(); i++) { // анализ на коллизии в канале
			if (server_time.get(i) - server_time.get(i - 1) > airtime) {
				received_mess.set(i, true);
			} else {
				received_mess.set(i - 1, false);
				received_mess.set(i, false);
			}
		}

		int message = 0;
		for (int i = 0; i < received_mess.size(); i++) {
			if (received_mess.get(i))
				message++;
		}

		double result = (double) message / (double) (mess * subs);

		if (show) {
			System.out.println("\ntest(ms) -> " + server_time);
			System.out.println("\ntest(ms) -> " + received_mess);
		} else {
			// System.out.println("\ntest(ms) -> " + received_mess);
			System.out.println(
					"Subscribers -> " + subs + "\nMessage -> " + message + " of " + mess * subs + " => " + result);
		}

		return result;

	}

	private static double SN(double lambda, int subs, int mess, double airtime, double power, boolean show) {
		if (!show)
			System.out.print("Subscribers -> " + subs);
		final double P_thr = 1.26;
		ArrayList<Boolean> received_mess = new ArrayList<>();
		// решение о том, принято сообщение или потеряно в результате коллизии.
		Map<Double, Double> power_time = new HashMap<Double, Double>();
		for (int i = 0; i < subs * mess; i++)
			received_mess.add(false);

		// Генерация потока
		double x = 0;
		if (show)
			System.out.println("Timer in min : ");
		for (int kx = 0; kx < subs; kx++) {
			double xfull = 0;
			double p = power - 127.41 + 20.8 * Math.log10((Math.random() * 100) / 40.0);
			for (int i = 0; i < mess; i++) {
				x = -Math.log(Math.random()) / lambda; // время появления нового сообщения
				xfull += x; // всё время в мс
				if (show)
					System.out.printf("%f;\t", xfull / 60000); // вывод в минутах
				power_time.put(xfull, p);
			}
		}

		Map<Double, Double> p_t = new TreeMap<Double, Double>(power_time);
		if (show) {
			System.out.println("\ntime(ms) -> " + power_time);
			System.out.println("\ntime(ms) -> " + p_t);
		}

		double time_prev = -2000.0;
		double power_prev = 1;
		int cur = 0;
		for (Map.Entry<Double, Double> entry : p_t.entrySet()) {
			/*
			 * System.out.println("Cur -> " + cur + "\tTime: " + entry.getKey() +
			 * "\tPower: " + entry.getValue() + "\tTime_prev: " + time_prev +
			 * "\tPower_prev: " + power_prev);
			 */
			if (cur != 0) {
				if (entry.getKey() - time_prev > airtime) {
					received_mess.set(cur, true);
				} else if (Math.max(entry.getValue(), power_prev) - Math.min(entry.getValue(), power_prev) > 1) {
					if (Math.max(entry.getValue(), power_prev) == entry.getValue()) {
						received_mess.set(cur, true);
						received_mess.set(cur - 1, false);
					} else {
						received_mess.set(cur - 1, true);
						received_mess.set(cur, false);
					}
				} else {
					received_mess.set(cur - 1, false);
					received_mess.set(cur, false);
				}
			} else
				received_mess.set(cur, true);
			time_prev = entry.getKey();
			power_prev = entry.getValue();
			cur++;
		}

		int message = 0;
		for (int i = 0; i < received_mess.size(); i++) {
			if (received_mess.get(i))
				message++;
		}

		double result = (double) message / (double) (mess * subs);

		if (show) {
			System.out.println("\ntest(ms) -> " + received_mess);
		} else {
			// System.out.println("\ntest(ms) -> " + received_mess);
			System.out.println("\nMessage -> " + message + " of " + mess * subs + " => " + result);
		}

		return result;

	}

	private static double HardModel(double lambda, int subs, int mess, double airtime, int TP, boolean show) {
		final double LPL = 127.41;
		ArrayList<ArrayList<Double>> time = new ArrayList<>();
		// время начала передачи сообщения от абонента к серверу
		ArrayList<Double> server_time = new ArrayList<>();
		// время начала передачи на сервере
		ArrayList<Boolean> received_mess = new ArrayList<>();
		// решение о том, принято сообщение или потеряно в результате коллизии.
		ArrayList<End_Note> abonents = new ArrayList<>();

		for (int i = 0; i < subs; i++)
			time.add(new ArrayList<Double>());
		for (int i = 0; i < subs * mess; i++)
			received_mess.add(true);

		// Генерация потока
		double x = 0;
		if (show)
			System.out.println("Timer in min : ");

		End_Note tmp_node;
		for (int kx = 0; kx < subs; kx++) {
			double xfull = 0;
			ArrayList<Double> curr = new ArrayList<>();
			for (int i = 0; i < mess; i++) {
				x = -Math.log(Math.random()) / lambda; // время появления нового сообщения
				xfull += x; // всё время в мс
				if (show)
					System.out.printf("%f;\t", xfull / 60000); // вывод в минутах
				curr.add(xfull);
				server_time.add(xfull);
			}
			double d = 0;
			tmp_node = new End_Note((int) Math.round(Math.random() * 3));
			while (true) {
				tmp_node.x = (short) (Math.ceil(Math.random() * 60) + 40);
				tmp_node.y = (short) (Math.ceil(Math.random() * 60) + 40);
				d = Math.sqrt(tmp_node.x * tmp_node.x + tmp_node.y * tmp_node.y);
				if (d <= 100)
					break;
			}
			tmp_node.P = (double) TP - (LPL + 20.8 * Math.log10(d / 40));

			/*
			 * Для абсолютно случайных абонентов tmp_node.SF = (short)
			 * (Math.ceil(Math.random() * 6) + 6); tmp_node.CF = (int)
			 * Math.round((Math.random() * 7000000.0) / 61.0); tmp_node.CR = (short)
			 * (Math.round(Math.random() * 3) + 5); tmp_node.BW = (short)
			 * Math.round(Math.random() * 3);
			 */
			tmp_node = abonents.set(kx, tmp_node);
			time.set(kx, curr);
		}
		///////////////////////////////////////////////////////////////////
		Collections.sort(server_time); // заменить на сортировку hashmap

		if (show) {
			System.out.println("\ntime(ms) -> " + time);
			System.out.println("test(ms) -> " + server_time);
		}

		for (int i = 1; i < server_time.size(); i++) { // анализ на коллизии в канале
			if (server_time.get(i) - server_time.get(i - 1) > airtime) {
				received_mess.set(i, true);
			} else {
				received_mess.set(i - 1, false);
				received_mess.set(i, false);
			}
		}

		int message = 0;
		for (int i = 0; i < received_mess.size(); i++) {
			if (received_mess.get(i))
				message++;
		}

		double result = (double) message / (double) (mess * subs);

		if (show) {
			System.out.println("\ntest(ms) -> " + server_time);
			System.out.println("\ntest(ms) -> " + received_mess);
		} else {
			// System.out.println("\ntest(ms) -> " + received_mess);
			System.out.println(
					"Subscribers -> " + subs + "\nMessage -> " + message + " of " + mess * subs + " => " + result);
		}

		return result;

	}

}
