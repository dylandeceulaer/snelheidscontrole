package be.dylandeceulaer.snelheidscontrole3;
import com.google.android.gms.maps.model.LatLng;

public class Test {

	public static LatLng lambert72toWGS84(double x, double y) {
		double newLongitude;
		double newLatitude;

		double n = 0.77164219;
		double F = 1.81329763;
		double thetaFudge = 0.00014204;
		double e = 0.08199189;
		double a = 6378388;
		double xDiff = 149910;
		double yDiff = 5400150;

		double theta0 = 0.07604294;

		double xReal = xDiff - x;
		double yReal = yDiff - y;

		double rho = Math.sqrt(xReal * xReal + yReal * yReal);
		double theta = Math.atan(xReal / -yReal);

		newLongitude = (theta0 + (theta + thetaFudge) / n) * 180 / Math.PI;
		newLatitude = 0;

		for (int i = 0; i < 5; ++i) {
			newLatitude = (2 * Math.atan(Math.pow(F * a / rho, 1 / n)
					* Math.pow(
							(1 + e * Math.sin(newLatitude))
									/ (1 - e * Math.sin(newLatitude)), e / 2)))
					- Math.PI / 2;
		}
		newLatitude *= 180 / Math.PI;
		return new LatLng(newLatitude, newLongitude);

	}


}