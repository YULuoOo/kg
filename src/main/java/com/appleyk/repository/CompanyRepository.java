package com.appleyk.repository;

import com.appleyk.node.Company;
import com.appleyk.node.Movie;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;



public interface CompanyRepository extends Neo4jRepository<Company,Long> {


	@Query("match(n:company) where n.c_name={name} return n.code")
	String getCompanyCode(@Param("name") String name);
	
}
