package com.nanuvem.lom.kernel.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.nanuvem.lom.api.Attribute;
import com.nanuvem.lom.api.AttributeValue;
import com.nanuvem.lom.api.Entity;
import com.nanuvem.lom.api.Instance;
import com.nanuvem.lom.api.Relation;
import com.nanuvem.lom.api.RelationType;

public class MemoryDatabase {

    private HashMap<Long, Entity> entitiesById = new HashMap<Long, Entity>();
    private HashMap<Long, List<Instance>> instancesByEntityId = new HashMap<Long, List<Instance>>();
    private HashMap<Long, RelationType> relationTypesById = new HashMap<Long, RelationType>();
    private HashMap<Long, Relation> relationsById = new HashMap<Long, Relation>();

    public void addEntity(Entity entity) {
        entitiesById.put(entity.getId(), entity);
    }

    public Collection<Entity> getEntities() {
        return entitiesById.values();
    }

    public Collection<RelationType> getRelationTypes() {
        return relationTypesById.values();
    }

    public void updateEntity(Entity entity) {
        Entity myEntity = findEntityById(entity.getId());

        myEntity.setName(entity.getName());
        myEntity.setNamespace(entity.getNamespace());
        myEntity.setVersion(entity.getVersion());
    }

    public void deleteEntity(Long id) {
        entitiesById.remove(id);
    }

    public Entity findEntityByFullName(String fullName) {
        Collection<Entity> values = entitiesById.values();
        for (Entity entity : values) {
            if (entity.getFullName().equalsIgnoreCase(fullName)) {
                return entity;
            }
        }

        return null;
    }

    public Entity findEntityById(Long id) {
        return entitiesById.get(id);
    }

    public void addAttribute(Attribute attribute) {
        Entity entity = findEntityById(attribute.getEntity().getId());
        shiftSequence(attribute, entity);
        attribute.setEntity(entity);
    }

    public Attribute updateAtribute(Attribute attribute) {
        Entity entity = findEntityById(attribute.getEntity().getId());

        Attribute attributeInEntity = null;
        boolean changeSequence = false;

        for (int i = 0; i < entity.getAttributes().size(); i++) {
            attributeInEntity = entity.getAttributes().get(i);
            if (attribute.getId().equals(attributeInEntity.getId())) {

                if (!attribute.getSequence().equals(attributeInEntity.getSequence())) {
                    changeSequence = true;
                }

                attributeInEntity.setName(attribute.getName());
                attributeInEntity.setType(attribute.getType());
                attributeInEntity.setConfiguration(attribute.getConfiguration());
                attributeInEntity.setVersion(attributeInEntity.getVersion() + 1);
                break;
            }
        }

        if (changeSequence) {
            Attribute temp = null;
            for (Attribute at : entity.getAttributes()) {
                if (attribute.getId().equals(at.getId())) {
                    temp = at;
                    entity.getAttributes().remove(at);
                    temp.setSequence(attribute.getSequence());

                    this.shiftSequence(temp, entity);
                    break;
                }
            }
        }

        return attributeInEntity;
    }

    private void shiftSequence(Attribute attribute, Entity entity) {
        int i = 0;
        for (; i < entity.getAttributes().size(); i++) {
            if (attribute.getSequence().equals(entity.getAttributes().get(i).getSequence())) {
                break;
            }
        }

        i++;
        entity.getAttributes().add(i - 1, attribute);

        for (; i < entity.getAttributes().size(); i++) {
            Attribute nextAttribute = null;
            try {
                nextAttribute = entity.getAttributes().get(i);
            } catch (IndexOutOfBoundsException e) {
                break;
            }

            if (nextAttribute.getSequence().equals(entity.getAttributes().get(i - 1).getSequence())) {
                nextAttribute.setSequence(nextAttribute.getSequence() + 1);
            }
        }
    }

    public void addInstance(Instance instance) {
        Entity entity = findEntityById(instance.getEntity().getId());
        instance.setEntity(entity);
        getInstances(entity.getId()).add(instance);
    }

    public List<Instance> getInstances(Long idEntity) {
        if (instancesByEntityId.get(idEntity) == null) {
            instancesByEntityId.put(idEntity, new ArrayList<Instance>());
        }

        return instancesByEntityId.get(idEntity);
    }

    public void addAttributeValue(AttributeValue value) {
        Instance instance = findInstanceById(value.getInstance().getId());
        value.setInstance(instance);
        instance.getValues().add(value);
    }

    private Instance findInstanceById(Long id) {
        for (Entity entity : getEntities()) {
            for (Instance instance : getInstances(entity.getId())) {
                if (instance.getId().equals(id)) {
                    return instance;
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

    public List<Relation> findRelationsBySourceInstance(Instance source) {
        List<Relation> relations = new ArrayList<Relation>();
        for (Relation relation : this.relationsById.values()) {
            if (relation.getSource().equals(source))
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

}
