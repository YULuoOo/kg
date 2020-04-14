package com.appleyk.repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.appleyk.node.BaseEntity;
import com.appleyk.node.company;
import com.appleyk.node.person;
import com.appleyk.relationship.work_in;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
@Repository
public class Driver {
    @Autowired
    private Session session;

    public static <T> T map2Object(Map<String, Object> map, Class<T> clazz) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (map == null) {
            return null;
        }
        T obj = null;
        try {
            // 使用newInstance来创建对象
            obj = clazz.newInstance();
            // 获取类中的所有字段
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                // 判断是拥有某个修饰符
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                // 当字段使用private修饰时，需要加上
                field.setAccessible(true);
                // 获取参数类型名字
                String filedTypeName = field.getType().getName();
                // 判断是否为时间类型，使用equalsIgnoreCase比较字符串，不区分大小写
                // 给obj的属性赋值
                if (filedTypeName.equalsIgnoreCase("java.util.date")) {
                    String datetimestamp = (String) map.get(field.getName());
                    if (datetimestamp.equalsIgnoreCase("null")) {
                        field.set(obj, null);
                    } else {
                        field.set(obj, sdf.parse(datetimestamp));
                    }
                } else {
                    field.set(obj, map.get(field.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
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
                /**
                 * 结果里面只要类型为节点的值
                 */
                if (value.type().name().equals("NODE")) {
                    Node noe4jNode = value.asNode();
                    BaseEntity c;
                    c = new company();
                    c.setProperties(noe4jNode.asMap());
                    Map<String, Object> map = c.getProperties();
                    Set<String> keySet = map.keySet();
                    JSONObject node = new JSONObject();
                    Iterator<String> it1 = keySet.iterator();
                    while (it1.hasNext()) {
                        String ID = it1.next();
                        String val = (String) map.get(ID);
                        if(ID.equals("code"))
                            ID = "name";
                        node.put(ID,val);
                        System.out.println(ID + " " + val);
                    }
                    nodes.put(String.valueOf(noe4jNode.id()),node);
                }
                else if(value.type().name().equals("RELATIONSHIP")) {
                    Relationship noe4jEdge = value.asRelationship();
                    work_in c = new work_in();
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
                    edges.add(edge);
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
        JSONObject edges = new JSONObject();
        JSONObject ret = new JSONObject();


        while (result.hasNext()) {
            Record record = result.next();
            for (Value value : record.values()) {
                System.out.println(value.toString());
                /**
                 * 结果里面只要类型为节点的值
                 */
                if (value.type().name().equals("NODE")) {
                    Node noe4jNode = value.asNode();
                    BaseEntity c;
                    Collection<String> labels = (Collection<String>) noe4jNode.labels();
                    if(labels.contains("company")) {
                        c = new company();
                    } else {
                        c = new person();
                    }
                    c.setProperties(noe4jNode.asMap());
                    Map<String, Object> map = c.getProperties();
                    Set<String> keySet = map.keySet();
                    JSONObject node = new JSONObject();
                    Iterator<String> it1 = keySet.iterator();
                    while (it1.hasNext()) {
                        String ID = it1.next();
                        String val = (String) map.get(ID);
                        node.put(ID,val);
                        System.out.println(ID + " " + val);
                    }
                    nodes.put(String.valueOf(noe4jNode.id()),node);
                }
                else if(value.type().name().equals("RELATIONSHIP")) {
                    Relationship noe4jEdge = value.asRelationship();
                    work_in c = new work_in();
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
                    edges.put(String.valueOf(noe4jEdge.id()),edge);
                }
            }
        }
        ret.put("nodes",nodes);
        ret.put("edges",edges);
        return ret;
    }
}
