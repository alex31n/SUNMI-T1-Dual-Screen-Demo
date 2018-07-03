package com.alex.sunmitscreendemo;

import android.app.Application;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by highsixty on 2017/11/20.
 * mail  gaolulin@sunmi.com
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                initAssets();
            }
        }).start();

    }

    private void initAssets() {
        Log.d("TAG", "initAssets: ------------->");
        AssetManager assetManager = getAssets();
        InputStream inputStream = null;
        FileOutputStream fos = null;
        try {
            String fileNames[] = assetManager.list("custom_resource");
            String rootPath = Environment.getExternalStorageDirectory().getPath();
            for (int i = 0; i < fileNames.length; i++) {
                File file = new File(rootPath + "/" + fileNames[i]);
                if (file.exists()) {
                    Log.d("TAG", "initAssets: -------->文件存在");
                    continue;
                }
                Log.d("TAG", "initAssets: -------->文件不存在");
                inputStream = getClass().getClassLoader().getResourceAsStream("assets/custom_resource/" + fileNames[i]);
                fos = new FileOutputStream(new File(rootPath + "/" + fileNames[i]));
                int len = 0;
                byte[] buffer = new byte[1024];
                while ((len = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                inputStream.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
