package com.github.houbb.mybatis.handler;

import com.github.houbb.heaven.util.lang.reflect.ClassUtil;
import com.github.houbb.heaven.util.lang.reflect.ReflectFieldUtil;
import com.github.houbb.mybatis.exception.MybatisException;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author binbin.hou
 * @since 0.0.1
 */
public class ResultHandler {

    /**
     * 结果类型
     * @since 0.0.1
     */
    private final Class resultType;

    public ResultHandler(Class resultType) {
        this.resultType = resultType;
    }

    /**
     * 构建结果
     * @param resultSet 结果集合
     * @return 结果
     * @since 0.0.1
     */
    public Object buildResult(final ResultSet resultSet) {
        try {
            // 基本类型，非 java 对象，直接返回即可。

            // 可以进行抽象
            Object instance = resultType.newInstance();

            // 结果大小的判断
            // 为空直接返回，大于1则报错
            if(resultSet.next()) {
                List<Field> fieldList = ClassUtil.getAllFieldList(resultType);

                for(Field field : fieldList) {
                    Object value = getResult(field, resultSet);

                    ReflectFieldUtil.setValue(field, instance, value);
                }

                // 返回设置值后的结果
                return instance;
            }

            return null;
        } catch (InstantiationException | IllegalAccessException | SQLException e) {
            throw new MybatisException(e);
        }
    }

    /**
     * 获取对应的结果
     * @param field 子弹
     * @param rs 结果集
     * @return 结果
     * @since 0.0.1
     */
    public Object getResult(Field field, ResultSet rs) {
        try {
            Class<?> type = field.getType();
            if (Integer.class == type) {
                return rs.getInt(field.getName());
            } else if (String.class == type) {
                return rs.getString(field.getName());
            } else if (Long.class == type) {
                return rs.getLong(field.getName());
            } else {
                //TODO: 添加特定的类型处理器
                return rs.getString(field.getName());
            }
        } catch (SQLException throwables) {
            throw new MybatisException(throwables);
        }
    }

}
