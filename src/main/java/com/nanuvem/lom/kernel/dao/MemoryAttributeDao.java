package com.nanuvem.lom.kernel.dao;

import java.util.Collection;

import com.nanuvem.lom.api.PropertyType;
import com.nanuvem.lom.api.EntityType;
import com.nanuvem.lom.api.dao.AttributeDao;

public class MemoryAttributeDao implements AttributeDao {

    private Long id = 1L;
    private MemoryDatabase memoryDatabase;

    public MemoryAttributeDao(MemoryDatabase memoryDatabase) {
        this.memoryDatabase = memoryDatabase;
    }

    public PropertyType create(PropertyType propertyType) {
        propertyType.setId(id++);
        propertyType.setVersion(0);

        memoryDatabase.addAttribute(propertyType);

        return propertyType;
    }

    public PropertyType findAttributeById(Long id) {
        Collection<EntityType> entityTypes = memoryDatabase.getEntities();

        for (EntityType entityEach : entityTypes) {
            for (PropertyType attributeEach : entityEach.getAttributes()) {
                if (attributeEach.getId().equals(id)) {
                    return attributeEach;
                }
            }
        }
        return null;
    }

    public PropertyType findAttributeByNameAndEntityFullName(String nameAttribute, String entityFullName) {

        EntityType entityType = memoryDatabase.findEntityByFullName(entityFullName);
        if (entityType.getAttributes() != null) {
            for (PropertyType propertyType : entityType.getAttributes()) {
                if (propertyType.getName().equalsIgnoreCase(nameAttribute)) {
                    return propertyType;
                }
            }
        }
        return null;
    }

    public PropertyType update(PropertyType propertyType) {
        return memoryDatabase.updateAtribute(propertyType);
    }
}