package com.os.logger;

import android.app.Activity;
import android.database.Cursor;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.monitor.greendao.LocationEntity;
import com.monitor.greendao.LocationEntityDao;
import com.monitor.greendao.LogEntity;
import com.monitor.greendao.LogEntityDao;
import com.monitor.greendao.MediaEntity;
import com.monitor.greendao.MediaEntityDao;
import com.os.logger.track.CallTrack;
import com.os.logger.track.LocationTrack;
import com.os.logger.track.PictureTrack;
import com.os.logger.track.SmsTrack;
import com.os.logger.track.TestTrack;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Alan on 16/5/9.
 */
public class TestActivity extends Activity{

    private List<String> datas = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getData();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,datas);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (files!=null){
//                    play(files[position]);
//                }
            }
        });

    }

    private void getData(){
        datas.clear();
//        File dir = new File(FileHelper.getDir(this),"log");
//        files = dir.listFiles();
//        if (files == null){
//            return;
//        }
//        for (File file:files){
//            datas.add(file.getName() + "-" + file.length());
//        }
        MediaEntityDao dao = DBHelper.getInstance().getDaoSession().getMediaEntityDao();
        List<MediaEntity> list =  dao.queryBuilder()
                .orderDesc(MediaEntityDao.Properties.ModifyTime)
                .list();
        if (list!=null){
            for (MediaEntity entity:list){
                StringBuilder builder = new StringBuilder();
                Date date = new Date(entity.getModifyTime());
                DateFormat format = new SimpleDateFormat("MMdd-HH:mm");
                builder.append(format.format(date))
                        .append("-")
                        .append(entity.getName());
                datas.add(builder.toString());
            }
        }


    }


    public void clear(View view) {
        LogEntityDao dao = DBHelper.getInstance().getDaoSession().getLogEntityDao();
        dao.deleteAll();

        datas.clear();
        adapter.notifyDataSetChanged();
    }
}
