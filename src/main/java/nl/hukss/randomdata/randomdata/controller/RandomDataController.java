package nl.hukss.randomdata.randomdata.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.yindz.random.RandomSource;
import io.github.yindz.random.constant.Province;
import io.github.yindz.random.source.PersonInfoSource;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import nl.hukss.randomdata.randomdata.entity.Company;
import nl.hukss.randomdata.randomdata.entity.PersonalInfo;
import nl.hukss.randomdata.randomdata.service.CompanyService;
import nl.hukss.randomdata.randomdata.service.PersonalInfoService;
import nl.hukss.randomdata.randomdata.vo.PersonalInfoVo;
import nl.hukss.randomdata.randomdata.vo.ResultData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/randomData")
public class RandomDataController {
    @Resource
    private PersonalInfoService personalInfoService;
    @Resource
    private CompanyService companyService;
    @GetMapping("/getOne/{userId}")
    public ResultData getOne(@PathVariable("userId") String userId, HttpServletRequest request) {
        if (StringUtils.isBlank(userId) || "0".equals(userId)) {
            userId = request.getRemoteHost();
        }
        PersonInfoSource personInfoSource = RandomSource.personInfoSource();
        PersonalInfoVo vo = new PersonalInfoVo();
        vo.setName(personInfoSource.randomChineseName());
        int i = new Random().nextInt();
        if (i % 2 == 0) {
            vo.setSex("男");
        } else {
            vo.setSex("女");
        }
        vo.setMobile(personInfoSource.randomChineseMobile());
        vo.setIdCard(personInfoSource.randomMaleIdCard(Province.CQ,18,50));
        Company one = companyService.getOne(Wrappers.lambdaQuery(Company.class)
                .isNull(Company::getPersonalInfoId)
                .last("LIMIT 1"));
//        vo.setCreditCode(RandomSource.otherSource().randomSocialCreditCode());
        vo.setCreditCode(one.getCode());
        vo.setCompanyName(RandomSource.otherSource().randomCompanyName(Province.CQ));
        vo.setBankcard(RandomSource.financialSource().randomDebitCardNo());
        vo.setProvince(one.getProvince());
        vo.setCity(one.getCity());
        vo.setAddress(one.getAddress());
        PersonalInfo personalInfo = new PersonalInfo();
        BeanUtils.copyProperties(vo,personalInfo);
        personalInfo.setUserId(userId);
        boolean saved = personalInfoService.save(personalInfo);
        if (saved) {
            companyService.updateById(new Company().setId(one.getId()).setPersonalInfoId(personalInfo.getId()));
        }
        return ResultData.success(vo);
    }
}
