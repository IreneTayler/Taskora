@ExtendWith(MockitoExtension::class)
class TaskServiceTest {

    @Mock
    lateinit var repository: TaskRepository

    @InjectMocks
    lateinit var service: TaskService
}