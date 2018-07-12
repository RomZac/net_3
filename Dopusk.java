/**
 * моделирование синхрон. и асинхр. системы M|D|1
 */

import java.util.ArrayList;

public class Dopusk {
    public static void main(String[] args) {
        double[] lambda = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 0.95};
        for (double l : lambda) {
        	async(l);
        	sync(l);
        }
    }

    private static void async(double lambda) {
        int M = 7; //кол-во сообщений
        ArrayList<Double> time = new ArrayList<>(); //время промежутка между сообщениями
        ArrayList<Double> time_new = new ArrayList<>(); //время промежутка между сообщениями
        ArrayList<Double> delays = new ArrayList<>(); //время когда сообщение в системе
        delays.add(1.0); //первое сообщ без очереди передаётся
        double D = 1; // общая задержка
        float fullTime = 0;

        for (int i = 0; i < M; i++) {
            double x = -Math.log(Math.random()) / lambda;
            time.add(x);
            fullTime += x;
        }
        double z = 0;
        for (int i = 0; i < M; i++){
        	z+= time.get(i);
        	time_new.add(z);
        }

        System.out.println("time -> " + time_new);
        
        for (int i = 1; i < M; i++) {
            double x = time.get(i);
            double d = delays.get(i - 1);
            if (x < d) //если пред. сообщение еще не отправилось
                delays.add(d - x + 1); // вот столько система ещё будет занята
            else
                delays.add(1.0);
            D += delays.get(i);
        }
        
        System.out.println("Delays -> " + delays);
        ArrayList<Integer> countN = new ArrayList<>(Math.round(fullTime + 1)); //счетчик кол-ва сообщ в сист в единицу времени
        for (int i = 0; i < Math.round(fullTime + 1); i++) countN.add(i, 0);

        double start = 0, end;
        for (int i = 0; i < M; i++) {
            double x = time.get(i);
            start += x;
            end = start + delays.get(i);
            for (int k = new Double(Math.ceil(start)).intValue(); k < Math.ceil(end); k++)
                try {
                    countN.set(k, countN.get(k) + 1); // увеличиваем счетчик
                } catch (Exception e) { // если вышли за границы массива
                    for (int s = countN.size(); s < Math.ceil(end); s++) // увеличиваем размер
                        countN.add(s, 0);
                    countN.set(k, countN.get(k) + 1); // теперь уже увеличиваем счетчик
                }

        }

        double d_th = (2 - lambda) / (2 * (1 - lambda));
        double N_th = lambda * d_th;
        double N = 0;
        for (Integer i : countN) N += i;
       
        System.out.println("\nAsync system. Lambda = " + lambda);
        System.out.println("d (theory) = " + d_th);
        System.out.println("d (pract)  = " + D / M);

        System.out.println("N (theory) = " + N_th);
        System.out.println("N (pract)  = " + N / countN.size());
        
    }

    private static void sync(double lambda) {
        int M = 7; //кол-во сообщений
        ArrayList<Double> time = new ArrayList<>(); //время между появлениями сообщений
        ArrayList<Double> time_new = new ArrayList<>(); //время между появлениями сообщений
        ArrayList<Double> delays = new ArrayList<>(); //время пребывания сообщений в сист
        float fullTime = 0;
        double D = 1; // общая задержка

        for (int i = 0; i < M; i++) {
            double x = -Math.log(Math.random()) / lambda;
            time.add(x);
            fullTime += x;
        }

        double z = 0;
        for (int i = 0; i < M; i++){
        	z+= time.get(i);
        	time_new.add(z);
        }

        System.out.println("time -> " + time_new);
        ArrayList<Integer> countN = new ArrayList<>(Math.round(fullTime + 1)); //счетчик кол-ва сообщ в сист в единицу времени
        for (int i = 0; i < Math.round(fullTime + 1); i++) countN.add(i, 0);

        delays.add(1 + Math.ceil(time.get(0)) - time.get(0));//время ожидания окна + время отправки для 1 сообщения
        int w = new Double(Math.ceil(time.get(0))).intValue();
        countN.set(w, countN.get(w) + 1); // увеличиваем счетчик - 1е сообщ только в одно окно попадает

        double start = time.get(0);
        for (int i = 1; i < M; i++) {
            double x = time.get(i); 		// время до появления след. заявки
            double d = delays.get(i - 1);	// время отправки предыдущего сообщения
            start += x;
            if (x < d) 						// значит предыдущее сообщение еще отправляется
                delays.add(d - x + 1);		// вот столько система ещё будет занята
            else
                delays.add(1 + Math.ceil(start) - start); // время ожидания окна + время отправки сообщения
            D += delays.get(i);

            double end = start + delays.get(i);
            for (int k = new Double(Math.ceil(start)).intValue(); k < Math.ceil(end); k++)
                try {
                    countN.set(k, countN.get(k) + 1); 	// увеличиваем счетчик сообщений в ед. времени
                } catch (Exception e) { 				// если вышли за границы массива
                    for (int s = countN.size(); s < Math.ceil(end); s++) // увеличиваем размер
                        countN.add(s, 0);
                    countN.set(k, countN.get(k) + 1); // увеличиваем счетчик
                }
        }
        System.out.println("Delays -> " + delays);
        
        double d_th = (2 - lambda) / (2 * (1 - lambda)) + 0.5;
        double N_th = (lambda * (2 - lambda)) / (2 * (1 - lambda));
        double N = 0;
        for (Integer i : countN) N += i;
             
        System.out.println("\nSync system. Lambda = " + lambda);
        System.out.println("d (theory) = " + d_th);
        System.out.println("d (pract)  = " + D / M);

        System.out.println("N (theory) = " + N_th);
        System.out.println("N (pract)  = " + N / countN.size());
      	
    }
}
//*/