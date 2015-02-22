package com.nanuvem.lom.kernel.dao;

import java.util.List;

import com.nanuvem.lom.api.Instance;
import com.nanuvem.lom.api.MetadataException;
import com.nanuvem.lom.api.Relation;
import com.nanuvem.lom.api.RelationType;
import com.nanuvem.lom.api.dao.RelationDao;

public class MemoryRelationDao implements RelationDao {

    private Long id = 1L;

    private MemoryDatabase memoryDatabase;

    public MemoryRelationDao(MemoryDatabase memoryDatabase) {
        this.memoryDatabase = memoryDatabase;
    }

    public Relation create(Relation relation) {
        relation.setId(id++);
        relation.setVersion(0);
        memoryDatabase.addRelation(relation);
        return relation;
    }

    public Relation findById(Long id) {
        return memoryDatabase.findRelationById(id);
    }

    public Relation update(Relation relation) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Relation> listAllRelations() {
        return memoryDatabase.listAllRelations();
    }

    public void delete(Long id) {
        if (findById(id) == null) {
            throw new MetadataException("Unknown id for Relation: " + id);
        }
        memoryDatabase.deleteRelation(id);

    }

    public List<Relation> findRelationsBySourceInstance(Instance source) {
        return memoryDatabase.findRelationsBySourceInstance(source);
    }

    public List<Relation> findRelationsByRelationType(RelationType relationType) {
        return memoryDatabase.findRelationsByRelationType(relationType);
    }

}
