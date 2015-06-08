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
	private final boolean DEBUG = false;
	/** 码流　unit:Kbps */
	private int mDataRate = 4 * 1024;
	/** raid_limit */
	private final String[] mRaidLimits = { "RAID0或不做RAID", "RAID1/RAID10",
			"RAID3/RAID5", "RAID5+1热备盘/RAID6", "RAID6+1热备盘" };
	private Spinner mRaidLimitSpinner;

	private EditText daysOfDateStorageEdittext;
	private TextView mNetCapacityTextView;
	private TextView drive_nums_textview;
	private EditText raid_panshu_edittext;
	private EditText zhugui_drive_nums_edittext;
	private EditText mLushuEdittext;
	private TextView result_textview;
	private TextView mTotalCapacityTextView;
	private TextView mTotalCapacityTimesLabelTextView;
	private Spinner mDatarateSpinner;
	private EditText mDatarateEdittext;
	private EditText mDriveCapacityEditText;
	private Spinner mDriveCapacitySpinner;

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

	/** 单机头个数 */
	private int mSingleHandpieceNums;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();
	}

	private void init() {
		/* 码流相关 */
		mDatarateSpinner = (Spinner) this.findViewById(R.id.data_rate_spinner);
		mDatarateEdittext = (EditText) this.findViewById(R.id.data_rate_edittext);
		ArrayAdapter<CharSequence> data_rate_adapter = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_item, CapacityService.getDateRate());
		data_rate_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mDatarateSpinner.setAdapter(data_rate_adapter);
		mDatarateSpinner.setPrompt("设置码流");
		mDatarateSpinner.setSelection(CapacityService.dateRateToNum(4*1024));
		mDatarateSpinner
		.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(9 == arg2) {
					/*自定义*/
					mDatarateEdittext.setText(String.valueOf(mDataRate));
					mDatarateEdittext.setEnabled(true);
				} else {
					mDataRate = CapacityService.setDataRate(arg2);
					mDatarateEdittext.setText(String.valueOf(mDataRate));
					mDatarateEdittext.setEnabled(false);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		mDatarateEdittext.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (!mDatarateEdittext.getText().toString().matches("")) {
					mDataRate = Integer.valueOf(mDatarateEdittext.getText()
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

		mNetCapacityTextView = (TextView) this
				.findViewById(R.id.capacity_textview);

		/* 输入路数相关 */
		mInputNums = 36;
		mLushuEdittext = (EditText) this.findViewById(R.id.lushu_edittext);
		mLushuEdittext.setFocusableInTouchMode(true);
		mLushuEdittext.requestFocus();
		mLushuEdittext.setText(String.valueOf(mInputNums));
		mLushuEdittext.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (!mLushuEdittext.getText().toString().matches("")) {
					mInputNums = Integer.valueOf(mLushuEdittext.getText()
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
		daysOfDateStorageEdittext = (EditText) this
				.findViewById(R.id.daysOfDateStorage_edittext);
		daysOfDateStorageEdittext.setText(String.valueOf(mDaysOfDateStorage));
		daysOfDateStorageEdittext.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (!daysOfDateStorageEdittext.getText().toString()
						.matches("")) {
					mDaysOfDateStorage = Integer
							.valueOf(daysOfDateStorageEdittext.getText()
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
		mTotalCapacityTextView = (TextView) this
				.findViewById(R.id.total_capacity_textview);
		mTotalCapacityTimesLabelTextView = (TextView) this
				.findViewById(R.id.total_capacity_times_label_textview);

		/* 单个磁盘容量相关 */
		mDriveCapacity = 2;
		mDriveCapacityEditText = (EditText) this.findViewById(R.id.drive_capacity_edittext);
		mDriveCapacitySpinner = (Spinner) this.findViewById(R.id.drive_capacity_spinner);
		ArrayAdapter<CharSequence> drive_capacity_adapter = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_item, CapacityService.getDriveCapacity());
		drive_capacity_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mDriveCapacitySpinner.setAdapter(drive_capacity_adapter);
		mDriveCapacitySpinner.setPrompt("设置码流");
		mDriveCapacitySpinner.setSelection(CapacityService.driveCapacityToNum(2));
		mDriveCapacitySpinner
		.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(4 == arg2) {
					/*自定义*/
					mDriveCapacityEditText.setEnabled(true);
				} else {
					mDriveCapacityEditText.setEnabled(false);
					mDriveCapacityEditText.setText(String.valueOf(CapacityService.getDriveCapacity(arg2)));
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
			@Override
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
			@Override
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
		mInputNums = Integer.valueOf(mLushuEdittext.getText().toString());
		mNetCapacity_float = CapacityService.getNetCapcity(mDataRate,
				mInputNums, mDaysOfDateStorage);
		mNetCapacity_int = (int) (Math.ceil(mNetCapacity_float));
		if (DEBUG)
			Log.i(LOG_TAG, "净容量为: " + mNetCapacity_float + "TB;\n" + "取整为: "
					+ mNetCapacity_int + "TB");
		mNetCapacityTextView.setText("净容量为:" + mNetCapacity_float + "TB\n"
				+ "取整后为:" + mNetCapacity_int + "TB");
	}
	String old_tmp_list = "";
	public void countTotalNetCapacity(View v) {
		mTotalNetCapacity += mNetCapacity_int;
		String tmp_list = old_tmp_list;
		if(mCumulativeNumberOfTimes == 0) {
			tmp_list = "";
		}
		tmp_list = tmp_list + (mCumulativeNumberOfTimes == 0 ? "" : ",") + mNetCapacity_int;
		old_tmp_list = tmp_list;
		mCumulativeNumberOfTimes++;
		//total_capacity_textview.setText(mTotalNetCapacity + "TB");
		mTotalCapacityTextView.setText(tmp_list + " " + mTotalNetCapacity);
		mTotalCapacityTimesLabelTextView.setText("次数:"
				+ mCumulativeNumberOfTimes);
	}

	public void cleanTotalNetCapacity(View v) {
		mTotalNetCapacity = 0;
		mCumulativeNumberOfTimes = 0;
		mTotalCapacityTextView.setText("");
		old_tmp_list = "";
		mTotalCapacityTimesLabelTextView.setText("次数:"
				+ mCumulativeNumberOfTimes);
	}

	public void countDriveNums(View v) {
		if (DEBUG)
			Log.i(LOG_TAG, "计算所需硬盘数,总净容量为: " + mTotalNetCapacity + "TB;\n"
					+ "单个磁盘容量为: " + mDriveCapacity + "TB;\n" + "RAID级别为: "
					+ mRaidLimit + ";\n" + mRaidPanShu + "盘位;");

		// 获取最新Drive Capacity值
		String capString = mDriveCapacityEditText.getText().toString();
		if(capString.equals("")) {
			Toast.makeText(MainActivity.this, "磁盘容量不能为空!",
					Toast.LENGTH_SHORT).show();
			return;
		}

		mDriveCapacity = Integer.valueOf(capString);

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

	public void openAboutActivity() {
		Intent intent = new Intent();
		intent.setClass(this, AboutActivity.class);
		startActivity(intent);
	}
//
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(LOG_TAG, "onOptionsItemSelected");
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
