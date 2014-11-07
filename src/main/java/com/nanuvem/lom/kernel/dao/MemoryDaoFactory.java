package com.nanuvem.lom.kernel.dao;

import com.nanuvem.lom.api.dao.DaoFactory;
import com.nanuvem.lom.api.dao.EntityDao;

public class MemoryDaoFactory implements DaoFactory {

	private MemoryEntityDao entityDao;

	public EntityDao createEntityDao() {
		if (entityDao == null) {
			this.entityDao = new MemoryEntityDao();
		}
		return this.entityDao;
	}

}
