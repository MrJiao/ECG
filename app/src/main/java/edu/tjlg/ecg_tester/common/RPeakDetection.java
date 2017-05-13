package edu.tjlg.ecg_tester.common;

import java.util.ArrayList;

public class RPeakDetection {
	private int nRWidth = 180;
	public RPeakDetection()
	{

	}
	public RPeakDetection(int samplingRate)
	{
		this.nRWidth = samplingRate;
	}
	/// <summary>
	/// 基于斜率的R波检测算法,实现了对给定ECG数据的R点定位并返回检测到的R点位置
	/// </summary>
	/// <param name="data">需要检测R点的数据数组</param>
	/// <returns>返回找到的R数组</returns>
	public float[] RPeakRecognize(float[] data)
	{
		if (null == data)
		{
			return null;
		}
		ArrayList<Float> ExtremeTemp = new  ArrayList<Float>();  //存放R点的临时列表
		ArrayList<Float> ExtremeSlop = new ArrayList<Float>(); //通过比较斜率也排除了大T波的影响

		int nBase = 0;           //坡地位置
		float nMaxExtent = 0;     //当前最高点的值
		int nMaxExtentPos = 0;  //当前最高点位置,即在R中的索引
		int nRHeight = 60;      //R高度
		//int nRWidth = 180;    //单个R波宽度

		int nRWaveCount = 0;  //当前找到的R的数量

		float slop_temp;
		int count = 20;  //计数，取前20个的值为高度的计算值

		for (int i = 0; i < data.length - 1; i++)//对全部点进行自比较
		{
			if (Math.abs(data[i]) >= nMaxExtent)//坡还在上升                
			{
				nMaxExtent = Math.abs(data[i]);
				nMaxExtentPos = i;
			}
			else//在第一个下降的点处进行R波判定
			{
				if (Math.abs(data[i] - data[nMaxExtentPos]) > nMaxExtent / 2 || nMaxExtent > nRHeight / 2) //第一个急剧下降的点，或高度> nRHeight / 2
				{
					if (nMaxExtent > nRHeight / 2)//设置高度
					{
						if ((nRWaveCount == 0) || (i - ExtremeTemp.get(nRWaveCount - 1)) > nRWidth)//第一个波形,或者超出一个波形的宽度,即采样点个数
						{
							if (nRWaveCount > 0 && count > 0)
							{
								nRHeight = (int)(0.7 * nRHeight + 0.3 * Math.abs(data[(int)(float)ExtremeTemp.get(nRWaveCount - 1)]));//动态调整R高度阈值
								count--;
							}
							ExtremeTemp.add((float)nMaxExtentPos);//找到一个R点,添加到R列表
							int t_x = nMaxExtentPos - nBase;
							if (t_x == 0)
							{
								ExtremeSlop.add((float)0);
							}
							else
							{
								ExtremeSlop.add(Math.abs(data[nMaxExtentPos] - data[nBase]) / t_x); //斜率
							}
							nRWaveCount++;
						}
						else if (nRWaveCount != 0)
						{
							slop_temp = Math.abs(data[nMaxExtentPos] - data[nBase]) / (nMaxExtentPos - nBase);
							if (Math.abs(data[nMaxExtentPos]) > Math.abs(data[(int)(float)ExtremeTemp.get(nRWaveCount - 1)]) && Math.abs(slop_temp) > 1/*&& ExtremeSlop[nRWaveCount - 1] < slop_temp*/)
							{
								ExtremeTemp.set(nRWaveCount - 1,(float)nMaxExtentPos);
								ExtremeSlop.set(nRWaveCount - 1,(float)slop_temp);
							}

						}
					}//针对抖动爬坡的情况，可以除去一些爬坡的点，并不算r点
					else if (nRWaveCount != 0)
					{
						slop_temp = Math.abs(data[nMaxExtentPos] - data[nBase]) / (nMaxExtentPos - nBase);
						if (Math.abs(data[nMaxExtentPos]) > Math.abs(data[(int)(float)ExtremeTemp.get(nRWaveCount - 1)]) && Math.abs(slop_temp) > 1/*&& ExtremeSlop[nRWaveCount - 1] < slop_temp*/)
						{
							ExtremeTemp.set(nRWaveCount - 1,(float)nMaxExtentPos);
							ExtremeSlop.set(nRWaveCount - 1,(float)slop_temp);
						}

					}
					nBase = nMaxExtentPos;
					nMaxExtent = 0;
				}
			}
		}

		float[] RpeakPointData = new float[ExtremeTemp.size()];
		for(int j = 0; j < ExtremeTemp.size(); j++){
			RpeakPointData[j] = ExtremeTemp.get(j);
		}
		return RpeakPointData;
	}
}


