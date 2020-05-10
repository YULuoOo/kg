package com.kg.node;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

@NodeEntity
public class person extends BaseEntity {

    private String p_intro;
    private String p_mainintro;
    private String p_name;

    @Relationship(type="work_in", direction=Relationship.OUTGOING)
    private List<company> companies;

    public person(){
    }

    public String getP_intro() {
        return p_intro;
    }

    public void setP_intro(String p_intro) {
        this.p_intro = p_intro;
    }

    public String getP_mainintro() {
        return p_mainintro;
    }

    public void setP_mainintro(String p_mainintro) {
        this.p_mainintro = p_mainintro;
    }

    public String getP_name() {
        return p_name;
    }

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }

    public List<company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<company> companies) {
        this.companies = companies;
    }
}