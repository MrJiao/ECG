package edu.tjlg.ecg_tester.filter;

public class MovingAverageFilter {

	public double[] filter(double a[]){
		double array[]=new double[a.length];
		array[0]=(3*a[0]+2*a[1]+a[2]-a[3])/5;
		array[1]=(4*a[0]+3*a[2]+2*a[2]+a[3])/10;
		for(int i=2;i<a.length-2;i++)
			array[i]=(a[i-2]+a[i-1]+a[i]+a[i+1]+a[i+2])/5;
		array[a.length-2]=(a[a.length-4]+2*a[a.length-3]+3*a[a.length-2]+4*a[a.length-1])/10;
		array[a.length-1]=(-a[a.length-4]+a[a.length-3]+2*a[a.length-2]+3*a[a.length-1])/5; 
		return array;	
	}
}
