package com.kg.repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kg.node.BaseEntity;
import com.kg.relationship.BaseRelationship;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class Driver {
    @Autowired
    private Session session;


    public JSONObject addNode(JSONObject nodes,Value value,String name,String type){
        Node noe4jNode = value.asNode();
        BaseEntity c = new BaseEntity();
        c.setProperties(noe4jNode.asMap());
        Map<String, Object> map = c.getProperties();
        Set<String> keySet = map.keySet();
        JSONObject node = new JSONObject();
        Iterator<String> it1 = keySet.iterator();
        while (it1.hasNext()) {
            String ID = it1.next();
            String val = (String) map.get(ID);
            if(ID.equals(name))
                node.put("name",val);
            node.put(ID,val);
            System.out.println(ID + " " + val);
        }
        node.put("type",type);
        node.put("id",String.valueOf(noe4jNode.id()));
        nodes.put(String.valueOf(noe4jNode.id()),node);
        return node;
    }

    public JSONObject addNode2(JSONObject nodes,Node noe4jNode,String name,String type){
        BaseEntity c = new BaseEntity();
        c.setProperties(noe4jNode.asMap());
        Map<String, Object> map = c.getProperties();
        Set<String> keySet = map.keySet();
        JSONObject node = new JSONObject();
        Iterator<String> it1 = keySet.iterator();
        while (it1.hasNext()) {
            String ID = it1.next();
            String val = (String) map.get(ID);
            if(ID.equals(name))
                node.put("name",val);
            node.put(ID,val);
            System.out.println(ID + " " + val);
        }
        node.put("type",type);
        node.put("id",String.valueOf(noe4jNode.id()));
        nodes.put(String.valueOf(noe4jNode.id()),node);
        return node;
    }

    public JSONObject addEdge(JSONArray edges,Value value,String name,JSONObject from,JSONObject to){
        Relationship noe4jEdge = value.asRelationship();
        BaseRelationship c = new BaseRelationship();
        c.setProperties(noe4jEdge.asMap());
        JSONObject edge = new JSONObject();
        Map<String, Object> map = c.getProperties();
        Set<String> keySet = map.keySet();
        Iterator<String> it1 = keySet.iterator();
        while (it1.hasNext()) {
            String ID = it1.next();
            String val = (String) map.get(ID);
            edge.put(ID,val);
            System.out.println(ID + " " + val);
        }
        edge.put("rela",name);
        edge.put("type",name);
        edge.put("source",from.get("id"));
        edge.put("target",to.get("id"));
        edges.add(edge);
        return edge;
    }

    public JSONObject addEdge2(JSONArray edges,Relationship noe4jEdge,String name,Long from,Long to){
        BaseRelationship c = new BaseRelationship();
        c.setProperties(noe4jEdge.asMap());
        JSONObject edge = new JSONObject();
        Map<String, Object> map = c.getProperties();
        Set<String> keySet = map.keySet();
        Iterator<String> it1 = keySet.iterator();
        while (it1.hasNext()) {
            String ID = it1.next();
            String val = (String) map.get(ID);
            edge.put(ID,val);
            System.out.println(ID + " " + val);
        }
        edge.put("rela",name);
        edge.put("type",name);
        edge.put("source",from);
        edge.put("target",to);
        edges.add(edge);
        System.out.println(edge.toJSONString());
        return edge;
    }


    public JSONObject get_code(String question) throws Exception {
        String cypherSql = String.format("match(n:company) where n.c_name=\"%s\" return n",question);
        StatementResult result = session.run(cypherSql);
        JSONObject nodes = new JSONObject();
        JSONArray edges = new JSONArray();
        JSONObject ret = new JSONObject();


        while (result.hasNext()) {
            Record record = result.next();
            for (Value value : record.values()) {
                System.out.println(value.toString());
                if (value.type().name().equals("NODE")) {
                    addNode(nodes,value,"code","company");
                }
            }
        }
        ret.put("nodes",nodes);
        ret.put("edges",edges);
        return ret;
    }
    public JSONObject get_c_info(String question) throws Exception {
        String cypherSql = String.format("match(n:company) where n.c_name=\"%s\" return n",question);
        StatementResult result = session.run(cypherSql);
        JSONObject nodes = new JSONObject();
        JSONArray edges = new JSONArray();
        JSONObject ret = new JSONObject();


        while (result.hasNext()) {
            Record record = result.next();
            for (Value value : record.values()) {
                System.out.println(value.toString());
                if (value.type().name().equals("NODE")) {
                    addNode(nodes,value,"c_name","company");
                }
            }
        }
        ret.put("nodes",nodes);
        ret.put("edges",edges);
        return ret;
    }
    public JSONObject get_eng(String question) throws Exception {
        String cypherSql = String.format("match(n:company) where n.c_name=\"%s\" return n",question);
        StatementResult result = session.run(cypherSql);
        JSONObject nodes = new JSONObject();
        JSONArray edges = new JSONArray();
        JSONObject ret = new JSONObject();


        while (result.hasNext()) {
            Record record = result.next();
            for (Value value : record.values()) {
                System.out.println(value.toString());
                if (value.type().name().equals("NODE")) {
                    addNode(nodes,value,"c_name_en","company");
                }
            }
        }
        ret.put("nodes",nodes);
        ret.put("edges",edges);
        return ret;
    }

    public JSONObject work_in(String question) throws Exception {
        String cypherSql = String.format("MATCH p=(a:person)-[r:work_in]->(n:company) where n.c_name= \"%s\" RETURN a,r,n",question);
        StatementResult result = session.run(cypherSql);
        JSONObject nodes = new JSONObject();
        JSONArray edges = new JSONArray();
        JSONObject ret = new JSONObject();
        JSONObject from = new JSONObject();
        JSONObject to = new JSONObject();
        JSONObject rela;
        Value temp = null;

        boolean flag = false;

        while (result.hasNext()) {
            Record record = result.next();
            for (Value value : record.values()) {
                System.out.println(value.toString());
                /**
                 * 结果里面只要类型为节点的值
                 */
                if (value.type().name().equals("NODE")) {
                    Collection<String> labels = (Collection<String>) value.asNode().labels();
                    if(labels.contains("company")) {
                        to = addNode(nodes,value,"c_name","company");
                        if(flag = true && temp != null){
                            addEdge(edges,temp,"work_in",from,to);
                            flag = false;
                        }
                    } else {
                        from = addNode(nodes,value,"p_name","person");
                        if(flag = true && temp != null){
                            addEdge(edges,temp,"work_in",from,to);
                            flag = false;
                        }
                    }
                }
                else if(value.type().name().equals("RELATIONSHIP")) {
                    temp = value;
                    flag = true;
                }
            }
        }
        ret.put("nodes",nodes);
        ret.put("edges",edges);
        return ret;
    }
    public JSONObject stock_holder(String question) throws Exception {
        String cypherSql = String.format("MATCH p=(c:Holder)-[r:is_holder_of]->(s:stock) where s.code = \"%s\" RETURN c,r,s limit 10",question);
        StatementResult result = session.run(cypherSql);
        JSONObject nodes = new JSONObject();
        JSONArray edges = new JSONArray();
        JSONObject ret = new JSONObject();
        JSONObject from = new JSONObject();
        JSONObject to = new JSONObject();
        JSONObject rela;
        Value temp = null;

        boolean flag = false;

        while (result.hasNext()) {
            Record record = result.next();
            for (Value value : record.values()) {
                System.out.println(value.toString());
                /**
                 * 结果里面只要类型为节点的值
                 */
                if (value.type().name().equals("NODE")) {
                    Collection<String> labels = (Collection<String>) value.asNode().labels();
                    if(labels.contains("Holder")) {
                        from = addNode(nodes,value,"holder_name","Holder");
                        if(flag = true && temp != null){
                            addEdge(edges,temp,"is_holder_of",from,to);
                            flag = false;
                        }
                    } else {
                        to = addNode(nodes,value,"code","stock");
                        if(flag = true && temp != null){
                            addEdge(edges,temp,"is_holder_of",from,to);
                            flag = false;
                        }
                    }
                }
                else if(value.type().name().equals("RELATIONSHIP")) {
                    temp = value;
                    flag = true;
                }
            }
        }
        ret.put("nodes",nodes);
        ret.put("edges",edges);
        return ret;
    }
    //陈贤军任职公司的行业
    public JSONObject person_company_industry(String question) throws Exception {
        String cypherSql = String.format("MATCH p1=(p:person)-[r1:work_in]->(c:company) where p.p_name = \"%s\" with p1,p,r1,c match p2=(c:company)-[r2:c_belong_to]->(i:industry) RETURN p1,p2",question);
        StatementResult result = session.run(cypherSql);
        JSONObject nodes = new JSONObject();
        JSONArray edges = new JSONArray();
        JSONObject ret = new JSONObject();
        Map<Long, Node> nodesMap = new HashMap<>();

        while (result.hasNext()) {
            Record record = result.next();
            for (Value value : record.values()) {
                if (value.type().name().equals("PATH")) {
                    Path p = value.asPath();
                    Iterable<Node> nodess = p.nodes();
                    for (Node node : nodess) {
                        nodesMap.put(node.id(), node);
                    }
                    Iterable<Relationship> relationships = p.relationships();
                    for (Relationship relationship : relationships) {
                        Long startID = relationship.startNodeId();
                        Long endID = relationship.endNodeId();
                        String rType = relationship.type();
                        System.out.println("-------"+startID);
                        if(rType.equals("work_in")) {
                            addNode2(nodes, nodesMap.get(startID), "p_name", "person");
                            addNode2(nodes, nodesMap.get(endID), "c_name", "company");
                            addEdge2(edges, relationship, "work_in", startID, endID);
                        } else {
                            addNode2(nodes, nodesMap.get(startID), "c_name", "company");
                            addNode2(nodes, nodesMap.get(endID), "i_name", "industry");
                            addEdge2(edges, relationship, "c_belong_to", startID, endID);
                        }
                    }
                }
            }
        }
        ret.put("nodes",nodes);
        ret.put("edges",edges);
        return ret;
    }
    //国泰君安证券股份有限公司主承销的股票有新材料概念概念的
    //中国工商银行股份有限公司主承销的有锂电池概念的股票
    public JSONObject underwrite_concept_industry(String s1,String s2) throws Exception {
        String cypherSql = String.format("MATCH p1=(c:company)-[r:is_lead_underwriter_of]->(s:stock), p2=(s:stock)-[r2:have_a_concept]->(co:Concept) where c.c_name = \"%s\" and co.c_name = \"%s\" RETURN p1,p2 LIMIT 25",s1,s2);
        StatementResult result = session.run(cypherSql);
        JSONObject nodes = new JSONObject();
        JSONArray edges = new JSONArray();
        JSONObject ret = new JSONObject();
        Map<Long, Node> nodesMap = new HashMap<>();

        while (result.hasNext()) {
            Record record = result.next();
            for (Value value : record.values()) {
                if (value.type().name().equals("PATH")) {
                    Path p = value.asPath();
                    Iterable<Node> nodess = p.nodes();
                    for (Node node : nodess) {
                        nodesMap.put(node.id(), node);
                    }
                    Iterable<Relationship> relationships = p.relationships();
                    for (Relationship relationship : relationships) {
                        Long startID = relationship.startNodeId();
                        Long endID = relationship.endNodeId();
                        String rType = relationship.type();
                        System.out.println("-------"+startID);
                        if(rType.equals("is_lead_underwriter_of")) {
                            addNode2(nodes, nodesMap.get(startID), "c_name", "company");
                            addNode2(nodes, nodesMap.get(endID), "code", "stock");
                            addEdge2(edges, relationship, "is_lead_underwriter_of", startID, endID);
                        } else if(rType.equals("have_a_concept")) {
                            addNode2(nodes, nodesMap.get(startID), "code", "stock");
                            addNode2(nodes, nodesMap.get(endID), "c_name", "concept");
                            addEdge2(edges, relationship, "have_a_concept", startID, endID);
                        }
                    }
                }
            }
        }
        ret.put("nodes",nodes);
        ret.put("edges",edges);
        return ret;
    }
    //天邦食品股份有限公司什么业务收入最高
    public JSONObject business_income_rate_highest(String s1) throws Exception {
        String cypherSql = String.format(" MATCH p=(c:company)-[r:have_income_on]->(b:business) where c.c_name = \"%s\" with p,c,r,b  ORDER BY r.income_rate DESC LIMIT 1 RETURN p",s1);
        StatementResult result = session.run(cypherSql);
        JSONObject nodes = new JSONObject();
        JSONArray edges = new JSONArray();
        JSONObject ret = new JSONObject();
        Map<Long, Node> nodesMap = new HashMap<>();

        while (result.hasNext()) {
            Record record = result.next();
            for (Value value : record.values()) {
                if (value.type().name().equals("PATH")) {
                    Path p = value.asPath();
                    Iterable<Node> nodess = p.nodes();
                    for (Node node : nodess) {
                        nodesMap.put(node.id(), node);
                    }
                    Iterable<Relationship> relationships = p.relationships();
                    for (Relationship relationship : relationships) {
                        Long startID = relationship.startNodeId();
                        Long endID = relationship.endNodeId();
                        String rType = relationship.type();
                        System.out.println("-------"+startID);
                        if(rType.equals("have_income_on")) {
                            String str1 = (String) relationship.asMap().get("income_rate");
                            String str2 = (String) relationship.asMap().get("income");

                            addNode2(nodes, nodesMap.get(startID), "c_name", "company");
                            addNode2(nodes, nodesMap.get(endID), "b_name", "business");
                            addEdge2(edges, relationship, "最高为"+str2+str1, startID, endID);
                        }
                    }
                }
            }
        }
        ret.put("nodes",nodes);
        ret.put("edges",edges);
        return ret;
    }
    //002967股票上市日期为同一天的有
    public JSONObject same_date_shangshi(String s1) throws Exception {
        String cypherSql = String.format("MATCH p1=(a:stock)-[r:`上市日期为`]->(b:`日期`),p2=(a2:stock)-[r2:`上市日期为`]->(b2:`日期`) where a.code = \"%s\" and b.name = b2.name  RETURN p1,p2 ",s1);
        StatementResult result = session.run(cypherSql);
        JSONObject nodes = new JSONObject();
        JSONArray edges = new JSONArray();
        JSONObject ret = new JSONObject();
        Map<Long, Node> nodesMap = new HashMap<>();

        while (result.hasNext()) {
            Record record = result.next();
            for (Value value : record.values()) {
                if (value.type().name().equals("PATH")) {
                    Path p = value.asPath();
                    Iterable<Node> nodess = p.nodes();
                    for (Node node : nodess) {
                        nodesMap.put(node.id(), node);
                    }
                    Iterable<Relationship> relationships = p.relationships();
                    for (Relationship relationship : relationships) {
                        Long startID = relationship.startNodeId();
                        Long endID = relationship.endNodeId();
                        String rType = relationship.type();
                        System.out.println("-------"+startID);
                        if(rType.equals("上市日期为")) {
                            addNode2(nodes, nodesMap.get(startID), "code", "stock");
                            addNode2(nodes, nodesMap.get(endID), "name", "日期");
                            addEdge2(edges, relationship, "上市日期为", startID, endID);
                        }
                    }
                }
            }
        }
        ret.put("nodes",nodes);
        ret.put("edges",edges);
        return ret;
    }
    //在相同省份都拥有LED产品的公司
    public JSONObject product_same_position(String s1) throws Exception {
        String cypherSql = String.format("MATCH p1=(t1)-[rr:`位于`]-(g:公司)-[r1:`拥有产品`]-(p:产品),p2=(t2)-[rr2:`位于`]-(g2:公司)-[r2:`拥有产品`]-(p:产品) where t1.name = t2.name and p.name = \"%s\" RETURN p1,p2 ",s1);
        StatementResult result = session.run(cypherSql);
        JSONObject nodes = new JSONObject();
        JSONArray edges = new JSONArray();
        JSONObject ret = new JSONObject();
        Map<Long, Node> nodesMap = new HashMap<>();

        while (result.hasNext()) {
            Record record = result.next();
            for (Value value : record.values()) {
                if (value.type().name().equals("PATH")) {
                    Path p = value.asPath();
                    Iterable<Node> nodess = p.nodes();
                    for (Node node : nodess) {
                        nodesMap.put(node.id(), node);
                    }
                    Iterable<Relationship> relationships = p.relationships();
                    for (Relationship relationship : relationships) {
                        Long startID = relationship.startNodeId();
                        Long endID = relationship.endNodeId();
                        String rType = relationship.type();
                        System.out.println("-------"+startID);
                        if(rType.equals("位于")) {
                            addNode2(nodes, nodesMap.get(startID), "name", "公司");
                            addNode2(nodes, nodesMap.get(endID), "name", "地区");
                            addEdge2(edges, relationship, "位于", startID, endID);
                        }else if(rType.equals("拥有产品")) {
                            addNode2(nodes, nodesMap.get(startID), "name", "公司");
                            addNode2(nodes, nodesMap.get(endID), "那么", "产品");
                            addEdge2(edges, relationship, "拥有产品", startID, endID);
                        }
                    }
                }
            }
        }
        ret.put("nodes",nodes);
        ret.put("edges",edges);
        return ret;
    }
}
