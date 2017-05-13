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
	/// ����б�ʵ�R������㷨,ʵ���˶Ը���ECG���ݵ�R�㶨λ�����ؼ�⵽��R��λ��
	/// </summary>
	/// <param name="data">��Ҫ���R�����������</param>
	/// <returns>�����ҵ���R����</returns>
	public float[] RPeakRecognize(float[] data)
	{
		if (null == data)
		{
			return null;
		}
		ArrayList<Float> ExtremeTemp = new  ArrayList<Float>();  //���R�����ʱ�б�
		ArrayList<Float> ExtremeSlop = new ArrayList<Float>(); //ͨ���Ƚ�б��Ҳ�ų��˴�T����Ӱ��

		int nBase = 0;           //�µ�λ��
		float nMaxExtent = 0;     //��ǰ��ߵ��ֵ
		int nMaxExtentPos = 0;  //��ǰ��ߵ�λ��,����R�е�����
		int nRHeight = 60;      //R�߶�
		//int nRWidth = 180;    //����R�����

		int nRWaveCount = 0;  //��ǰ�ҵ���R������

		float slop_temp;
		int count = 20;  //������ȡǰ20����ֵΪ�߶ȵļ���ֵ

		for (int i = 0; i < data.length - 1; i++)//��ȫ��������ԱȽ�
		{
			if (Math.abs(data[i]) >= nMaxExtent)//�»�������                
			{
				nMaxExtent = Math.abs(data[i]);
				nMaxExtentPos = i;
			}
			else//�ڵ�һ���½��ĵ㴦����R���ж�
			{
				if (Math.abs(data[i] - data[nMaxExtentPos]) > nMaxExtent / 2 || nMaxExtent > nRHeight / 2) //��һ�������½��ĵ㣬��߶�> nRHeight / 2
				{
					if (nMaxExtent > nRHeight / 2)//���ø߶�
					{
						if ((nRWaveCount == 0) || (i - ExtremeTemp.get(nRWaveCount - 1)) > nRWidth)//��һ������,���߳���һ�����εĿ��,�����������
						{
							if (nRWaveCount > 0 && count > 0)
							{
								nRHeight = (int)(0.7 * nRHeight + 0.3 * Math.abs(data[(int)(float)ExtremeTemp.get(nRWaveCount - 1)]));//��̬����R�߶���ֵ
								count--;
							}
							ExtremeTemp.add((float)nMaxExtentPos);//�ҵ�һ��R��,��ӵ�R�б�
							int t_x = nMaxExtentPos - nBase;
							if (t_x == 0)
							{
								ExtremeSlop.add((float)0);
							}
							else
							{
								ExtremeSlop.add(Math.abs(data[nMaxExtentPos] - data[nBase]) / t_x); //б��
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
					}//��Զ������µ���������Գ�ȥһЩ���µĵ㣬������r��
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


