package com.amiablecore.warehouse.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class LocalStorageUtil {
    private static final String TAG = "LocalStorageUtil";
    public boolean checkCommoditiesCache(String[] list) {
        StringBuilder ch = new StringBuilder();
        try {
            File f = new File("commodities.txt");
            FileOutputStream fout = null;
            FileInputStream fin = null;
            Log.i("File Present :", String.valueOf(f.exists()));
            if (f.exists()) {
                fin = new FileInputStream(f);
                int content;

                while ((content = fin.read()) != -1) {
                    ch.append((char) content);
                }
                fin.close();
            } else {
                fout = new FileOutputStream(f);
                for (String s : list) {
                    fout.write(s.getBytes());
                    fout.write("\n".getBytes());
                }
                fout.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean checkTradersCache(String[] list) {
        StringBuilder ch = new StringBuilder();
        try {
            File f = new File("traders.txt");
            FileOutputStream fout = null;
            FileInputStream fin = null;
            Log.i("File Present :", String.valueOf(f.exists()));
            if (f.exists()) {
                fin = new FileInputStream(f);
                int content;

                while ((content = fin.read()) != -1) {
                    ch.append((char) content);
                }
                fin.close();
            } else {
                fout = new FileOutputStream(f);
                for (String s : list) {
                    fout.write(s.getBytes());
                    fout.write("\n".getBytes());
                }
                fout.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean checkCategoriesCache(String[] list) {
        StringBuilder ch = new StringBuilder();
        try {
            File f = new File("categories.txt");
            FileOutputStream fout = null;
            FileInputStream fin = null;
            Log.i("File Present :", String.valueOf(f.exists()));
            if (f.exists()) {
                fin = new FileInputStream(f);
                int content;

                while ((content = fin.read()) != -1) {
                    ch.append((char) content);
                }
                fin.close();
            } else {
                fout = new FileOutputStream(f);
                for (String s : list) {
                    fout.write(s.getBytes());
                    fout.write("\n".getBytes());
                }
                fout.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
