package com.sambi.app.rest.controllers;

import org.springframework.web.bind.annotation.*;

/**
 * @author rbensassi
 * 
 * TODO: all the Controllers (except AuthenticationController) should extends AbstractController!!!
 */
@RestController
@RequestMapping(value = "/task", produces = "application/json; charset=utf-8")
public class ExampleController extends AbstractController {

	/*@Autowired
	private TaskService taskService;

    @RequestMapping(value="/search", method=RequestMethod.POST)
    public List<TaskElement> search(@RequestBody TaskSearchCriteria options) {
        return taskService.searchTasks(options);
    }

    @RequestMapping(value="/approve/{taskId}", method = RequestMethod.GET)
    public MessageResponse approve(@PathVariable("taskId") long taskId) {

        final String userName = getAuthorizedUserName();
        this.taskService.approveTask(taskId, userName);
        return new MessageResponse("Task "+taskId+" freigegeben");
    }

    @RequestMapping(value="/abandon/{taskId}", method = RequestMethod.GET)
    public MessageResponse abandon(@PathVariable("taskId") long taskId) {

        final String userName = getAuthorizedUserName();
        this.taskService.abandonTask(taskId, userName);
        return new MessageResponse("Task "+taskId+" verworfen");
    }*/
}