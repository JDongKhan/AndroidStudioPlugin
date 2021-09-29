package com.jd.dart.action;


import com.google.gson.Gson;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.jd.dart.utils.FileGenerator;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class JDCreateDartModelFromJsonAction extends AnAction {

    private String basePath = "";
    String outFilePath = null ;
    Project project;
    @Override
    public void actionPerformed(AnActionEvent e) {
        project = e.getProject();
        if (project == null ) {
            return;
        }
        basePath = project.getBasePath();
        outFilePath = basePath + "/lib" + "/Models" ;
        String jsonFilesDir = basePath + "/" + "JsonTemplate";


        //创建目录和清理目录
        File dir = new File(outFilePath);
        if (!dir.exists()) {
            dir.mkdirs();
        } else {
            dir.delete();
            dir.mkdirs();
        }

        //先刷新一下
        e.getProject().getBaseDir().refresh(false,true);

        File dir1 = new File(jsonFilesDir);
        if (!dir1.exists()) {
            dir1.mkdirs();
        }

        //处理Json文件，创建对应的类
        e.getProject().getBaseDir().refresh(false,true);
        handleJsonFiles(jsonFilesDir);
        e.getProject().getBaseDir().refresh(false,true);
    }

    private void handleJsonFiles(String jsonFilesDir){
        File dir = new File(jsonFilesDir);
        File[] jsonFiles  = dir.listFiles();
        if (jsonFiles.length == 0) {
            Messages.showInfoMessage(project, "Json模板为空", "提示");
            return;
        }
        for (File file : jsonFiles) {
            try {
                InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
                BufferedReader bufferedReader = new BufferedReader(reader);
                String lineText = null;
                StringBuffer contentBuffer = new StringBuffer();
                while ((lineText = bufferedReader.readLine()) != null) {
                    contentBuffer.append(lineText);
                }
                reader.close();

                String templates = FileGenerator.readContentFormTemplates("/templates/jsonmodel.dart");


                String jsonString = contentBuffer.toString();
                Gson gson = new Gson();
                Map<String,Object> map = gson.fromJson(jsonString, HashMap.class);

                String fileName = file.getName();

                generatorClass(templates,fileName,map);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void generatorClass(String templates,String fileName,Map<String,Object> map) throws IOException {
        String newTemplates = templates;
        fileName = fileName.replace(".json","");

        System.out.println("["+fileName+"]：创建");
        String className = FileGenerator.generatorClassName(fileName);
        templates = templates.replaceAll("@className",className);

        StringBuffer importConfig = new StringBuffer();
        StringBuffer vars = new StringBuffer();
        StringBuffer attribute = new StringBuffer();
        StringBuffer json2var = new StringBuffer();
        StringBuffer var2json = new StringBuffer();

        for (Map.Entry<String,Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();
            if (value instanceof String) {
                vars.append("\tString " + key+ ";");
                json2var.append("\t  " + key + " = json['" + key + "'];");
                json2var.append("\n");

                var2json.append("\t  data['" + key + "'] = " + key + ";");
                var2json.append("\n");
            } else if (value instanceof Integer) {
                vars.append("\tInteger " + key + ";");
                json2var.append("\t  " + key + " = json['" + key + "'];");
                json2var.append("\n");

                var2json.append("\t  data['" + key + "'] = " + key + ";");
                var2json.append("\n");
            } else if (value instanceof Boolean) {
                vars.append("\tbool " + key + ";");
                json2var.append("\t  " + key + " = json['" + key + "'];");
                json2var.append("\n");

                var2json.append("\t  data['" + key + "'] = " + key + ";");
                var2json.append("\n");
            } else if (value instanceof Double) {
                vars.append("\tdouble " + key + ";");
                json2var.append("\t  " + key + " = json['" + key + "'];");
                json2var.append("\n");

                var2json.append("\t  data['" + key + "'] = " + key + ";");
                var2json.append("\n");
            } else if (value instanceof Map) {
                String keyClassName = FileGenerator.generatorClassName(key);
                vars.append("\t" + keyClassName + " "+ key + ";");
                importConfig.append("import '"+key+".dart';");
                importConfig.append("\n");

                json2var.append("\t  " + key + " = " + keyClassName+".fromJson(json['" + key + "']);");
                json2var.append("\n");

                var2json.append("\t  data['" + key + "'] = " + key + ".toJson();");
                var2json.append("\n");

                //生成依赖的类
                generatorClass(newTemplates,key, (Map<String, Object>) value);
            } else if (value instanceof List) {
                String keyClassName = FileGenerator.generatorClassName(key);
                vars.append("\tList<" + keyClassName + "> "+ key + ";");

                importConfig.append("import '"+key+".dart';");
                importConfig.append("\n");

//                json2var.append("if (data['"+key+"'] != null) {");
                json2var.append("\t  "+ key + " = [];");
                json2var.append("\n");
                json2var.append("\t  json['"+key+"']?.forEach((v) {");
                json2var.append("\n");
                json2var.append("\t\t  "+key+".add("+keyClassName+".fromJson(v));");
                json2var.append("\n");
                json2var.append("\t  });");
//                json2var.append("}");
                json2var.append("\n");

                var2json.append("\t  data['" + key + "'] = " + key +"?.map((v) => v.toJson())?.toList();");
                var2json.append("\n");
                List<Map<String,Object>> list = (List<Map<String, Object>>) value;
                //生成依赖的类
                if (list.size() > 0) {
                    generatorClass(newTemplates, key, list.get(0));
                }
            }
            vars.append("\n");

            attribute.append("\tthis."+ key + ", ");
            attribute.append("\n");

        }
        templates = templates.replaceAll("@import",importConfig.toString());
        templates = templates.replaceAll("@vars",vars.toString());
        templates = templates.replaceAll("@attribute",attribute.substring(0,attribute.length()-1).toString());
        templates = templates.replaceAll("@json2var",json2var.substring(0,json2var.length()-1).toString());
        templates = templates.replaceAll("@var2json",var2json.substring(0,var2json.length()-1).toString());

        //生成类文件
        String filePath  = outFilePath+ "/" + fileName + ".dart";
        FileGenerator.writeContent(filePath,templates);
        System.out.println("["+fileName+"]：创建成功");
    }

}
