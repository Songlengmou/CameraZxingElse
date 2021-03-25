package com.anningtex.camerazxingelse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.uuzuche.lib_zxing.activity.ZXingLibrary;

/**
 * @author Song
 * desc：CameraZxingElse里的第一种    依赖于module(lib-zxing)
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        ZXingLibrary.initDisplayOpinion(this);

        findViewById(R.id.btn_input_camera).setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, CameraActivity.class));
        });
    }
}