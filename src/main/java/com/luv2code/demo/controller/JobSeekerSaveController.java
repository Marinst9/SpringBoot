package com.luv2code.demo.controller;


import com.luv2code.demo.entity.JobPostActivity;
import com.luv2code.demo.entity.JobSeekerProfile;
import com.luv2code.demo.entity.JobSeekerSave;
import com.luv2code.demo.entity.Users;
import com.luv2code.demo.services.JobPostActivityService;
import com.luv2code.demo.services.JobSeekerProfileService;
import com.luv2code.demo.services.JobSeekerSaveService;
import com.luv2code.demo.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class JobSeekerSaveController {

    private final UsersService usersService;

    private final JobSeekerProfileService jobSeekerProfileService;

    private final JobPostActivityService jobPostActivityService;

    private final JobSeekerSaveService jobSeekerSaveService;


    @Autowired
    public JobSeekerSaveController(UsersService usersService,
                                    JobSeekerProfileService jobSeekerProfileService,
                                   JobPostActivityService jobPostActivityService,
                                   JobSeekerSaveService jobSeekerSaveService) {
        this.usersService = usersService;
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerSaveService = jobSeekerSaveService;
    }

    @PostMapping("job-details/save/{id}")
    public String save(@PathVariable("id") int id, JobSeekerSave jobSeekerSave){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)){
            String currentUsername = authentication.getName();
            Users user = usersService.findByEmail(currentUsername);
            Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(user.getUserId());
            JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
            if (seekerProfile.isPresent() && jobPostActivity != null){
                jobSeekerSave.setJobPostActivity(jobPostActivity);
                jobSeekerSave.setUserId(seekerProfile.get());
            } else {
                throw new RuntimeException("User not found");
            }
            jobSeekerSaveService.addNew(jobSeekerSave);
        }
        return "redirect:/dashboard/";
    }

    @GetMapping("saved-jobs/")
    public String savedJobs(Model model){

        List<JobPostActivity> jobPost = new ArrayList<>();
        Object currentUserProfile = usersService.getCurrentUserProfile();

        List<JobSeekerSave> jobSeekerSaveList =
                jobSeekerSaveService.getCandidatesJob((JobSeekerProfile) currentUserProfile);

        for (JobSeekerSave jobSeekerSave : jobSeekerSaveList){
            jobPost.add(jobSeekerSave.getJobPostActivity());
        }

        model.addAttribute("jobPost", jobPost);
        model.addAttribute("user", currentUserProfile);

        return "saved-jobs";
    }
}