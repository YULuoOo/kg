package com.kg.repository;

import com.kg.node.company;
import com.kg.node.person;
import com.kg.relationship.work_in;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;



public interface CompanyRepository extends Neo4jRepository<company,Long> {


	@Query("match(n:company) where n.c_name={name} return n.code")
	String getCompanyCode(@Param("name") String name);

	@Query("match(n:company) where n.c_name={name} return n.c_name_en")
	String getCompanyEngName(@Param("name") String name);

	@Query("match(n:company) where n.c_name={name} return n")
	List<company> getCompanyInfo(@Param("name") String name);

	@Query("MATCH p=(a:person)-[r:work_in]->(n:company) where n.c_name={name} RETURN a")
	List<person> getCompanyPersons(@Param("name") String name);

	@Query("MATCH p=(a:person)-[r:work_in]->(n:company) where n.c_name={name} RETURN r")
	List<work_in> getCompanyPersonsEdge(@Param("name") String name);
}
