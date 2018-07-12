import java.util.ArrayList;

public class End_Note {
	ArrayList<Double> timer;
	short SF, CR, x, y;
	int CF, BW;
	double P;

	public End_Note() {
		SF = 0;
		P = 0;
		CF = 0;
		CR = 0;
		x = 0;
		y = 0;
		BW = 0;
	}

	public End_Note(int var) {
		P = 0;
		x = 0;
		y = 0;
		if (var == 1) {
			SF = 12;
			CF = 868000000;
			CR = 8;
			BW = 125;
		} else if (var == 2) {
			SF = 6;
			CF = 868000000;
			CR = 5;
			BW = 500;
		} else if (var == 2) {
			SF = 12;			
			CF = 868000000;
			CR = 5;
			BW = 125;
		}
	}
}