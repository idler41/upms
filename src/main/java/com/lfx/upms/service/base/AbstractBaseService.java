package com.lfx.upms.service.base;

import com.github.pagehelper.PageHelper;
import com.lfx.upms.mapper.base.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@Transactional(rollbackFor = Exception.class)
@Slf4j
public abstract class AbstractBaseService<Mapper extends BaseMapper<R, E, ID>, R, E, ID>
        implements BaseService<R, E, ID> {

    protected final Mapper mapper;

    public AbstractBaseService(Mapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public long countByExample(E example) {
        return mapper.countByExample(example);
    }

    @Override
    public int deleteByExample(E example){
        throw new UnsupportedOperationException();
    }

    @Override
    public int deleteByPrimaryKey(ID id) {
        return mapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(R record) {
        return mapper.insert(record);
    }

    @Override
    public int insertSelective(R record) {
        return mapper.insertSelective(record);
    }

    @Override
    public List<R> selectByExample(E example) {
        return mapper.selectByExample(example);
    }

    @Override
    public R selectByPrimaryKey(ID id) {
        return mapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(R record) {
        return mapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(R record) {
        return mapper.updateByPrimaryKey(record);
    }

    @Override
    public List<R> selectByPagination(int pageNum, int pageSize, E example) {
        List<R> result;
        PageHelper.startPage(pageNum, pageSize);
        try {
            result = mapper.selectByExample(example);
        } finally {
            PageHelper.clearPage();
        }
        return result;
    }
}
