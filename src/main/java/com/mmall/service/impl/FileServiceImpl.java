package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

@Service("iFileService")
public class FileServiceImpl implements IFileService {
    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    public String upload(MultipartFile file,String path){
        String fileName = file.getOriginalFilename();
        // 扩展名 go.jpg
        String fileExtName = fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtName;
        logger.info("开始上传文件，文件名{}, 上传的路径{},新文件名{}",fileName,path,uploadFileName);
        // 如果文件夹不存在的话就创造文件夹
        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }

        File targetFile = new File(path,uploadFileName);
        try{
            // 上传文件到upload文件夹
            file.transferTo(targetFile);
            // 将文件传到FTP
            FTPUtil.uploadFile(Lists.<File>newArrayList(targetFile));
            // 将upload下的临时文件清空
            boolean deleteResult = targetFile.delete();
            System.out.println("删除临时文件结果:"+deleteResult);

        }catch (IOException e){
            logger.error("上传文件失败",e);
            return null;
        }
        return targetFile.getName();
    }
}
