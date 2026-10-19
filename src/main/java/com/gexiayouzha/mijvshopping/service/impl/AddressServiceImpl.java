package com.gexiayouzha.mijvshopping.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gexiayouzha.mijvshopping.DTO.AddressReturnDTO;
import com.gexiayouzha.mijvshopping.DTO.Result;
import com.gexiayouzha.mijvshopping.entity.Address;
import com.gexiayouzha.mijvshopping.mapper.AddressMapper;
import com.gexiayouzha.mijvshopping.service.IAddressService;
import com.gexiayouzha.mijvshopping.utils.UserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {
    @Override
    public Result addAddress(Address address) {
        Long userId = UserHolder.getUser().getUserId();
        //我用userid=2的用户登陆, 获得了token, 然后用这个token去访问了
        //新增地址的接口, 导致了数据库里存的address表里的user_id是2,即使
        address.setUserId(userId);
        if(address.getIsDefault()==null){
            address.setIsDefault(0);
        }
        if(address.getIsDefault()==1){
            clearDefault(userId);
        }
        save(address);
        AddressReturnDTO addressReturnDTO = BeanUtil.copyProperties(address, AddressReturnDTO.class);
        return Result.success(200, "添加成功", addressReturnDTO);
    }

    @Override
    public Result updateAddress(Long addressId, AddressReturnDTO addressDTO) {
        //获取数据库的原地址
        Address addressExist = getById(addressId);
        if(addressExist==null){
            return Result.fail(404, "地址不存在");
        }
        //保证默认地址只有一个
        Long userId = UserHolder.getUser().getUserId();
        if(addressDTO.getIsDefault()==null){
            addressDTO.setIsDefault(0);
        }
        if(addressDTO.getIsDefault()==1){
            clearDefault(userId);
        }
        //新地址类型转换
        BeanUtil.copyProperties(addressDTO, addressExist, CopyOptions.create().setIgnoreNullValue(true));
        updateById(addressExist);
        return Result.success(200, "更新成功", addressDTO);
    }

    @Override
    public Result delete(Long addressId) {
        Address addressExist = getById(addressId);
        Long userId = UserHolder.getUser().getUserId();
        //防止删除别人的地址

        //TODO如果删除了默认地址怎么办
        if(addressExist==null){
            return Result.fail(404,"地址不存在");
        }
        if(!userId.equals(addressExist.getUserId())){
            return Result.fail(403,"权限不足");
        }

        removeById(addressId);
        return Result.success(200,"地址删除成功", null);
    }

    @Override
    public Result listAddresses() {
        Long userId = UserHolder.getUser().getUserId();
        List<AddressReturnDTO> addressReturnDTOList = query()
                .eq("user_id", userId)
                .list()
                .stream()
                .map(address -> BeanUtil.copyProperties(address, AddressReturnDTO.class))
                .toList();

        return Result.success(200, "获取成功", addressReturnDTOList);
    }

    @Override
    public Result setDefaultAddress(Long addressId) {
        //1.校验地址是否存在
        Address address = getById(addressId);
        if(address == null){
            return Result.fail(404, "地址不存在");
        }
        //2.校验归属
        Long userId = UserHolder.getUser().getUserId();
        if(!userId.equals(address.getUserId())){
            return Result.fail(403, "无权操作此地址");
        }
        //3.先把当前用户所有地址的 is_default 置 0
        clearDefault(userId);
        //4.再把目标地址置 1
        address.setIsDefault(1);
        updateById(address);
        return Result.success(200, "设置成功", null);
    }


    private void clearDefault(Long userId){
        //两个eq如果没找到, 不会报错, 不用try-catch
        //一个user会有多个地址
        UpdateWrapper<Address> updateWrapper= new UpdateWrapper<>();
        updateWrapper.eq("user_id",userId)
                    .eq("is_default",1)
                    .set("is_default",0);
        update(updateWrapper);
    }



}
