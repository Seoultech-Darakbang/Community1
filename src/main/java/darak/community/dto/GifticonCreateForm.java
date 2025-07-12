package darak.community.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GifticonCreateForm {

    private String title;
    private String description;
    private String imageUrl;
    private String brand;
    private Integer totalQuantity;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
}
