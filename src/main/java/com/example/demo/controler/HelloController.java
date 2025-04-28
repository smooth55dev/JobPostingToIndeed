package com.example.demo.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.GraphQLService;
import com.example.demo.service.OAuthService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class HelloController {

    private final static Logger logger = LoggerFactory.getLogger(HelloController.class);
    private final OAuthService oauthService;  
    private final GraphQLService graphQLService;

    @Autowired
    public HelloController(OAuthService oauthService, GraphQLService graphQLService) {
        this.oauthService = oauthService;
        this.graphQLService = graphQLService;
    }
    
    @GetMapping("/")
    public String index() {
        String accessToken = oauthService.getAccessToken().block();

         String employerId = oauthService.getEmployer(accessToken).block();
         String getEmployerAccessToken = oauthService.getEmployerAccessToken(employerId).block();
        // logger.info(accessToken);
        // return employerId;
        String getUserInfo = oauthService.getUserInfo(getEmployerAccessToken).block();
        // String getAuthentication = oauthService.getAuthentication().block();                         
        return getUserInfo;
        // String requestBody = "{\n" +
        //     "  \"query\": \"mutation {\\n" +
        //     "    jobsIngest {\\n" +
        //     "      createSourcedJobPostings(input: {\\n" +
        //     "        jobPostings: [{\\n" +
        //     "          body: {\\n" +
        //     "            title: \\\"title 1\\\"\\n" +
        //     "            description: \\\"description 1\\\"\\n" +
        //     "            location: {\\n" +
        //     "              country: \\\"US\\\"\\n" +
        //     "              cityRegionPostal: \\\"Syracuse, New York 13209\\\"\\n" +
        //     "            }\\n" +
        //     "            benefits: []\\n" +
        //     "          }\\n" +
        //     "          metadata: {\\n" +
        //     "            jobSource: {\\n" +
        //     "              companyName: \\\"Company\\\"\\n" +
        //     "              sourceName: \\\"Source\\\"\\n" +
        //     "              sourceType: \\\"Employer\\\"\\n" +
        //     "            }\\n" +
        //     "            jobPostingId: \\\"JobId1\\\"\\n" +
        //     "            datePublished: \\\"2023-01-02T12:00Z\\\"\\n" +
        //     "            url: \\\"http://example.com/careers/job1.html\\\"\\n" +
        //     "            contacts: [{\\n" +
        //     "              contactType: [\\\"contact\\\", \\\"recruiter\\\"]\\n" +
        //     "              contactInfo: {\\n" +
        //     "                contactEmail: \\\"songdatadrop2@gmail.com\\\"\\n" +
        //     "                contactPhone: \\\"+10001112223\\\"\\n" +
        //     "                contactName: \\\"SL1\\\"\\n" +
        //     "              }\\n" +
        //     "            }]\\n" +
        //     "          }\\n" +
        //     "        }]\\n" +
        //     "      }) {\\n" +
        //     "        results {\\n" +
        //     "          jobPosting {\\n" +
        //     "            sourcedPostingId\\n" +
        //     "          }\\n" +
        //     "        }\\n" +
        //     "      }\\n" +
        //     "    }\\n" +
        //     "  }\\n" +
        //     "}" +
        //     "}";

        //     return graphQLService.postJob(accessToken, requestBody);
    
    }
}
