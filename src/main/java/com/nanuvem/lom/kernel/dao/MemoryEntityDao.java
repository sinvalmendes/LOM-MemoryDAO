package com.nanuvem.lom.kernel.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;

import com.nanuvem.lom.api.Entity;
import com.nanuvem.lom.api.MetadataException;
import com.nanuvem.lom.api.dao.EntityDao;

public class MemoryEntityDao implements EntityDao {

	private Long id = 1L;

	private List<Entity> entities = new ArrayList<Entity>();

	public Entity create(Entity entity) {
		entity.setId(id++);
		entity.setVersion(0);

		Entity clone = (Entity) SerializationUtils.clone(entity);
		entities.add(clone);
		
		return (Entity) SerializationUtils.clone(clone);
	}

	public List<Entity> listAll() {
		List<Entity> classesReturn = new ArrayList<Entity>();

		for (Entity entity : this.entities) {
			classesReturn.add((Entity) SerializationUtils.clone(entity));
		}
		return classesReturn;
	}

	public Entity update(Entity entity) {
		for (Entity e : this.listAll()) {
			if (e.getId().equals(entity.getId())) {
				if (e.getVersion() > entity.getVersion()) {
					throw new MetadataException(
							"Updating a deprecated version of Entity "
									+ e.getNamespace()
									+ "."
									+ e.getName()
									+ ". Get the Entity again to obtain the newest version and proceed updating.");
				}
				this.entities.remove(e);
				this.entities.add(entity);
				return (Entity) SerializationUtils.clone(entity);
			}
		}
		throw new MetadataException("Invalid id for Entity "
				+ entity.getNamespace() + "." + entity.getName());
	}

	public Entity findById(Long id) {
		for (Entity e : this.entities) {
			if (e.getId().equals(id)) {
				return (Entity) SerializationUtils.clone(e);
			}
		}
		return null;
	}

	public List<Entity> listByFullName(String fragment) {
		List<Entity> results = new ArrayList<Entity>();
		for (Entity e : this.entities) {
			if (e.getNamespace().toLowerCase()
					.contains(fragment.toLowerCase())
					|| e.getName().toLowerCase()
							.contains(fragment.toLowerCase())) {
				results.add((Entity) SerializationUtils.clone(e));
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

		for (Entity entity : this.entities) {
			if ((namespace + "." + name).equalsIgnoreCase(entity.getFullName())) {
				return (Entity) SerializationUtils.clone(entity);
			}
		}
		return null;
	}

	public void delete(Long id) {
		for (int i = 0; i < this.entities.size(); i++) {
			Entity e = this.entities.get(i);
			if (e.getId().equals(id)) {
				this.entities.remove(e);
				return;
			}
		}
	}
}
