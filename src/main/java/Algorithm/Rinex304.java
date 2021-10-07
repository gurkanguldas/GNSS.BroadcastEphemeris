/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algorithm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gurkan
 */
public class Rinex304 {
    public static void WriteBRDC(BroadcastVariables adjustment , String URL)
    {
        BufferedWriter Wbuffer;  
        try {
            Wbuffer = new BufferedWriter(new FileWriter(URL+"BRDC.txt",true));
            String text="";
            double a_m=Math.sqrt(adjustment.a*1000.0);
            if(adjustment.referance<10)
            text += String.format(adjustment.satellite+" 2020 05 01 0"+adjustment.referance+" 00 00%19s%19s%19s\n",e_Notation(adjustment.a0, 12),e_Notation(adjustment.a1, 12),e_Notation(adjustment.a2, 12));
            else
            text += String.format(adjustment.satellite+" 2020 05 01 "+adjustment.referance+" 00 00%19s%19s%19s\n",e_Notation(adjustment.a0, 12),e_Notation(adjustment.a1, 12),e_Notation(adjustment.a2, 12)); 
            text += String.format("     %20s%19s%19s%19s\n",e_Notation(0, 12),e_Notation(adjustment.Crs*1000.0, 12),e_Notation(adjustment.dn, 12),e_Notation(adjustment.M, 12));
            text += String.format("     %20s%19s%19s%19s\n",e_Notation(adjustment.Cuc, 12),e_Notation(adjustment.e, 12),e_Notation(adjustment.Cus, 12),e_Notation(a_m, 12));
            text += String.format("     %20s%19s%19s%19s\n",e_Notation(adjustment.t0, 12),e_Notation(adjustment.Cic, 12),e_Notation(adjustment.l0, 12),e_Notation(adjustment.Cis, 12));
            text += String.format("     %20s%19s%19s%19s\n",e_Notation(adjustment.i0, 12),e_Notation(adjustment.Crc*1000.0, 12),e_Notation(adjustment.w, 12),e_Notation(adjustment.dl, 12));
            text += String.format("     %20s%19s%19s%19s\n",e_Notation(adjustment.di, 12),e_Notation(0, 12),e_Notation(adjustment.WGps, 12),e_Notation(0, 12));
            text += String.format("     %20s%19s%19s%19s\n",e_Notation(0, 12),e_Notation(0, 12),e_Notation(0, 12),e_Notation(0, 12));
            text += String.format("     %20s%19s\n",e_Notation(adjustment.tk, 12),e_Notation(0, 12));
            Wbuffer.write(text);
            Wbuffer.close();
        } catch (IOException ex) {
            
        }
    }
    
    public static String e_Notation(double b , int e) {
	String wrt = "";
	DecimalFormat df = new DecimalFormat();
	df.setMinimumFractionDigits(12);
	
	int e_ = 0;
	
	for (int i = -20; i < 50; i++) 
        {
            double f = b/Math.pow(10.0, i);
            if(1.0<=Math.abs(f) && Math.abs(f)<10.0) 
            {
		e_=i;
		if(e_<10 && e_>=0)
                    wrt = df.format(f)+"e+0"+Math.abs(e_);
		else if(e_>-10 && e_<0)
                    wrt = df.format(f)+"e-0"+Math.abs(e_);
		else if(e_<=-10)
                    wrt = df.format(f)+"e-"+Math.abs(e_);
		else if(e_ >= 10)
                    wrt = df.format(f)+"e+"+Math.abs(e_);
		break;
            }
            else if(f==0.0) 
            {
		wrt = df.format(f)+"e+00";
		break;
            }
	}
	return wrt;
}
}
