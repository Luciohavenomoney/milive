package com.lucio.milive.util;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.lucio.milive.adapter.ProgramModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * author: li xiao
 * created on: 2020/7/15
 */
public class JsonUtil {

    public static final String listFile = Environment.getExternalStorageDirectory() + File.separator + "milive" + File.separator + "m3u";

    public static String loadJson(Context context){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    context.getAssets().open("program.json")));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static List<ProgramModel> loadM3U(Context context){
        List<ProgramModel> models = new ArrayList<>();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    context.getAssets().open("programs.m3u")));
            String line;
            String name = null;
            while ((line = bf.readLine()) != null) {
                if(line.startsWith("#")){
                    //这里是Metadata信息
                    String[] info = line.split(",");
                    if(info.length>0){
                        name = info[info.length-1];
                    }else {
                        name = "未知频道";
                    }
                }else if(line.length() > 0){
                    //这里是一个指向的音频流路径
                    models.add(new ProgramModel(line,name,""));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return models;
    }

    public static List<ProgramModel> readM3U(String filename){
        List<ProgramModel> models = new ArrayList<>();
        try {
            //打开文件输入流
            FileInputStream input = new FileInputStream(new File(filename));
            BufferedReader bf = new BufferedReader(new InputStreamReader(input));
            String line;
            String name = null;
            while ((line = bf.readLine()) != null) {
                if(line.startsWith("#")){
                    //这里是Metadata信息
                    String[] info = line.split(",");
                    if(info.length>0){
                        name = info[info.length-1];
                    }else {
                        name = "未知频道";
                    }
                }else if(line.length() > 0){
                    //这里是一个指向的音频流路径
                    models.add(new ProgramModel(line,name,""));
                }
            }
            Log.e("List",models.size()+"");
            //关闭输入流
            input.close();
        } catch (Exception e) {
            Log.e("Exception",e.toString());
            e.printStackTrace();
        }
        return models;
    }

    /**
     * 获取指定目录内所有文件路径
     * @param dirPath 需要查询的文件目录
     * @param _type 查询类型，比如mp3什么的
     */
    public static List<ProgramModel> getAllFiles(String dirPath, String _type) {
        List<ProgramModel> channel = new ArrayList<>();
        File f = new File(dirPath);
        if (!f.exists()) {//判断路径是否存在
            return null;
        }
        File[] files = f.listFiles();
        if(files==null){//判断权限
            return null;
        }
        for (File _file : files) {//遍历目录
            if(_file.isFile() && _file.getName().endsWith(_type)){
                String _name=_file.getName();
                String filePath = _file.getAbsolutePath();//获取文件路径
                String fileName = _file.getName().substring(0,_name.length()-4);//获取文件名
                Log.d("LOGCAT","fileName:"+fileName);
                Log.d("LOGCAT","filePath:"+filePath);
                channel.add(new ProgramModel(filePath,fileName,""));
            }
        }
        return channel;
    }
}
