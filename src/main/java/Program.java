
import Algorithm.Adjustment;
import Algorithm.BroadcastVariables;
import Algorithm.Rinex304;
import Algorithm.SatelliteCoordinate;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Gurkan
 */
public class Program {
    
        
    public static void main(String[] args) {
        String URL = "Sta30s21035.sp3";
        String URL2= "";
        String Satellite = "G21";
        Adjustment adjustment = null;
        
        BroadcastVariables variables = new BroadcastVariables(Satellite, 12.5, URL);
        adjustment = new Adjustment(variables);
        SatelliteCoordinate.calculateAxisErrors(variables);
        
       // SatelliteCoordinate.drawAxisErrors();
       // Rinex304.WriteBRDC(variables, URL2);
        
    }
    
    
}
