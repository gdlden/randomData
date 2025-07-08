package nl.hukss.randomdata.randomdata.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class PersonalInfo {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
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
}
