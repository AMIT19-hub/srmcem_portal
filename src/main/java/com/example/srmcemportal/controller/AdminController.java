package com.example.srmcemportal.controller;

import com.example.srmcemportal.entity.Application;
import com.example.srmcemportal.entity.Job;
import com.example.srmcemportal.entity.User;
import com.example.srmcemportal.repository.ApplicationRepository;
import com.example.srmcemportal.repository.JobRepository;
import com.example.srmcemportal.repository.UserRepository;
import com.example.srmcemportal.service.ApplicationService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final ApplicationService applicationService;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    public AdminController(ApplicationService applicationService, UserRepository userRepository,
                           JobRepository jobRepository,ApplicationRepository applicationRepository) {
        this.applicationService = applicationService;
        this.userRepository = userRepository;
        this.jobRepository=jobRepository;
        this.applicationRepository=applicationRepository;
    }

    @GetMapping("/applications")
    public List<Application> getAllApplications() {

        return applicationService.getAllApplications();
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/jobs")
    public List<Job> getAllJobs(){
        return jobRepository.findAll();
    }

    @Transactional
    @DeleteMapping("/jobs/{jobId}")
    public String deleteJob(@PathVariable Long jobId) {

        Job job=jobRepository.findById(jobId)
                .orElseThrow(()->new RuntimeException("job not found"));

        applicationRepository.deleteByJob(job);
        jobRepository.delete(job);
        return "Job deleted successfully";
    }
}