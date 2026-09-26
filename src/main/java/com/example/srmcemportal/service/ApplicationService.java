package com.example.srmcemportal.service;

import com.example.srmcemportal.entity.Application;
import com.example.srmcemportal.entity.ApplicationStatus;
import com.example.srmcemportal.entity.Job;
import com.example.srmcemportal.entity.User;
import com.example.srmcemportal.repository.ApplicationRepository;
import com.example.srmcemportal.repository.JobRepository;
import com.example.srmcemportal.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ApplicationService(ApplicationRepository applicationRepository, UserRepository userRepository, JobRepository jobRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public Application applyForJob(Long jobId, String email) {

        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("student not found"));


        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("job not found"));

        if (job.getLastDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Application deadline has passed");
        }

        if (applicationRepository.existsByStudentAndJob(student, job)) {
            throw new RuntimeException("you have already applied for this job");
        }

        Application application = new Application();
        application.setStudent(student);
        application.setJob(job);
        application.setAppliedAt(LocalDateTime.now());
        application.setStatus(ApplicationStatus.APPLIED);
        return applicationRepository.save(application);
    }

    public List<Application> getMyApplications(String email) {

        User student = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("student not found"));

        return applicationRepository.findByStudent(student);
    }

    public List<Application> getApplicantsForJob(Long jobId, String email) {
        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("recruiter not found"));

        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("job not found"));

        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new RuntimeException("joc can only see applicants for your own jobs");
        }
        return applicationRepository.findByJob(job);
    }
    @Transactional
    public Application updateStatus(Long applicationId,ApplicationStatus status, String email){

        User recruiter=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("recruiter not found"));

        Application application= applicationRepository.findById(applicationId)
                .orElseThrow(()->new RuntimeException("application not found"));

        Job job=application.getJob();
        if (!job.getRecruiter().getId()
                .equals(recruiter.getId())) {

            throw new RuntimeException(
                    "You can update applications only for your own jobs");
        }

        application.setStatus(status);
        return application;
    }

    public List<Application> getAllApplications(){
        return applicationRepository.findAll();
    }

    public void withdrawApplication(Long applicationId,String email){
        Application application=applicationRepository.findById(applicationId)
                .orElseThrow(()->new RuntimeException("Application not found"));

        User student=userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("student not found"));

        if(!application.getStudent().getId().equals(student.getId())){
            throw new RuntimeException("you can withdraw only your application");
        }

        applicationRepository.delete(application);
    }

    public List<Application> getMyJobApplications(String email) {

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        List<Job> jobs = jobRepository.findByRecruiter(recruiter);

        List<Application> applications = new ArrayList<>();

        for (Job job : jobs) {
            applications.addAll(
                    applicationRepository.findByJob(job)
            );
        }

        return applications;
    }
}
