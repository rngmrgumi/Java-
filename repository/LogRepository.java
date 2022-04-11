package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Log;
import org.fatmansoft.teach.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface LogRepository extends JpaRepository<Log,Integer>  {
    /*List<Log> findByStudentId(Integer studentId);
    List<Log> findByStudentName(String studentName);*/

    @Query(value = "select max(id) from Log  ")
    Integer getMaxId();





}
