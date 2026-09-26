package com.example.srmcemportal.repository;

import com.example.srmcemportal.entity.Application;
import com.example.srmcemportal.entity.Job;
import com.example.srmcemportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application,Long> {

    boolean existsByStudentAndJob(User student, Job job);
    List<Application> findByStudent(User user);

    List<Application> findByJob(Job job);
    void deleteByJob(Job job);


}
