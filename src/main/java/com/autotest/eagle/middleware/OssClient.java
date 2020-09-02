package com.autotest.eagle.middleware;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;

/**
 * @author wuranxu
 * @date 2020/9/2 6:36 下午
 */
@Getter
@Setter
@Component
@Slf4j
public class OssClient {

    @Value("${qiniu.AccessKey}")
    private String accessKey;

    @Value("${qiniu.SecretKey}")
    private String secretKey;

    @Value("${qiniu.bucket}")
    private String bucketName;

    private Auth auth;

    private UploadManager uploadManager;

    @PostConstruct
    public void init() {
        Configuration cfg = new Configuration(Region.huadong());
        //...其他参数参考类注释
        this.uploadManager = new UploadManager(cfg);
        this.auth = Auth.create(accessKey, secretKey);
    }

    public OssClient() {
    }

    public Boolean uploadFile(String filename, InputStream input) {
        try {
            String upToken = this.auth.uploadToken(bucketName);
            Response response = uploadManager.put(input, filename, upToken, null, null);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        } catch (QiniuException ex) {
            Response r = ex.response;
            log.error("上传文件出错, error: "+ r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
            return false;
        }
        return true;
    }
}
