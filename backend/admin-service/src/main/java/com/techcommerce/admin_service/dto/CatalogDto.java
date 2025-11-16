package com.techcommerce.admin_service.dto;

import java.util.ArrayList;
import java.util.List;

public class CatalogDto {
    private Long id;
    private String name;
    private String description;
    private List<Long> categoryIds = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Long> getCategoryIds() { return categoryIds; }
    public void setCategoryIds(List<Long> categoryIds) { this.categoryIds = categoryIds; }
}
