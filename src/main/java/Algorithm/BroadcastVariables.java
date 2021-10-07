package Algorithm;

public class BroadcastVariables {
	
	public String satellite = "";
	public String url = "";
	
	public double referance = 0;
	public double start = -2;
	public double end = 2;
	
	public double l0  = 0.0; 
	public double e   = 0.0; 
	public double a    = 0.0;
	public double i0  = 0.0; 
	public double M   = 0.0; 
	public double w    = 0.0;
	
	public double Cuc = 0.0; 
	public double Crc = 0.0; 
	public double Cic  = 0.0;
	public double Cus = 0.0; 
	public double Crs = 0.0; 
	
	public double Cis  = 0.0;
	public double dl  = 0.0; 
	public double di  = 0.0; 
	public double dn   = 0.0;
	
	public double a0  = 0.0; 
	public double a1  = 0.0; 
	public double a2   = 0.0;
	
	public double t0  = 0.0; 
	public double tk  = 0.0; 
	public double WGps = 0.0;
	
	public double GM = 398600.5; 
	public double we = 7.2921151467e-5;
	
	public BroadcastVariables(String satellite, double referance , String url) {

		this.satellite = satellite;
		this.url = url;
		this.referance = referance;
	} 
	public BroadcastVariables() {

	} 
	
}
