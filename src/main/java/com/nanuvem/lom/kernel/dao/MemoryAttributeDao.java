package com.nanuvem.lom.kernel.dao;

import java.util.Collection;

import com.nanuvem.lom.api.Attribute;
import com.nanuvem.lom.api.Entity;
import com.nanuvem.lom.api.dao.AttributeDao;

public class MemoryAttributeDao implements AttributeDao {

    private Long id = 1L;
    private MemoryDatabase memoryDatabase;

    public MemoryAttributeDao(MemoryDatabase memoryDatabase) {
        this.memoryDatabase = memoryDatabase;
    }

    public Attribute create(Attribute attribute) {
        attribute.setId(id++);
        attribute.setVersion(0);

        memoryDatabase.addAttribute(attribute);

        return attribute;
    }

    public Attribute findAttributeById(Long id) {
        Collection<Entity> entities = memoryDatabase.getEntities();

        for (Entity entityEach : entities) {
            for (Attribute attributeEach : entityEach.getAttributes()) {
                if (attributeEach.getId().equals(id)) {
                    return attributeEach;
                }
            }
        }
        return null;
    }

    public Attribute findAttributeByNameAndEntityFullName(String nameAttribute, String entityFullName) {

        Entity entity = memoryDatabase.findEntityByFullName(entityFullName);
        if (entity.getAttributes() != null) {
            for (Attribute attribute : entity.getAttributes()) {
                if (attribute.getName().equalsIgnoreCase(nameAttribute)) {
                    return attribute;
                }
            }
        }
        return null;
    }

    public Attribute update(Attribute attribute) {
        return memoryDatabase.updateAtribute(attribute);
    }
}