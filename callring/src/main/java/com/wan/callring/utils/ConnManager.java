package com.wan.callring.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by 95470 on 2017/10/26.
 */

public class ConnManager {
    private static String Success = "Success";
    public static final int STATE_FROM_SERVER_OK = 3;
    public static final int STATE_Success = 10000;
    private static String dsName = "223.87.178.46";
    private static int dstPort = 12345;
    private Socket socket;
    //队列，封装发送的信息
    private ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(8);

    private static ConnManager instance;

    public ConnManager() {
    }

//    public static ConnManager getInstance() {
//        if (instance == null) {
//            synchronized (ConnManager.class) {
//                if (instance == null) {
//                    instance = new ConnManager();
//                }
//            }
//        }
//        return instance;
//    }

    /**
     * 连接
     *
     * @return
     */
    public boolean connect(final Handler handler) {
        Log.e("ZX","连接");
        if (true||socket == null || socket.isClosed()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket = new Socket(dsName, dstPort);
                        socket.setSoTimeout(200*1000);
                        Message msg = Message.obtain();
                        msg.obj =Success;
                        msg.what = STATE_Success;
                        handler.sendMessage(msg);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                            e.printStackTrace();
//                            disConnect();
                    }
                    request = true;
                    new Thread(new RequestWorker()).start();

                    try {
                        // 输入流
                        InputStream is = socket.getInputStream();
                        Log.e("zx",is.available()+"");
                        byte[] buffer = new byte[1024];
                        int len = -1;
                        StringBuffer sb = new StringBuffer();
                        while ((len = is.read(buffer)) != -1) {
                            String result = new String(buffer, 0, len);
                            Log.e("zx","result : "+result);
                            Message msg = Message.obtain();
                            msg.obj = sb.toString();
                            msg.what = STATE_FROM_SERVER_OK;
                            handler.sendMessage(msg);
                            sb.append(result);
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
//                        disConnect();
                        //throw new RuntimeException("getInputStream错误: " + e.getMessage());
                    }

                }
            }).start();
        }else {
            Log.e("ZX","socket不为空");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = Message.obtain();
                    msg.obj =Success;
                    msg.what = STATE_Success;
                    handler.sendMessage(msg);
//                    new Thread(new RequestWorker()).start();
//
//                    try {
//                        // 输入流，为了获取客户端发送的数据
//                        InputStream is = socket.getInputStream();
//                        Log.e("zx",is.available()+"");
//                        byte[] buffer = new byte[1024];
//                        int len = -1;
//                        StringBuffer sb = new StringBuffer();
//                        while ((len = is.read(buffer)) != -1) {
//                            String result = new String(buffer, 0, len);
//                            sb.append(result);
//                            Message msg = Message.obtain();
//                            msg.obj = sb.toString();
//                            msg.what = STATE_FROM_SERVER_OK;
//                            handler.sendMessage(msg);
//                            Log.e("zx","msg : "+msg.obj.toString());
//                        }

//                    } catch (IOException e) {
////                        disConnect();
//                    }

                }
            }).start();
        }

        return true;
    }



    /**
     * 添加请求
     * @param content
     */
    public void putRequest(String content) {
        try {
            queue.put(content);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    /**
     * 关闭连接
     */
    public void disConnect() {
        if (socket != null && !socket.isClosed()) {
            try {
                request = false;
                socket.close();
            } catch (IOException e) {
                Log.e("zx","关闭异常:");
                e.getSuppressed();
            }
            socket = null;
        }
    }

    boolean request = true;

    public class RequestWorker implements Runnable {
        @Override
        public void run() {
            OutputStream os = null;
            try {
                if (socket != null) {
                    os = socket.getOutputStream();
                    //take是个阻塞式方法，所以必须是用while(true)
                    Log.e("zx","发送:request="+request);
                    while(request){
                        Log.e("zx","发送:"+1);
                        String content = queue.take();
                        Log.e("zx","发送:"+content);
                        os.write(content.getBytes());
                        os.flush();
                        Log.e("zx","发送:"+2);
                    }
                }
            } catch (IOException e) {
                Log.e("zx","发送失败:");
                e.printStackTrace();
            } catch (InterruptedException e) {
                Log.e("zx","信息被中断:");
                e.printStackTrace();
            }
        }
    }
}
