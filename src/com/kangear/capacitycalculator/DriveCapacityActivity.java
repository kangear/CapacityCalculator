package com.kangear.capacitycalculator;

import com.example.service.CapacityService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class DriveCapacityActivity extends Activity {
	private final String LOG_TAG = "DriveCapacityActivity";
	/** 磁盘容量　单位:TB */
	private int mDriveCapacity;
	private RadioGroup mRadioGroup;
	private Button mButton;
	private EditText mEdittext;
	private RadioButton drive_capacity_custom_radiobutton;
	private RadioButton drive_capacity_1t_radiobutton;
	private RadioButton drive_capacity_2t_radiobutton;
	private RadioButton drive_capacity_3t_radiobutton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drive);

		mButton = (Button) this.findViewById(R.id.drive_capacity_ok_button);
		mEdittext = (EditText) this.findViewById(R.id.drive_capacity_edittext);
		drive_capacity_custom_radiobutton = (RadioButton) this
				.findViewById(R.id.drive_capacity_custom_radiobutton);
		mRadioGroup = (RadioGroup) this
				.findViewById(R.id.drive_capacity_radiogroup);
		mRadioGroup.setOnCheckedChangeListener(listen);

		mEdittext.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				if (!mEdittext.getText().toString().matches("")) {
					mDriveCapacity = Integer.valueOf(mEdittext.getText()
							.toString());
					mButton.setEnabled(true);
				} else {
					mButton.setEnabled(false);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				drive_capacity_custom_radiobutton.setChecked(true);
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});

		drive_capacity_1t_radiobutton = (RadioButton) this
				.findViewById(R.id.drive_capacity_1t_radiobutton);
		drive_capacity_2t_radiobutton = (RadioButton) this
				.findViewById(R.id.drive_capacity_2t_radiobutton);
		drive_capacity_3t_radiobutton = (RadioButton) this
				.findViewById(R.id.drive_capacity_3t_radiobutton);

		/* 获取MainActivity传递过来的值　适当填充到界面中　 */
		Intent intent = getIntent();
		mDriveCapacity = Integer
				.valueOf(intent.getStringExtra("DriveCapacity"));
		Log.i(LOG_TAG, "mDriveCapacity: " + mDriveCapacity);
		if (mDriveCapacity != 0) {
			switch (mDriveCapacity) {
			case 1:
				drive_capacity_1t_radiobutton.setChecked(true);
				break;
			case 2:
				drive_capacity_2t_radiobutton.setChecked(true);
				break;
			case 3:
				drive_capacity_3t_radiobutton.setChecked(true);
				break;
			default:
				drive_capacity_custom_radiobutton.setChecked(true);
				mEdittext.setText(String.valueOf(mDriveCapacity));
				break;

			}
		}

	}

	public void OnButtonClick(View v) {
		Intent data = new Intent();
		data.putExtra(CapacityService.REQUESTED_NAME_DRIVE_CAPACITY,
				mDriveCapacity);
		DriveCapacityActivity.this.setResult(RESULT_OK, data);
		DriveCapacityActivity.this
				.setRequestedOrientation(CapacityService.REQUESTED_ORIENTATION_DRIVE_CAPACITY);
		DriveCapacityActivity.this.finish();
	}

	private OnCheckedChangeListener listen = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			mButton.setEnabled(true);
			mEdittext.setEnabled(false);
			switch (group.getCheckedRadioButtonId()) {
			case R.id.drive_capacity_1t_radiobutton:
				mDriveCapacity = 1;
				break;
			case R.id.drive_capacity_2t_radiobutton:
				mDriveCapacity = 2;
				break;
			case R.id.drive_capacity_3t_radiobutton:
				mDriveCapacity = 3;
				break;
			case R.id.drive_capacity_custom_radiobutton:
				mEdittext.setEnabled(true);
				if (!mEdittext.getText().toString().matches("")) {
					mDriveCapacity = Integer.valueOf(mEdittext.getText()
							.toString());
				} else {
					mButton.setEnabled(false);
				}

				break;
			default:
				break;
			}
		}
	};

}
