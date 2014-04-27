package com.example.service;

import android.text.Html;

public class CapacityService {
	private final static String LOG_TAG = "CapacityService";

	public final static String REQUESTED_NAME_DATA_RATE = "DataRate";
	public final static int REQUESTED_ORIENTATION_DATA_RATE = 1;

	public final static String REQUESTED_NAME_DRIVE_CAPACITY = "DriveCapacity";
	public final static int REQUESTED_ORIENTATION_DRIVE_CAPACITY = 2;

	public static float getNetCapcity(float DataRate, int InputNums,
			int DaysOfDateStorage) {
		/**
		 * 码流单位是KB 如果是MB要多除以1000
		 * 总净容量公式：总净容量=(码流(Mbps)*摄像头数*存储时间*3600*24)/(8*1000*1000)
		 */
		float NetCapacity = 0;
		// Log.i(LOG_TAG, "DataRate: "+DataRate + " InputNums: " + InputNums +
		// " DaysOfDateStorage: " + DaysOfDateStorage);
		NetCapacity = (((float)DataRate / 1024) * InputNums * DaysOfDateStorage * 3600 * 24)
				/ (8 * 1000 * 1000);
		return NetCapacity;
	}

	public static String floatToString(float var) {
		String unit;
		float tmp = var;
		if ((int) (tmp / 1024) <= 0) {
			unit = "Kbps";
			if ((int) (tmp % 1) == 0) {
				/* qu 0 */
				return (int) tmp + unit;
			}

			return tmp + unit;
		} else {
			unit = "Mbps";
			if ((int) (tmp % 1024) == 0) {
				/* qu 0 */
				return (int) tmp / 1024 + unit;
			}
			return (float)(tmp / 1024) + unit;
		}
		
		//Log.i(LOG_TAG, "var=" + var + ";" + "tmp=" + tmp);

	}

	/**
	 * 
	 * @param raidLimit
	 *            RAID级别
	 * @param netCapacity
	 *            净总容量
	 * @param driveCapacity
	 *            单个硬盘容量
	 * @param raidPanShu
	 *            RAID盘数
	 * @return 返回总硬盘数　　　　　　　　
	 * @throws Exception
	 *             RAID级别异常
	 */
	public static int getDriveNums(int raidLimit, int netCapacity,
			int driveCapacity, int raidPanShu) throws Exception {

		int driveNums = 0;
		/**
		 * Raid级别 【RAID0或不做RAID】：N=A/B，然后再取整，因为硬盘不能是小数，必须为整数；
		 * 【RAID1/RAID10】:N={A/B取整}*2； 【RAID3/RAID5】：N=((A/B)/C)取整+（A/B）取整
		 * 【RAID3或5+热备盘/RAID6】:N=((A/B)/C)取整*2+（A/B）取整
		 * 【RAID6+1热备盘】：N=((A/B)/C)取整*3+（A/B）取整
		 * 
		 * 【共需硬盘数】：N 【净容量取整】：A 【单块硬盘的容量】：B 【RAID盘数】：C
		 */
		switch (raidLimit) {
		case 0:
			driveNums = (int) Math.ceil((float)netCapacity / (float)driveCapacity);
			break;
		case 1:
			driveNums = (int) Math.ceil((float)netCapacity / (float)driveCapacity) * 2;
			break;
		case 2:
			driveNums = (int) Math.ceil((float)netCapacity / (float)driveCapacity
					/ raidPanShu)
					+ (int) Math.ceil((float)netCapacity / (float)driveCapacity);
			break;
		case 3:
			driveNums = (int) Math.ceil((float)netCapacity / (float)driveCapacity
					/ raidPanShu)
					* 2 + (int) Math.ceil((float)netCapacity / (float)driveCapacity);
			break;
		case 4:
			driveNums = (int) Math.ceil((float)netCapacity / (float)driveCapacity
					/ raidPanShu)
					* 3 + (int) Math.ceil((float)netCapacity / (float)driveCapacity);
			break;

		default:
			throw new Exception();
		}

		return driveNums;
	}

	/**
	 * @param mDriveNums
	 * @param mZhuGuiDriveNums
	 * @return driveNums / zhuGuiDriveNums 然后向上取整
	 */
	public static int getSingleHandpieceNums(int driveNums, int zhuGuiDriveNums)
			throws Exception {
		if (zhuGuiDriveNums != 0)
			return (int) Math.ceil((float) driveNums / (float) zhuGuiDriveNums);
		else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * 
	 * @param mDriveNums
	 * @param mZhuGuiDriveNums
	 * @param mKuoZhanGuiNums
	 * @return 【共需硬盘数】/(【主柜硬盘数】+【扩展柜数目】*【主柜盘位数】)向上取整
	 */
	public static int getZhuJiGuiNums(int driveNums, int zhuGuiDriveNums,
			int kuoZhanGuiNums) {
		return (int) Math.ceil((float) driveNums
				/ (float) (zhuGuiDriveNums + kuoZhanGuiNums * zhuGuiDriveNums));
	}
	
	public static int getKuoZhanGuiNums(int zhuJiGuiNums, int kuoZhanGuisPerZhuji) {
		return 1;
	}

	public static int setDataRate(int arg2) {
		int dateRate = 0;
		switch (arg2) {
		case 0:
			dateRate = 512;
			break;
		case 1:
			dateRate = 1 * 1024;
			break;
		case 2:
			dateRate = (int) (1.5 * 1024);
			break;
		case 3:
			dateRate = 2 * 1024;
			break;
		case 4:
			dateRate = 3 * 1024;
			break;
		case 5:
			dateRate = 4 * 1024;
			break;
		case 6:
			dateRate = 5 * 1024;
			break;
		case 7:
			dateRate = 6 * 1024;
			break;
		case 8:
			dateRate = 8 * 1024;
		default:
			/*自定义*/
			break;
		}
		
		return dateRate;
	}
	
	public static CharSequence[] getDateRate() {
		CharSequence[] data_rate_array = {
				Html.fromHtml("<font color=\"#FFFF00\">512Kbps</font>"),
				Html.fromHtml("<font color=\"#008000\">1Mbps</font>"),
				Html.fromHtml("<font color=\"#008000\">1.5Mbps</font>"),
				Html.fromHtml("<font color=\"#008000\">2Mbps</font>"),
				Html.fromHtml("<font color=\"#0000FF\">3Mbps</font>"),
				Html.fromHtml("<font color=\"#0000FF\">4Mbps</font>"),
				Html.fromHtml("<font color=\"#800080\">5Mbps</font>"),
				Html.fromHtml("<font color=\"#800080\">6Mbps</font>"),
				Html.fromHtml("<font color=\"#800080\">8Mbps</font>"),
				Html.fromHtml("<font color=\"#000000\">自定义(Kbps)</font>") };
		return data_rate_array;
	}

	public static int dateRateToNum(int dataRate) {
		int num = 0;
		switch (dataRate) {
		case 512:
			num = 0;
			break;
		case 1 * 1024:
			num = 1;
			break;
		case (int) (1.5 * 1024):
			num = 2;
			break;
		case 2 * 1024:
			num = 3;
			break;
		case 3 * 1024:
			num = 4;
			break;
		case 4 * 1024:
			num = 5;
			break;
		case 5 * 1024:
			num = 6;
			break;
		case 6 * 1024:
			num = 7;
			break;
		case 8 * 1024:
			num = 8;
		default:
			break;
		}
		
		return num;
	}

	public static CharSequence[] getDriveCapacity() {
		CharSequence[] sequence = {"1TB", "2TB", "3TB", "自定义(TB)"};
		return sequence;
	}

	public static int driveCapacityToNum(int driveCapacity) {
		int num = 0;
		switch (driveCapacity) {
		case 1:
			num = 0;
			break;
		case 2:
			num = 1;
			break;
		case 3:
			num = 2;
			break;
		default:
			break;
		}
		
		return num;
	}
	
	public static int getDriveCapacity(int arg2) {
		int dateRate = 0;
		switch (arg2) {
		case 0:
			dateRate = 1;
			break;
		case 1:
			dateRate = 2;
			break;
		case 2:
			dateRate = 3;
			break;
		default:
			/*自定义*/
			break;
		}
		
		return dateRate;
	}
}
