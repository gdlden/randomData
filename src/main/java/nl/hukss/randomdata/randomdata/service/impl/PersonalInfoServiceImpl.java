package nl.hukss.randomdata.randomdata.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import nl.hukss.randomdata.randomdata.entity.PersonalInfo;
import nl.hukss.randomdata.randomdata.mapper.PersonalInfoMapper;
import nl.hukss.randomdata.randomdata.service.PersonalInfoService;
import org.springframework.stereotype.Service;

@Service
public class PersonalInfoServiceImpl extends ServiceImpl<PersonalInfoMapper,PersonalInfo> implements PersonalInfoService {
}
