package com.lucio.milive;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lucio.milive.activity.AddChannelActivity;
import com.lucio.milive.adapter.ProgramModel;
import com.lucio.milive.adapter.ProgramsAdapter;
import com.lucio.milive.fragment.ListFragment;
import com.lucio.milive.util.JsonUtil;
import com.lucio.milive.util.LandLayoutVideo;
import com.lucio.milive.util.StatusBarUtil;
import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.bean.Permissions;
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.video_player)
    LandLayoutVideo videoPlayer;
    @BindView(R.id.rv_programs)
    RecyclerView rvPrograms;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_url)
    TextView tvUrl;
    @BindView(R.id.tv_channel)
    TextView tvChanel;
    @BindView(R.id.btn_more)
    FloatingActionButton btn_more;
    @BindView(R.id.btn_add)
    FloatingActionButton btn_add;
    private GSYVideoOptionBuilder gsyVideoOptionBuilder;
    private ProgramsAdapter programsAdapter;
    private long lastTime = 0;
    private static final long KEY_DELAY_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        initVideo();
        start();
    }

    private void initPrograms() {
        rvPrograms.setLayoutManager(new LinearLayoutManager(this));
        programsAdapter = new ProgramsAdapter(this);
        programsAdapter.setClickCallback((position, bean) -> {
            tvName.setText(bean.name);
            tvUrl.setText(bean.url);
            gsyVideoOptionBuilder.setVideoTitle(bean.name);
            gsyVideoOptionBuilder.setUrl(bean.url).build(videoPlayer);
            videoPlayer.loadCoverImage("",R.drawable.tv_place);
            playDelayed(500);
        });
        rvPrograms.setAdapter(programsAdapter);
        videoPlayer.loadCoverImage("",R.drawable.tv_place);
        refreshList(0,null);
        tvChanel.setText("当前播放列表：原始节目列表1");
    }

    private void initVideo() {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) videoPlayer.getLayoutParams();
        int hei = StatusBarUtil.getStatusHeight(this);
        lp.setMargins(0, hei, 0, 0);
        videoPlayer.setLayoutParams(lp);
        gsyVideoOptionBuilder = new GSYVideoOptionBuilder();
        gsyVideoOptionBuilder
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setAutoFullWithSize(true)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setCacheWithPlay(false)
                .setVideoTitle("")
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onQuitFullscreen(String url, Object... objects) {
                        super.onQuitFullscreen(url, objects);
                        //不静音
                        GSYVideoManager.instance().setNeedMute(false);
                    }

                    @Override
                    public void onEnterFullscreen(String url, Object... objects) {
                        super.onEnterFullscreen(url, objects);
                        GSYVideoManager.instance().setNeedMute(false);
                        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
                        videoPlayer.getCurrentPlayer().getTitleTextView().setText((String)objects[0]);
                    }
                }).build(videoPlayer);
        videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        videoPlayer.getBackButton().setVisibility(View.GONE);
        //设置全屏按键功能
        videoPlayer.getFullscreenButton().setOnClickListener(v -> resolveFullBtn(videoPlayer));
        btn_more.setOnClickListener(v -> {
            ListFragment listFragment = new ListFragment();
            listFragment.show(getSupportFragmentManager(), ListFragment.class.getSimpleName());
        });
        btn_add.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddChannelActivity.class)));
    }

    /**
     * 全屏幕按键处理
     */
    private void resolveFullBtn(final StandardGSYVideoPlayer standardGSYVideoPlayer) {
        standardGSYVideoPlayer.startWindowFullscreen(this, true, true);
    }

    @Override
    protected void onDestroy() {
        videoPlayer.release();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        if (System.currentTimeMillis() - lastTime < KEY_DELAY_TIME) {
            finish();
        } else {
            Toast.makeText(MainActivity.this, "再按一次退出！", Toast.LENGTH_SHORT).show();
            lastTime = System.currentTimeMillis();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onVideoResume();
    }

    public void loadPrograms(boolean isJson,String file){
        Observable.create((ObservableOnSubscribe<List<ProgramModel>>) emitter -> {
            if(isJson){
                String program = JsonUtil.loadJson(MainActivity.this);
                ArrayList<ProgramModel> models = new Gson().fromJson(program,new TypeToken<ArrayList<ProgramModel>>(){}.getType());
                emitter.onNext(models);
            }else {
                if(file == null){
                    emitter.onNext(JsonUtil.loadM3U(MainActivity.this));
                }else {
                    emitter.onNext(JsonUtil.readM3U(file));
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<ProgramModel>>() {
            @Override
            public void onSubscribe(Disposable d) {
                //Toast.makeText(MainActivity.this, "节目加载中", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNext(List<ProgramModel> models) {
                Log.e("models",""+models.size());
                programsAdapter.refresh(models);
                if(models.size()>10)
                    rvPrograms.scrollToPosition(0);
            }
            @Override
            public void onError(Throwable e) {}
            @Override
            public void onComplete() {}
        });
    }

    //首次播放延时处理
    public void playDelayed(long milliseconds){
        Observable.timer(milliseconds, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        mDisposable = disposable;
                    }
                    @Override
                    public void onNext(@NonNull Long number) {
                        videoPlayer.startPlayLogic();
                    }
                    @Override
                    public void onError(@NonNull Throwable e) {cancel();}
                    @Override
                    public void onComplete() {cancel();}
                });
    }
    private Disposable mDisposable;
    public void cancel(){
        if(mDisposable!=null&&!mDisposable.isDisposed()){
            mDisposable.dispose();
        }
    }

    private void start(){
        SoulPermission.getInstance().checkAndRequestPermissions(
                Permissions.build(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE),
                new CheckRequestPermissionsListener() {
                    @Override
                    public void onAllPermissionOk(Permission[] allPermissions) {
                        initPrograms();
                    }

                    @Override
                    public void onPermissionDenied(Permission[] refusedPermissions) {
                        Toast.makeText(MainActivity.this, "已拒绝", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void refreshList(int position,ProgramModel bean){
        if(position == 0){
            tvChanel.setText("当前播放列表：原始节目列表1");
            loadPrograms(true,null);
        }else if (position == 1){
            tvChanel.setText("当前播放列表：原始节目列表2");
            loadPrograms(false,null);
        }else {
            tvChanel.setText(new StringBuffer("当前播放列表："+bean.name));
            loadPrograms(false,bean.url);
        }
    }
}
