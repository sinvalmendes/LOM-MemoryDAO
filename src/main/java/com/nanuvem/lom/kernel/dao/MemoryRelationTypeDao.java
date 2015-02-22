package com.nanuvem.lom.kernel.dao;

import java.util.List;

import com.nanuvem.lom.api.RelationType;
import com.nanuvem.lom.api.dao.RelationTypeDao;

public class MemoryRelationTypeDao implements RelationTypeDao {

    private Long id = 1L;

    private MemoryDatabase memoryDatabase;

    public MemoryRelationTypeDao(MemoryDatabase memoryDatabase) {
        this.memoryDatabase = memoryDatabase;
    }

    public RelationType create(RelationType relationType) {
        relationType.setId(id++);
        relationType.setVersion(0);
        memoryDatabase.addRelationType(relationType);
        return relationType;
    }

    public RelationType findRelationTypeById(Long id) {
        return memoryDatabase.findRelationTypeById(id);
    }

    public RelationType update(RelationType relationType) {
        return memoryDatabase.updateRelationType(relationType);
    }

    public List<RelationType> listAllRelationTypes() {
        return memoryDatabase.listAllRelationTypes();
    }

    public void delete(Long id) {
        memoryDatabase.deleteRelationType(id);
    }

}
