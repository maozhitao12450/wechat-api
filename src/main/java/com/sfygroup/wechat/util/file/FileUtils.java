package com.sfygroup.wechat.util.file;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;

@Slf4j
public class FileUtils {

    public void traverseFolder2(String path, FileHandler fileHandler) throws FileNotFoundException {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null == files || files.length == 0) {
                //空文件夹不做处理
                return;
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        traverseFolder2(file2.getAbsolutePath(), fileHandler);
                    } else {
                        try {
                            fileHandler.handlerFile(file2);
                        } catch (FileNotFoundException e) {
                            log.error("加载配置文件出错：" + file2.getAbsolutePath());
                            throw e;
                        }
                    }
                }
            }
        } else {
            log.error("文件不存在：" + file.getAbsolutePath());
            throw new FileNotFoundException(file.getAbsolutePath()) ;
        }
    }

}
