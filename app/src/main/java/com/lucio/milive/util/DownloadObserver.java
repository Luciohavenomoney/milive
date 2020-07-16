package com.lucio.milive.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observer;
import okhttp3.ResponseBody;

public abstract class DownloadObserver implements Observer<File> {

    @Override
    public void onNext(File file) {
        onSuccess(file);
    }

    @Override
    public void onError(Throwable e) {
        onFail(e);
    }

    @Override
    public void onComplete() {

    }

    public abstract void onSuccess(File file);

    public abstract void onFail(Throwable throwable);

    public abstract void onProgress(int progress, long total);

    public File saveFile(ResponseBody responseBody, String destFileDir, String destFileName) throws IOException {
        InputStream is = null;
        byte[] bytes = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = responseBody.byteStream();
            final long total = responseBody.contentLength();
            long sum = 0;
            File dir = new File(destFileDir);
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, destFileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(bytes)) != -1) {
                sum += len;
                fos.write(bytes, 0, len);
                final long finalSum = sum;
                onProgress((int) (finalSum * 100 / total), total);
            }
            fos.close();
            return file;
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
