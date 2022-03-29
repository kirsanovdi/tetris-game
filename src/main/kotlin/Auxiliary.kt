const val windowHeight = 800
const val windowWidth = 400
const val windowTitle = "Tetris"
const val delay = 160L
const val updateRate = 10
const val heightGrip = 20
const val widthGrip = 10
const val lineScore = 400
const val line4Score = 2000
const val boxSize = 80
val gridColor = Color.BlACK

enum class Color {
    RED, GREEN, BLUE, BlACK, WHITE
}

enum class GameAction {
    RIGHT, LEFT, DOWN, ROTATE, NONE, RESTART
}

val figuresData = listOf(
    figureData(listOf(0, 0, 1, 0, 0, 1, 1, 1), true),
    figureData(listOf(-1, -1, 0, -1, 0, 0, 0, 1), false),
    figureData(listOf(1, -1, 0, -1, 0, 0, 0, 1), false),
    figureData(listOf(0, -1, 0, 0, 1, 0, 1, 1), false),
    figureData(listOf(1, -1, 1, 0, 0, 0, 0, 1), false),
    figureData(listOf(0, -1, 0, 0, 0, 1, 0, 2), true),
    figureData(listOf(-1, 0, 0, 0, 1, 0, 0, 1), false)
)

private fun figureData(list: List<Int>, boolean: Boolean): Pair<List<Cell>, Boolean> =
    (0..3).map { i -> Cell(list[i * 2], list[i * 2 + 1]) } to boolean

//-------------------------------
data class Cell(val row: Int, val column: Int)

interface MatrixInterface<E> {
    val height: Int
    val width: Int
    operator fun get(row: Int, column: Int): E
    operator fun get(cell: Cell): E
    operator fun set(row: Int, column: Int, value: E)
    operator fun set(cell: Cell, value: E)
}

fun <E> createMatrix(height: Int, width: Int, e: E): Matrix<E> = Matrix(height, width, e)