import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteBuilder(
    onQuizFinished: () -> Unit,
    updateScore: (Int) -> Unit
){}
// TODO make randomized quizzes
// kinda useful funky green eye attractor
