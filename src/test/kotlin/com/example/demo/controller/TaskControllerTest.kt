@WebFluxTest(TaskController::class)
class TaskControllerTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockBean
    lateinit var taskService: TaskService
}