package edu.fdzc.project.service;

import edu.fdzc.project.entity.Resource;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.fdzc.project.entity.vo.PageVo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
* @author zhangjiu
* @description 针对表【resource】的数据库操作Service
* @createDate 2025-06-21 17:10:34
*/
public interface ResourceService extends IService<Resource> {

    Resource selectById(Long id);

    PageVo<Resource> selectPage(Long pageNum, Long pageSize, Resource resource);

    Resource upload(MultipartFile file);

    Integer insert(Resource resource);

    Integer updateByResId(Resource resource);

    void download(HttpServletResponse response, Long id);

    String getPreviewUrl(Long id);

    Integer deleteById(Long id);
}
