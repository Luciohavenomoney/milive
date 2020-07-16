package com.lucio.milive.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lucio.milive.util.DownloadManager;
import com.lucio.milive.util.DownloadObserver;
import com.lucio.milive.R;
import com.lucio.milive.util.StatusBarUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

public class AddChannelActivity extends AppCompatActivity {

    @BindView(R.id.title_layout)
    RelativeLayout title_layout;
    @BindView(R.id.rl_dialog)
    RelativeLayout rl_dialog;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.edit_url)
    EditText editUrl;
    @BindView(R.id.edit_name)
    EditText editName;
    @BindView(R.id.btn_add)
    Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_channel);
        ButterKnife.bind(this);
        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        init();
    }

    public void init(){
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) title_layout.getLayoutParams();
        int hei = StatusBarUtil.getStatusHeight(this);
        lp.setMargins(0, hei, 0, 0);
        title_layout.setLayoutParams(lp);
        ivBack.setOnClickListener(v -> onBackPressed());
        btnAdd.setOnClickListener(v -> {
            if(TextUtils.isEmpty(editUrl.getText().toString().trim())){
                Toast.makeText(this, "请输入地址！", Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(editName.getText().toString().trim())){
                Toast.makeText(this, "请输入列表名！", Toast.LENGTH_SHORT).show();
                return;
            }
            download();
        });
    }

    public void download(){
        hideSoftInput();
        rl_dialog.setVisibility(View.VISIBLE);
        String file = Environment.getExternalStorageDirectory() + File.separator + "milive" + File.separator + "m3u";
        DownloadManager.download(editUrl.getText().toString().trim(),
                file, editName.getText().toString().trim()+".m3u", new DownloadObserver() {
                    @Override
                    public void onSuccess(File file) {
                        rl_dialog.setVisibility(View.GONE);
                        Toast.makeText(AddChannelActivity.this, "添加成功！", Toast.LENGTH_SHORT).show();
                        finish();
                        //loadPrograms();
                    }
                    @Override
                    public void onFail(Throwable throwable) {
                        rl_dialog.setVisibility(View.GONE);
                        Toast.makeText(AddChannelActivity.this, "添加失败，请确认下载地址是否准确", Toast.LENGTH_SHORT).show();}
                    @Override
                    public void onProgress(int progress, long total) {}
                    @Override
                    public void onSubscribe(Disposable d) {}
                });
    }

    public void hideSoftInput() {
        editUrl.clearFocus();
        editName.clearFocus();
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(editName.getWindowToken(), 0);//强制隐藏键盘
    }
}