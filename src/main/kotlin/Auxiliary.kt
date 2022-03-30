const val windowHeight = 800 // высота окна
const val windowWidth = 400 // ширина окна
const val windowTitle = "Tetris" // название
const val delay = 160L // время 1 такта
const val updateRate = 10 // количество обновлений в одном такте
const val heightGrip = 20 // высота игрового поля
const val widthGrip = 10 // ширина игрового поля
const val lineScore = 400 // количество очков за 1 заполненную строчку (при общем количестве <4)
const val line4Score = 2000 // количество очков за 4 заполненных строчки
const val boxSize = 40 // размер 1 клетки
val gridColor = Color.BlACK // цвет сетки

/**цвета*/
enum class Color {
    RED, GREEN, BLUE, BlACK, WHITE
}
/**команды*/
enum class GameAction {
    RIGHT, LEFT, DOWN, ROTATE, NONE, RESTART
}

val figuresData = listOf( // данные для фигур
    figureData(listOf(0, 0, 1, 0, 0, 1, 1, 1), true),
    figureData(listOf(-1, -1, 0, -1, 0, 0, 0, 1), false),
    figureData(listOf(1, -1, 0, -1, 0, 0, 0, 1), false),
    figureData(listOf(0, -1, 0, 0, 1, 0, 1, 1), false),
    figureData(listOf(1, -1, 1, 0, 0, 0, 0, 1), false),
    figureData(listOf(0, -1, 0, 0, 0, 1, 0, 2), true),
    figureData(listOf(-1, 0, 0, 0, 1, 0, 0, 1), false)
)
/**функция для более удобного представления данных в figuresData*/
private fun figureData(list: List<Int>, boolean: Boolean): Pair<List<Cell>, Boolean> =
    (0..3).map { i -> Cell(list[i * 2], list[i * 2 + 1]) } to boolean

/**ячейка матрицы*/
data class Cell(val row: Int, val column: Int)

/**интерфейс матрицы*/
interface MatrixInterface<E> {
    val height: Int
    val width: Int
    operator fun get(row: Int, column: Int): E
    operator fun get(cell: Cell): E
    operator fun set(row: Int, column: Int, value: E)
    operator fun set(cell: Cell, value: E)
}

/**fabric-метод для матрицы*/
fun <E> createMatrix(height: Int, width: Int, e: E): Matrix<E> = Matrix(height, width, e)