package com.jerry.qrcodedemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jerry.zxing.library.android.CaptureActivity;
import com.jerry.zxing.library.encode.QRCodeUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String CONTENT = "codedContent";
    public static final String BITMAP = "codedBitmap";
    public static final int REQUEST_CODE = 1;
    private EditText etInput;
    private Button btnMake;
    private CheckBox cbLogo;
    private ImageView ivQrcode;
    private Button btnSao;
    private TextView tvResult;

    private void assignViews() {
        etInput = (EditText) findViewById(R.id.et_input);
        btnMake = (Button) findViewById(R.id.btn_make);
        cbLogo = (CheckBox) findViewById(R.id.cb_logo);
        ivQrcode = (ImageView) findViewById(R.id.iv_qrcode);
        btnSao = (Button) findViewById(R.id.btn_sao);
        tvResult = (TextView) findViewById(R.id.tv_result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignViews();

        setListener();
    }

    private void setListener() {
        btnMake.setOnClickListener(this);
        btnSao.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_make:
                final String s = etInput.getText().toString();
//                final int len = PixelUtil.dp2px(MainActivity.this, 300);
                final Bitmap bitmapLogo = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                if (!TextUtils.isEmpty(s)) {
                    //开启子线程，二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (cbLogo.isChecked()) {
                                final String qrImagePath = QRCodeUtil.createQRImage(MainActivity.this, s, bitmapLogo);
                                if (!TextUtils.isEmpty(qrImagePath)) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ivQrcode.setImageBitmap(BitmapFactory.decodeFile(qrImagePath));
                                        }
                                    });
                                }
                            } else {
                                final String qrImagePath = QRCodeUtil.createQRImage(MainActivity.this, s, null);
                                if (!TextUtils.isEmpty(qrImagePath)) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ivQrcode.setImageBitmap(BitmapFactory.decodeFile(qrImagePath));
                                        }
                                    });
                                }
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(MainActivity.this, "请输入字符串", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_sao:
                Intent intent = new Intent(this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String content = data.getStringExtra(CONTENT);
            Bitmap bitmap = data.getParcelableExtra(BITMAP);
            ivQrcode.setImageBitmap(bitmap);
            tvResult.setText(content);
        }
    }
}
