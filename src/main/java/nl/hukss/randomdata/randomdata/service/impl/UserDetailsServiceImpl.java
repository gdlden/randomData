package nl.hukss.randomdata.randomdata.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import nl.hukss.randomdata.randomdata.entity.User;
import nl.hukss.randomdata.randomdata.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
 
    @Autowired
    UserMapper userMapper;
 
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getUsername, username));
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new UserDetailsImpl(user);	// UserDetailsImpl 是我们实现的类
    }
}