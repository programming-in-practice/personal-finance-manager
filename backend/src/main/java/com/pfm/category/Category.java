package com.pfm.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pfm.history.DifferenceProvider;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class Category implements DifferenceProvider<Category> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @ApiModelProperty(value = "Category id (generated by application)", required = true, example = "1")
  private Long id;

  @ApiModelProperty(value = "Category name", required = true, example = "Eating out")
  private String name;

  @ManyToOne
  @ApiModelProperty(value = "Parent category object", required = true)
  //TODO try using parentCategoryID
  private Category parentCategory;

  @JsonIgnore
  private Long userId;

  @Override
  public List<String> getDifferences(Category category) {
    List<String> differences = new ArrayList<>();

    if (!(this.getName().equals(category.getName()))) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "name", this.getName(), category.getName()));
    }

    if (this.parentCategory == null && category.parentCategory != null) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "parent category",
          "'Main Category'", category.parentCategory.getName()));
    }

    if (this.parentCategory != null && category.parentCategory == null) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "parent category",
          this.parentCategory.getName(),
          "'Main Category'"));
    }
    if (this.parentCategory != null && category.parentCategory != null && !this.parentCategory.equals(category.getParentCategory())) {
      differences.add(String.format(UPDATE_ENTRY_TEMPLATE, "parent category", this.parentCategory.getName(),
          category.parentCategory.getName()));
    }

    return differences;
  }

  @Override
  public List<String> getObjectPropertiesWithValues() {
    List<String> newValues = new ArrayList<>();
    newValues.add(String.format(ENTRY_VALUES_TEMPLATE, "name", this.getName()));
    if (!(this.parentCategory == null)) {
      newValues.add(String.format(ENTRY_VALUES_TEMPLATE, "parent category", this.getParentCategory().getName()));
    }
    return newValues;
  }

  @Override
  public String getObjectDescriptiveName() {
    return this.getName();
  }

}