package com.lfx.upms.mapper.base;

import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
public interface BaseMapper<R, E, ID> {
    /**
     * 根据过滤条件,查询数据总条数
     *
     * @param example 过滤条件对象
     * @return 数据总条数
     */
    long countByExample(E example);

    /**
     * 删除所有符合过滤条件的数据
     *
     * @param example 过滤条件对象
     * @return 删除数据总条数
     */
    int deleteByExample(E example);

    /**
     * 根据主键，删除数据。
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteByPrimaryKey(ID id);

    /**
     * 插入一条数据(可插入值为null的字段)
     *
     * @param record 待插入数据对象
     * @return 影响行数
     */
    int insert(R record);

    /**
     * 插入一条数据(不插入值为null的字段)
     *
     * @param record 待插入数据对象
     * @return 影响行数
     */
    int insertSelective(R record);

    /**
     * 查询所有符合过滤条件的数据
     *
     * @param example 过滤条件对象
     * @return 数据结果集合
     */
    List<R> selectByExample(E example);

    /**
     * 根据主键，查询一条数据
     *
     * @param id 主键
     * @return 单条数据结果
     */
    R selectByPrimaryKey(ID id);

    /**
     * 根据主键，更新一条数据(不更新值为null的字段)
     *
     * @param record 待更新数据对象
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(R record);

    /**
     * 根据主键，更新一条数据(可更新值为null的字段)
     *
     * @param record 待更新数据对象
     * @return 影响行数
     */
    int updateByPrimaryKey(R record);
}
