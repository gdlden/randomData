package nl.hukss.randomdata.randomdata.controller;

import io.github.yindz.random.RandomSource;
import io.github.yindz.random.constant.Province;
import io.github.yindz.random.source.PersonInfoSource;
import jakarta.annotation.Resource;
import nl.hukss.randomdata.randomdata.entity.PersonalInfo;
import nl.hukss.randomdata.randomdata.service.PersonalInfoService;
import nl.hukss.randomdata.randomdata.vo.PersonalInfoVo;
import nl.hukss.randomdata.randomdata.vo.ResultData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/randomData")
public class RandomDataController {
    @Resource
    private PersonalInfoService personalInfoService;
    @GetMapping("/getOne")
    public ResultData getOne() {
        PersonInfoSource personInfoSource = RandomSource.personInfoSource();
        PersonalInfoVo vo = new PersonalInfoVo();
        vo.setName(personInfoSource.randomChineseName());
        vo.setSex("男");
        vo.setMobile(personInfoSource.randomChineseMobile());
        vo.setIdCard(personInfoSource.randomMaleIdCard(Province.CQ,18,50));
        vo.setCreditCode(RandomSource.otherSource().randomSocialCreditCode());
        vo.setCompanyName(RandomSource.otherSource().randomCompanyName(Province.CQ));
        PersonalInfo personalInfo = new PersonalInfo();
        BeanUtils.copyProperties(vo,personalInfo);
        boolean saved = personalInfoService.save(personalInfo);
        return ResultData.success(vo);
    }
}
