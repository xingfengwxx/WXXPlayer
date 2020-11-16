package com.wangxingxing.wxxplayer.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wangxingxing.skinsupport.SkinManager;
import com.wangxingxing.wxxplayer.R;
import com.wangxingxing.wxxplayer.skin.Skin;
import com.wangxingxing.wxxplayer.skin.SkinUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;



public class SkinActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivColor1, ivColor2, ivColor3, ivColor4;
    private TextView tvState1, tvState2, tvState3, tvState4;
    private TextView[] tvStates;

    /**
     * 从服务器拉取的皮肤表
     */
    List<Skin> skins = new ArrayList<>();

    int skinMode = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin);
        initViews();
        addSkins();
        SkinManager.getInstance().updateSkin(this);
    }

    private void addSkins() {
        skins.add(new Skin("b9078cdea98eea7c73452ebed9aae17e", "1111111.skin", "app-debug.apk"));
    }

    private void initViews() {
        TextView tv = findViewById(R.id.tv_main_title);
        tv.setText("个性换肤");

        ivColor1 = findViewById(R.id.iv_color1);
        ivColor2 = findViewById(R.id.iv_color2);
        ivColor3 = findViewById(R.id.iv_color3);
        ivColor4 = findViewById(R.id.iv_color4);
        tvState1 = findViewById(R.id.tv_state1);
        tvState2 = findViewById(R.id.tv_state2);
        tvState3 = findViewById(R.id.tv_state3);
        tvState4 = findViewById(R.id.tv_state4);
        tvStates = new TextView[]{tvState1, tvState2, tvState3, tvState4};

        findViewById(R.id.iv_close).setOnClickListener(this);
        ivColor1.setOnClickListener(this);
        ivColor2.setOnClickListener(this);
        ivColor3.setOnClickListener(this);
        ivColor4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                finish();
                break;
            case R.id.iv_color1:
                for (TextView tv : tvStates) {
                    if (tv == tvState1) {
                        tv.setVisibility(View.VISIBLE);
                    } else {
                        tv.setVisibility(View.GONE);
                    }
                }
                changeSkin(1);
                break;
            case R.id.iv_color2:
                for (TextView tv : tvStates) {
                    if (tv == tvState2) {
                        tv.setVisibility(View.VISIBLE);
                    } else {
                        tv.setVisibility(View.GONE);
                    }
                }
                changeSkin(2);
                break;
            case R.id.iv_color3:
                for (TextView tv : tvStates) {
                    if (tv == tvState3) {
                        tv.setVisibility(View.VISIBLE);
                    } else {
                        tv.setVisibility(View.GONE);
                    }
                }
                changeSkin(3);
                break;
            case R.id.iv_color4:
                for (TextView tv : tvStates) {
                    if (tv == tvState4) {
                        tv.setVisibility(View.VISIBLE);
                    } else {
                        tv.setVisibility(View.GONE);
                    }
                }
                changeSkin(4);
                break;
        }

    }

    private void changeSkin(int mode) {
        if (skinMode == mode) return;
        skinMode = mode;
        switch (mode) {
            case 1:
                SkinManager.getInstance().loadSkin(null);
                break;
            case 4:
                // 使用第4个皮肤
                Skin skin = skins.get(0);
                selectSkin(skin);
                // 换肤
                SkinManager.getInstance().loadSkin(skin.path);
                break;
        }
    }

    /**
     * 下载皮肤包
     */
    private void selectSkin(Skin skin) {
        File theme = new File(getFilesDir(), "theme");
        if (theme.exists() && theme.isFile()) {
            theme.delete();
        }
        theme.mkdirs();
        File skinFile = skin.getSkinFile(theme);
        if (skinFile.exists()) {
            Log.i("TAG", "皮肤已存在,开始换肤");
            return;
        }
        Log.i("TAG", "皮肤不存在,开始下载");
        FileOutputStream fos = null;
        InputStream is = null;
        // 临时文件
        File tempSkin = new File(skinFile.getParentFile(), skin.name + ".temp");
        try {
            fos = new FileOutputStream(tempSkin);
            // 假设下载皮肤包
            is = getAssets().open(skin.url);
            byte[] bytes = new byte[10240];
            int len;
            while ((len = is.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }
            // 下载成功，将皮肤包信息insert已下载数据库
            Log.i("TAG", "皮肤包下载完成开始校验");
            // 皮肤包的md5校验 防止下载文件损坏(但是会减慢速度。从数据库查询已下载皮肤表数据库中保留md5字段)
            String md5 = SkinUtils.getSkinMD5(tempSkin);
            Log.i("TAG", "md5=" + md5);
            if (TextUtils.equals(md5, skin.md5)) {
                Log.i("TAG", "校验成功,修改文件名。");
                tempSkin.renameTo(skinFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tempSkin.delete();
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}