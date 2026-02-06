package com.graduation.graduationdesign.service;

import com.graduation.graduationdesign.entity.Address;

import java.util.List;

/**
 * 收货地址服务接口
 */
public interface AddressService {
    void addAddress(Address address, Long userId);
    List<Address> getAddresses(Long userId);
    Address getAddress(Long id, Long userId);
    void updateAddress(Address address, Long userId);
    void deleteAddress(Long id, Long userId);
    void setDefaultAddress(Long id, Long userId);
}
