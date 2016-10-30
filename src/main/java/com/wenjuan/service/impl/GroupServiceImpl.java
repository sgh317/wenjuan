package com.wenjuan.service.impl;

import com.wenjuan.dao.GroupMapper;
import com.wenjuan.model.Group;
import com.wenjuan.service.GroupService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("groupService")
public class GroupServiceImpl implements GroupService {
    @Resource
    GroupMapper groupMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return groupMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(Group record) {
        return groupMapper.insert(record);
    }

    @Override
    public Group selectByPrimaryKey(Integer id) {
        return groupMapper.selectByPrimaryKey(id);
    }

    @Override
    public Group selectByName(String grpName) {
        return groupMapper.selectByName(grpName);
    }

    @Override
    public int updateByPrimaryKey(Group record) {
        return groupMapper.updateByPrimaryKey(record);
    }

    @Override
    public List<String> getExistsGroup() {
        return groupMapper.getExistsGroup();
    }

    @Override
    public int deleteGroup(String grp) {
        return groupMapper.deleteGroup(grp);
    }

    @Override
    public int isGroupExists(String grp) {
        return groupMapper.isGroupExists(grp);
    }

    @Override
    public List<Group> getExistsGroupInfo(boolean containUngroup) {
        List<Group> existsGroupInfo = groupMapper.getExistsGroupInfo();
        if (containUngroup && existsGroupInfo != null) {
            existsGroupInfo.add(0, Group.getUngroup());
        }
        return existsGroupInfo;
    }

    @Override
    public Group[] getMyGroup(int userId) {
        return groupMapper.getMyGroup(userId);
    }
}
