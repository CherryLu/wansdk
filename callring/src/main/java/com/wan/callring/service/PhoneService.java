package com.wan.callring.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PhoneService extends Service {
	public final static int GRAY_SERVICE_ID = 20160907;
    public static boolean isRunning = false;
	private static final String TAG = "PhoneService";
	private PhoneTipController phoneTipController;
	private PhoneTipController2 phoneTipController2;
	private String outNumber="";

	private TelephonyManager telephonyManager;
	private MyPhoneListener listener=new MyPhoneListener();
	String requesphone;//请求的号码，有可能是自己的号码
	int phoneState = 0;//0空闲1来电，2去电通话
	String myPhone;
	@Override
	public IBinder onBind(Intent intent) {
		Log.e(TAG, "onBind");
		return null;
	}


	@Override
	public void onCreate() {
		super.onCreate();

		telephonyManager= (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE);
		telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
//
        isRunning = true;
//		startForeground(GRAY_SERVICE_ID,new Notification());
		Log.e(TAG, "onCreate myPhone:"+myPhone);

	}
	@SuppressWarnings("WrongConstant")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG, "onStartCommand");
		if(intent!=null){
			String ret=intent.getStringExtra("outNumber");
			outNumber= TextUtils.isEmpty(ret)?outNumber:ret;
			myPhone = intent.getStringExtra("myPhone");
			Log.e(TAG, "onStartCommand:"+outNumber+"---"+myPhone);
		}
		if(myPhone==null){
			myPhone = Preferences.getString(Preferences.UserMobile,"");// UserManager.getInstance().getRegisterName();
		}
		Log.e(TAG, "onStartCommand myphone"+myPhone);
//		if (Build.VERSION.SDK_INT < 18) {
//			startForeground(GRAY_SERVICE_ID, new Notification());//API < 18 ，此方法能有效隐藏Notification上的图标
//		} else {
//			Intent innerIntent = new Intent(this, GrayInnerService.class);
//			startService(innerIntent);
//			startForeground(GRAY_SERVICE_ID, new Notification());
//		}
//		return START_STICKY;
//		Intent notificationIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//		Notification noti = new Notification.Builder(this)
//				.setContentTitle("Title")
//				.setContentText("Message")
//				.setSmallIcon(R.drawable.ic_launcher)
//				.setContentIntent(pendingIntent)
//				.build();
//		startForeground(123456,noti);
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	GetCallVideoInfoProtocol getCallVideoInfoProtocol;
	private void getVideo(String phone){
		Log.e(TAG, "getVideo"+phone);
		requesphone = phone;
		UpGetCallVideoData upGetCallVideoData = new UpGetCallVideoData();
		upGetCallVideoData.sothermobile = phone;
		upGetCallVideoData.smobile ="";
		if (getCallVideoInfoProtocol == null) {
			getCallVideoInfoProtocol = new GetCallVideoInfoProtocol(null, upGetCallVideoData, handler);
			getCallVideoInfoProtocol.showWaitDialog();
		}
		getCallVideoInfoProtocol.stratDownloadThread(null, ServiceUri.Spcl, upGetCallVideoData, handler,true);
		Log.e(TAG, "callShowGetVideoInfoProtocol=refresh");
	}
	String callTime;
	String videoName;
	class MyPhoneListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			Log.e(TAG,state+"");
		    switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: //空间
				Log.e(TAG, "电话空闲" + incomingNumber);
				phoneState = 0;
                if(!"".equals(incomingNumber)){
                    if(TextUtils.isEmpty(callTime)){
                        callTime = getTime();
                    }
//					getVideo(incomingNumber);
                }
				if(phoneTipController!=null){
					phoneTipController.hide();
					phoneTipController=null;
                    //保持拨打历史
//                    callshowBean.time = callTime;
//                    callshowBean.phone = incomingNumber;

//                    DbCacheHelper dbHelper = new DbCacheHelper(PhoneService.this);
//                    dbHelper.insertCallshowBean(callshowBean, DbCacheHelper.TABLE_history);
//                    int size = dbHelper.getAllCallshowBean(DbCacheHelper.TABLE_history).size();
//                    Log.e("dbHelper",size+"");
				}
				if(phoneTipController2!=null){
					phoneTipController2.hide();
					phoneTipController2=null;
                    //保持拨打历史
//                    callshowBean.time = callTime;
//                    callshowBean.phone = incomingNumber;

//                    DbCacheHelper dbHelper = new DbCacheHelper(PhoneService.this);
//                    dbHelper.insertCallshowBean(callshowBean, DbCacheHelper.TABLE_history);
//                    int size = dbHelper.getAllCallshowBean(DbCacheHelper.TABLE_history).size();
//                    Log.e("dbHelper",size+"");
				}


				break;
			case TelephonyManager.CALL_STATE_RINGING: //来电状态
				Log.e(TAG, "来电话了"+incomingNumber);
				if ("Xiaomi".equals(Build.MANUFACTURER)||"Meizu".equals(Build.MANUFACTURER)) {//小米手机
					Log.e(TAG,"小米手机魅族手机");
					if (!SystemUtil.isFloatWindowOpAllowed(PhoneService.this)) {//已经开启
						break;
					}
				}
//				bInComing=true;
//				if(phoneTipController==null){
//					phoneTipController=new PhoneTipController(PhoneService.this,incomingNumber);
//					phoneTipController.show();
//				}
//				phoneTipController.setState(true);

				phoneState = 1;
				Log.e(TAG, "正在通信" + incomingNumber);
				incomingNumber= TextUtils.isEmpty(incomingNumber)?outNumber:incomingNumber;
				outNumber  = incomingNumber;
				Log.e(TAG, "正在通信" + incomingNumber+"--"+videoName);
				if("".equals(incomingNumber)){
					break;
				}
				Preferences.init(PhoneService.this.getApplicationContext());
				Boolean ans = Preferences.getBoolean(Preferences.CALLSHOW_ANSWER,true);
				Log.e(TAG,"ans:"+ans);
				if(!ans){
					break;
				}
//				videoName = MyDataBase.getInstances(PhoneService.this).query(incomingNumber);
//				Log.e(TAG,"videoName:"+videoName);
//				if(videoName!=null){
//					if(phoneTipController2==null){
//						phoneTipController2=new PhoneTipController2(PhoneService.this.getApplicationContext(),incomingNumber,videoName);
//						phoneTipController2.show();
//					}
//
//				}else{
//
//					if(phoneTipController2==null){
//						phoneTipController2=new PhoneTipController2(PhoneService.this.getApplicationContext(),incomingNumber,"-1");//-1表示默认视频
//						phoneTipController2.show();
//					}
//
//				}
////				handler.sendEmptyMessageDelayed(callstate,8000);
//				phoneTipController2.setState(true);
//				boolean wifi1 = NetUtils.isWifi(PhoneService.this);
//				Log.e(TAG, "wifi="+wifi1);
//				if(wifi1){
					getVideo(incomingNumber);
//				}else{
//					if(TextUtils.isEmpty(videoName)){
//						final ArrayList<CallshowBean> mylist = MySetDataBase.getInstances(PhoneService.this.getApplicationContext()).getAllCallSetBean(MySetDataBase.TABLE_NAME);
//						Log.e(TAG, "mylist.size="+mylist.size());
//						if(mylist.size()>0){
//							handler.post(new Runnable() {
//								@Override
//								public void run() {
//									phoneTipController2.upDateUrl(mylist.get(0).fileName);
//								}
//							});
//						}
//					}
//				}

				break;
			case TelephonyManager.CALL_STATE_OFFHOOK://通话状态
//				boolean isCome = false;
//				if(phoneTipController2!=null){
//					sentGet();
//					phoneTipController2.hide();
//					phoneTipController2=null;
//					isCome = true;
//				}
				Log.e(TAG, "正在通信" +":state"+telephonyManager.getDataState());
				if ("Xiaomi".equals(Build.MANUFACTURER)||"Meizu".equals(Build.MANUFACTURER)) {//小米手机
					Log.e(TAG,"小米手机魅族手机");
					if (!SystemUtil.isFloatWindowOpAllowed(PhoneService.this)) {//已经开启
						break;
					}
				}
				phoneState = 2;
				Log.e(TAG, "正在通信" +":state"+telephonyManager.getDataState()+"incomingNumber:"+incomingNumber+" outNumber:"+outNumber);
				incomingNumber= TextUtils.isEmpty(incomingNumber)?outNumber:incomingNumber;
				outNumber  = incomingNumber;
				Log.e(TAG, "正在通信" + incomingNumber+"--"+videoName);
				if("".equals(incomingNumber)){
					break;
				}
				Preferences.init(PhoneService.this.getApplicationContext());
				boolean dial = Preferences.getBoolean(Preferences.CALLSHOW_DIAl,true);
				Log.e(TAG,"dial:"+dial+"");
				if(!dial){
					break;
				}

//				videoName = MyDataBase.getInstances(PhoneService.this).query(incomingNumber);
//				Log.e(TAG,"videoName:"+videoName);
//				if(videoName!=null){
//					if(phoneTipController==null){
//						phoneTipController=new PhoneTipController(PhoneService.this.getApplicationContext(),incomingNumber,videoName);
//						phoneTipController.show();
//					}
//
//				}else{
//
//					if(phoneTipController==null){
//						phoneTipController=new PhoneTipController(PhoneService.this.getApplicationContext(),incomingNumber,"-1");//-1表示默认视频
//						phoneTipController.show();
//					}
//
//				}
//				if(phoneTipController!=null&&isCome){
//					phoneTipController.closeVoice();
//				}
// else{
//					handler.sendEmptyMessageDelayed(callstate,8000);
//				}

				callTime = getTime();
//				boolean wifi = NetUtils.isWifi(getApplicationContext());
//				Log.e(TAG, "wifi="+wifi);
//				if(wifi){
					getVideo(incomingNumber);
//				}else {
//					if(TextUtils.isEmpty(videoName)){
//						final ArrayList<CallshowBean> mylist = MySetDataBase.getInstances(PhoneService.this.getApplicationContext()).getAllCallSetBean(MySetDataBase.TABLE_NAME);
//						Log.e(TAG, "mylist.size="+mylist.size());
//						if(mylist.size()>0){
//							handler.post(new Runnable() {
//								@Override
//								public void run() {
//									phoneTipController.upDateUrl(mylist.get(0).fileName);
//								}
//							});
//						}
//					}
//				}
				if(phoneTipController!=null)
				phoneTipController.setState(false);
				break;
			default:
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}

		
	}
	DetailsBean detailsBean;
	public static final int callstate=100000;
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case GetCallVideoInfoProtocol.MSG_WHAT_OK:
					Log.e(TAG, "handler CallShowGetVideoInfoProtocol");
					detailsBean =  getCallVideoInfoProtocol.detailsBean;
					Log.e(TAG, "handler"+detailsBean.surl);
					if(requesphone.equals(myPhone)){
						if(detailsBean.surl!=null&&detailsBean.surl.startsWith("http")){
							if(phoneState==1){
//								boolean isLuancherForegroud = SystemUtil.isLauncherForeground(PhoneService.this);
//								Log.e(TAG, "isLuancherForegroud "+isLuancherForegroud);
								if(phoneTipController2==null){
									phoneTipController2=new PhoneTipController2(PhoneService.this.getApplicationContext(),outNumber,detailsBean.surl);
									phoneTipController2.show();
								}
							}else if(phoneState==2){
								if(phoneTipController2!=null){
									sentGet();
									phoneTipController2.hide();
									phoneTipController2 = null;
								}else{
                                    if(phoneTipController==null){
                                        phoneTipController=new PhoneTipController(PhoneService.this.getApplicationContext(),outNumber,detailsBean.surl);
                                        phoneTipController.show(myPhone);
                                    }
                                }
							}
//							doDownload();
						}else {
							if(phoneState==1){
//								boolean isLuancherForegroud = SystemUtil.isLauncherForeground(PhoneService.this);
//								Log.e(TAG, "isLuancherForegroud2 "+isLuancherForegroud);
								if(phoneTipController2==null){
									phoneTipController2=new PhoneTipController2(PhoneService.this.getApplicationContext(),outNumber,"-1");
									phoneTipController2.show();
								}
							}else if(phoneState==2){
								if(phoneTipController2!=null){
									sentGet();
									phoneTipController2.hide();
									phoneTipController2 = null;
								}else{
                                    if(phoneTipController==null){
                                        phoneTipController=new PhoneTipController(PhoneService.this.getApplicationContext(),outNumber,"-1");
                                        phoneTipController.show(myPhone);
                                    }
                                }
							}
						}
					}else{
                        Log.e(TAG, "handler"+0);
						if(detailsBean.surl!=null&&detailsBean.surl.startsWith("http")){
							Log.e(TAG, "handler"+1);
							if(phoneState==1){
//								boolean isLuancherForegroud = SystemUtil.isLauncherForeground(PhoneService.this);
//								Log.e(TAG, "isLuancherForegroud2 "+isLuancherForegroud);
								if(phoneTipController2==null){
									phoneTipController2=new PhoneTipController2(PhoneService.this.getApplicationContext(),requesphone,detailsBean.surl);
									phoneTipController2.show();
								}
							}else if(phoneState==2){
								if(phoneTipController2!=null){
									sentGet();
									phoneTipController2.hide();
									phoneTipController2 = null;
								}else {
                                    if(phoneTipController==null){
                                        phoneTipController=new PhoneTipController(PhoneService.this.getApplicationContext(),requesphone,detailsBean.surl);
                                        phoneTipController.show(myPhone);
                                    }
                                }
							}

//							doDownload();
						}else{
//							boolean wifi = NetUtils.isWifi(getApplicationContext());
//                            Log.e(TAG, "handler"+2+"wifi:"+wifi+" phoneState:"+phoneState);
							if(phoneState!=0){
								getVideo(myPhone);
							}
						}
					}

					break;
				case GetCallVideoInfoProtocol.MSG_WHAT_FAIL:
					Log.e(TAG, "handler fail1");
					if(!requesphone.equals(myPhone)){
						if(phoneState!=0){
							getVideo(myPhone);
						}
					}else {
						if(phoneState==1){
//								boolean isLuancherForegroud = SystemUtil.isLauncherForeground(PhoneService.this);
//								Log.e(TAG, "isLuancherForegroud2 "+isLuancherForegroud);
							if(phoneTipController2==null){
								phoneTipController2=new PhoneTipController2(PhoneService.this.getApplicationContext(),outNumber,"-1");
								phoneTipController2.show();
							}
						}else if(phoneState==2){
							if(phoneTipController2!=null){
								sentGet();
								phoneTipController2.hide();
								phoneTipController2 = null;
							}else{
								Log.e(TAG, "handler fail4");
								if(phoneTipController==null){
									phoneTipController=new PhoneTipController(PhoneService.this.getApplicationContext(),outNumber,"-1");
									phoneTipController.show(myPhone);
								}
							}
						}
					}
					break;
                case callstate:
                	if(phoneTipController!=null)
					phoneTipController.closeVoice();
					if(phoneTipController2!=null)
					phoneTipController2.closeVoice();
//                    int  state = telephonyManager.getCallState();
//                    Log.e(TAG, callstate+"="+state);
//                    if(state==Call.STATE_ACTIVE&&phoneTipController!=null){
//                        phoneTipController.closeVoice();
//                    }else{
//                        if(phoneTipController!=null)
//                        handler.sendEmptyMessageDelayed(callstate,1000);
//                    }
                    //初始化实例

                    break;
				default:
					break;

			}
		}
	};

	public String getTime(){
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		telephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE);
        isRunning = false;
		if(phoneTipController!=null){
			phoneTipController.hide();
			phoneTipController=null;
		}
		if(phoneTipController2!=null){
			sentGet();
			phoneTipController2.hide();
			phoneTipController2=null;
		}
//        sendBroadcast(new Intent(AutoStartReceiver.AUTO_START_RECEIVER));
        Log.e(TAG, "onDestroy");

	}

	public void sentGet(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				Log.e("zx", "sentGet");
				HttpRequest.sendGet("http://223.87.178.46:12346/spcl/bm","AM="+outNumber+"&BM="+myPhone);
			}
		}).start();
	}

//	private void stateChanged(){
//		Phone curPhone=cm.getPhone();
//		//Phone的状态：RING,IDLE,OFFHOOK
//		Phone.State state=cm.getState();
//		//Call的状态
//		Call.State callState=curPhone.getForgroundCall().getState();
//		//电话状态的判断
//		switch (callState) {
//			case DIALING:
//				//正在拨号
//				break;
//			case ACTIVE:
//				//已经摘机
//				break;
//			case DISCONNECTED:
//				//断开连接
//			case IDLE:
//				//挂断或者等待接听状态
//				if(state==Phone.State.RING){
//					//来电
//				}else{
//					//挂断
//				}
//				break;
//
//			default:
//				break;
//		}
//
//	}

}
