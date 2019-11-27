package com.erin.sun.system.controller;

import com.erin.sun.common.util.CheckFileSize;
import com.erin.sun.common.util.FastDFSClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author erin
 * @Title: FastDFSController
 * @ProjectName
 * @Description: TODO
 * @date 2019/6/17 15:14
 */
@RestController
@RequestMapping("/fdfs")
@Api(value = "/fdfs", description = "上传下载")
public class FastDFSController {

    @Autowired
    private FastDFSClient fdfsClient;

    /**
     * 文件上传
     * @param file
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "上传文件", notes = "选择文件上传")
    @ApiImplicitParam(name = "file",paramType= "File",value = "选择上上传的文件",required = true)
    @RequestMapping(value = "/upload",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON,headers = "content-type=multipart/form-data")
    public Map<String,Object> upload(MultipartFile file) throws Exception{
        Map<String,Object> result = new HashMap<>();
        if (file == null){
            result.put("status", false);
            result.put("code",500);
            result.put("msg", "请选择图片上传");
            return result;
        }
        try {
            boolean ifsize = CheckFileSize.check(file,10240,"M");
            if(ifsize){
                String url = fdfsClient.uploadFile(file);
                result.put("code", 200);
                result.put("msg", "上传成功");
                result.put("filename",file.getOriginalFilename());
                String extname = file.getOriginalFilename().split("\\.")[1];
                result.put("extname",extname);
                if (!"jpg".equals(extname) && !"jpeg".equals(extname) && !"png".equals(extname)){
                    result.put("url", url + "?attname=" + file.getOriginalFilename());
                }else {
                    result.put("url", url);
                }
            }else {
                result.put("code", 500);
                result.put("msg", "允许上传最大为10G！");
            }
            return result;
        } catch (IOException e) {
        e.printStackTrace();
        result.put("status", false);
        result.put("code",500);
        result.put("msg", "系统错误");
        return result;
      }
    }

    /**
     * 文件下载
     * @param fileUrl  url 开头从组名开始
     * @param response
     * @throws Exception
     */
    @ApiOperation(value = "下载文件", notes = "根据url下载文件")
    @ApiImplicitParams({@ApiImplicitParam(name = "fileUrl",value = "文件路径")})
    @RequestMapping(value = "/download",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON)
    public void  download(String fileUrl, HttpServletResponse response) throws Exception{

        byte[] data = fdfsClient.download(fileUrl);

        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("test.7z", "UTF-8"));

        // 写出
        ServletOutputStream outputStream = response.getOutputStream();
        IOUtils.write(data, outputStream);
    }

}
