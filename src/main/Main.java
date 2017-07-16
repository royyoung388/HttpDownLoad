package main;

import http.DownLoadManager;
import http.HttpLength;

/**
 * Created by Administrator on 2017/7/10.
 */
public class Main {
    public static void main(String[] args) {
//        String spec = "https://bitbucket.org/royyoung/exclusivetrace/get/3ea587953077.zip";
        String spec = "http://osplxqyfq.bkt.clouddn.com/static/images/test/test.png";
        String path = "F:\\安装文件\\";

        System.out.println("正在连接");
        HttpLength http = new HttpLength(spec);
        DownLoadManager downLoadManager = new DownLoadManager(spec, http.getLength(), 5, path, http.getName());
        downLoadManager.start();
    }
}
