package com.nanuvem.lom.kernel.dao;

import java.util.List;

import org.apache.commons.lang.SerializationUtils;

import com.nanuvem.lom.api.Attribute;
import com.nanuvem.lom.api.Entity;
import com.nanuvem.lom.api.dao.AttributeDao;
import com.nanuvem.lom.api.dao.EntityDao;

public class MemoryAttributeDao implements AttributeDao {

	private Long id = 1L;
	private EntityDao entityDao;

	public MemoryAttributeDao(EntityDao entityDao) {
		this.entityDao = entityDao;
	}

	public Attribute create(Attribute attribute) {
		attribute.setId(id++);
		attribute.setVersion(0);

		Entity entity = attribute.getEntity();
		attribute.setEntity(entity);

		this.shiftSequence(attribute, entity);
		this.entityDao.update(entity);
		
		return (Attribute) SerializationUtils.clone(attribute);
	}

	private void shiftSequence(Attribute attribute, Entity entity) {
		int i = 0;
		for (; i < entity.getAttributes().size(); i++) {
			if (attribute.getSequence().equals(
					entity.getAttributes().get(i).getSequence())) {
				break;
			}
		}

		i++;
		entity.getAttributes().add(i - 1, attribute);

		for (; i < entity.getAttributes().size(); i++) {
			Attribute attributeNext = null;
			try {
				attributeNext = entity.getAttributes().get(i);
			} catch (IndexOutOfBoundsException e) {
				break;
			}

			if (attributeNext.getSequence().equals(
					entity.getAttributes().get(i - 1).getSequence())) {
				attributeNext.setSequence(attributeNext.getSequence() + 1);
			}
		}
	}

	public Attribute findAttributeById(Long id) {
		List<Entity> classes = this.entityDao.listAll();

		for (Entity entityEach : classes) {
			for (Attribute attributeEach : entityEach.getAttributes()) {
				if (attributeEach.getId().equals(id)) {
					return attributeEach;
				}
			}
		}
		return null;
	}

	public Attribute findAttributeByNameAndEntityFullName(String nameAttribute,
			String classFullName) {

		Entity entityFound = this.entityDao.findByFullName(classFullName);
		if (entityFound.getAttributes() != null) {
			for (Attribute attributeEach : entityFound.getAttributes()) {
				if (attributeEach.getName().equalsIgnoreCase(nameAttribute)) {
					return (Attribute) SerializationUtils.clone(attributeEach);
				}
			}
		}
		return null;
	}

	public Attribute update(Attribute attribute) {
		Entity entity = this.entityDao.findById(attribute.getEntity().getId());

		Attribute attributeInEntity = null;
		boolean changeSequence = false;

		for (int i = 0; i < entity.getAttributes().size(); i++) {
			if (attribute.getId().equals(entity.getAttributes().get(i).getId())) {
				attributeInEntity = entity.getAttributes().get(i);

				if (!attribute.getSequence().equals(
						attributeInEntity.getSequence())) {
					changeSequence = true;
				}

				Attribute attributeClone = (Attribute) SerializationUtils
						.clone(attribute);
				attributeInEntity.setEntity(entity);
				attributeInEntity.setName(attributeClone.getName());
				attributeInEntity.setType(attributeClone.getType());
				attributeInEntity.setConfiguration(attributeClone
						.getConfiguration());
				attributeInEntity
						.setVersion(attributeInEntity.getVersion() + 1);
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

		this.entityDao.update(entity);
		return (Attribute) SerializationUtils.clone(attributeInEntity);

	}
}