/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algorithm;

import Matrix.Equation;
import Vec.vector;

/**
 *
 * @author Gurkan
 */
public class LambertGauss {
	public double E = 0.0, i = 0.0, w = 0.0, ohm = 0.0, a = 0.0, e = 0.0;
	private double GM = 398600.5, u = 0.0, v = 0.0, M = 0.0, t = 0.0, t0 = 0.0;

	public LambertGauss(double[] vec1, double t1, double[] vec2, double t2) {
		t0 = t1;

		double ra = vector.Magnitude(vec1);
		double rb = vector.Magnitude(vec2);
		double Dt = (t2 - t1) * 60.0 * 60.0;
		double DV = Math.atan(vector.Magnitude(vector.Vectorial(vec1, vec2)) / vector.Scalar(vec1, vec2));
		double d = 2 * Math.sqrt(ra * rb) * Math.cos(DV / 2.0);
		double k = 1.0 - Math.cos(DV);
		double l = (ra + rb) / 2.0 / d - 0.5;
		double m = GM * Dt * Dt / Math.pow(d, 3);

		double y = 1.0, y0 = 0.0, x1 = 0.0;
		while (Math.abs(y - y0) > 1e-15) {
			y0 = y;
			x1 = m / y0 / y0 - l;
			double x2 = 4.0 / 3.0 * (1.0 + x1 * 6.0 / 5.0 + x1 * x1 * 6.0 * 8.0 / 5.0 / 7.0
					+ x1 * x1 * x1 * 6.0 * 8.0 * 10.0 / 5.0 / 7.0 / 9.0);
			y = 1.0 + x2 * (l + x1);
		}

		double[] ea = vector.IVec(vec1);
		double[] R0 = vector.Subtract(vec2, vector.Mult(vector.Scalar(vec2, ea), ea));
		double[] e0 = vector.IVec(R0);
		double[] W = vector.Vectorial(ea, e0);

		double Rb_ea = vector.Scalar(vec2, ea);
		double r0 = vector.Magnitude(R0);
		double p = ra * rb * k / (ra + rb - d * (1.0 - 2.0 * x1));
		double x = p / ra - 1.0;

		double Xi = 0.0, Yi = 0.0;

		Xi = vec1[2];
		Yi = -vec1[0] * W[1] + vec1[1] * W[0];

		y = ((p / ra - 1.0) * Rb_ea / rb - (p / rb - 1.0)) / r0 * rb;
		u = Equation.RegionAtan(Xi, Yi);
		v = Equation.RegionAtan(y, x);

		Xi = Math.sqrt(W[0] * W[0] + W[1] * W[1]);
		Yi = W[2];

		i = Equation.RegionAtan(Xi, Yi);
		ohm = Equation.RegionAtan(W[0], -W[1]);
		w = (u - v + 2.0 * Math.PI) % (2.0 * Math.PI);
		e = Math.sqrt(x * x + y * y);
		a = p / (1.0 - e * e);

		Xi = Math.sqrt(1.0 - e * e) * Math.sin(v);
		Yi = e + Math.cos(v);

		E = Equation.RegionAtan(Xi, Yi);
		M = E - e * Math.sin(E);

		Xi = Math.sqrt(GM / Math.pow(a, 3));
		Yi = 2.0 * Math.PI / Xi;

		t = ((Xi * t1 - M) / Xi + Yi) % Yi;
		// System.out.printf("%25s%25s%25s\n","E: "+E,"ohm: "+ohm,"w: "+w);
	}
}
