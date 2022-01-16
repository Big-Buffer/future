package com.future.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;

import com.future.bean.Event;
import com.future.future.R;
import com.future.util.DBUtils;
import com.future.util.DateUtils;
import com.future.util.DialogUtils;


import java.text.ParseException;

/**
 * @author ：shenmegui
 * @date ：Created in 2021/12/19 22:07
 */
public class EditFragment extends Fragment implements View.OnClickListener {

    private DBUtils dbUtils;
    private int id;
    private Uri uri;
    private String date = "";
    private String time = "";
    private String music_name = "";
    private String music_path = "";
    private Boolean advance;
    private EditText text_content;
    private TextView view_date;
    private TextView view_time;
    private TextView view_music;
    private CheckBox button_alarm_in_advance;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit, null);
        dbUtils = new DBUtils(this.getContext());
        id = getArguments().getInt("id");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Event event = dbUtils.selectById(id);
        SharedPreferences preferences = getActivity().getSharedPreferences(String.valueOf(id), Context.MODE_PRIVATE);
        date = event.getEndDate().split(" ")[0];
        time = event.getEndDate().split(" ")[1];
        music_name = preferences.getString("name", null);
        music_path = preferences.getString("path", null);
        advance = preferences.getBoolean("advance", true);
        text_content = getActivity().findViewById(R.id.text_content);
        view_date = getActivity().findViewById(R.id.text_view_date);
        view_time = getActivity().findViewById(R.id.text_view_time);
        view_music = getActivity().findViewById(R.id.text_view_music);
        button_alarm_in_advance = getActivity().findViewById(R.id.button_alarm_in_advance);
        text_content.setText(event.getContent());
        view_date.setText(date);
        view_time.setText(time);
        view_music.setText(music_name);
        button_alarm_in_advance.setChecked(advance);

        ImageButton button_back = getActivity().findViewById(R.id.button_back);
        ImageButton button_commit = getActivity().findViewById(R.id.button_commit);
        ImageButton button_select_date = getActivity().findViewById(R.id.button_select_date);
        ImageButton button_select_alarm_time = getActivity().findViewById(R.id.button_select_alarm_time);
        ImageButton button_select_music = getActivity().findViewById(R.id.button_select_music);
        button_back.setOnClickListener(this);
        button_commit.setOnClickListener(this);
        button_select_date.setOnClickListener(this);
        button_select_alarm_time.setOnClickListener(this);
        button_select_music.setOnClickListener(this);
        button_alarm_in_advance.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_back: {
                Log.e("back====>","click");
                DialogUtils.setDialog(this.getContext(), "是否取消编辑", (dialog, which) -> getFragmentManager().popBackStack());
                break;
            }
            case R.id.button_commit: {
                String endDate = date + " " + time;
                String content = text_content.getText().toString();
                if (!"".equals(content) && !"".equals(date) && !"".equals(time) && !"".equals(music_name)) {
                    try {
                        if (DateUtils.getDateDiff(endDate).contains("-")) {
                            Toast.makeText(getActivity(), "花有重开日，人物在少年\n该时间已过,请重选时间", Toast.LENGTH_SHORT).show();
                        } else {
                            DialogUtils.setDialog(this.getContext(), "是否保存编辑？", (dialog, which) -> {
                                Event event = new Event();
                                event.setId(id);
                                event.setEndDate(endDate);
                                event.setContent(content);
                                dbUtils.update(event, id);
                                // sharedPreferences保存音乐以及是否提前半小时提醒
                                Log.e("advance====>",String.valueOf(advance));
                                SharedPreferences preferences = getActivity().getSharedPreferences(String.valueOf(id), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("name",music_name);
                                editor.putString("path", music_path);
                                editor.putBoolean("advance", advance);
                                editor.apply();
                                getFragmentManager().popBackStack();
                            });
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if ("".equals(content)) {
                    Toast.makeText(getActivity(), "请填写事件内容", Toast.LENGTH_SHORT).show();
                } else if ("".equals(date)) {
                    Toast.makeText(getContext(), "请选择日期", Toast.LENGTH_SHORT).show();
                } else if ("".equals(time)) {
                    Toast.makeText(getActivity(), "请选择时间", Toast.LENGTH_SHORT).show();
                } else if ("".equals(music_name)) {
                    Toast.makeText(getActivity(), "请选择音乐", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "未知错误", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case R.id.button_select_date: {
                DialogUtils.setDateDialog(this.getContext(), (view, year, month, dayOfMonth) -> {
                    Log.e("date====>", "你选择了" + year + "年" +
                            (month + 1) + "月" + dayOfMonth + "日");
                    date = year + "-" + (month + 1) + "-" + dayOfMonth;
                    view_date.setText(date);
                });
                break;
            }

            case R.id.button_select_alarm_time: {
                DialogUtils.setTimeDialog(this.getContext(), (view, hourOfDay, minute) -> {
                    Log.e("time====>", "你选择了" + hourOfDay + "时" +
                            minute + "分");
                    time = hourOfDay + ":" + minute;
                    view_time.setText(time);
                });
                break;
            }

            case R.id.button_select_music: {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
                break;
            }
            case R.id.button_alarm_in_advance:{
                advance = !advance;
                Log.e("advance====>",String.valueOf(advance));
                break;
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
            if (requestCode == 1) {
                uri = data.getData();
                music_name = getFileRealNameFromUri(this.getContext(), uri);
                music_path = uri.getPath();
                view_music.setText(music_name);
                Log.e("文件","文件路径："+uri.getPath()+"\n文件名称："+getFileRealNameFromUri(this.getContext(), uri));
            }
        } else {
            Toast.makeText(getActivity(), "暂未选择音乐", Toast.LENGTH_SHORT).show();
        }
    }
    public static String getFileRealNameFromUri(Context context, Uri fileUri) {
        if (context == null || fileUri == null) return null;
        DocumentFile documentFile = DocumentFile.fromSingleUri(context, fileUri);
        if (documentFile == null) return null;
        return documentFile.getName();
    }
}
