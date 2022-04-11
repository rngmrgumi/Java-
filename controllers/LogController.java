package org.fatmansoft.teach.controllers;



import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.repository.LogRepository;
import org.fatmansoft.teach.models.Dailyactivity;
import org.fatmansoft.teach.models.Log;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.service.IntroduceService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")


public class LogController {

    @Autowired
    private LogRepository logRepository;
    @Autowired
    private IntroduceService introduceService;
    @Autowired
    private StudentRepository studentRepository;


    public List getlogMapList(String numName) {
        List dataList = new ArrayList();
        List<Log> sList = logRepository.findAll();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        Log l;
        Student s;
        Map m;
        for (int i = 0; i < sList.size(); i++) {
            l = sList.get(i);
            s = l.getStudent();
            m = new HashMap();
            m.put("id", l.getId());
            m.put("studentNum", s.getStudentNum());
            m.put("studentName", s.getStudentName());
            m.put("logmessage",l.getLogmessage());

            dataList.add(m);
        }
        return dataList;
    }

    //log页面初始化方法
    //Table界面初始是请求列表的数据，这里缺省查出所有学生的信息，传递字符“”给方法getlogMapList，返回所有学生数据，
    @PostMapping("/logInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse logInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getlogMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    //log页面点击查询按钮请求
    //Table界面初始是请求列表的数据，从请求对象里获得前端界面输入的字符串，作为参数传递给方法getlogMapList，返回所有学生数据，
@PostMapping("/logQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse logQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List dataList = getlogMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }


    //  学生信息删除方法
    //log页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/logDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse logDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Log s = null;
        Optional<Log> op;
        if (id != null) {
            op = logRepository.findById(id);   //查询获得实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s != null) {
            logRepository.delete(s);//数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    //logEdit初始化方法
    //logEdit编辑页面进入时首先请求的一个方法， 如果是Edit,再前台会把对应要编辑的那个学生信息的id作为参数回传给后端，我们通过Integer id = dataRequest.getInteger("id")
    //获得对应学生的id， 根据id从数据库中查出数据，存在Map对象里，并返回前端，如果是添加， 则前端没有id传回，Map 对象数据为空（界面上的数据也为空白）

    @PostMapping("/logEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse logEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Log l = null;
        Student s;
        Optional<Log> op;
        if (id != null) {
            op = logRepository.findById(id);
            if (op.isPresent()) {
                l = op.get();
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
        Map form = new HashMap();
        form.put("studentId","");
        if (l != null) {
            form.put("id", l.getId());
            form.put("studentId",l.getStudent().getId());
            form.put("logmessage", l.getLogmessage());  //这里不需要转换

        }
        form.put("studentIdList",studentIdList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }

    //  学生信息提交按钮方法
    //相应提交请求的方法，前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
    //实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new log 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
    //id 不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
    public synchronized Integer getNewlogId() {
        Integer id = logRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }

    ;

    @PostMapping("/logEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse logEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form, "id");
        String logmessage = CommonMethod.getString(form, "logmessage");
        Integer studentId = CommonMethod.getInteger(form,"studentId");
        Log l = null;
 Student s= null;

        Optional<Log> op;
        if (id != null) {
            op = logRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                l = op.get();
            }
        }
        if (l == null) {
            l = new Log();   //不存在 创建实体对象
            id = getNewlogId(); //获取鑫的主键，这个是线程同步问题;
            l.setId(id);  //设置新的id
        }
        l.setStudent(studentRepository.findById(studentId).get());
        l.setLogmessage(logmessage);
        logRepository.save(l);//新建和修改都调用save方法

        return CommonMethod.getReturnData(l.getId());  // 将记录的id返回前端
    }







}

