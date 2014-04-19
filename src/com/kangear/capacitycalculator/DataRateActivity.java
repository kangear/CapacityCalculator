package com.kangear.capacitycalculator;

import com.example.service.CapacityService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class DataRateActivity extends Activity {
	private final String LOG_TAG = "DataRateActivity";
	private int mDataRate;
	private int mOldDataRate;
	private EditText custom_data_rate;
	private TextView custom_data_rate_unit;
	private RadioGroup mRadioGroup;
	private Button ok_button;

	RadioButton _512kbps_radiobutton;
	RadioButton _1mbps_radiobutton;
	RadioButton _1_5mbps_radiobutton;
	RadioButton _2mbps_radiobutton;
	RadioButton _3mbps_radiobutton;
	RadioButton _4mbps_radiobutton;
	RadioButton _5mbps_radiobutton;
	RadioButton _6mbps_radiobutton;
	RadioButton _8mbps_radiobutton;
	RadioButton input_by_hand_radiobutton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_rate);
		init();
	}

	private void init() {
		/* RadioButton 相关 */
		_512kbps_radiobutton = (RadioButton) this
				.findViewById(R.id._512kbps_radiobutton);
		_1mbps_radiobutton = (RadioButton) this
				.findViewById(R.id._1mbps_radiobutton);
		_1_5mbps_radiobutton = (RadioButton) this
				.findViewById(R.id._1_5mbps_radiobutton);
		_2mbps_radiobutton = (RadioButton) this
				.findViewById(R.id._2mbps_radiobutton);
		_3mbps_radiobutton = (RadioButton) this
				.findViewById(R.id._3mbps_radiobutton);
		_4mbps_radiobutton = (RadioButton) this
				.findViewById(R.id._4mbps_radiobutton);
		_5mbps_radiobutton = (RadioButton) this
				.findViewById(R.id._5mbps_radiobutton);
		_6mbps_radiobutton = (RadioButton) this
				.findViewById(R.id._6mbps_radiobutton);
		_8mbps_radiobutton = (RadioButton) this
				.findViewById(R.id._8mbps_radiobutton);
		input_by_hand_radiobutton = (RadioButton) this
				.findViewById(R.id.input_by_hand_radiobutton);

		/* other 相关 */
		mRadioGroup = (RadioGroup) this.findViewById(R.id.data_rate_radiogroup);
		custom_data_rate = (EditText) this
				.findViewById(R.id.custom_data_rate_edittext);
		custom_data_rate_unit = (TextView) this
				.findViewById(R.id.custom_data_rate_textview);

		ok_button = (Button) this.findViewById(R.id.data_rate_ok_button);
		ok_button.setOnClickListener(new ButtonOnClickListener());
		mRadioGroup.setOnCheckedChangeListener(listen);
		setInputByHandState(false);

		custom_data_rate.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				if (!custom_data_rate.getText().toString().matches("")) {
					mDataRate = Integer.valueOf(custom_data_rate.getText()
							.toString());
					ok_button.setEnabled(true);
					Log.i(LOG_TAG, "DataRate: " + mDataRate);
				} else {
					ok_button.setEnabled(false);
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
		custom_data_rate
				.setOnEditorActionListener(new OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						boolean handled = false;
						if (actionId == EditorInfo.IME_ACTION_DONE) {
							closeThisActivity();
							handled = true;
						}
						return handled;
					}
				});

		/* 获取MainActivity传递过来的值　适当填充到界面中　 */
		Intent intent = getIntent();
		mOldDataRate = mDataRate = Integer.valueOf(intent.getStringExtra("DataRate"));
		Log.i(LOG_TAG, "mDataRate: " + mDataRate);
		if (mDataRate != 0) {
			switch (mDataRate) {
			case 512:
				_512kbps_radiobutton.setChecked(true);
				break;
			case 1 * 1024:
				_1mbps_radiobutton.setChecked(true);
				break;
			case (int) (1.5 * 1024):
				_1_5mbps_radiobutton.setChecked(true);
				break;
			case 2 * 1024:
				_2mbps_radiobutton.setChecked(true);
				break;
			case 3 * 1024:
				_3mbps_radiobutton.setChecked(true);
				break;
			case 4 * 1024:
				_4mbps_radiobutton.setChecked(true);
				break;
			case 5 * 1024:
				_5mbps_radiobutton.setChecked(true);
				break;
			case 6 * 1024:
				_6mbps_radiobutton.setChecked(true);
				break;
			case 8 * 1024:
				_8mbps_radiobutton.setChecked(true);
				break;
			default:
				input_by_hand_radiobutton.setChecked(true);
				custom_data_rate.setText(String.valueOf(mDataRate));
				break;

			}
		}
	}

	private void closeThisActivity() {
		Log.i(LOG_TAG, "mDataRate "+ mDataRate + "mOldDataRate" + mOldDataRate);
		Toast.makeText(
				DataRateActivity.this,
				"码流值:" + CapacityService.floatToString(mDataRate)
						+ (mDataRate != mOldDataRate ? "" : "(没有改变)"),
				Toast.LENGTH_SHORT).show();
		Intent data = new Intent();
		data.putExtra("DataRate", mDataRate);
		DataRateActivity.this.setResult(RESULT_OK, data);
		DataRateActivity.this.finish();
	}

	private final class ButtonOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			closeThisActivity();
		}
	}

	private OnCheckedChangeListener listen = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			setInputByHandState(false);
			ok_button.setEnabled(true);
			switch (group.getCheckedRadioButtonId()) {
			case R.id._512kbps_radiobutton:
				mDataRate = 512;
				break;
			case R.id._1mbps_radiobutton:
				mDataRate = 1 * 1024;
				break;
			case R.id._1_5mbps_radiobutton:
				mDataRate = (int) (1.5 * 1024);
				break;
			case R.id._2mbps_radiobutton:
				mDataRate = 2 * 1024;
				break;
			case R.id._3mbps_radiobutton:
				mDataRate = 3 * 1024;
				break;
			case R.id._4mbps_radiobutton:
				mDataRate = 4 * 1024;
				break;
			case R.id._5mbps_radiobutton:
				mDataRate = 5 * 1024;
				break;
			case R.id._6mbps_radiobutton:
				mDataRate = 6 * 1024;
				break;
			case R.id._8mbps_radiobutton:
				mDataRate = 8 * 1024;
				break;
			case R.id.input_by_hand_radiobutton:
				if (!custom_data_rate.getText().toString().matches("")) {
					mDataRate = Integer.valueOf(custom_data_rate.getText()
							.toString());
				} else {
					ok_button.setEnabled(false);
				}
				setInputByHandState(true);

				break;
			default:
				break;
			}
			Log.i(LOG_TAG, "DataRate: " + mDataRate);
		}
	};

	private void setInputByHandState(boolean state) {
		if (state == false) {
			custom_data_rate.setEnabled(false);
			custom_data_rate_unit.setEnabled(false);
		} else {
			custom_data_rate.setEnabled(true);
			custom_data_rate_unit.setEnabled(true);
		}
	}

}
