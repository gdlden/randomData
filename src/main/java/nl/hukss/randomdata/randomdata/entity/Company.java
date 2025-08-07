package nl.hukss.randomdata.randomdata.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("companys")
public class Company {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
    private String code;
    private String registrationDay;
    private String character;
    private String legalRepresentative;
    private String capital;
    private String businessScope;
    private String province;
    private String city;
    private String address;
    private String year;
}
