package com.future;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.future.bean.Event;
import com.future.future.R;
import com.future.service.AlarmService;
import com.future.ui.fragment.AddEventFragment;
import com.future.ui.fragment.DetailFragment;
import com.future.util.DBUtils;
import com.future.util.DateUtils;
import com.future.util.DialogUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {

    private boolean alarm;
    private boolean search;
    private int lastAdvance = -1;// 上一次提前闹id
    private int lastAlarm = -1;// 上次闹钟id
    private String path;
    private DBUtils dbUtils;
    private ListView events;
    private EditText text_query;
    private Bundle length;
    private List<Event> eventList;
    private List<String> motto;
    private Intent intent;
    private Context context;
    private final Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    // 当时间到了->铃响
                    initListView(events, length, dbUtils);
                    int id = -1;
                    for (int i = 0; i < eventList.size(); i++) {
                        id = i + 1;
                        SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(id), MODE_PRIVATE);
                        boolean advance = sharedPreferences.getBoolean("advance", false);
                        try {
                            // 提前闹
                            if (lastAdvance != id && advance && "0天0小时30分钟".equals(DateUtils.getDateDiff(eventList.get(i).getEndDate()))) {
                                path = sharedPreferences.getString("path", null);
                                alarm = true;
                                lastAdvance = id;
                            }
                            // 准时闹
                            if (lastAlarm != id && "0天0小时0分钟".equals(DateUtils.getDateDiff(eventList.get(i).getEndDate()))) {
                                path = sharedPreferences.getString("path", null);
                                alarm = true;
                                lastAlarm = id;
                                Log.e("alarm====>should start", "应该开始了");
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                case 1: {
                    intent.putExtra("path", path);
                    startService(intent);
                    Log.e("alarm====>really start", "已经开始");
                    DialogUtils.setConfirmDialog(context, "时间到了", null, (dialog, which) -> stopService(intent));
                }
                default:
                    Log.e("handler====>", "error");
                    break;
            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    handler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                if (alarm) {
                    handler.sendEmptyMessage(1);
                    alarm = false;
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarm = false;
        search = false;
        dbUtils = new DBUtils(this);
        events = findViewById(R.id.listView_event);
        intent = new Intent(this, AlarmService.class);
        text_query = findViewById(R.id.text_query);
        ImageButton button_search = findViewById(R.id.button_search);
        ImageButton button_add = findViewById(R.id.button_add);
        length = new Bundle();
        context = this;
        motto = new ArrayList<>();
        motto.add("花有重开日，人物在少年");
        motto.add("年难留,时易损");
        motto.add("光阴潮汐不等人");

        initListView(events, length, dbUtils);

        // 点击查看详情，内部修改
        events.setOnItemClickListener((parent, view, position, id) -> {
            Bundle bundle = new Bundle();
            bundle.putInt("id", position + 1);
            DetailFragment fragment = new DetailFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            fragment.setArguments(bundle);
            transaction.addToBackStack(null);
            transaction.replace(R.id.layout_main, fragment);
            transaction.commit();
        });

        // 长按删除
        events.setOnItemLongClickListener((parent, view, position, id) -> {
            DialogUtils.setDialog(this, "确定删除该事件？", (dialog, which) -> {
                dbUtils.delete(position + 1);
                Toast.makeText(view.getContext(), "删除成功", Toast.LENGTH_SHORT);
                initListView(events, null, dbUtils);
            });
            return true;
        });

        // 搜索
        button_search.setOnClickListener(v -> {
            search = "".contentEquals(text_query.getText());
        });

        // 新增
        button_add.setOnClickListener(v -> {
            Fragment fragment = new AddEventFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            fragment.setArguments(length);
            transaction.addToBackStack(null);
            transaction.replace(R.id.layout_main, fragment);
            transaction.commit();
        });
    }

    public void initListView(ListView listView, Bundle bundle, DBUtils dbUtils) {

        eventList = new ArrayList<>();
        try {
            if (search = true){
                eventList = dbUtils.selectByInfo(text_query.getText().toString());
            } else {
                eventList = dbUtils.selectAll();
            }

            // 测试
//            for (int i = 0; i < eventList.size(); i++) {
//                Log.e(String.valueOf(i), eventList.get(i).toString());
//            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int length = eventList.size();
        if (bundle != null) {
            bundle.putInt("id", length + 1);// 以后用来设置之后的id  ->暗度陈仓
        }

        if (length > 0) {
            ArrayList<HashMap<String, Object>> list = new ArrayList<>();
            String[] date = new String[length];
            String[] time = new String[length];
            String[] content = new String[length];
            for (int i = 0; i < eventList.size(); i++) {
                date[i] = eventList.get(i).getEndDate();
                try {
                    time[i] = DateUtils.getDateDiff(eventList.get(i).getEndDate());
                    if (time[i].contains("-")) {
                        time[i] = motto.get(new Random().nextInt(motto.size()));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                content[i] = eventList.get(i).getContent();
            }
            for (int i = 0; i < eventList.size(); i++) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("date", date[i]);
                map.put("time", time[i]);
                map.put("content", content[i]);
                list.add(map);
            }

            SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.event_item,
                    new String[]{"date", "time", "content"},
                    new int[]{R.id.current_date_view, R.id.diff_time_view, R.id.content_view});
            listView.setAdapter(adapter);
        }
    }
}