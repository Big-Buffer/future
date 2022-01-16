package com.future.ui.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.future.bean.Event;
import com.future.future.R;
import com.future.util.DBUtils;
import com.future.util.DateUtils;

import java.text.ParseException;

/**
 * @author ：shenmegui
 * @date ：Created in 2021/12/16 19:27
 */

// TODO：thread停不下
public class DetailFragment extends Fragment implements View.OnClickListener {

    private int id;
    private boolean stop;
    private DBUtils dbUtils;
    private TextView view_content;
    private TextView view_time;
    private Thread thread;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0:
//                    Log.e("detail====>","update");
                    updateView();
                    break;
                default:
                    Log.e("handler====>","error");
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        id = getArguments().getInt("id");
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ImageButton button_back = view.findViewById(R.id.button_back);
        ImageButton button_edit = view.findViewById(R.id.button_edit);
        ImageButton button_delete = view.findViewById(R.id.button_delete);
        button_back.setOnClickListener(this);
        button_edit.setOnClickListener(this);
        button_delete.setOnClickListener(this);
        return view;
    }

    @Override
    public void onDestroy() {
        Log.e("destroy====>","ok");
        super.onDestroy();
        stop = true;
        thread = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        stop = false;
        dbUtils = new DBUtils(this.getContext());
        view_content = getActivity().findViewById(R.id.view_content);
        view_time = getActivity().findViewById(R.id.view_time);
        // 线程刷新ui
        thread = new Thread(mBackgroundRunnable);
        thread.start();//创建一个HandlerThread并启动它
        updateView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_back:{
                stop = true;
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getFragmentManager().popBackStack();
                break;
            }
            case R.id.button_edit: {
                Bundle bundle = new Bundle();
                bundle.putInt("id",id);
                Fragment fragment = new EditFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                fragment.setArguments(bundle);
                transaction.addToBackStack(null);
                transaction.replace(R.id.layout_detail, fragment);
                transaction.commit();
                break;
            }
            case R.id.button_delete: {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("提示");
                builder.setMessage("确定删除该事件？");
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确定", (dialog, which) -> {
                    dbUtils.delete(id);
                    getFragmentManager().popBackStack();
                    Toast.makeText(v.getContext(), "删除成功", Toast.LENGTH_SHORT);
                });
                builder.create().show();
                break;
            }
        }
    }

    protected void updateView(){
        Event event = dbUtils.selectById(id);
        if (event!=null){
            view_content.setText(event.getContent());
            try {
                view_time.setText(DateUtils.getDateDiff(event.getEndDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    Runnable mBackgroundRunnable = new Runnable() {

        @Override
        public void run() {
            while (!stop) {
                try {
                    Thread.sleep(1000);
                    handler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
