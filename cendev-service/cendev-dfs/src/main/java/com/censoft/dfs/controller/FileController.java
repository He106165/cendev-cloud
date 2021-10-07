package com.censoft.dfs.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.censoft.common.core.domain.R;
import com.censoft.common.utils.StringUtils;
import com.censoft.common.utils.file.FileUploadUtils;
import com.censoft.common.utils.file.FileUtils;
import com.censoft.dfs.config.DfsConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * 通用请求处理
 *
 * @author censoft
 */
@Slf4j
@RestController
public class FileController
{
    @Autowired
    private DfsConfig dfsConfig;

    //生产环境建议用nginx绑定域名映射
    @Value("${dfs.domain}")
    private String domain;
    /**
     * 通用下载请求
     *
     * @param fileName 文件名称
     * @param delete 是否删除
     */
    @GetMapping("download")
    public void download(String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request)
    {
        try
        {
            if (!FileUtils.isValidFilename(fileName))
            {
                throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", fileName));
            }
            String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
            String filePath = dfsConfig.getPath() + fileName;
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition",
                    "attachment;fileName=" + FileUtils.setFileDownloadHeader(request, realFileName));
            FileUtils.writeBytes(filePath, response.getOutputStream());
            if (delete)
            {
                FileUtils.deleteFile(filePath);
            }
        }
        catch (Exception e)
        {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 通用上传请求
     */
    @PostMapping("upload")
    @CrossOrigin
    public R upload(@RequestParam("file") MultipartFile file) throws Exception
    {
        try
        {
            // 上传文件路径
            String filePath = dfsConfig.getPath();
            // 上传并返回新文件名称
            System.out.println("文件位置   "+filePath);
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = "http://register.censoft.com.cn/api/dfs/" + fileName.replace("/upload","");
            return R.ok().put("fileName", fileName).put("url", url);
        }
        catch (Exception e)
        {
            log.error("上传文件失败", e);
            return R.error(e.getMessage());
        }
    }
}
