package nl.hukss.randomdata.randomdata.service;

import com.baomidou.mybatisplus.extension.service.IService;
import nl.hukss.randomdata.randomdata.entity.User;

public interface UserService extends IService<User> {
    String register(User user);

    String login(User user);
}
