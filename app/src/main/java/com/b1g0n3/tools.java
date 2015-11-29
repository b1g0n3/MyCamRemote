package com.b1g0n3;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by B1g0n3 on 29/11/2015.
 */
public class tools {


    public static void out(String Trace) {
        String path="/ReconApps/MyCamRemote/";
        String outFile="Mydebug";
        boolean ForceToFile=true;
        System.out.println(Trace);
        File file = new File(Environment.getExternalStorageDirectory()+path + outFile);
        if (file.exists() == true) {
            try {
                FileWriter fos = new FileWriter(Environment.getExternalStorageDirectory()+path + outFile,true);
                fos.write(Trace+"\n");
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
