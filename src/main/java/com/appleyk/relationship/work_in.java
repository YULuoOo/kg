
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
}