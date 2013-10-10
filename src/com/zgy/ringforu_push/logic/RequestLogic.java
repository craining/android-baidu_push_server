package com.zgy.ringforu_push.logic;

import com.zgy.ringforu_push.observer.RequestObserver;
import com.zgy.ringforu_push.response.PushMessageResponse;
import com.zgy.ringforu_push.util.RequestUtil;

public class RequestLogic {

	private static RequestLogic mRequestLogic;

	private RequestLogic() {

	}

	public static RequestLogic getInstence() {
		if (mRequestLogic == null) {
			mRequestLogic = new RequestLogic();
		}
		return mRequestLogic;
	}

	public void pushMessage(final String tag, final String title, final String content, final String picTag, final RequestObserver observer) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					// 发起http请求，数据库操作等
					PushMessageResponse response = RequestUtil.getInstance().pushMessage(tag, title, content, picTag, true);

					if (response != null) {
						boolean result = response.getResult();
						observer.pushMessageFinished(result);
					} else {
						observer.pushMessageFinished(false);
					}
				} catch (Exception e) {
					e.printStackTrace();
					observer.pushMessageFinished(false);
				}
			}
		}).start();
	}
}
