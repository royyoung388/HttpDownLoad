package http;

import file.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2017/7/10.
 */
public class DownLoadManager extends Thread{

    private String spec, path, name;
    private long length;
    private int count;

    private Timer timer;
    private TimerTask timerTask;

    public DownLoadManager(String spec, long length, int count, String path, String name) {
        this.spec = spec;
        this.length = length;
        this.count = count;
        this.path = path;
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println("正在下载");
        CountDownLatch countDownLatch = new CountDownLatch(count);
        //开始下载,创建临时文件
        long every = length / count;
        for (int i = 0; i < count; i++) {
            new DownLoadThread(path, name + i, every * i,
                    i == count - 1 ? length - 1 : every * (i + 1) - 1, countDownLatch).start();
        }

        //开启进度检查功能
        process(path, name, count, length);

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //整合文件
        FileUtils.mergeTempFiles(path, name, length, count);

        //删除临时文件
        FileUtils.delTempFile(path, name, count);
    }

    private void process(String path, String name, int count, long length) {
        timer = new Timer();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                double size = 0;
                for (int i = 0; i < count; i++) {
                    File file = new File(path + name + i + ".tmp");
                    if (file.exists()) {
                        size += file.length();
                    }
                }
                File file = new File(path + name);
                if (file.exists()) {
                    size = file.length();
                    if (size >= length) {
                        timer.cancel();
                        System.out.println("下载完成");
                        return;
                    }
                }
                System.out.println(size + "   " + length);
                System.out.println("下载进度:" + (size / length * 100) + "%");
            }
        };

        timer.schedule(timerTask, 0, 100);
    }

    class DownLoadThread extends Thread {

        private long begin, end;
        private String path, name;
        private CountDownLatch countDownLatch;

        public DownLoadThread(String path, String name, long begin, long end, CountDownLatch countDownLatch) {
            this.path = path;
            this.name = name;
            this.begin = begin;
            this.end = end;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(spec);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Range", "bytes=" + begin + "-" + end);
                conn.getDoInput();
                FileUtils.createTempFile(path, name, conn.getInputStream());
                conn.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
        }
    }
}
