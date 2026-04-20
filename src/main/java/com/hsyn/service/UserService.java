package com.hsyn.service;


import com.hsyn.model.User;
import com.hsyn.payload.dto.UserDTO;

import java.util.List;

public interface UserService {

    public User getCurrentUser();
    public List<UserDTO> getAllUsers();
    User findById(Long id) throws Exception;

}
