package com.zgy.ringforu_push.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

import com.zgy.ringforu_push.response.BaseResponse;

public class HttpUtil {

	private static final String TAG = "HttpUtil";

	private static final int TIME_OUT = 10000;// 连接超时时间
	private static final String CHARSET_UTF8 = "UTF-8";

	private static HttpUtil mHttpUtil;

	private HttpUtil() {

	}

	public static HttpUtil getInstence() {
		if (mHttpUtil == null) {
			mHttpUtil = new HttpUtil();
		}

		return mHttpUtil;
	}

	/**
	 * 发送请求
	 * 
	 * @Description:
	 * @param url
	 *            目标url
	 * @param params
	 *            参数
	 * @param isPost
	 *            是否为Post提交
	 * @return
	 * @see:
	 * @since:
	 * @author: huangyongxing
	 * @date:2012-6-29
	 */
	public BaseResponse sendRequest(BaseResponse response, String url, List<NameValuePair> params, boolean isPost) {
		String result = null;
		try {
			if (isPost) {
				result = postMethod(url, params);
			} else {
				result = getMethod(url, params);
			}
			if (!StringUtil.isNull(result)) {
				response.initFeild(result);
				return response;
			}
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, " sendRequest " + url + " error:" + e.getMessage());
		} catch (ClientProtocolException e) {
			Log.e(TAG, " sendRequest " + url + " error:" + e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, " sendRequest " + url + " error:" + e.getMessage());
		}

		return null;
	}

	private HttpURLConnection getConnection(String uri) {
		HttpURLConnection httpConn = null;
		URL url = null;
		try {
			url = new URL(uri);
			httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setConnectTimeout(TIME_OUT);
			httpConn.setReadTimeout(TIME_OUT);
			// 打开读写属性，默认均为false
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.setInstanceFollowRedirects(true);
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		return httpConn;
	}

	/**
	 * 提交POST请求
	 * 
	 * @Description:
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException
	 * @see:
	 * @since:
	 * @author: huangyongxing
	 * @date:2012-8-8
	 */
	private String postMethod(String url, List<NameValuePair> params) throws IOException {

		HttpURLConnection conn = getConnection(url);
		if (conn == null) {
			return null;
		}
		conn.setRequestMethod("POST");
		conn.setUseCaches(false);
		conn.setRequestProperty(" Content-Type ", " application/x-www-form-urlencoded ");

		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		StringBuffer buffer = new StringBuffer();
		if (params != null) {
			int i = 0;
			for (NameValuePair value : params) {
				buffer.append(i == 0 ? "" : "&");
				buffer.append(value.getName());
				buffer.append("=");
				String v = value.getValue();
				// if(!StringUtil.isEmpty(v, true)){
				if (!StringUtil.isNull(v)) {
					buffer.append(URLEncoder.encode(v, CHARSET_UTF8));
				}
				i++;
			}
		}
		out.writeBytes(buffer.toString());
		out.flush();
		out.close(); // flush and close

		String result = null;
		Log.i(TAG, "response code :" + conn.getResponseCode());
		if (conn.getResponseCode() == 200) {
			if ("gzip".equalsIgnoreCase(conn.getContentEncoding())) {
				result = requestResult(conn.getInputStream(), true);
			} else {
				result = requestResult(conn.getInputStream(), false);
			}
		}

		conn.disconnect();

		return result;
	}

	/**
	 * 提交GET请求，不用httpurlconnection是为了和4.0兼容
	 * 
	 * @Description:
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException
	 * @see:
	 * @since:
	 * @author: huangyongxing
	 * @date:2012-8-8
	 */
	private String getMethod(String url, List<NameValuePair> params) throws IOException {
		StringBuffer buffer = new StringBuffer();

		// 拼url
		if (params != null) {
			int i = 0;
			for (NameValuePair value : params) {
				buffer.append(i == 0 ? "?" : "&");
				buffer.append(value.getName());
				buffer.append("=");
				String v = value.getValue();
				// if (!StringUtil.isEmpty(v, true)) {
				if (!StringUtil.isNull(v)) {
					buffer.append(URLEncoder.encode(value.getValue(), CHARSET_UTF8));
				}
				i++;
			}
		}

		HttpParams httpParams = new BasicHttpParams();
		// 设置超时时间
		HttpConnectionParams.setConnectionTimeout(httpParams, TIME_OUT);
		HttpConnectionParams.setSoTimeout(httpParams, TIME_OUT);
		HttpClient client = new DefaultHttpClient(httpParams);
		HttpGet get = new HttpGet(url + buffer.toString());
		Log.i(TAG, "REQUEST URL:" + url + buffer.toString());
		HttpResponse response = client.execute(get);

		String result = null;
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity entity = response.getEntity();
			Header header = entity.getContentEncoding();
			if (header == null || !"gzip".equalsIgnoreCase(header.getValue())) {
				result = requestResult(entity.getContent(), false);
			} else {
				result = requestResult(entity.getContent(), true);
			}
		}
		return result;
	}

	/**
	 * 解析服务器返回数据
	 * 
	 * @Description:
	 * @param is
	 * @param isGzip
	 * @return
	 * @throws IOException
	 * @see:
	 * @since:
	 * @author: huangyongxing
	 * @date:2012-8-8
	 */
	private String requestResult(InputStream is, boolean isGzip) throws IOException {
		BufferedReader bufferedReader = null;
		StringBuilder builder = new StringBuilder();
		try {
			if (isGzip) {
				is = new GZIPInputStream(is);
			}
			bufferedReader = new BufferedReader(new InputStreamReader(is, CHARSET_UTF8));
			// 读取服务器返回数据，转换成BufferedReader
			for (String s = bufferedReader.readLine(); s != null; s = bufferedReader.readLine()) {
				builder.append(s);
			}
		} finally {
			is.close();
			bufferedReader.close();
		}
		return builder.toString();
	}

}
