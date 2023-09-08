package kr.foryou.ddcheon;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.foryou.ddcheon.count.CountItem;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import util.RetrofitService;

public class SplashActivity extends AppCompatActivity {
    private static final int APP_PERMISSION_STORAGE = 9787;
    private final int APPS_PERMISSION_REQUEST=1000;
    final int SEC=2000;//다음 화면에 넘어가기 전에 머물 수 있는 시간(초)
    @BindView(R.id.accumulateTxt)
    TextView accumulateTxt;
    @BindView(R.id.todayTxt)
    TextView todayTxt;
    int totalCount=0;//전체 누적수 ++
    int todayCount=0;//오늘  ++
    int sum=0;//전체 누적수
    int today=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        /*//버전별 체크를 한 후 마시멜로 이상이면 퍼미션 체크 여부
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                checkPermission();
            } else {
                goHandler();
            }
        } catch (Exception e) {
        }*/
        getCount();
    }
    public void getCount(){
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //클라이언트 설정
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();
        //레트로핏 설정
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(getString(R.string.domain))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //파라미터 넘길 값 설정
        Map map=new HashMap();
        map.put("k","1");
        //레트로핏 서비스 실행하기
        RetrofitService retrofitService=retrofit.create(RetrofitService.class);
        //데이터 불러오기
        Call<CountItem> call = retrofitService.getCountData(map);
        call.enqueue(new Callback<CountItem>() {
            @Override
            public void onResponse(Call<CountItem> call, Response<CountItem> response) {
                CountItem repo = response.body();
                Log.d("sum",repo.getSum()+"");
                sum = repo.getSum();
                today = repo.getToday();
                new Thread(){
                    @Override
                    public void run() {
                        loop();
                    }
                }.start();
                Log.d("count",repo.toString());
            }

            @Override
            public void onFailure(Call<CountItem> call, Throwable t) {
                Log.d("count","error"+t.toString());
            }
        });

    }

    private  void loop(){
        for(int i=0;i <= sum ;i++) {
            try {
                mHandler.sendEmptyMessage(0);
                mHandler.sendEmptyMessage(1);
                if(totalCount>=sum) mHandler.sendEmptyMessageDelayed(2,750);
                Thread.sleep(1);
            }catch (Exception e){

            }
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
    }

    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if(sum >= totalCount){
                        int mod = (sum / 100000 / 9) +1;
                        totalCount = totalCount + (sum / 1000) + mod;
                        Log.d("totalCount",todayCount +"+"+"("+sum+"/"+ 1000+") +"+mod);
                        accumulateTxt.setText(Math.min(sum,totalCount)+"명");
                    }
                    break;
                case 1:
                    if(today > todayCount) {
                        todayCount++;
                        todayTxt.setText(todayCount + "명");
                    }
                    break;
                case 2:
                    mHandler.removeMessages(0);
                    mHandler.removeMessages(1);
                    mHandler.removeMessages(2);
                    finish();
            }
        }
    };


}
