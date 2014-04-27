package com.kangear.capacitycalculator;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.service.CapacityService;

public class MainActivity extends Activity {
	private final String LOG_TAG = "MainActivity";
	private boolean DEBUG = false;
	/** 码流　unit:Kbps */
	private int mDataRate = 4 * 1024;
	/** raid_limit */
	private String[] mRaidLimits = { "RAID0或不做RAID", "RAID1/RAID10",
			"RAID3/RAID5", "RAID5+1热备盘/RAID6", "RAID6+1热备盘" };
	private Spinner mRaidLimitSpinner;

	private EditText inputNums_edittext;
	private EditText daysOfDateStorage_edittext;
	private TextView net_capacity_textview;
	private TextView drive_nums_textview;
	private EditText raid_panshu_edittext;
	private EditText zhugui_drive_nums_edittext;
	private EditText lushu_edittext;
	private TextView result_textview;
	private TextView total_capacity_textview;
	private TextView total_capacity_times_label_textview;
	private Spinner data_rate_spinner;
	private EditText data_rate_edittext;
	private EditText drive_capacity_edittext;
	private Spinner drive_capacity_spinner;

	/** 输入路数 */
	private int mInputNums = 0;

	/** 存储天数 */
	private int mDaysOfDateStorage = 30;

	/**
	 * 总净容量公式：总净容量=(码流*摄像头数*存储时间*3600*24)/(8*1000*1000)
	 */
	private float mNetCapacity_float;

	/** 向上取整后总净容量 */
	private int mNetCapacity_int;

	private int mTotalNetCapacity;

	private int mCumulativeNumberOfTimes;

	/** 　磁盘容量 单位TB */
	private int mDriveCapacity = 2;

	/** 　raid盘数 */
	private int mRaidPanShu;

	/**
	 * Raid级别 【RAID0或不做RAID】：N=A/B，然后再取整，因为硬盘不能是小数，必须为整数；
	 * 【RAID1/RAID10】:N={A/B取整}*2； 【RAID3/RAID5】：N=((A/B)/C)取整+（A/B）取整
	 * 【RAID3或5+热备盘/RAID6】:N=((A/B)/C)取整*2+（A/B）取整
	 * 【RAID6+1热备盘】：N=((A/B)/C)取整*3+（A/B）取整 【共需硬盘数】：N 【净容量取整】：A 【单块硬盘的容量】：B
	 * 【RAID盘数】：C
	 */
	private int mRaidLimit;

	/** 　共需硬盘数 */
	private int mDriveNums;

	/** 主柜硬盘数 */
	private int mZhuGuiDriveNums;

	/**
	 * 共需阵列数 【共需硬盘数】/【主柜硬盘数】然后向上取整
	 */
	private int mZhenLieNums;

	/** 单机头个数 */
	private int mSingleHandpieceNums;

	private boolean isSingleHandpiece = true;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();

	}

	private void init() {
		/* 码流相关 */
		data_rate_spinner = (Spinner) this.findViewById(R.id.data_rate_spinner);
		data_rate_edittext = (EditText) this.findViewById(R.id.data_rate_edittext);
		ArrayAdapter<CharSequence> data_rate_adapter = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_item, CapacityService.getDateRate());
		data_rate_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		data_rate_spinner.setAdapter(data_rate_adapter);
		data_rate_spinner.setPrompt("设置码流");
		data_rate_spinner.setSelection(CapacityService.dateRateToNum(4*1024));
		data_rate_spinner
		.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(9 == arg2) {
					/*自定义*/
					data_rate_edittext.setText(String.valueOf(mDataRate));
					data_rate_edittext.setEnabled(true);
				} else {
					mDataRate = CapacityService.setDataRate(arg2);
					data_rate_edittext.setText(String.valueOf(mDataRate));
					data_rate_edittext.setEnabled(false);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		data_rate_edittext.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				if (!data_rate_edittext.getText().toString().matches("")) {
					mDataRate = Integer.valueOf(data_rate_edittext.getText()
							.toString());
				} else {
					mDataRate = 0;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}
		});

		net_capacity_textview = (TextView) this
				.findViewById(R.id.capacity_textview);

		/* 输入路数相关 */
		mInputNums = 36;
		inputNums_edittext = (EditText) this.findViewById(R.id.lushu_edittext);
		lushu_edittext = (EditText) this.findViewById(R.id.lushu_edittext);
		lushu_edittext.setFocusableInTouchMode(true);
		lushu_edittext.requestFocus();
		lushu_edittext.setText(String.valueOf(mInputNums));
		lushu_edittext.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				if (!lushu_edittext.getText().toString().matches("")) {
					mInputNums = Integer.valueOf(lushu_edittext.getText()
							.toString());
				} else {
					mInputNums = 0;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}
		});
		
		/*　存储时间　*/
		mDaysOfDateStorage = 30;
		daysOfDateStorage_edittext = (EditText) this
				.findViewById(R.id.daysOfDateStorage_edittext);
		daysOfDateStorage_edittext.setText(String.valueOf(mDaysOfDateStorage));
		daysOfDateStorage_edittext.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				if (!daysOfDateStorage_edittext.getText().toString()
						.matches("")) {
					mDaysOfDateStorage = Integer
							.valueOf(daysOfDateStorage_edittext.getText()
									.toString());
				} else {
					mDaysOfDateStorage = 0;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}
		});
		
		/* 总容量相关　 */
		total_capacity_textview = (TextView) this
				.findViewById(R.id.total_capacity_textview);
		total_capacity_times_label_textview = (TextView) this
				.findViewById(R.id.total_capacity_times_label_textview);
		
		/* 单个磁盘容量相关 */
		mDriveCapacity = 2;
		drive_capacity_edittext = (EditText) this.findViewById(R.id.drive_capacity_edittext);
		drive_capacity_spinner = (Spinner) this.findViewById(R.id.drive_capacity_spinner);
		ArrayAdapter<CharSequence> drive_capacity_adapter = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_item, CapacityService.getDriveCapacity());
		drive_capacity_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		drive_capacity_spinner.setAdapter(drive_capacity_adapter);
		drive_capacity_spinner.setPrompt("设置码流");
		drive_capacity_spinner.setSelection(CapacityService.driveCapacityToNum(2));
		drive_capacity_spinner
		.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(3 == arg2) {
					/*自定义*/
					drive_capacity_edittext.setText(String.valueOf(mDriveCapacity));
					drive_capacity_edittext.setEnabled(true);
				} else {
					mDriveCapacity = CapacityService.getDriveCapacity(arg2);
					drive_capacity_edittext.setEnabled(false);
					drive_capacity_edittext.setText(String.valueOf(mDriveCapacity));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		
		
		/* RAID盘数相关 */
		mRaidPanShu = 16;
		raid_panshu_edittext = (EditText) this
				.findViewById(R.id.raid_panshu_edittext);
		raid_panshu_edittext.setText(mRaidPanShu + "");
		raid_panshu_edittext.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				if (!raid_panshu_edittext.getText().toString().matches("")) {
					// mRaidPanShu = Integer.valueOf(raid_panshu_edittext
					// .getText().toString());
					Log.i(LOG_TAG, "mRaidPanShu: " + mRaidPanShu);
				} else {
					// mRaidPanShu = Integer.valueOf(raid_panshu_edittext
					// .getText().toString());
					Log.i(LOG_TAG, "mRaidPanShu: " + mRaidPanShu);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}
		});

		/* RAID级别相关 */
		mRaidLimitSpinner = (Spinner) this
				.findViewById(R.id.raid_limit_spinner);

		ArrayAdapter<String> raidlimit_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mRaidLimits);
		raidlimit_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mRaidLimitSpinner.setAdapter(raidlimit_adapter);
		mRaidLimitSpinner.setPrompt("选择RAID级别");
		mRaidLimit = 3;
		mRaidLimitSpinner.setSelection(mRaidLimit);
		mRaidLimitSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						if (DEBUG)
							Log.i(LOG_TAG, "RAID级别: " + String.valueOf(arg2));
						mRaidLimit = arg2;
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});

		/* 硬盘数量相关 */
		drive_nums_textview = (TextView) this
				.findViewById(R.id.drive_nums_textview);

		/* 主柜硬盘数　 */
		mZhuGuiDriveNums = 16;
		zhugui_drive_nums_edittext = (EditText) this
				.findViewById(R.id.zhugui_drive_nums_edittext);
		zhugui_drive_nums_edittext.setText(mZhuGuiDriveNums + "");
		zhugui_drive_nums_edittext.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				if (!zhugui_drive_nums_edittext.getText().toString()
						.matches("")) {
					mZhuGuiDriveNums = Integer
							.valueOf(zhugui_drive_nums_edittext.getText()
									.toString());
					Log.i(LOG_TAG, "mZhuGuiDriveNums: " + mZhuGuiDriveNums);
				} else {
					// mRaidPanShu = Integer.valueOf(raid_panshu_edittext
					// .getText().toString());
					Log.i(LOG_TAG, "mZhuGuiDriveNums: " + mZhuGuiDriveNums);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}
		});

		/* 阵列数相关　 */
		result_textview = (TextView) this.findViewById(R.id.result_textview);

	}

	public void openDriveCapacityActivity(View v) {
		Intent intent = new Intent();
		intent.putExtra("DriveCapacity", String.valueOf(mDriveCapacity));
		intent.setClass(this, DriveCapacityActivity.class);
		startActivityForResult(intent,
				CapacityService.REQUESTED_ORIENTATION_DRIVE_CAPACITY);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			break;
		default:
			break;
		}
	}

	public void countNetCapacity(View v) {
		mInputNums = Integer.valueOf(inputNums_edittext.getText().toString());
		mNetCapacity_float = CapacityService.getNetCapcity(mDataRate,
				mInputNums, mDaysOfDateStorage);
		mNetCapacity_int = (int) (Math.ceil(mNetCapacity_float));
		if (DEBUG)
			Log.i(LOG_TAG, "净容量为: " + mNetCapacity_float + "TB;\n" + "取整为: "
					+ mNetCapacity_int + "TB");
		net_capacity_textview.setText("净容量为:" + mNetCapacity_float + "TB\n"
				+ "取整后为:" + mNetCapacity_int + "TB");
	}

	public void countTotalNetCapacity(View v) {
		mTotalNetCapacity += mNetCapacity_int;
		mCumulativeNumberOfTimes++;
		total_capacity_textview.setText(mTotalNetCapacity + "TB");
		total_capacity_times_label_textview.setText("次数:"
				+ mCumulativeNumberOfTimes);
	}

	public void cleanTotalNetCapacity(View v) {
		mTotalNetCapacity = 0;
		mCumulativeNumberOfTimes = 0;
		total_capacity_textview.setText(mTotalNetCapacity + "TB");
		total_capacity_times_label_textview.setText("次数:"
				+ mCumulativeNumberOfTimes);
	}

	public void countDriveNums(View v) {
		if (DEBUG)
			Log.i(LOG_TAG, "计算所需硬盘数,总净容量为: " + mTotalNetCapacity + "TB;\n"
					+ "单个磁盘容量为: " + mDriveCapacity + "TB;\n" + "RAID级别为: "
					+ mRaidLimit + ";\n" + mRaidPanShu + "盘位;");
		if (mTotalNetCapacity != 0) {
			try {
				mDriveNums = CapacityService.getDriveNums(mRaidLimit,
						mTotalNetCapacity, mDriveCapacity, mRaidPanShu);
			} catch (Exception e) {
				e.printStackTrace();
			}
			drive_nums_textview.setText(String.valueOf(mDriveNums) + "块");
		} else if (mNetCapacity_int != 0) {
			try {
				mDriveNums = CapacityService.getDriveNums(mRaidLimit,
						mNetCapacity_int, mDriveCapacity, mRaidPanShu);
			} catch (Exception e) {
				e.printStackTrace();
			}
			drive_nums_textview.setText(String.valueOf(mDriveNums) + "块");
		} else {
			Toast.makeText(MainActivity.this, "啊哦，您忘记计算总净容量或总净容量为0!",
					Toast.LENGTH_LONG).show();
		}

	}

	public void countZhenLieNums(View v) {
		try {
			mSingleHandpieceNums = CapacityService.getSingleHandpieceNums(
					mDriveNums, mZhuGuiDriveNums);
			Log.i(LOG_TAG, "共需个 " + mSingleHandpieceNums + "单机头");
			result_textview.setText(Html.fromHtml("共需 " + "<u>"
					+ mSingleHandpieceNums + "</u>" + " 个单机头"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 菜单、返回键响应
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click(); // 调用双击退出函数
		}
		return false;
	}

	/**
	 * 双击退出函数
	 */
	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, "再按一次 退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

		} else {
			finish();
			System.exit(0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void openAboutActivity() {
		Intent intent = new Intent();
		intent.setClass(this, AboutActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_about:
			openAboutActivity();
			return true;
		case R.id.action_settings:
			// showHelp();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
