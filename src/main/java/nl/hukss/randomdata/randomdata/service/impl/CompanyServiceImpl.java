package nl.hukss.randomdata.randomdata.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import nl.hukss.randomdata.randomdata.entity.Company;
import nl.hukss.randomdata.randomdata.mapper.CompanyMapper;
import nl.hukss.randomdata.randomdata.service.CompanyService;
import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {
}
