package com.nanuvem.lom.kernel.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.nanuvem.lom.api.PropertyType;
import com.nanuvem.lom.api.Property;
import com.nanuvem.lom.api.EntityType;
import com.nanuvem.lom.api.Entity;
import com.nanuvem.lom.api.Relation;
import com.nanuvem.lom.api.RelationType;

public class MemoryDatabase {

    private HashMap<Long, EntityType> entitiesById = new HashMap<Long, EntityType>();
    private HashMap<Long, List<Entity>> instancesByEntityId = new HashMap<Long, List<Entity>>();
    private HashMap<Long, RelationType> relationTypesById = new HashMap<Long, RelationType>();
    private HashMap<Long, Relation> relationsById = new HashMap<Long, Relation>();

    public void addEntity(EntityType entityType) {
        entitiesById.put(entityType.getId(), entityType);
    }

    public Collection<EntityType> getEntities() {
        return entitiesById.values();
    }

    public Collection<RelationType> getRelationTypes() {
        return relationTypesById.values();
    }

    public void updateEntity(EntityType entityType) {
        EntityType myEntity = findEntityById(entityType.getId());

        myEntity.setName(entityType.getName());
        myEntity.setNamespace(entityType.getNamespace());
        myEntity.setVersion(entityType.getVersion());
    }

    public void deleteEntity(Long id) {
        entitiesById.remove(id);
    }

    public EntityType findEntityByFullName(String fullName) {
        Collection<EntityType> values = entitiesById.values();
        for (EntityType entityType : values) {
            if (entityType.getFullName().equalsIgnoreCase(fullName)) {
                return entityType;
            }
        }

        return null;
    }

    public EntityType findEntityById(Long id) {
        return entitiesById.get(id);
    }

    public void addAttribute(PropertyType propertyType) {
        EntityType entityType = findEntityById(propertyType.getEntity().getId());
        shiftSequence(propertyType, entityType);
        propertyType.setEntity(entityType);
    }

    public PropertyType updateAtribute(PropertyType propertyType) {
        EntityType entityType = findEntityById(propertyType.getEntity().getId());

        PropertyType attributeInEntity = null;
        boolean changeSequence = false;

        for (int i = 0; i < entityType.getAttributes().size(); i++) {
            attributeInEntity = entityType.getAttributes().get(i);
            if (propertyType.getId().equals(attributeInEntity.getId())) {

                if (!propertyType.getSequence().equals(attributeInEntity.getSequence())) {
                    changeSequence = true;
                }

                attributeInEntity.setName(propertyType.getName());
                attributeInEntity.setType(propertyType.getType());
                attributeInEntity.setConfiguration(propertyType.getConfiguration());
                attributeInEntity.setVersion(attributeInEntity.getVersion() + 1);
                break;
            }
        }

        if (changeSequence) {
            PropertyType temp = null;
            for (PropertyType at : entityType.getAttributes()) {
                if (propertyType.getId().equals(at.getId())) {
                    temp = at;
                    entityType.getAttributes().remove(at);
                    temp.setSequence(propertyType.getSequence());

                    this.shiftSequence(temp, entityType);
                    break;
                }
            }
        }

        return attributeInEntity;
    }

    private void shiftSequence(PropertyType propertyType, EntityType entityType) {
        int i = 0;
        for (; i < entityType.getAttributes().size(); i++) {
            if (propertyType.getSequence().equals(entityType.getAttributes().get(i).getSequence())) {
                break;
            }
        }

        i++;
        entityType.getAttributes().add(i - 1, propertyType);

        for (; i < entityType.getAttributes().size(); i++) {
            PropertyType nextAttribute = null;
            try {
                nextAttribute = entityType.getAttributes().get(i);
            } catch (IndexOutOfBoundsException e) {
                break;
            }

            if (nextAttribute.getSequence().equals(entityType.getAttributes().get(i - 1).getSequence())) {
                nextAttribute.setSequence(nextAttribute.getSequence() + 1);
            }
        }
    }

    public void addInstance(Entity entity) {
        EntityType entityType = findEntityById(entity.getEntity().getId());
        entity.setEntity(entityType);
        getInstances(entityType.getId()).add(entity);
    }

    public List<Entity> getInstances(Long idEntity) {
        if (instancesByEntityId.get(idEntity) == null) {
            instancesByEntityId.put(idEntity, new ArrayList<Entity>());
        }

        return instancesByEntityId.get(idEntity);
    }

    public void addAttributeValue(Property value) {
        Entity entity = findInstanceById(value.getInstance().getId());
        value.setInstance(entity);
        entity.getValues().add(value);
    }

    private Entity findInstanceById(Long id) {
        for (EntityType entityType : getEntities()) {
            for (Entity entity : getInstances(entityType.getId())) {
                if (entity.getId().equals(id)) {
                    return entity;
                }
            }
        }

        return null;
    }

    public void addRelationType(RelationType relationType) {
        this.relationTypesById.put(relationType.getId(), relationType);
    }

    public List<RelationType> listAllRelationTypes() {
        List<RelationType> relationTypesToReturn = new ArrayList<RelationType>();
        for (RelationType rt : this.getRelationTypes()) {
            relationTypesToReturn.add(rt);
        }
        return relationTypesToReturn;
    }

    public void deleteRelationType(Long id) {
        this.relationTypesById.remove(id);
    }

    public RelationType updateRelationType(RelationType relationType) {
        int version = relationType.getVersion().intValue();
        relationType.setVersion(++version);
        this.relationTypesById.put(relationType.getId(), relationType);
        return this.relationTypesById.get(relationType.getId());
    }

    public RelationType findRelationTypeById(Long id) {
        for (RelationType rt : this.getRelationTypes()) {
            if (rt.getId().equals(id)) {
                return rt;
            }
        }
        return null;
    }

    public void addRelation(Relation relation) {
        this.relationsById.put(relation.getId(), relation);
    }

    public Relation findRelationById(Long id) {
        return this.relationsById.get(id);
    }

    public List<Relation> listAllRelations() {
        List<Relation> allRelations = new ArrayList<Relation>();
        for (Relation relation : this.relationsById.values()) {
            allRelations.add(relation);
        }
        return allRelations;
    }

    public void deleteRelation(Long id) {
        this.relationsById.remove(id);
    }

    public List<Relation> findRelationsBySourceInstance(Entity source, RelationType relationType) {
        List<Relation> relations = new ArrayList<Relation>();
        for (Relation relation : this.relationsById.values()) {
            if (relation.getSource().equals(source) && relation.getRelationType().getId().equals(relationType.getId()))
                relations.add(relation);
        }
        return relations;
    }

    public List<Relation> findRelationsByRelationType(RelationType relationType) {
        List<Relation> relations = new ArrayList<Relation>();
        for (Relation relation : this.relationsById.values()) {
            if (relation.getRelationType().getId().equals(relationType.getId()))
                relations.add(relation);
        }
        return relations;
    }

    public List<Relation> findRelationsByTargetInstance(Entity targetInstance) {
        List<Relation> relations = new ArrayList<Relation>();
        for (Relation relation : this.relationsById.values()) {
            if (relation.getTarget().equals(targetInstance))
                relations.add(relation);
        }
        return relations;
    }

}
