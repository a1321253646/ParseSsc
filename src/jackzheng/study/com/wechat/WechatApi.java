package jackzheng.study.com.wechat;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jackzheng.study.com.wechat.sscManager.ServerManager;
import jackzheng.study.com.wechat.utils.TextUtils;
import jackzheng.study.com.wechat.utils.XposedBridge;

public class WechatApi {
    public static final Integer[][] mTimeLsit ={
            {0,5},{0,10},{0,15},{0,20},{0,25},{0,30},{0,35},{0,40},{0,45},{0,50},{0,55},{1,0},
            {1,5},{1,10},{1,15},{1,20},{1,25},{1,30},{1,35},{1,40},{1,45},{1,50},{1,55},
            {10,0},{10,10},{10,20},{10,30},{10,40},{10,50},
            {11,0},{11,10},{11,20},{11,30},{11,40},{11,50},
            {12,0},{12,10},{12,20},{12,30},{12,40},{12,50},
            {13,0},{13,10},{13,20},{13,30},{13,40},{13,50},
            {14,0},{14,10},{14,20},{14,30},{14,40},{14,50},
            {15,0},{15,10},{15,20},{15,30},{15,40},{15,50},
            {16,0},{16,10},{16,20},{16,30},{16,40},{16,50},
            {17,0},{17,10},{17,20},{17,30},{17,40},{17,50},
            {18,0},{18,10},{18,20},{18,30},{18,40},{18,50},
            {19,0},{19,10},{19,20},{19,30},{19,40},{19,50},
            {20,0},{20,10},{20,20},{20,30},{20,40},{20,50},
            {21,0},{21,10},{21,20},{21,30},{21,40},{21,50},
            {22,0},{22,5},{22,10},{22,15},{22,20},{22,25},{22,30},{22,35},{22,40},{22,45},{22,50},{22,55},
            {23,0},{23,5},{23,10},{23,15},{23,20},{23,25},{23,30},{23,35},{23,40},{23,45},{23,50},{23,55},{24,0},
    } ;
	
	private ThreadPoolExecutor mExecutor;
    HtmlParse.MaxIndexResult mCurrentResult;
    int mIndexMax = 0;
    private final int WECHAT_TEXT_MSG_TYPE = 1;
	//接手信息的处理
    //目前type == 1 为文本信息，在web 中微信的文本信息对应的type你对应看一下是什么，要对应改一下WECHAT_TEXT_MSG_TYPE
	public void receviceMessage(final String message,final String userId,final String groupID,final String typeStr ,final String takeID) {
		mExecutor.execute(new Runnable() {
	        @Override
	        public void run() {
                XposedBridge.log("GroupID = "+groupID+" userId = "+userId+" content = "+message+" type ="+typeStr+" takeID="+takeID);
                int type = Integer.parseInt(typeStr);
                if(type == WECHAT_TEXT_MSG_TYPE && !TextUtils.isEmpty(userId)){
                    getMessage(userId,groupID,message,takeID);
                }
	        	
	        }
		});
	}
	
    private void getMessage(String talkerId,String groupId,String content,String takerId){
        XposedBridge.log("getMessage");
        ServerManager.getIntance().receiveMessage(content,talkerId,groupId,takerId);
    }
	//发送信息的处理
	public void sendMessage(String message,String userId) {
		
		
	}
	
	 Runnable mRequitRun = new Runnable() {
	        @Override
	        public void run() {
	            Thread thread= new Thread(){
	                @Override
	                public void run() {
	                    while(true){
	                        mCurrentResult = HtmlParse.parseQuite();
	                        if(mCurrentResult  == null){
	                            XposedBridge.log("parseQuite 没数据");
	                        }else{
	                            XposedBridge.log("mIndexMax ="+mIndexMax+" parseQuite mCurrentResult index ="+mCurrentResult.index+" mCurrentResult str"+mCurrentResult.str);
	                        }
	                        if(mCurrentResult != null && ( (mIndexMax >= 10 &&  mCurrentResult.index >= mIndexMax) || (mIndexMax < 10 && mCurrentResult.index!= 120 &&  mCurrentResult.index >= mIndexMax) ) ){
	                            //mHandler.removeCallbacks(mTimeRun);
	                            //mHandler.postDelayed(mTimeRun,2000);
	                        	timer(2000,mTimeRun);
	                            break;
	                        }else{
	                            mCurrentResult = HtmlParse.parse();
	                            if(mCurrentResult  == null){
	                                XposedBridge.log("parse 没数据");
	                            }else{
	                                XposedBridge.log("mIndexMax ="+mIndexMax+" parse mCurrentResult index ="+mCurrentResult.index+" mCurrentResult str"+mCurrentResult.str);
	                            }
	                            if(mCurrentResult != null && ( (mIndexMax >= 10 && mCurrentResult.index >= mIndexMax) || (mIndexMax < 10 && mCurrentResult.index!= 120 &&  mCurrentResult.index >= mIndexMax) ) ){
	                            	timer(2000,mTimeRun);
	                            	//mHandler.removeCallbacks(mTimeRun);
	                                //mHandler.postDelayed(mTimeRun,2000);
	                                break;
	                            }else{
	                                try {
	                                    Thread.sleep(2000);
	                                } catch (InterruptedException e) {
	                                    e.printStackTrace();
	                                }
	                            }
	                        }
	                    }
	                }
	            };
	            thread.start();
	        }
	    };
    Runnable mTimeRun  = new Runnable() {
        @Override
        public void run() {
            if(mCurrentResult != null){
                ServerManager.getIntance().announceByAuto(mCurrentResult.str,mCurrentResult.index,mIndexMax);
                mIndexMax = mCurrentResult.index;
                mCurrentResult = null;
            }

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH)+1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            int msecond =calendar.get(Calendar.MILLISECOND);
            XposedBridge.log("Calendar获取当前日期"+year+"年"+month+"月"+day+"日"+hour+":"+minute+":"+second+"."+msecond);
            long delay = 0;
            if(second < 11) {
                dealOther(hour,minute);
                mIndexMax = getIndex(hour, minute);
                if (isOpen) {
                    ServerManager.getIntance().setFalseByAuto(mIndexMax);
                    timer(0,mRequitRun);
                 //  mHandler.post(mRequitRun);
                   return;
                } else {
                    Integer[] tmp;
                    tmp = mTimeLsit[mIndexMax];
                    XposedBridge.log("目标时间"+tmp[0]+"时"+tmp[1]+"分");
                    if((hour > 1 && hour <3)||(hour == 1  && minute > 55)) {
                        delay = getDelayMs(3, 0, hour, minute, second, msecond);//定时3：00 全面清盘
                    } else if((hour >7 && hour < 9 )||(hour == 9  && minute < 50)){
                        delay = getDelayMs(9, 50, hour, minute, second, msecond);//定时9：50 全面开盘
                    }else if (tmp[0] - hour > 1) {
                        delay = 3600000;
                    } else {
                        delay = getDelayMs(tmp[0],tmp[1],hour,minute,second,msecond);
                    }
                }
            }else{
                delay =(60-second)*1000;
            }
            long delay2 = delay;
            msecond =(int) delay2 % 1000;
            delay2 = delay2/1000;
            second = (int)delay2 % 60;
            delay2 = delay2/60;
             minute = (int)delay2%60;
            delay2 = delay2/60;
             hour =(int) delay2;

            XposedBridge.log("延时为:"+hour+":"+minute+":"+second+"-"+msecond);
            timer(delay+9000,mTimeRun);
            //mHandler.removeCallbacks(mTimeRun);
            //mHandler.postDelayed(mTimeRun,delay+9000);
        }
    };
    private void dealOther(long hour ,long min){
        if(hour == 3 && min == 0){
            ServerManager.getIntance().clearAllForAllGroup();
        }else if(hour == 9 && min == 50){
            ServerManager.getIntance().setTrueByDayStrart();
        }
    }

    //hh:mm
    private long getDelayMs(long targetH,long targetM,long currentH,long currentM,long currentS,long currentMs){
        long ms = 0;
        long s = 0;
        long min = targetM;
        long hou = targetH;
        if (currentMs != 0) {
            ms = 1000 - currentMs;
            s = 59;
            if (min == 0) {
                min = 59;
                hou = hou - 1;
            } else {
                min = min - 1;
            }
        }
        if (currentS > s) {
            if (min == 0) {
                min = 59;
                hou = hou - 1;
            } else {
                min = min - 1;
            }
            s = s + 60 - currentS;
        }else{
            s = s-currentS;
        }
        if (currentM > min) {
            hou = hou - 1;
            min = min + 60 - currentM;
        }else{
            min = min - currentM;
        }
        hou = hou - currentH;
        return hou * 3600000 + min * 60000 + s * 1000 + ms;
    }
    private boolean isOpen;
    private int getIndex(int hour , int min){
        Integer[] time;
        if(hour == 0 && min == 0){
            isOpen =true;
            return mTimeLsit.length;
        }
        for(int i = 0 ; i<mTimeLsit.length;i++){
            time = mTimeLsit[i];
            if(time[0]  == hour  || (time[0] ==24 && hour ==0) ){
                if(time[1] <min){
                    continue;
                }else if(time[1] == min){
                    isOpen = true;
                    return i+1;
                }else{
                    isOpen = false;
                    return i;
                }
            }else if(hour < time[0]){
                isOpen = false;
                return i;
            }else{
                continue;
            }
        }
        return 0;
    }
	
   private void timer(long delay,final Runnable run) {
	     Timer timer = new Timer();
	     timer.schedule(new TimerTask() {
	       public void run() {
	    	   mExecutor.equals(run);
	       }
     }, delay);
   }
    
    private static WechatApi mIntance = new WechatApi();
    public static WechatApi getIntance(){
        return mIntance;
    }

    private WechatApi(){
    	mExecutor =  new ThreadPoolExecutor(5, 10, 200, TimeUnit.DAYS,
                new ArrayBlockingQueue<Runnable>(1000));
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int msecond =calendar.get(Calendar.MILLISECOND);

        if((hour <9 && hour >=2) || (hour == 9 && minute <50)||(hour == 1 && minute > 55) ){
            ServerManager.getIntance().setFalseByDayEndNoNotification();
            XposedBridge.log("初始设置为关");
        }else{
            ServerManager.getIntance().setTrueByDayStrartNoNotification();
            XposedBridge.log("初始设置为开");
        }
        
        mExecutor.equals(mTimeRun);
    }
}
