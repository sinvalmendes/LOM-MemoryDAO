package com.nanuvem.lom.kernel.dao;

import java.util.ArrayList;
import java.util.List;

import com.nanuvem.lom.api.Entity;
import com.nanuvem.lom.api.MetadataException;
import com.nanuvem.lom.api.dao.EntityDao;

public class MemoryEntityDao implements EntityDao {

	private Long id = 1L;

	private MemoryDatabase memoryDatabase;

	public MemoryEntityDao(MemoryDatabase memoryDatabase) {
		this.memoryDatabase = memoryDatabase;
	}

	public Entity create(Entity entity) {
		entity.setId(id++);
		entity.setVersion(0);

		memoryDatabase.addEntity(entity);
		
		return entity;
	}

	public List<Entity> listAll() {
		List<Entity> classesReturn = new ArrayList<Entity>();

		for (Entity entity : memoryDatabase.getEntities()) {
			classesReturn.add(entity);
		}
		return classesReturn;
	}

	public Entity update(Entity entity) {
		for (Entity e : memoryDatabase.getEntities()) {
			if (e.getId().equals(entity.getId())) {
				if (e.getVersion() > entity.getVersion()) {
					throw new MetadataException(
							"Updating a deprecated version of Entity "
									+ e.getNamespace()
									+ "."
									+ e.getName()
									+ ". Get the Entity again to obtain the newest version and proceed updating.");
				}
				memoryDatabase.updateEntity(entity);
				return entity;
			}
		}
		throw new MetadataException("Invalid id for Entity "
				+ entity.getNamespace() + "." + entity.getName());
	}

	public Entity findById(Long id) {
		for (Entity e : memoryDatabase.getEntities()) {
			if (e.getId().equals(id)) {
				return e;
			}
		}
		return null;
	}

	public List<Entity> listByFullName(String fragment) {
		List<Entity> results = new ArrayList<Entity>();
		for (Entity e : memoryDatabase.getEntities()) {
			if (e.getNamespace().toLowerCase()
					.contains(fragment.toLowerCase())
					|| e.getName().toLowerCase()
							.contains(fragment.toLowerCase())) {
				results.add(e);
			}
		}
		return results;
	}

	public Entity findByFullName(String fullName) {
		String namespace = null;
		String name = null;

		if (fullName.contains(".")) {
			namespace = fullName.substring(0,
					fullName.lastIndexOf("."));
			name = fullName.substring(fullName.lastIndexOf(".") + 1,
					fullName.length());
		} else {
			name = fullName;
		}

		for (Entity entity : memoryDatabase.getEntities()) {
			if ((namespace + "." + name).equalsIgnoreCase(entity.getFullName())) {
				return entity;
			}
		}
		return null;
	}

	public void delete(Long id) {
		memoryDatabase.deleteEntity(id);
	}
}
