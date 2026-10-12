package com.innowise.academy.userservice.service;

import com.innowise.academy.userservice.dto.request.user.UpdateUserRequest;
import com.innowise.academy.userservice.dto.request.user.UserNewDto;
import com.innowise.academy.userservice.dto.response.user.UserResponseDto;
import com.innowise.academy.userservice.dto.response.user.UserResponseDtoWithCards;
import com.innowise.academy.userservice.entity.UserEntity;
import com.innowise.academy.userservice.exception.DuplicateCreationException;
import com.innowise.academy.userservice.exception.UserNotFoundException;
import com.innowise.academy.userservice.mapper.UserMapper;
import com.innowise.academy.userservice.repository.UserRepository;
import com.innowise.academy.userservice.repository.specification.UserSpecification;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.innowise.academy.userservice.config.RedisCacheConfig.USERS_CACHE;

@Service
@AllArgsConstructor
@Transactional
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository repository;

    @EntityGraph(attributePaths = "paymentCards")
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

    @Cacheable(cacheNames = USERS_CACHE, key = "#id")
    public UserResponseDtoWithCards getUser(Long id){
        UserEntity userEntity = repository.findWithCardsById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return userMapper.userToResponseDtoWithCards(userEntity);
    }

    @CacheEvict(cacheNames = USERS_CACHE, key = "#id")
    public void changeStatus(Boolean active,Long id){
        repository.changeActiveById(id,active);
    }

    public UserResponseDto create(UserNewDto user){
        if(repository.existsByEmail(user.getEmail())){
            throw new DuplicateCreationException(user.getEmail());
        }

        UserEntity newUser = userMapper.userNewDtoToUser(user);
        UserEntity createdUser = repository.save(newUser);
        return userMapper.userToResponseDto(createdUser);
    }

    @CacheEvict(cacheNames = USERS_CACHE, key = "#id")
    public void delete(Long id){
        repository.deleteById(id);
    }

    @CacheEvict(cacheNames = USERS_CACHE, key = "#id")
    public UserResponseDto update(Long id, UpdateUserRequest userDomain){
        UserEntity userEntity = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userMapper.updateEntity(userDomain,userEntity);
        UserEntity updatedUser = repository.save(userEntity);

        return userMapper.userToResponseDto(updatedUser);
    }
}
