package com.nanuvem.lom.kernel.dao;

import com.nanuvem.lom.api.dao.AttributeDao;
import com.nanuvem.lom.api.dao.AttributeValueDao;
import com.nanuvem.lom.api.dao.DaoFactory;
import com.nanuvem.lom.api.dao.EntityDao;
import com.nanuvem.lom.api.dao.InstanceDao;
import com.nanuvem.lom.api.dao.RelationTypeDao;

public class MemoryDaoFactory implements DaoFactory {

	private MemoryDatabase memoryDatabase;

	private MemoryEntityDao entityDao;
	private MemoryAttributeDao attributeDao;
	private MemoryInstanceDao instanceDao;
	private MemoryAttributeValueDao attributeValueDao;

	private MemoryRelationTypeDao relationTypeDao;

	public MemoryDaoFactory() {
		memoryDatabase = new MemoryDatabase();
	}

	public EntityDao createEntityDao() {
		if (entityDao == null) {
			this.entityDao = new MemoryEntityDao(memoryDatabase);
		}
		return this.entityDao;
	}

	public AttributeDao createAttributeDao() {
		if (attributeDao == null) {
			this.attributeDao = new MemoryAttributeDao(memoryDatabase);
		}
		return this.attributeDao;
	}

	public InstanceDao createInstanceDao() {
		if (instanceDao == null) {
			this.instanceDao = new MemoryInstanceDao(memoryDatabase);
		}
		return this.instanceDao;
	}

	public AttributeValueDao createAttributeValueDao() {
		if (attributeValueDao == null) {
			this.attributeValueDao = new MemoryAttributeValueDao(memoryDatabase);
		}
		return this.attributeValueDao;
	}

	public RelationTypeDao createRelationTypeDao() {
		if (relationTypeDao == null) {
			this.relationTypeDao = new MemoryRelationTypeDao(memoryDatabase);
		}
		return this.relationTypeDao;
	}

}
