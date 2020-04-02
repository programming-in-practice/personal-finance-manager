package com.pfm.category;

import com.pfm.history.HistoryField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class CategoryRequest {

  @ApiModelProperty(value = "Parent category id", example = "1")
  private Long parentCategoryId;

  @ApiModelProperty(value = "CATEGORY name", required = true, example = "Eating out")
  private String name;

  @ApiModelProperty(value = "CATEGORY priority", required = true, example = "1")
  @Builder.Default
  private int priority = 1000;
}
