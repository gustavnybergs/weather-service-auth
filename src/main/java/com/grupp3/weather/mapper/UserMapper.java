package com.grupp3.weather.mapper;

import com.grupp3.weather.dto.UserDTO;
import com.grupp3.weather.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        
        return new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRoles(),
            user.isEnabled(),
            user.getCreatedAt()
        );
    }

    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRoles(dto.getRoles());
        user.setEnabled(dto.isEnabled());
        user.setCreatedAt(dto.getCreatedAt());
        
        return user;
    }
}
