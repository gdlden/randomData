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
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.function.IOSupplier;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
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
        vo.setBusinessScope(one.getBusinessScope());
        PersonalInfo personalInfo = new PersonalInfo();
        BeanUtils.copyProperties(vo,personalInfo);
        personalInfo.setUserId(userId);
        boolean saved = personalInfoService.save(personalInfo);
        if (saved) {
            companyService.updateById(new Company().setId(one.getId()).setPersonalInfoId(personalInfo.getId()));
        }
        return ResultData.success(vo);
    }
    @PostMapping("/getImageCode")
    public ResultData getImageCode(@RequestBody Map<String,String> reqMap) throws IOException, InterruptedException {
        String fileBase64 = reqMap.get("base64Image");
//        String base64Image = fileBase64.substring(fileBase64.indexOf(",") + 1);
//        System.out.println(base64Image);
        String[] args1 = new String[] {
                "C:\\Users\\hukss\\.pyenv\\pyenv-win\\versions\\3.12.10\\python.exe",
                "-u",
                "C:\\Users\\hukss\\Documents\\code\\ddddocrDemo\\main.py",
                fileBase64
        };
        Process proc = Runtime.getRuntime().exec(args1);
        int i = proc.waitFor();
        if (i != 0) {
            throw new RuntimeException("Python script exited with code " + i + ": " );
        }
        String err = IOUtils.toString(proc.getErrorStream(), StandardCharsets.UTF_8);
        if (StringUtils.isNotBlank(err)) {
            System.out.println("err:"+err);
        }
        String res = IOUtils.toString(proc.getInputStream(), StandardCharsets.UTF_8);
        System.out.println(res);
        return ResultData.success(res);
    }

    public void execPy() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("python", "C:\\Users\\hukss\\Documents\\code\\ddddocrDemo\\main.py");
        Process p = pb.start();
        InputStream inputStream = p.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println( line);
        }
        p.waitFor();
        System.out.println("errr");

    }
}
