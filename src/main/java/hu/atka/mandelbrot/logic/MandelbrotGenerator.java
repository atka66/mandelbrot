package hu.atka.mandelbrot.logic;

public class MandelbrotGenerator {

	public boolean isSequenceBoundedForPosition(int x, int y, int iteration, double offsetX, double offsetY, double scale) {
		double cX = (x + offsetX) / scale;
		double cY = (y + offsetY) / scale;

		double currentZX = 0.0;
		double currentZY = 0.0;

		for (int i = 0; i < iteration; i++) {
			double newZX = (currentZX * currentZX) - (currentZY * currentZY) + cX;
			double newZY = (currentZX * currentZY) + (currentZY * currentZX) + cY;

			if (Math.abs(newZX) > 1E20 || Math.abs(newZY) > 1E20) {
				return false;
			}
			currentZX = newZX;
			currentZY = newZY;
		}
		return true;
	}
}
