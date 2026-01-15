package nl.hukss.randomdata.randomdata.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("users")
public class User {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    private String username;
    private String password;
    private String nickName;
    private String avatar;
}
