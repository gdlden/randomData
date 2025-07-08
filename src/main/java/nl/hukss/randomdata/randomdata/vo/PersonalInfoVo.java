package nl.hukss.randomdata.randomdata.vo;

import lombok.Data;

@Data
public class PersonalInfoVo {
    private String name;
    private String sex;
    private String mobile;
    /**
     * 身份证号码
     */
    private String idCard;
}
