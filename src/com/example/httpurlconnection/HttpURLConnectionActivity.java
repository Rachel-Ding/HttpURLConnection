package com.example.httpurlconnection;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HttpURLConnectionActivity extends Activity implements OnClickListener
{
	public static final int SHOW_RESPONSE =0;
	private Button sendRequest;
	private TextView responseText;
	
	private Handler handler = new Handler(){
		//主线程获取子线程传递回来的message,进行处理，显示在界面
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				 case SHOW_RESPONSE:
					 String response = (String) msg.obj;
					// 在这里进行UI操作，将结果显示到界面上
					 responseText.setText(response);
			}	
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sendRequest=(Button)findViewById(R.id.send_request);
		responseText=(TextView)findViewById(R.id.response);
		
		sendRequest.setOnClickListener(this);
			
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId()== R.id.send_request)
		{
			//调用该方法
			sendRequestWithHttpURLConnection();
		}
	}
	
	private void sendRequestWithHttpURLConnection()
	{
		// 开启子线程来发起网络请求
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				//使用HttpURLConnection（抽象类）发出一条 HTTP 请求，请求的目标地址为知乎首页
				HttpURLConnection connection=null;
				try
				{
					//连接
					URL url = new URL("http://www.zhihu.com");
					connection=(HttpURLConnection)url.openConnection();
					connection.setRequestMethod("GET");
					//设置允许最大连接时间，超出则抛出异常
					connection.setConnectTimeout(8000);
					//设置最大等待输入流(InputStream)读取完成时间，超出则抛出异常
					connection.setReadTimeout(8000);
					//in为获取的输入流
					InputStream in= connection.getInputStream();
					
					//对服务器返回的流进行读取，并将结果存放到了一个 Message 对象中
					BufferedReader reader=new BufferedReader(new InputStreamReader(in));
					StringBuilder response =new StringBuilder();
					String line;
					while((line=reader.readLine())!=null)//返回该读写器可用文本的下一行
					{
						//往response中添加下一行的内容
						response.append(line);
					}
					
					Message message = new Message();
					//message的标志，handler里用来区分是哪个消息
					message.what = SHOW_RESPONSE;
					// 将服务器返回的结果存放到Message中
					message.obj = response.toString();
					//handler将message传回给主线程
					handler.sendMessage(message);
					
				} catch (Exception e)//内部捕获异常并做处理
				{
					e.printStackTrace();
				}finally{
					//最后，释放连接
					if (connection !=null)
					{
						connection.disconnect();
					}
				}								
			}
		}).start();
	}
	
	
}
