package com.zgy.ringforu_push.util;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.zgy.ringforu_push.MainContants;
import com.zgy.ringforu_push.response.PushMessageResponse;

public class RequestUtil {

	// 单例
	private static RequestUtil mRequest = null;
	private static HttpUtil mHttpUtil;

	private RequestUtil() {
	}

	public static RequestUtil getInstance() {
		if (mRequest == null) {
			mRequest = new RequestUtil();
		}
		if (mHttpUtil == null) {
			mHttpUtil = HttpUtil.getInstence();
		}
		return mRequest;
	}

	public PushMessageResponse pushMessage(final String tag, final String title, final String content, final String picTag, boolean isPost) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		String id = String.valueOf(TimeUtil.getCurrentTimeMillis());

		// params.add(new BasicNameValuePair("apikey", "Ljc710pzAa99GULCo8y48NvB"));
		// params.add(new BasicNameValuePair("device_type", "3"));
		// params.add(new BasicNameValuePair("messages", "\"{\"title\":\"\",\"description\":\"test\"}\""));
		// params.add(new BasicNameValuePair("method", "push_msg"));
		// params.add(new BasicNameValuePair("msg_keys", id));
		// params.add(new BasicNameValuePair("push_type", "1"));
		// params.add(new BasicNameValuePair("sign", "8777F555E8C16715EBA5C85341684C58"));
		// params.add(new BasicNameValuePair("timestamp", id));

		// TODO 请求参数

//		String message = "{\"custom_content\":{\"content\":\"value2\",\"title\":\"value1\"},\"description\":\"hello world\",\"title\":\"title\"}";
		String message = "{\"description\":\"hello world\",\"title\":\"title\"}";

		params.add(new BasicNameValuePair("apikey", "zv2f7R1Q2bqcBK3SYZoNq8Zq"));
		params.add(new BasicNameValuePair("device_type", "3"));
		// params.add(new BasicNameValuePair("message_expires", "86400"));
		params.add(new BasicNameValuePair("message_type", "1"));
		params.add(new BasicNameValuePair("messages", message));
		params.add(new BasicNameValuePair("method", "push_msg"));
		params.add(new BasicNameValuePair("msg_keys", id));
		params.add(new BasicNameValuePair("push_type", "2"));
		params.add(new BasicNameValuePair("tag", "DEBUG"));
		params.add(new BasicNameValuePair("timestamp", id));
		// 拼凑签名
		StringBuffer buffer = new StringBuffer();
		buffer.append("POST").append(MainContants.URL_PUSH_MESSAGE);
		for (NameValuePair value : params) {
			buffer.append(value.getName());
			buffer.append("=");
			buffer.append(value.getValue());
		}
		buffer.append("tKTSwji6lDP4hmBHtu2GAycdRGz48LyA");
		String sign = buffer.toString();
		Log.e("", "sing=" + sign);

		try {
			String encodeSing = MD5Util.getMD5String(URLEncoder.encode(sign, "utf-8"));
			Log.e("", "encodeSing=" + encodeSing);
			params.add(new BasicNameValuePair("sign", encodeSing));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		PushMessageResponse response = new PushMessageResponse();
		// 执行
		mHttpUtil.sendRequest(response, MainContants.URL_PUSH_MESSAGE, params, isPost);

		return response;

	}
}
