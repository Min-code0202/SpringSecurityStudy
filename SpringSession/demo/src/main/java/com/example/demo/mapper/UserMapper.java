package com.example.demo.mapper;

import com.example.demo.dto.JoinDTO;
import com.example.demo.entity.UserEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    @Autowired
    protected BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mapping(target = "role", constant = "ROLE_USER")
    public abstract UserEntity toEntity(JoinDTO joinDTO);

    @AfterMapping
    protected void encryptPassword(@MappingTarget UserEntity userEntity, JoinDTO joinDTO){
        userEntity.setPassword(bCryptPasswordEncoder.encode(joinDTO.getPassword()));
    }
}
