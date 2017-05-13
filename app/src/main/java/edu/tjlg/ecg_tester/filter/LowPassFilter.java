package edu.tjlg.ecg_tester.filter;

public class LowPassFilter {

	static int fifterBegin=6;
	double xBuff[]=null;
	double yBuff[];
	public double[] lvboFilter(double a[] ){
		xBuff=a;
		yBuff=new double[xBuff.length];
		for(int i=0;i<xBuff.length;i++)
			yBuff[i]=fifter(i);
		return yBuff;

	}

	public double[] getyBuff(){
		return yBuff;
	}
	double  fifter(int i){
		double xtemBuff[]=new double[6];
		double ytemBuff[]=new double[6];
		int j;
		j=i;

		if(i<fifterBegin){

			for(int i1=5;i1>5-j-1;i1--)
				if(i>=0){
					xtemBuff[i1]=xBuff[i];
					if(i-1>=0){
						ytemBuff[i1]=yBuff[i-1];}
					i--;
				}
			for(int t=0;t<6-j-1;t++){
				xtemBuff[t]=0;
				ytemBuff[t]=0;
			}
		}
		else{
			for(int i1=5;i1>=0;i1--){
				xtemBuff[i1]=xBuff[i];
				if(i-1>=0){
					ytemBuff[i1]=yBuff[i-1];}
				else{
					ytemBuff[i1]=yBuff[i];
				}
				i--;
			}
		}

		yBuff[j]=(0.0017*xtemBuff[5]+0.0086*xtemBuff[4]+0.0172*xtemBuff[3]+0.0172*xtemBuff[2]+0.0086*xtemBuff[1]+0.0017*xtemBuff[0])-
				( -3.1266*ytemBuff[5]+4.4643*ytemBuff[4]-3.4929*ytemBuff[3] +1.4812*ytemBuff[2]-0.2708*ytemBuff[1]);
		return yBuff[j];    
	}
}

