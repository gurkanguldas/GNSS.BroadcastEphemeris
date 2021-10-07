/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algorithm;

import java.util.ArrayList;

import Matrix.Equation;
import Matrix.matris;
import Read.SP3Read;

/**
 *
 * @author Gurkan
 */
public class Adjustment {
    private BroadcastVariables variables;
    private Boolean FirstLoop = true;
    private double check_E=0.0;
    private ArrayList<Double> X , Y , Z;
    
    public Adjustment(BroadcastVariables variables)
    {
    	this.variables = variables;
        SP3Read.Read(variables.url, variables.satellite, 0.5);
        RotateCordinate();
        CalculateKepler();
        Stabilization(100);
        ClockErrorStabilization();
        SP3Read.ClearAll();
    }
                
    public void RotateCordinate()
    {
        X = new ArrayList<Double>();
        Y = new ArrayList<Double>();
        Z = new ArrayList<Double>();
        
        for (double f0 = 120 * (variables.referance+variables.start), i = 0; f0 <= 120 * (variables.referance+variables.end); f0++ , i++)
        {
            double T2 = -(30.0 * (f0) - variables.referance * 3600.0) * variables.we;
            int f1 = (int)Math.ceil(f0);
            X.add(SP3Read.X.get(f1) * Math.cos(T2) + SP3Read.Y.get(f1) * Math.sin(T2));
            Y.add(-SP3Read.X.get(f1) * Math.sin(T2) + SP3Read.Y.get(f1) * Math.cos(T2));
            Z.add(SP3Read.Z.get(f1));
        }
    }
    public void CalculateKepler()
    {
    	variables.t0 = SP3Read.t.get((int)Math.ceil(120 * (variables.referance)))*60.0;
        LambertGauss kepler;
        for (double f0 = 120 * (variables.referance+variables.start), i = 0; f0 < 120 * (variables.referance+variables.end); f0++, i++)
        {	
            double[] vec1 = new double[] { X.get((int)i), Y.get((int)i), Z.get((int)i) };
            double[] vec2 = new double[] { X.get((int)i + 1), Y.get((int)i + 1), Z.get((int)i + 1) };
            kepler = new LambertGauss(vec1, 30.0 * f0 / 3600.0, vec2, 30.0 * (f0 + 1) / 3600.0);   
            double E = kepler.E;
            
            if(FirstLoop)
            {
                check_E = E;
                FirstLoop = false;
            }
          else
        	  while(Math.abs(E-check_E)>Math.PI * 2.0 * 0.75)
	              E += 2.0 * Math.PI; 
            
            check_E = E;
            
            double Mk = E - kepler.e * Math.sin(E);;
            
            double M0 = Math.sqrt(variables.GM / Math.pow(kepler.a, 3.0)); //e
                   M0 *= ((SP3Read.t.get((int)Math.ceil(f0)) / 60.0) % 24.0 - variables.referance)*3600.0;    //t
                   
            M0 = Mk - M0  ;

            variables.e  += kepler.e;
            variables.a  += kepler.a;
            variables.l0 += kepler.ohm;
            variables.i0 += kepler.i;
            variables.M  += M0;
            variables.w  += kepler.w;
        }
        double size = X.size();
      
        variables.e  = variables.e  / (size - 1.0);
        variables.a  = variables.a  / (size - 1.0);
        variables.i0 = variables.i0 / (size - 1.0);
        variables.M  = variables.M  / (size - 1.0);
        variables.w  = variables.w  / (size - 1.0);
        variables.l0 = variables.l0 / (size - 1.0);
    }
   public void Stabilization(int MaxIteration)
   {
	   int unk = 15;
       for (int iteration = 0; iteration < MaxIteration; iteration++) 
       {
            double AtA[][] = new double[unk][unk];
            double AtL[] = new double[unk];	
        
            double AtAi[][] , AtLi[];
            double LtL = 0.0;
        
            double n = Math.sqrt(variables.GM / (variables.a * variables.a * variables.a));
        
            for (double f0 = 120 * (variables.referance+variables.start); f0 <= 120 * (variables.referance +variables.end); f0++) 
            {
                double t = (30.0 * (f0) - variables.referance * 3600.0);
                double Mk = variables.M + t * (n + variables.dn);
                double E = Mk + variables.e * Math.sin(Mk), Ea;

                //do {
                for (int i = 0; i < 50; i++) {
                    
                    Ea = E;
                    E = Mk + variables.e * Math.sin(Ea);
                }// while (Math.abs(Ea - E) > 1.0e-15);

                double c = Math.sqrt(1 - variables.e * variables.e) * Math.sin(E), v = (Math.cos(E) - variables.e);
                double fk = Equation.RegionAtan(c , v);
                double u = fk + variables.w + variables.Cuc * Math.cos(2.0 * (fk + variables.w)) + variables.Cus * Math.sin(2.0 * (fk + variables.w));
                double r = variables.a * (1.0 - variables.e * Math.cos(E)) + variables.Crc * Math.cos(2.0 * (fk + variables.w)) + variables.Crs * Math.sin(2.0 * (fk + variables.w));
                double i = variables.i0 + variables.di * t + variables.Cic * Math.cos(2.0 * (fk + variables.w)) + variables.Cis * Math.sin(2.0 * (fk + variables.w));
                double l = (variables.l0 + (variables.dl - variables.we) * t);
                double X0 = r * (Math.cos(l) * Math.cos(u) - Math.sin(l) * Math.sin(u) * Math.cos(i));
                double Y0 = r * (Math.sin(l) * Math.cos(u) + Math.cos(l) * Math.sin(u) * Math.cos(i));
                double Z0 = r * Math.sin(u) * Math.sin(i);
                
                int f1 = (int)Math.ceil(f0);
                double A[][] = Equation.Factors(variables.a, r, l, u,variables.w,fk, variables.e, i, E, t, variables.Cus, variables.Cus);
                double L[] = new double[] { SP3Read.Z.get(f1) - Z0, SP3Read.Y.get(f1) - Y0, SP3Read.X.get(f1) - X0 };
                
                AtAi = matris.MatMult(A, matris.Transpose(A));
                AtLi = matris.MatMult(A, L);
                LtL += matris.MatMultSingle(L, L);
            
                for (int j = 0; j < AtAi.length; j++) 
                {
                    AtL[j] += AtLi[j];
                    for (int j2 = 0; j2 < AtAi[0].length; j2++)
                        AtA[j][j2] += AtAi[j][j2];
                }
            } 
            
            double Q[][] = matris.invert(AtA);
            double x[] = matris.MatMult(Q, AtL);
            double vtv = LtL - matris.MatMultSingle(x,matris.MatMult(AtA,x));
            double m0 = Math.sqrt(vtv/720.0);
            
            variables.a   += x[0];    variables.dl  += x[7];
            variables.w   += x[1];    variables.Crc += x[8];
            variables.Cuc += x[2];    variables.Crs += x[9];
            variables.Cus += x[3];    variables.Cic += x[10];
            variables.i0  += x[4];    variables.Cis += x[11];
            variables.di  += x[5];    variables.e   += x[12];
            variables.l0  += x[6];    variables.M   += x[13];
            variables.dn  += x[14];  
            
        }		
       
       variables.tk=variables.t0;
       variables.l0 += variables.we*variables.t0;
       variables.l0 = variables.l0 % (2.0*Math.PI);
       variables.l0 = (variables.l0 + 2.0*Math.PI)%(2.0*Math.PI);
   }
   
   public void ClockErrorStabilization()
   {
	double AtA[][] = new double[3][3];
	double AtL[] = new double[3];
        
        double AtAi[][] , AtLi[];
        
        for (double f0 = 120 * (variables.referance + variables.start), i=0; f0 <= 120 * (variables.referance + variables.end); f0++,i++) 
        {
            double t = 30.0*i;
            double[][] A = new double[][] {{1.0 , t , t*t}};
            double[]   L = new double[] {SP3Read.T.get((int)Math.ceil(f0))*1.0e-6}; 
            
            AtAi = matris.MatMult(matris.Transpose(A), A);
            AtLi = matris.MatMult(matris.Transpose(A), L);
            
            for (int j = 0; j < AtAi.length; j++)
            {
		AtL[j] += AtLi[j];
		for (int j2 = 0; j2 < AtAi[0].length; j2++)
                    AtA[j][j2] += AtAi[j][j2];
            }
        }    
        double[][] Q = matris.invert(AtA);
        double[] x = matris.MatMult(Q, AtL);
            
        variables.a0 += x[0];
        variables.a1 += x[1];
        variables.a2 += x[2];
        
   }
}
