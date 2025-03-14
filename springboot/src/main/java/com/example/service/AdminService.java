package com.example.service;

import com.example.entity.Admin;
import com.example.exception.CustomerException;
import com.example.mapper.AdminMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    private final AdminMapper adminMapper;

    public AdminService(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    public String admin(String name) {
        if ("admin" .equals(name)) {
            return "admin";
        } else{
            throw new CustomerException("账号错误");
        }
    }

    public List<Admin> selectAll() {
        return adminMapper.selectAll();
    }

    public PageInfo<Admin> selectPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Admin> List = adminMapper.selectAll();
        return PageInfo.of(List);
    }
}

