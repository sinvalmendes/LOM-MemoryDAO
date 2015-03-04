package com.nanuvem.lom.kernel.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;

import com.nanuvem.lom.api.EntityType;
import com.nanuvem.lom.api.Entity;
import com.nanuvem.lom.api.dao.InstanceDao;

public class MemoryInstanceDao implements InstanceDao {

    private Long id = 1L;
    private MemoryDatabase memoryDatabase;

    public MemoryInstanceDao(MemoryDatabase memoryDatabase) {
        this.memoryDatabase = memoryDatabase;
    }

    public Entity create(Entity entity) {
        entity.setId(id++);
        entity.setVersion(0);

        memoryDatabase.addInstance(entity);

        return entity;
    }

    public List<Entity> listAllInstances(String fullEntityName) {
        EntityType entityType = memoryDatabase.findEntityByFullName(fullEntityName);

        List<Entity> cloneInstances = new ArrayList<Entity>();
        for (Entity it : memoryDatabase.getInstances(entityType.getId())) {
            cloneInstances.add((Entity) SerializationUtils.clone(it));
        }
        return cloneInstances;
    }

    public Entity findInstanceById(Long id) {
        Collection<EntityType> entityTypes = memoryDatabase.getEntities();

        for (EntityType entityType : entityTypes) {
            for (Entity instanceEach : memoryDatabase.getInstances(entityType.getId())) {
                if (instanceEach.getId().equals(id)) {
                    return instanceEach;
                }
            }
        }
        return null;
    }

    public List<Entity> findInstancesByEntityId(Long entityId) {
        return memoryDatabase.getInstances(entityId);
    }

    public Entity update(Entity entity) {
        // TODO Auto-generated method stub
        return null;
    }

    public void delete(Long id) {
        // TODO Auto-generated method stub

    }

}