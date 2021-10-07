/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algorithm;

import JGnuPlot.Plot;
import JGnuPlot.Run;
import Matrix.Equation;
import Read.SP3Read;

/**
 *
 * @author Gurkan
 */
public class SatelliteCoordinate {
	private static String drawX="";
	private static String drawY="";
	private static String drawZ="";
	
	public static void calculateAxisErrors(BroadcastVariables adjustment) {
		SP3Read.Read(adjustment.url, adjustment.satellite, 0.5);
		double n = Math.sqrt(398600.5 / (adjustment.a * adjustment.a * adjustment.a));

		for (int f0 = 0; f0 <= 2880; f0++) {
			double t = (30.0 * (f0) - adjustment.referance * 3600.0);// sn
			double Mk = adjustment.M + t * (n + adjustment.dn);
			double E = Mk + adjustment.e * Math.sin(Mk), Ea = 0.0;
			do {
				Ea = E;
				E = Mk + adjustment.e * Math.sin(Ea);

			} while (Math.abs(Ea - E) > 1.0e-10);

			double c = Math.sqrt(1 - adjustment.e * adjustment.e) * Math.sin(E), v = (Math.cos(E) - adjustment.e);
			double fk = Equation.RegionAtan(c, v);

			double u = fk + adjustment.w + adjustment.Cuc * Math.cos(2.0 * (fk + adjustment.w)) + adjustment.Cus * Math.sin(2.0 * (fk + adjustment.w));
			double r = adjustment.a * (1.0 - adjustment.e * Math.cos(E)) + adjustment.Crc * Math.cos(2.0 * (fk + adjustment.w)) + adjustment.Crs * Math.sin(2.0 * (fk + adjustment.w));
			double i = adjustment.i0 + adjustment.di * t + adjustment.Cic * Math.cos(2.0 * (fk + adjustment.w)) + adjustment.Cis * Math.sin(2.0 * (fk + adjustment.w));
			double l = (adjustment.l0 + (adjustment.dl - 7.2921151467e-5) * t - 7.2921151467e-5 * adjustment.t0);

			double X0 = r * (Math.cos(l) * Math.cos(u) - Math.sin(l) * Math.sin(u) * Math.cos(i));
			double Y0 = r * (Math.sin(l) * Math.cos(u) + Math.cos(l) * Math.sin(u) * Math.cos(i));
			double Z0 = r * Math.sin(u) * Math.sin(i);
			double T0 = (adjustment.a0 + adjustment.a1 * t + adjustment.a2 * t * t) * 1.0e6;

			double dx = (SP3Read.X.get(f0) - X0) * 1000.0;
			double dy = (SP3Read.Y.get(f0) - Y0) * 1000.0;
			double dz = (SP3Read.Z.get(f0) - Z0) * 1000.0;
			double dt = (SP3Read.T.get(f0) - T0) * 1000.0;

			double time = (30.0 * f0 / 3600.0);
			
			if (f0 % 30.0 == 0.0) {
				System.out.printf("%10s%25s%25s%25s%25s\n", time , dx, dy, dz, dt);
				drawX += time+" "+dx+"\n";
				drawY += time+" "+dy+"\n";
				drawZ += time+" "+dz+"\n";
			}
		}
	}
	public static void drawAxisErrors()
	{
            
                double ref = 12.5;
                double min = -750.0;
                double max = 1000.0;
                // Font and Name
                Plot.P += "set xlabel '[h]' font 'times,15'\n";
                Plot.P += "set ylabel '[m]' font 'times,15'\n";
                Plot.P += "set title 'G14 (12.30)' font 'times,20'\n";
                Plot.P += "set tics font 'times,13'\n";
                Plot.P += "set key font 'times,13'\n";
                // Increase of axis
                Plot.P += "set xtic 1.0\n";
                Plot.P += "set ytic 125.0\n";
                //Create to grid
                Plot.P += "set grid ytics lt 1 lw 0.75 lc rgb 'gray90'\n";
                Plot.P += "set grid xtics lt 1 lw 0.75 lc rgb 'gray90'\n";
                
		Plot.Titles("SP3_x - BRDC_x","SP3_y - BRDC_y","SP3_z - BRDC_z","","");
                
		Plot.Vector.LinesPoints(7, 1.0, 1, 1.5, "blue");
		Plot.Vector.LinesPoints(7, 1.0, 1, 1.5, "red");
		Plot.Vector.LinesPoints(7, 1.0, 1, 1.5, "green");
                
        Plot.Vector.Lines(1, 1.5, "gray");
        Plot.Vector.Lines(1, 1.5, "gray");
                
		Plot.Vector.Array(drawX);
		Plot.Vector.Array(drawY);
		Plot.Vector.Array(drawZ);
                
		Plot.Vector.Array("0 0\n 24 0");
		Plot.Vector.Array(ref+" "+min+"\n"+ref+" "+max);
                
		Plot.Write("brdcdrawerror.plt");
		Run.exec("brdcdrawerror.plt");
	}
}
