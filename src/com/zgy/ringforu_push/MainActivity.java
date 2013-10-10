package com.zgy.ringforu_push;

import android.app.Activity;
import android.os.Bundle;

import com.zgy.ringforu_push.logic.RequestLogic;
import com.zgy.ringforu_push.observer.RequestObserver;

public class MainActivity extends Activity {

	private RequestLogic  mLogic;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mLogic = RequestLogic.getInstence();
		mLogic.pushMessage("DEBUG", "", "", "", mObserver);
		
		
	}
	
	private RequestObserver mObserver = new RequestObserver() {

		@Override
		public void pushMessageFinished(boolean result) {
			// TODO Auto-generated method stub
			super.pushMessageFinished(result);
		}
		
	};

}
