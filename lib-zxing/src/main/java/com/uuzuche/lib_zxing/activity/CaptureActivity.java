package com.uuzuche.lib_zxing.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.uuzuche.lib_zxing.R;

/**
 * @author Song
 * desc:默认的二维码扫描Activity
 */
public class CaptureActivity extends AppCompatActivity {
    /**
     * 选择系统图片Request Code
     */
    public static final int REQUEST_IMAGE = 112;

    private TextView tv_pic, tv_chancel, tv_light;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process02_camera);

        tv_pic = findViewById(R.id.tv_pic);
        tv_chancel = findViewById(R.id.tv_chancel);
        tv_light = findViewById(R.id.tv_light);
        tv_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });
        tv_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("打开闪光灯".equals(tv_light.getText().toString())) {
                    CodeUtils.isLightEnable(true);
                    tv_light.setText("关闭闪光灯");
                } else if ("关闭闪光灯".equals(tv_light.getText().toString())) {
                    CodeUtils.isLightEnable(false);
                    tv_light.setText("打开闪光灯");
                }
            }
        });

        tv_chancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        CaptureFragment captureFragment = new CaptureFragment();
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_zxing_container, captureFragment).commit();
        captureFragment.setCameraInitCallBack(new CaptureFragment.CameraInitCallBack() {
            @Override
            public void callBack(Exception e) {
                if (e == null) {

                } else {
                    Log.e("TAG", "callBack: ", e);
                }
            }
        });
    }

    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            Log.e("TAG_resultString", result);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            CaptureActivity.this.setResult(RESULT_OK, resultIntent);
            CaptureActivity.this.finish();
        }

        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            CaptureActivity.this.setResult(RESULT_OK, resultIntent);
            CaptureActivity.this.finish();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == RESULT_OK) {
                // 首先获取到此图片的Uri
                uri = data.getData();
                //获取选取的图片的绝对地址
                //获取选取的图片的绝对地址
                String photoPath = getImagePathFromURI(CaptureActivity.this, uri);
                //  ContentResolver cr = getContentResolver();
                try {
                    if (photoPath != null) {
                        CodeUtils.analyzeBitmap(photoPath, new CodeUtils.AnalyzeCallback() {
                            @Override
                            public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                                Toast.makeText(CaptureActivity.this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent();
                                intent.putExtra(CodeUtils.RESULT_STRING, result);
                                setResult(0, intent);
                                finish();
                            }

                            @Override
                            public void onAnalyzeFailed() {
                                Toast.makeText(CaptureActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取选取的图片的绝对地址
     *
     * @param
     * @param uri
     * @return
     */
    public static String getImagePathFromURI(Activity activity, Uri uri) {
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
        String path = null;
        if (cursor != null) {
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = activity.getContentResolver().query(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            if (cursor != null) {
                cursor.moveToFirst();
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();
            }
        }
        return path;
    }
}