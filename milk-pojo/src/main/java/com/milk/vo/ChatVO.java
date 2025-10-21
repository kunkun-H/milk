package com.milk.vo;

import com.milk.entity.ChatSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatVO extends ChatSession implements Serializable {
    private String name;
    private String avatar;

}
