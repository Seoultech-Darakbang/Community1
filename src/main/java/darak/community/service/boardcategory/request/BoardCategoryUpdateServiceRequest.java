package darak.community.service.boardcategory.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardCategoryUpdateServiceRequest {

    private Long boardCategoryId;

    private String name;

    private Integer priority;

}
