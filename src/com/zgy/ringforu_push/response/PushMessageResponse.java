package com.zgy.ringforu_push.response;


public class PushMessageResponse extends BaseResponse{

	private boolean result;
	
	@Override
	public void initFeild(String response) {
		super.initFeild(response);
		//TODO json解析，获得result
		
	 
		
	}
	
	public boolean getResult() {
		return result;
	}
	
}
