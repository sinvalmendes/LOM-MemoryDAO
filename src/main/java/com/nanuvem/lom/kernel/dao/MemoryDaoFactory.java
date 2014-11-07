package com.nanuvem.lom.kernel.dao;

import com.nanuvem.lom.api.dao.AttributeDao;
import com.nanuvem.lom.api.dao.DaoFactory;
import com.nanuvem.lom.api.dao.EntityDao;

public class MemoryDaoFactory implements DaoFactory {

	private MemoryEntityDao entityDao;
	private MemoryAttributeDao attributeDao;

	public EntityDao createEntityDao() {
		if (entityDao == null) {
			this.entityDao = new MemoryEntityDao();
		}
		return this.entityDao;
	}

	public AttributeDao createAttributeDao() {
		if (attributeDao == null) {
			this.attributeDao = new MemoryAttributeDao(entityDao);
		}
		return this.attributeDao;
	}

}
