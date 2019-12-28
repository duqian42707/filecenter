package com.dqv5.filecenter.repository;

import com.dqv5.filecenter.entity.FilecenterInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author duqian
 * @date 2019-08-20
 */
public interface FilecenterInfoRepository extends JpaRepository<FilecenterInfo, String>, JpaSpecificationExecutor<FilecenterInfo> {
    /**
     * 查询所有文件大小之和
     *
     * @return
     */
    @Query("select sum(t.length) from FilecenterInfo t")
    Long sumTotalSize();

    List<FilecenterInfo> findByRemark(String remark);

    List<FilecenterInfo> findByStoreType(String storeType);

    List<FilecenterInfo> findByMd5(String md5);

    List<FilecenterInfo> findByRemarkStartsWith(String remark);

}
