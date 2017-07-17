package file;

import java.io.*;

/**
 * Created by Administrator on 2017/7/10.
 */
public class FileUtils {
    public static void createTempFile(String path, String name, InputStream is) {
        try {
            File file = new File(path + name + ".tmp");
            //FileOutputStream fos = new FileOutputStream(file);
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] bytes = new byte[1024];

            //断点续传
            long startIndex = 0;
            if (file.exists()) {
                startIndex = file.length();
            }

            bis.skip(startIndex);
            raf.seek(startIndex);

            int len = 0;
            while((len = bis.read(bytes)) != -1) {
                raf.write(bytes, 0, len);
            }
            //bis.flush();

            rsaf.close();
            bis.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void mergeTempFiles(String path, String name, long length, int count) {
        for (int i = 0; i < count; i++) {
            File file = new File(path + name + i + ".tmp");
            File aimFile = new File(path + name);
            try {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                RandomAccessFile raf = new RandomAccessFile(aimFile, "rw");

                long every = length / count;
                raf.seek(i * every);
                byte[] bytes = new byte[bis.available()];

                bis.read(bytes);
                raf.write(bytes);

                bis.close();
                raf.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //删除临时文件
    public static void delTempFile(String path, String name, int count) {
        for (int i = 0; i < count; i++) {
            File file = new File(path + name + i + ".tmp");
            file.delete();
        }
    }
}
