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
    /**
     * 企业社会信用代码
     */
    private String creditCode;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 银行卡
     */
    private String bankcard;
}
