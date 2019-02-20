package com.imooc.dao;

import com.imooc.domain.CourseClickCount;
import com.imooc.utils.HBaseUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CourseClickCountDAO {

    /**
     * 根据天查询
     */
    public List<CourseClickCount> query(String day) throws IOException {

        List<CourseClickCount> list = new ArrayList<>();

        //去HBase表中根据day获取实战课程对应的访问量
        Map<String, Long> map = HBaseUtils.getInstance().query("course_clickcount", day);

        for(Map.Entry<String, Long> entry: map.entrySet()){
            CourseClickCount model = new CourseClickCount();
            model.setName(entry.getKey());
            model.setValue(entry.getValue());

            list.add(model);
        }
        return list;
    }

    public static void main(String[] args) throws IOException {
        CourseClickCountDAO dao = new CourseClickCountDAO();
        List<CourseClickCount> list = dao.query("20190218");
        for(CourseClickCount model : list){
            System.out.println(model.getName() + " : " + model.getValue());
        }
    }
}
