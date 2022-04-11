package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Dailyactivity;
import org.fatmansoft.teach.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DailyactivityRepository extends JpaRepository<Dailyactivity,Integer> {

    Optional<Dailyactivity> findByStudentNum(String studentNum);
    List<Dailyactivity> findByStudentName(String studentName);

    @Query(value = "select max(id) from Dailyactivity  ")
    Integer getMaxId();

    @Query(value = "from Dailyactivity where ?1='' or studentNum like %?1% or studentName like %?1% ")
    List<Dailyactivity> findStudentListByNumName(String numName);

}