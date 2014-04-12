package com.example.service;


import android.test.AndroidTestCase;
import android.util.Log;

public class TestCapacityCalculator extends AndroidTestCase {
	private final static String LOG_TAG = "TestCapacityCalculator";

	public void testgetDriveNums() {
		int n = (int) Math.ceil(25 / 16);
		Log.i(LOG_TAG, "n = " + n);
//		try {
//			Log.i(LOG_TAG, "" + CapacityService.getDriveNums(0, 45, 2, 16));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

}
