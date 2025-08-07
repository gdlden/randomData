package nl.hukss.randomdata.randomdata;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import nl.hukss.randomdata.randomdata.entity.Company;
import nl.hukss.randomdata.randomdata.service.CompanyService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@SpringBootTest
class RandomDataApplicationTests {
    @Resource
    private CompanyService companyService;

    @Test
    void contextLoads() {
    }
    @Test
    public void addData() throws IOException {
        File file = new File("C:\\Users\\hukss\\Documents\\code\\-\\Enterprise-Registration-Data\\json");
        File[] files = file.listFiles();
        for (File f : files) {
            String year = f.getName();
            File[] files1 = f.listFiles();
            for (File f1 : files1) {
                String jsonString = FileUtils.readFileToString(f1, StandardCharsets.UTF_8);
                JSONObject jsonObject = JSONObject.parseObject(jsonString);
                JSONArray dataList = jsonObject.getJSONArray("erDataList");
                for (Object o : dataList) {
                    String data = o.toString();
                    if (StringUtils.isNotBlank(data)) {
                        Company company = JSONObject.parseObject(data, Company.class);
                        company.setYear(year);
                        companyService.save(company);
                    }

                }
            }
        }
    }

}
