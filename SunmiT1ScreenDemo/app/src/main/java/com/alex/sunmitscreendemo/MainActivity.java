package com.alex.sunmitscreendemo;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alex.sunmitscreendemo.utils.DataModel;
import com.alex.sunmitscreendemo.utils.SharePreferenceUtil;
import com.alex.sunmitscreendemo.utils.UPacketFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import sunmi.ds.DSKernel;
import sunmi.ds.SF;
import sunmi.ds.callback.ICheckFileCallback;
import sunmi.ds.callback.IConnectionCallback;
import sunmi.ds.callback.IReceiveCallback;
import sunmi.ds.callback.ISendCallback;
import sunmi.ds.callback.ISendFilesCallback;
import sunmi.ds.callback.QueryCallback;
import sunmi.ds.data.DSData;
import sunmi.ds.data.DSFile;
import sunmi.ds.data.DSFiles;
import sunmi.ds.data.DataPacket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = MainActivity.class.getSimpleName();
    private DSKernel mDSKernel = null;
    private MyHandler myHandler;
    private Button btnTest; //(show test title)
    private Button btnWelcom; //(show welcome)


    private IConnectionCallback mIConnectionCallback = new IConnectionCallback() {
        @Override
        public void onDisConnect() {
            Message message = new Message();
            message.what = 1;
            message.obj = "与远程服务连接中断";
            myHandler.sendMessage(message);
        }

        @Override
        public void onConnected(ConnState state) {
            Message message = new Message();
            message.what = 1;
            switch (state) {
                case AIDL_CONN:
                    message.obj = "与远程服务绑定成功";
                    break;
                case VICE_SERVICE_CONN:
                    message.obj = "与副屏服务通讯正常";
                    break;
                case VICE_APP_CONN:
                    message.obj = "与副屏app通讯正常";
                    break;
                default:
                    break;
            }
            myHandler.sendMessage(message);
        }
    };

    private IReceiveCallback mIReceiveCallback = new IReceiveCallback() {
        @Override
        public void onReceiveData(DSData data) {

        }

        @Override
        public void onReceiveFile(DSFile file) {

        }

        @Override
        public void onReceiveFiles(DSFiles files) {

        }

        @Override
        public void onReceiveCMD(DSData cmd) {

        }
    };
    /*private IReceiveCallback mIReceiveCallback2 = new IReceiveCallback() {
        @Override
        public void onReceiveData(DSData data) {

        }

        @Override
        public void onReceiveFile(DSFile file) {

        }

        @Override
        public void onReceiveFiles(DSFiles files) {

        }

        @Override
        public void onReceiveCMD(DSData cmd) {

        }
    };*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myHandler = new MyHandler(this);
        initSdk();
        initView();
        initAction();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDSKernel != null) {
            mDSKernel.checkConnection();
        } else {
            initSdk();
        }
    }

    @Override
    protected void onPause() { //如果存在activity跳转，需要做清理操作
        super.onPause();
        mDSKernel.onDestroy();
        mDSKernel = null;
    }

    private void initSdk() {
        mDSKernel = DSKernel.newInstance();
        mDSKernel.init(this, mIConnectionCallback);
        mDSKernel.addReceiveCallback(mIReceiveCallback);
       // mDSKernel.addReceiveCallback(mIReceiveCallback2);
        mDSKernel.removeReceiveCallback(mIReceiveCallback);
       // mDSKernel.removeReceiveCallback(mIReceiveCallback2);
    }

    private void initView() {
        btnTest = (Button) findViewById(R.id.btn_test);
        btnWelcom = (Button) findViewById(R.id.btn_welcome);
    }

    private void initAction() {
        btnTest.setOnClickListener(this);
        btnWelcom.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_test://显示欢迎

                try {
                    JSONObject json = new JSONObject();
                    json.put("title", "title");    //title is the content of the line above
                    json.put("content", "content");    //content is the content of the following line
                    String jsonStr = json.toString();    //Build the DataPacket class
                    DataPacket packet = UPacketFactory.buildShowText(DSKernel.getDSDPackageName(), jsonStr, new ISendCallback() {
                        @Override
                        public void onSendSuccess(long taskId) {

                        }

                        @Override
                        public void onSendFail(int errorId, String errorInfo) {

                        }

                        @Override
                        public void onSendProcess(long totle, long sended) {

                        }
                    });    //The first parameter is the package name of data receiving sub-application, you can refer the demo here, the second parameter is the displaying contents string, the third parameter is the result callback.

                    mDSKernel.sendData(packet);    //Call SendData to send text
                } catch (JSONException e) {
                    e.printStackTrace();
                }


//                showToast(Settings.Global.getString(getContentResolver(), "custom_launcher"));
//                Log.d("GLL", "onClick: ---------------->"+Settings.Global.getString(getContentResolver(),"sunmi_sub_model"));
//                Log.d("GLL", "onClick: ---------------->"+Settings.Global.getString(getContentResolver(), "custom_launcher"));
//                showToast(Settings.Global.getString(getContentResolver(),"custom_launcher"));
                break;
            case R.id.btn_welcome://显示欢迎
                try {
                    JSONObject json = new JSONObject();
                    json.put("dataModel", "SHOW_IMG_WELCOME");
                    json.put("data", "gaolulin");
                    mDSKernel.sendCMD(SF.DSD_PACKNAME, json.toString(), -1, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                showToast(Settings.Global.getString(getContentResolver(), "custom_launcher"));
//                Log.d("GLL", "onClick: ---------------->"+Settings.Global.getString(getContentResolver(),"sunmi_sub_model"));
//                Log.d("GLL", "onClick: ---------------->"+Settings.Global.getString(getContentResolver(), "custom_launcher"));
//                showToast(Settings.Global.getString(getContentResolver(),"custom_launcher"));
                break;

            default:
                break;
        }
    }


    private static class MyHandler extends Handler {
        private WeakReference<Activity> mActivity;

        public MyHandler(Activity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity.get() != null && !mActivity.get().isFinishing()) {
                switch (msg.what) {
                    case 1://消息提示用途
                        Toast.makeText(mActivity.get(), msg.obj + "", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        }

    }





}

