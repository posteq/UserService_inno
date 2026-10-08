package com.innowise.academy.userservice.service;

import com.innowise.academy.userservice.dto.user.UpdateUserRequest;
import com.innowise.academy.userservice.dto.user.UserNewDto;
import com.innowise.academy.userservice.dto.user.UserResponseDto;
import com.innowise.academy.userservice.exception.UserNotFoundException;
import com.innowise.academy.userservice.mapper.UserMapper;
import com.innowise.academy.userservice.entity.UserEntity;
import com.innowise.academy.userservice.repository.UserRepository;
import com.innowise.academy.userservice.repository.specification.UserSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository repository;

    public List<UserResponseDto> getUsers(String firstname, String surname,
                                          int page, int size, String sortBy, String sortDir ){

        Specification<UserEntity> entitySpecification = Specification
                                                                .where(UserSpecification.hasFirstname(firstname))
                                                                .and(UserSpecification.hasSurname(surname));

        Sort sortOrder = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        PageRequest pageRequest = PageRequest.of(page, size, sortOrder);

        return repository.findAll(entitySpecification, pageRequest)
                .getContent().stream()
                .map(userMapper::userToResponseDto)
                .toList();

    }

    public UserResponseDto getUser(Long id){
        UserEntity userEntity = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return userMapper.userToResponseDto(userEntity);
    }

    public void changeStatus(Boolean active,Long id){
        repository.changeActiveById(id,active);
    }

    public UserResponseDto create(UserNewDto user){
        UserEntity newUser = userMapper.userNewDtoToUser(user);
        UserEntity createdUser = repository.save(newUser);
        return userMapper.userToResponseDto(createdUser);
    }

    public void delete(Long id){
        repository.deleteById(id);
    }

    public UserResponseDto update(Long id, UpdateUserRequest userDomain){
        UserEntity userEntity = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userMapper.updateEntity(userDomain,userEntity);
        UserEntity updatedUser = repository.save(userEntity);

        return userMapper.userToResponseDto(updatedUser);
    }
}
