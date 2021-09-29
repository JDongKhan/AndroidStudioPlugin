package com.jd.dart.utils;

import java.io.*;
import java.util.Locale;

public class FileGenerator {

    //生成类文件
    public static void generateClassFile(String fileName, String filePath,String templateFilePath) {
        try {
            String className = generatorClassName(fileName);
            String outFile = null;
            if (filePath.endsWith("/")) {
                outFile = filePath + fileName;
            }  else {
                outFile = filePath + "/" + fileName;
            }
            String templates = readContentFormTemplates(templateFilePath);
            templates = templates.replace("@className",className);
            writeContent(outFile,templates);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //读取模板内容
    public static  String readContentFormTemplates(String templatesFilePath) throws IOException {
        InputStream inputStream = FileGenerator.class.getResourceAsStream(templatesFilePath);
        int len;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while ((len = inputStream.read(buffer) ) != -1) {
            outputStream.write(buffer,0,len);
        }
        inputStream.close();
        outputStream.close();

        String content = new String(outputStream.toByteArray());
        return content;
    }

    //写类文件
    public static  void writeContent(String outFile,String content) throws IOException {
        File file = new File(outFile);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(content);
        bufferedWriter.close();
    }


    //处理类名
    public static  String generatorClassName(String str) {
        String[]  strs = str.split("_");
        if (strs.length == 1) {
            return toUpperCase(str);
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < strs.length; i ++) {
            String s = strs[i];
            stringBuffer.append(toUpperCase(s));
        }
        return stringBuffer.toString();
    }

    //大写
    private static   String toUpperCase(String str) {
        return str.substring(0, 1).toUpperCase(Locale.getDefault()) + str.substring(1);
    }


}
