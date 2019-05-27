package com.wan.callring.utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * SharedPreferences工具类
 * 
 */
public class Preferences {
	public static final String CALLSHOW_DIAl = "callshow_dial";
	public static final String CALLSHOW_ANSWER = "callshow_answer";
	public static final String VIDEO_SWITCH = "video_switch";
    public static final String WELCOME_ACTIVITY = "welcome_activity";
	public static final String UserMobile = "UserMobile";
	public static final String SHOWWELCOME = "showwelcome";
	public static final String GUID_MAIN_FIRST = "guid_main_first";
	public static final String GUID_PRE_FIRST = "guid_preview_first";
	public static final String AID = "aid";//活动id
	private static Context sContext;
	public static final String CALLSHOW_NUM = "callshow_num";//保持拨打的电话号码
	public static void init(Context context) {
		sContext = context.getApplicationContext();
	}


	public static boolean getBoolean(String key, boolean defValue) {
		return getPreferences().getBoolean(key, defValue);
	}

	public static void saveBoolean(String key, boolean value) {
		getPreferences().edit().putBoolean(key, value).apply();
	}

	private static int getInt(String key, int defValue) {
		return getPreferences().getInt(key, defValue);
	}

	private static void saveInt(String key, int value) {
		getPreferences().edit().putInt(key, value).apply();
	}

	private static long getLong(String key, long defValue) {
		return getPreferences().getLong(key, defValue);
	}

	private static void saveLong(String key, long value) {
		getPreferences().edit().putLong(key, value).apply();
	}

	public static String getString(String key, String defValue) {
		return getPreferences().getString(key, defValue);
	}

	public static void saveString(String key, String value) {
		getPreferences().edit().putString(key, value).apply();
	}

	private static SharedPreferences getPreferences() {
		return sContext.getSharedPreferences("callshow", Context.MODE_PRIVATE );
//		return PreferenceManager.getDefaultSharedPreferences(sContext);
	}

	/*public static void saveList(List<CallshowBean> SceneList, String key) {
		try {
			getPreferences().edit()
					.putString(key, SceneList2String(SceneList)).apply();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ArrayList<CallshowBean> getList(String key) {
		try {
			return String2SceneList(getPreferences()
					.getString(key, null));
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String SceneList2String(List<CallshowBean> SceneList)
			throws IOException {
		// 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		// 然后将得到的字符数据装载到ObjectOutputStream
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				byteArrayOutputStream);
		// writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
		objectOutputStream.writeObject(SceneList);
		// 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
		String SceneListString = new String(Base64.encode(
				byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
		// 关闭objectOutputStream
		objectOutputStream.close();
		return SceneListString;
	}

	public static ArrayList<CallshowBean> String2SceneList(String SceneListString)
			throws StreamCorruptedException, IOException,
            ClassNotFoundException {
		Log.e("String2SceneList", SceneListString+"");
		if (SceneListString == null) {
			return null;
		}
		byte[] mobileBytes = Base64.decode(SceneListString.getBytes(),
				Base64.DEFAULT);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				mobileBytes);
		ObjectInputStream objectInputStream = new ObjectInputStream(
				byteArrayInputStream);
		ArrayList<CallshowBean> SceneList = (ArrayList<CallshowBean>) objectInputStream
				.readObject();
		objectInputStream.close();
		return SceneList;
	}*/
}
