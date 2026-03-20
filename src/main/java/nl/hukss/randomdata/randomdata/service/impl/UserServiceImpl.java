package nl.hukss.randomdata.randomdata.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import nl.hukss.randomdata.randomdata.entity.User;
import nl.hukss.randomdata.randomdata.mapper.UserMapper;
import nl.hukss.randomdata.randomdata.service.UserService;
import nl.hukss.randomdata.randomdata.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private AuthenticationManager authenticationManager;

    @Override
    public String register(User user) {
        return "";
    }

    @Override
    public String login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        Object principal = authenticate.getPrincipal();
        User userDb;
        if (principal instanceof UserDetailsImpl loginUser) {
            userDb = loginUser.getUser();
        } else {
            userDb = (User) principal;
        }

        return JwtUtil.createJWT(userDb.getId().toString());
    }
}
