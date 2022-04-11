package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.fatmansoft.teach.models.Course;

@Entity
@Table(	name = "student",
        uniqueConstraints = {
        })
public class Student {
    @Id
    private Integer id;
    //级联保存、更新、删除、刷新;延迟加载。当删除用户，会级联删除该用户的所有日志信息
    //拥有mappedBy注解的实体类为关系被维护端
    //mappedBy="student"中的student是Log中的student属性
    @OneToMany(mappedBy = "student",cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    private List<Log> logList;//文章列表
    @OneToMany(mappedBy = "student",cascade=CascadeType.ALL,fetch=FetchType.LAZY)
    private List<Score> scoreList ;//文章列表



    @NotBlank
    @Size(max = 20)
    private String studentNum;

    @Size(max = 50)
    private String studentName;
    @Size(max = 2)
    private String sex;
    private Integer age;
    private Date birthday;
    @Size(max=11)
    private String teleNum;
    @Size(max = 50)
    private String dept;



    /*public List<Log> getLog() {
        return logList;
    }

    public void setLog(List<Log> logList) {
        this.logList = logList;
    }
*/
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getTeleNum() { return teleNum; }

    public void  setTeleNum(String tel) { this.teleNum = tel; }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    @ManyToMany(mappedBy = "studentList")
    private List<Course> courseList = new ArrayList<>();


    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

}