package com.os.logger;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by Alan on 16/5/9.
 */
public class FileHelper {

    public static File getDir(Context context){
        return context.getFilesDir();
//        return Environment.getExternalStorageDirectory();
    }
}
