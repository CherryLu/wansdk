/****************************
文件名:JsonUtils.java

创建时间:2013-3-29
所在包:
作者:罗泽锋
说明:Json解析模块通用工具类
 ****************************/

package com.wan.callring.utils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {
	public static String getString(JSONObject jo, String key) {
		String ret = "";
		if (jo != null) {
			try {
				String r = jo.optString(key);
				if (!TextUtils.isEmpty(r))
					ret = r;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	public static String getString(JSONArray jo, int index) {
		String word;
		try {
			word = jo.optString(index);
			return word;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static boolean getBoolean(JSONObject jo, String key) {
		boolean ret = false;
		if (jo != null) {
			try {
				ret = jo.optBoolean(key);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	public static int getInt(JSONObject jo, String key) {
		int ret = 0;
		if (jo != null) {
			try {
				ret = jo.optInt(key);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	public static long getLong(JSONObject jo, String key) {
		if (jo != null) {
			try {
				return jo.optLong(key);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	public static double getDouble(JSONObject jo, String key) {
		if (jo != null) {
			try {
				return jo.optDouble(key);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	public static JSONObject getJSONObject(JSONObject jo, String key) {
		if (jo != null) {
			try {
				return jo.getJSONObject(key);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static JSONArray getJSONArray(JSONObject jo, String key) {
		JSONArray jsonArray = null;
		try {
			jsonArray = (JSONArray) jo.getJSONArray(key);
		} catch (JSONException e) {
			jsonArray = new JSONArray();
		}
		return jsonArray;
	}

//	public static void parseActionArray(ArrayList<Action> actionList,
//			JSONObject jo) {
//		JSONArray jsonArray = getJSONArray(jo, "action");
//		for (int i = 0; i < jsonArray.length(); ++i) {
//			Action item = new Action();
//			actionList.add(item);
//			item.parse((JSONObject) JsonUtils.getJsonArray(jsonArray, i));
//		}
//	}

	public static JSONObject getJsonArray(JSONArray jsonArray, int index) {
		try {
			Object obj = jsonArray.get(index);
			if (obj instanceof JSONObject) {
				return (JSONObject)obj;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}
