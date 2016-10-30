package com.wenjuan.dao;

import com.wenjuan.model.Group;

import java.util.List;

public interface GroupMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Group record);

    Group selectByPrimaryKey(Integer id);

    Group selectByName(String grpName);

    int updateByPrimaryKey(Group record);

    List<String> getExistsGroup();

    int deleteGroup(String grp);

    int isGroupExists(String grp);

    List<Group> getExistsGroupInfo();

    Group[] getMyGroup(int userId);
}