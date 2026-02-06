package com.graduation.graduationdesign.service.impl;

import com.graduation.graduationdesign.entity.Address;
import com.graduation.graduationdesign.mapper.AddressMapper;
import com.graduation.graduationdesign.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 收货地址服务实现类
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public void addAddress(Address address, Long userId) {
        address.setUserId(userId);
        // 如果设置为默认地址，先取消其他默认地址
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressMapper.cancelAllDefault(userId);
        }
        addressMapper.insert(address);
    }

    @Override
    public List<Address> getAddresses(Long userId) {
        return addressMapper.findByUserId(userId);
    }

    @Override
    public Address getAddress(Long id, Long userId) {
        Address address = addressMapper.findById(id);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权限访问该地址");
        }
        return address;
    }

    @Override
    public void updateAddress(Address address, Long userId) {
        // 1. 查询地址是否存在
        Address existAddress = addressMapper.findById(address.getId());
        if (existAddress == null) {
            throw new RuntimeException("地址不存在");
        }
        // 2. 校验权限
        if (!existAddress.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改该地址");
        }
        // 3. 如果设置为默认地址，先取消其他默认地址
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressMapper.cancelAllDefault(userId);
        }
        // 4. 更新地址
        addressMapper.update(address);
    }

    @Override
    public void deleteAddress(Long id, Long userId) {
        // 1. 查询地址是否存在
        Address address = addressMapper.findById(id);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }
        // 2. 校验权限
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除该地址");
        }
        // 3. 删除地址
        addressMapper.delete(id);
    }

    @Override
    public void setDefaultAddress(Long id, Long userId) {
        // 1. 查询地址是否存在
        Address address = addressMapper.findById(id);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }
        // 2. 校验权限
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权限设置该地址");
        }
        // 3. 取消所有默认地址
        addressMapper.cancelAllDefault(userId);
        // 4. 设置当前地址为默认
        addressMapper.setDefault(id);
    }
}
