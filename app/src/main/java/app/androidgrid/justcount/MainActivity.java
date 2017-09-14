package app.androidgrid.justcount;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

/**
 * This app created and developed by Android Grid. You can use this source code for free.
 */

public class MainActivity extends AppCompatActivity {

    private Button btnLeft, btnRight;
    private RecyclerView mLapList;
    private TextView textCount;
    private View shadowTop, shadowBottom;
    private FrameLayout frameCount, frameList;

    private RecyclerView.Adapter adapter;
    private List<LapModel> lapModelList;
    private DividerItemDecoration dividerItemDecoration;
    private LinearLayoutManager linearLayoutManager;

    private LinearLayout.LayoutParams p;

    /**
     * CURRENT_STATE = 1 , Count is null;
     * CURRENT_STATE = 2 , Count is started;
     * CURRENT_STATE = 3 , Count is paused;
     */
    private int CURRENT_STATE = 1;
    private int PLUS_LAP = 0;

    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;

    Handler handler;

    int Hours, Seconds, Minutes, MilliSeconds;
    int lastMinutes, lastSeconds, lastMilliSeconds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLeft = findViewById(R.id.btn_left);
        btnRight = findViewById(R.id.btn_right);
        textCount = findViewById(R.id.text_count);
        mLapList = findViewById(R.id.list_lap);
        shadowBottom = findViewById(R.id.shadow_bottom);
        shadowTop = findViewById(R.id.shadow_top);
        frameCount = findViewById(R.id.count_frame);
        frameList = findViewById(R.id.list_frame);

        p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.weight = 1;

        frameCount.setLayoutParams(p);

        shadowTop.setVisibility(View.GONE);
        shadowBottom.setVisibility(View.GONE);
        frameList.setVisibility(View.GONE);

        lapModelList = new ArrayList<>();
        adapter = new LapAdapter(getApplicationContext(), lapModelList);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        dividerItemDecoration = new DividerItemDecoration(mLapList.getContext(), linearLayoutManager.getOrientation());
        mLapList.addItemDecoration(dividerItemDecoration);
        mLapList.setHasFixedSize(true);
        mLapList.setLayoutManager(linearLayoutManager);
        mLapList.setAdapter(adapter);

        handler = new Handler();

        mLapList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();

                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    //End of list
                    shadowBottom.setVisibility(View.GONE);
                    shadowTop.setVisibility(View.VISIBLE);
                } else if (pastVisibleItems + visibleItemCount <= totalItemCount) {
                    shadowTop.setVisibility(View.GONE);
                    shadowBottom.setVisibility(View.VISIBLE);
                }
            }
        });

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leftClick();
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rightClick();
            }
        });

    }

    private void leftClick() {
        switch (CURRENT_STATE) {
            case 1:
                Log.i("MainActivity", "left clicked with null");
                break;
            case 2:
                lapCount();
                break;
            case 3:
                resetCount();
                break;
        }
        updateButtons();
    }

    private void rightClick() {
        switch (CURRENT_STATE) {
            case 1:
                startCount();
                break;
            case 2:
                pauseCount();
                break;
            case 3:
                contiCount();
                break;
        }
        updateButtons();
    }

    private void updateButtons() {
        switch (CURRENT_STATE) {
            case 1:
                btnLeft.setVisibility(View.GONE);
                btnRight.setText(R.string.start);
                btnRight.setBackgroundResource(R.drawable.start_btn);
                break;
            case 2:
                btnLeft.setVisibility(View.VISIBLE);
                btnLeft.setText(R.string.lap);
                btnRight.setBackgroundResource(R.drawable.pause_btn);
                btnRight.setText(R.string.pause);
                break;
            case 3:
                btnLeft.setVisibility(View.VISIBLE);
                btnLeft.setText(R.string.reset);
                btnRight.setBackgroundResource(R.drawable.pause_btn);
                btnRight.setText(R.string.conti);
                break;
        }

        if (lapModelList.size() >= 1) {
            frameList.setVisibility(View.VISIBLE);
            p.weight = 0;
            frameCount.setLayoutParams(p);
        } else {
            frameList.setVisibility(View.GONE);
            p.weight = 1;
            frameCount.setLayoutParams(p);
        }
    }

    private void startCount() {
        CURRENT_STATE = 2;
        updateButtons();

        StartTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);
    }

    private void pauseCount() {
        CURRENT_STATE = 3;
        updateButtons();

        TimeBuff += MillisecondTime;

        handler.removeCallbacks(runnable);
    }

    private void contiCount() {
        CURRENT_STATE = 2;
        updateButtons();

        startCount();
    }

    private void lapCount() {
        updateButtons();
        PLUS_LAP++;
        LapModel lapModel = new LapModel();
        lapModel.setLapTime(String.format("%02d", Minutes) + ":" + String.format("%02d", Seconds) + "." + MilliSeconds);
        /*lapModel.setLapTime(Hours + String.format("%02d", Minutes) + ":"
                + String.format("%02d", Seconds) + ":"
                + String.format("%03d", MilliSeconds));*/
        if (lastMinutes == 0 && lastSeconds == 0 && lastMilliSeconds == 0) {
            lapModel.setLapPlusTime("+" + String.format("%02d", Minutes) + ":" + String.format("%02d", Seconds) + "." + MilliSeconds);
        } else {
            lapModel.setLapPlusTime("+" + timeDiff(lastMinutes, lastSeconds, lastMilliSeconds));
        }
        lapModel.setLapPosition(valueOf(PLUS_LAP));

        lapModelList.add(lapModel);
        adapter.notifyDataSetChanged();

        lastSeconds = Seconds;
        lastMilliSeconds = MilliSeconds;
        lastMinutes = Minutes;

        try {
            mLapList.smoothScrollToPosition(lapModelList.size() +1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void resetCount() {
        CURRENT_STATE = 1;
        PLUS_LAP = 0;
        updateButtons();
        adapter.notifyItemRangeRemoved(0, lapModelList.size());
        lapModelList.clear();
        adapter.notifyDataSetChanged();
        adapter.notifyItemRemoved(-1);
        adapter.notifyItemRangeChanged(0, lapModelList.size());
        adapter = new LapAdapter(getApplicationContext(), lapModelList);
        mLapList.setAdapter(adapter);

        shadowBottom.setVisibility(View.GONE);
        shadowTop.setVisibility(View.GONE);

        MillisecondTime = 0L ;
        StartTime = 0L ;
        TimeBuff = 0L ;
        UpdateTime = 0L ;
        Seconds = 0 ;
        Minutes = 0 ;
        MilliSeconds = 0 ;

        textCount.setText(R.string.time_format);
    }

    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            Hours = Seconds / 60 / 60;

            Minutes = Seconds / 60;

            Seconds = Seconds % 60;

            MilliSeconds = (int) (UpdateTime % 1000);

            textCount.setText(String.format("%02d", Minutes) + ":" + String.format("%02d", Seconds) + "." + String.format("%03d", MilliSeconds));

            /*textCount.setText(Hours  + ":" + String.format("%02d", Minutes) + ":"
                    + String.format("%02d", Seconds) + ":"
                    + String.format("%03d", MilliSeconds));*/

            handler.postDelayed(this, 0);
        }

    };

    private String timeDiff(int Min, int Sec, int Milli) {

        /*long mills = time1 - time2;
        int diffH = (int) (mills/60/60);
        int diffM = (int) (mills/60);
        int diffS = (int) (mills%60);
        int diffMS = (int) (mills%1000);

        long sec = time1 - time2;
        int mills = (int) sec * 1000;

        return String.valueOf(sec + "." + mills);

        return diffH  + ":" + String.format("%02d", diffM) + ":"
                + String.format("%02d", diffS) + ":"
                + String.format("%03d", diffMS);
       // return diffH + ":" + diffM + ":" + diffS + ":" + diffMS;*/

        int diffM = Minutes - Min;
        int diffS = Seconds - Sec;
        int diffSm = MilliSeconds - Milli;

        //String diff = String.format("%02d", valueOf(diffM)) + ":" + String.format("%02d", valueOf(diffS)) + "." + String.format("%03d", valueOf(diffSm));

        return String.format("%02d", diffM).replace("-", "") + ":"
                + String.format("%02d", diffS).replace("-", "") + "."
                + String.format("%03d", diffSm).replace("-", "");
    }

    @Override
    public void onBackPressed() {
        if (CURRENT_STATE == 2 | CURRENT_STATE == 3) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            this.startActivity(i);
        } else if (CURRENT_STATE == 1) {
            super.onBackPressed();
        }
    }
}
