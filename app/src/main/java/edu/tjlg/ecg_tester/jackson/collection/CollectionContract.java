package edu.tjlg.ecg_tester.jackson.collection;

/**
 * Created by jackson on 2017/5/13.
 */

public class CollectionContract {

    interface View{

        void showConnecting();//��������

        void showConnectSuccess();//���ӳɹ�

        void showConnectError();//����ʧ��

        void showCollecting();//���ڲɼ�

        void showBluetoothDisabled();//����������

        void showFindDevice();//�����豸

        void showDisConnect();//�Ͽ�����

    }


}
