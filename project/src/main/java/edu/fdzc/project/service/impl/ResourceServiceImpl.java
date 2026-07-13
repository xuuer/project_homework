package edu.fdzc.project.service.impl;

import com.alibaba.excel.util.IoUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.fdzc.project.config.MinioConfig;
import edu.fdzc.project.entity.HomeworkAttachment;
import edu.fdzc.project.entity.Resource;
import edu.fdzc.project.entity.vo.PageVo;
import edu.fdzc.project.mapper.HomeworkAttachmentMapper;
import edu.fdzc.project.service.ResourceService;
import edu.fdzc.project.mapper.ResourceMapper;
import edu.fdzc.project.utils.EncodingUtil;
import edu.fdzc.project.utils.JwtUtil;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
* @author zhangjiu
* &#064;description  针对表【resource】的数据库操作Service实现
* @createDate 2025-06-21 17:10:34
*/
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService{

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private HomeworkAttachmentMapper homeworkAttachmentMapper;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${kkFileViewServer}")
    private String kkfileviewUrl;

    @Autowired
    private MinioClient minioClient;

    @Override
    public Resource selectById(Long id) {
        return resourceMapper.selectById(id);
    }

    @Override
    public PageVo<Resource> selectPage(Long pageNum, Long pageSize, Resource resource) {
        Long teacherId = JwtUtil.getUserId();
        String name = resource.getName();
        Long categoryId = resource.getCategoryId();

        //查询方式
        LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resource::getTeacherId, teacherId); // 查找老师名下的所有资源
        if(name != null && !name.isEmpty())
        {
            wrapper.like(Resource::getName, name); //模糊查询
        }
        if(categoryId != null)
        {
            wrapper.eq(Resource::getCategoryId, categoryId); // 分类的id
        }
        wrapper.orderByAsc(Resource::getCreateTime); // 根据上传文件的时间排序

        //执行
        Page<Resource> res = resourceMapper.selectPage(new Page<>(pageNum, pageSize), wrapper); //分页查询
        PageVo<Resource> pageVo = new PageVo<>();
        pageVo.setTotal(res.getTotal()); //设置条数
        pageVo.setList(res.getRecords()); // 设置查询列表
        return pageVo;
    }

    @Override
    public Resource upload(MultipartFile file) {
        if(file == null)
        {
            return null;
        }
        String originalFilename = file.getOriginalFilename();//获取原始文件名
        String newFilename = UUID.randomUUID().toString() + "_" + originalFilename; //新文件名称
        Resource resource = new Resource();
        resource.setName(originalFilename);  //设置原始文件名
        resource.setUrl(newFilename); // 设置的新文件的名称

        try {
            InputStream in = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(newFilename) //上传到minio 中的是获取的uuid 中的文件名称
                            .stream(in,file.getSize(),-1)
                            .contentType(file.getContentType())
                            .build()
            );
            in.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(resource);
        return resource; //返回文件的详细信息
    }

    @Override
    public Integer insert(Resource resource) {
        Long teacherId = JwtUtil.getUserId();
        resource.setTeacherId(teacherId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createTime = LocalDateTime.now().format(formatter);

        resource.setCreateTime(createTime); //设置创建时间
        //添加资源
        return resourceMapper.insert(resource);
    }

    @Override
    public Integer updateByResId(Resource resource) {
//        return 0;
        Long teacherId = JwtUtil.getUserId();
        resource.setTeacherId(teacherId);

        //设置时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String updateTime = LocalDateTime.now().format(formatter);

        resource.setUpdateTime(updateTime);// 设置更新时间

        return resourceMapper.updateById(resource);
    }

    @Override
    public void download(HttpServletResponse response, Long id) {
        //Resource resource = resourceMapper.selectById(id);
        //String fileName = resource.getUrl();
        //String originalFilename = resource.getName();
        HomeworkAttachment ment = homeworkAttachmentMapper.selectById(id);
        if (ment ==null){
            return;
        }
        String fileName = ment.getFileUrl();
        String originalFilename =ment.getFileName();
        InputStream in = null;

        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder().bucket(bucketName).object(fileName).build());
            response.setContentType( stat.contentType() + "; charset=utf-8");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(originalFilename, StandardCharsets.UTF_8));

            //文件下载
            in = minioClient.getObject(
                    GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
            IoUtils.copy(in,response.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if(in != null)
            {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public String getPreviewUrl(Long id) {
        Resource resource = resourceMapper.selectById(id);
        String url = null;
        if(resource != null)
        {
            String filename = resource.getUrl();
            try {
                url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs
                        .builder()
                        .bucket(bucketName)
                        .object(filename)
                        .method(Method.GET)
                        .build());
                System.out.println(url);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            try {
                url = EncodingUtil.base64AndUrlEncode(url);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            url = kkfileviewUrl + url;
        }
        return url; //返回这个kkfileview url实现文件的预览
    }

    @Override
    public Integer deleteById(Long id) {
        //resourceId
        return resourceMapper.deleteById(id); //删除文件
    }
}




