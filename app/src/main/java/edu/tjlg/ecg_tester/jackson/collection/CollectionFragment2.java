package edu.tjlg.ecg_tester.jackson.collection;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import edu.tjlg.ecg_tester.R;


@SuppressLint("NewApi")
public class CollectionFragment2 extends Fragment implements CollectionContract.View, View.OnClickListener {

	private TextView tv_state;//状态
	private Button btn_disconnect;
	private Button btn_measuring;
	private Button btn_connect;

	private ProgressBar draft_progress_bar;
	private TextView title_textView;
	private CollectionPresenter collectionPresenter;
	private View ll_celiang;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.collect_data2, null);
		bindView(view);
		setListener();
		initView();
		collectionPresenter = CollectionPresenter.newInstance(this);
		collectionPresenter.initBluetooth(getActivity());
		return view;
	}

	private void bindView(View view) {
		title_textView = (TextView) view.findViewById(R.id.title_textView);
		tv_state = (TextView) view.findViewById(R.id.tv_state);

		btn_disconnect = (Button) view.findViewById(R.id.btn_disconnect);
		btn_measuring = (Button) view.findViewById(R.id.btn_measuring);
		btn_connect = (Button) view.findViewById(R.id.btn_connect);

		draft_progress_bar = (ProgressBar) view.findViewById(R.id.draft_progress_bar);
		ll_celiang = view.findViewById(R.id.ll_celiang);
	}
	
	private void setListener() {
		btn_disconnect.setOnClickListener(this);
		btn_measuring.setOnClickListener(this);
		btn_connect.setOnClickListener(this);
	}
	
	private void initView() {
		title_textView.setText("心电采集");
	}

	@Override
	public void showConnecting() {
		ll_celiang.setVisibility(View.GONE);
		btn_connect.setVisibility(View.GONE);
		tv_state.setText("正在连接");
	}

	@Override
	public void showConnectSuccess() {
		ll_celiang.setVisibility(View.VISIBLE);
		btn_connect.setVisibility(View.GONE);
		tv_state.setText("连接蓝牙成功");

	}

	@Override
	public void showConnectError() {
		ll_celiang.setVisibility(View.GONE);
		btn_connect.setVisibility(View.VISIBLE);
		tv_state.setText("连接蓝牙失败");
	}

	@Override
	public void showCollecting() {
		tv_state.setText("正在采集");

	}

	@Override
	public void showBluetoothDisabled() {
		ll_celiang.setVisibility(View.GONE);
		btn_connect.setVisibility(View.VISIBLE);
		tv_state.setText("蓝牙连接失败");
	}

	@Override
	public void showFindDevice() {
		tv_state.setText("发现设备");
	}

	@Override
	public void showDisConnect() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btn_disconnect:
				collectionPresenter.disConnect();
				break;
			case R.id.btn_measuring:
				collectionPresenter.measuring(getActivity(),draft_progress_bar);
				break;
			case R.id.btn_connect:
				collectionPresenter.connect();
				break;
		}
	}
}
