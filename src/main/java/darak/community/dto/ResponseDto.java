package darak.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDto<T> {

    private int code;

    private String msg;

    private T data;
}
