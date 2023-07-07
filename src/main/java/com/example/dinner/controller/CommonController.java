package com.example.dinner.controller;

import com.example.dinner.common.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${dish.path}")
    private String basePath;

    @PostMapping("upload")
    public R<String> update(MultipartFile file){

        String filename = file.getOriginalFilename();
        //截取 ".jpg"
        String s = filename.substring(filename.lastIndexOf("."));
        //设置文件名
        String jpgName = UUID.randomUUID() + s;

        File file1 = new File(basePath);
        if (!file1.exists()){
            file1.mkdirs();
        }

//        try {
//            file.transferTo(new File(basePath + jpgName));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        //IO流基础
//        try {
//            InputStream is = file.getInputStream();
//            BufferedInputStream bis = new BufferedInputStream(is);
//
//            FileOutputStream fos=new FileOutputStream(basePath + jpgName);
//            BufferedOutputStream bos = new BufferedOutputStream(fos);
//            byte[] buffer=new byte[1024];
//            int length;
//            while ((length = bis.read(buffer)) > 0) {
//                bos.write(buffer, 0, length);
//            }
//            bos.close();
//            fos.close();
//            bis.close();
//            is.close();
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        //IO流工具类
        try {
            InputStream inputStream = file.getInputStream();
            OutputStream outputStream= new FileOutputStream(basePath + jpgName);

            IOUtils.copy(inputStream,outputStream);

            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return R.success(jpgName);
    }

    @GetMapping("/download")
    public void download(HttpServletResponse response, String name){

            //IO流基础
//        try {
//            FileInputStream fis=new FileInputStream(new File(basePath + name));
//
//            ServletOutputStream sos = response.getOutputStream();
//            byte[] buffer=new byte[1024];
//            int len;
//            while((len=fis.read(buffer))>0){
//                sos.write(buffer,0,len);
//                sos.flush();
//            }
//            sos.close();
//            fis.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //IO流工具类
        try {
            FileInputStream fis=new FileInputStream(new File(basePath + name));
            ServletOutputStream sos = response.getOutputStream();

            IOUtils.copy(fis,sos);

            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(sos);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
