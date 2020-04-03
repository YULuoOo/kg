
package com.appleyk.relationship;
import com.appleyk.node.BaseEntity;
import com.appleyk.node.company;
import com.appleyk.node.person;
import org.codehaus.jackson.map.Serializers;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

/**
 * 关系-工作在
 */
@RelationshipEntity(type="work_in") // 此处注解中type的值就是数据库中关系存储的name值
public class work_in extends BaseEntity {

    @StartNode
    private person person;

    @EndNode
    private company company;


    private String p_job;
    private String p_notice_date;
    private String p_salary;
    private String p_term_date;
    private String p_type;


    public com.appleyk.node.person getPerson() {
        return person;
    }

    public void setPerson(com.appleyk.node.person person) {
        this.person = person;
    }

    public com.appleyk.node.company getCompany() {
        return company;
    }

    public void setCompany(com.appleyk.node.company company) {
        this.company = company;
    }

    public String getP_job() {
        return p_job;
    }

    public void setP_job(String p_job) {
        this.p_job = p_job;
    }

    public String getP_notice_date() {
        return p_notice_date;
    }

    public void setP_notice_date(String p_notice_date) {
        this.p_notice_date = p_notice_date;
    }

    public String getP_salary() {
        return p_salary;
    }

    public void setP_salary(String p_salary) {
        this.p_salary = p_salary;
    }

    public String getP_term_date() {
        return p_term_date;
    }

    public void setP_term_date(String p_term_date) {
        this.p_term_date = p_term_date;
    }

    public String getP_type() {
        return p_type;
    }

    public void setP_type(String p_type) {
        this.p_type = p_type;
    }
}