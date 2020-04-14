package com.appleyk.node;

import org.neo4j.ogm.annotation.Id;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.Map;

/**
 * 抽取共同的属性字段
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public abstract class BaseEntity {

	/**
	 * Neo4j会分配的ID（节点唯一标识 当前类中有效）
	 */
	@Id
	private Long id;	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 节点标签名称 == Node Labels
	 */
	private String label;

	/**
	 * 节点属性键值对 == Property Keys
	 */
	private Map<String, Object> properties;
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Map<String, Object> getProperties() {
		return properties;
	}

	/**
	 * 添加属性
	 * @param key
	 * @param value
	 */
	public void addProperty(String key,Object value){
		properties.put(key, value);
	}

	/**
	 * 拿到属性
	 * @param key
	 * @return
	 */
	public Object getProperty(String key){
		return properties.get(key);
	}
	/**
	 * 移除属性
	 * @param key
	 */
	public void removeProperty(String key){
		properties.remove(key);
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

}
