package com.example.jsonproject;

import org.cuteson.datacenter.HttpHelper;
import org.cuteson.ojm.JsonPojoCallBack;
import org.cuteson.ojm.JsonPojoManager;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.ifreetalk.ftalk.basestruct.Gift;
import com.ifreetalk.pojo.User;

public class MainActivity extends Activity {

	Handler handler = new Handler(){

		@Override
		public void dispatchMessage(Message msg) {
			Toast.makeText(MainActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		
		 Gift gift = JsonPojoManager.getInstance().getPojo(Gift.class, new JsonPojoCallBack<Gift>() {

			@Override
			public void onComplete(Gift t) {
				System.out.println(t.getGiftId());
				System.out.println(t.getGiftName());
				System.out.println(t.getNum());
				System.out.println(t.getImgurl());
			}

			@Override
			public void onFailures(int erroCode) {
				switch (erroCode) {
				case HttpHelper.CONNECT_TIMEOUT:
					Message msg = handler.obtainMessage();
					msg.obj = "连接超时";
					handler.sendMessage(msg);
					break;

				default:
					Message msg1 = handler.obtainMessage();
					msg1.obj = "连接错误";
					handler.sendMessage(msg1);
					break;
				}
				
			}
			
			
		});
		 
		 User user = JsonPojoManager.getInstance().getPojo(User.class, new JsonPojoCallBack<User>() {

				@Override
				public void onComplete(User t) {
					System.out.println(t.getUserId());
					System.out.println(t.getUserName());
					System.out.println(t.getAge());
					System.out.println(t.getSon().getSonId());
				}

				@Override
				public void onFailures(int erroCode) {
					// TODO Auto-generated method stub
					
				}
				
				
			});
		
		
		
		/*for(int i=0;i<50;i++){
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(Thread.currentThread().getName());
					
				}
			});
			thread.start();
		}*/
	
		
		
	}
	


	
}
