package nl.hukss.randomdata.randomdata.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("personal_info")
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
    private String createTime;
    /**
     * 银行卡
     */
    private String bankcard;
    private String userId;
}
