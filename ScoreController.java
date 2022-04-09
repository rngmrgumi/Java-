package org.fatmansoft.teach.controllers;

import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.extend.impl.FSDefaultCacheStore;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.ScoreRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.service.IntroduceService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import java.io.InputStream;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")


public class ScoreController {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private IntroduceService introduceService;
    @Autowired
    private ResourceLoader resourceLoader;
    private FSDefaultCacheStore fSDefaultCacheStore = new FSDefaultCacheStore();


    public List getScoreMapList(String numName) {
        List dataList = new ArrayList();
        List<Score> sList = scoreRepository.findAll();  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Score sc;
        Student s;
        Course c;
        Map m;
        String courseParas,studentNameParas;
        for(int i = 0; i < sList.size();i++) {
            sc = sList.get(i);
            s = sc.getStudent();
            c = sc.getCourse();
            m = new HashMap();
            m.put("id", sc.getId());
            m.put("courseNum",c.getCourseNum());
            m.put("courseName",c.getCourseName());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());
            m.put("mark",sc.getMark());
            dataList.add(m);
        }
        return dataList;
    }
    @PostMapping("/scoreInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse scoreInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getScoreMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    //  学生信息删除方法
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/scoreDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse scoreDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Score s= null;
        Optional<Score> op;
        if(id != null) {
            op= scoreRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            scoreRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    @PostMapping("/scoreEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse scoreEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Score sc= null;
        Student s;
        Course c;
        Optional<Score> op;
        if(id != null) {
            op= scoreRepository.findById(id);
            if(op.isPresent()) {
                sc = op.get();
            }
        }
        Map m;
        int i;
        List studentIdList = new ArrayList();
        List<Student> sList = studentRepository.findAll();
        for(i = 0; i <sList.size();i++) {
            s =sList.get(i);
            m = new HashMap();
            m.put("label",s.getStudentName());
            m.put("value",s.getId());
            studentIdList.add(m);
        }
        List courseIdList = new ArrayList();
        List<Course> cList = courseRepository.findAll();
        for(i = 0; i <sList.size();i++) {
            c =cList.get(i);
            m = new HashMap();
            m.put("label",c.getCourseName());
            m.put("value",c.getId());
            courseIdList.add(m);
        }
        Map form = new HashMap();
        form.put("studentId","");
        form.put("courseId","");
        if(sc != null) {
            form.put("id",sc.getId());
            form.put("studentId",sc.getStudent().getId());
            form.put("courseId",sc.getCourse().getId());
            form.put("mark",sc.getMark());
        }
        form.put("studentIdList",studentIdList);
        form.put("courseIdList",courseIdList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }
    public synchronized Integer getNewScoreId(){
        Integer  id = scoreRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    @PostMapping("/scoreEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse scoreEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        Integer studentId = CommonMethod.getInteger(form,"studentId");
        Integer courseId = CommonMethod.getInteger(form,"courseId");
        Integer mark = CommonMethod.getInteger(form,"mark");
        Score sc= null;
        Student s= null;
        Course c = null;
        Optional<Score> op;
        if(id != null) {
            op= scoreRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                sc = op.get();
            }
        }
        if(sc == null) {
            sc = new Score();   //不存在 创建实体对象
            id = getNewScoreId(); //获取鑫的主键，这个是线程同步问题;
            sc.setId(id);  //设置新的id
        }
        sc.setStudent(studentRepository.findById(studentId).get());  //设置属性
        sc.setCourse(courseRepository.findById(courseId).get());
        sc.setMark(mark);
        scoreRepository.save(sc);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(sc.getId());  // 将记录的id返回前端
    }


}
