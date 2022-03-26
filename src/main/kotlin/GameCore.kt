import kotlin.random.Random

class GameCore(
    val height: Int,
    val width: Int,
    private val controller: Controller
) {
    private val field = createMatrix(height, width, Color.WHITE)
    var figure: Figure = emptyFigure()
    private var score = 0
    private var isGameOver = false
    operator fun get(row: Int, column: Int): Color = field[row, column]

    private val figuresData = listOf(
        listOf(Cell(0, 0), Cell(1, 0), Cell(0, 1), Cell(1, 1)) to true,//[]
        listOf(Cell(-1, -1), Cell(0, -1), Cell(0, 0), Cell(0, 1)) to false,//,--
        listOf(Cell(1, -1), Cell(0, -1), Cell(0, 0), Cell(0, 1)) to false,//[__
        listOf(Cell(0, -1), Cell(0, 0), Cell(1, 0), Cell(1, 1)) to false,//_/-
        listOf(Cell(1, -1), Cell(1, 0), Cell(0, 0), Cell(0, 1)) to false,//-\_
        listOf(Cell(0, -1), Cell(0, 0), Cell(0, 1), Cell(0, 2)) to true,//----
        listOf(Cell(-1, 0), Cell(0, 0), Cell(1, 0), Cell(0, 1)) to false//_|_
    )

    private fun emptyFigure(): Figure = Figure(listOf(), Color.WHITE, 0, 0, false)

    class Figure(
        var list: List<Cell>,
        var color: Color,
        var yCord: Int,
        var xCord: Int,
        val isSpecial: Boolean
    ) {
        fun checkCurrentCapability(checkingField: Matrix<Color>): Boolean = list.all { cell ->
            Cell(
                cell.row + yCord,
                cell.column + xCord
            ).let { checkingField.isValid(it) && checkingField[it] == Color.WHITE }
        }

        fun checkFallCapability(checkingField: Matrix<Color>): Boolean = list.all { cell ->
            Cell(
                cell.row + yCord - 1,
                cell.column + xCord
            ).let { checkingField.isValid(it) && checkingField[it] == Color.WHITE }
        }

        fun checkRightCapability(checkingField: Matrix<Color>): Boolean = list.all { cell ->
            Cell(
                cell.row + yCord,
                cell.column + xCord + 1
            ).let { checkingField.isValid(it) && checkingField[it] == Color.WHITE }
        }

        fun checkLeftCapability(checkingField: Matrix<Color>): Boolean = list.all { cell ->
            Cell(
                cell.row + yCord,
                cell.column + xCord - 1
            ).let { checkingField.isValid(it) && checkingField[it] == Color.WHITE }
        }
    }

    private fun spawnNewFigure(cFigure: Figure, color: Color) {
        figure = cFigure
        figure.color = color

        if (cFigure.checkCurrentCapability(field)) {
            val rotateCast = listOf(-1 to 1, -1 to 1)
            var i = 0
            while (cFigure.checkFallCapability(field) && !controller.toClose) {
                for (j in 0 until updateRate) {
                    do {
                        Thread.sleep(delay / updateRate)
                        val action = controller.getGameAction()
                        when (action) {
                            GameAction.LEFT -> if (cFigure.checkLeftCapability(field)) cFigure.xCord--
                            GameAction.RIGHT -> if (cFigure.checkRightCapability(field)) cFigure.xCord++
                            GameAction.DOWN -> {
                                if (cFigure.checkFallCapability(field)) cFigure.yCord--
                                if (cFigure.checkFallCapability(field)) cFigure.yCord--
                            }
                            GameAction.ROTATE -> {
                                val nextStepList = rotateCast[i++ % 2].let { cast ->
                                    cFigure.list.map { cell ->
                                        Cell(
                                            cell.column * cast.first + if (cFigure.isSpecial) 1 else 0,
                                            cell.row * cast.second
                                        )
                                    }
                                }
                                if (nextStepList.all { cell ->
                                        Cell(
                                            cell.row + cFigure.yCord,
                                            cell.column + cFigure.xCord
                                        ).let { field.isValid(it) && field[it] == Color.WHITE }
                                    }) {
                                    cFigure.list = nextStepList
                                }
                            }
                        }
                    } while (action != GameAction.NONE && !controller.toClose)
                }
                if (cFigure.checkFallCapability(field)) cFigure.yCord--
                if (!controller.toClose) Thread.sleep(delay)
            }
            cFigure.list.forEach { field[Cell(it.row + cFigure.yCord, it.column + cFigure.xCord)] = color }
            figure = emptyFigure()
        } else isGameOver = true
    }

    private fun getRandomFigure(): Figure {
        return figuresData[Random.nextInt(0, 7)].let { Figure(it.first, getRandomColor(), height - 2, width / 2, it.second) }
    }

    private fun getRandomColor(): Color = listOf(Color.RED, Color.GREEN, Color.BLUE)[Random.nextInt(0, 3)]

    private fun checkLines() {
        var i = 0
        for (y in height - 1 downTo 0) {
            if ((0 until width).all { x -> field[y, x] != Color.WHITE }) {
                for (yMove in y until height - 1) (0 until width).forEach { x -> field[yMove, x] = field[yMove + 1, x] }
                i++
            } else {
                score += (i / 4) * line4Score
                score += (i % 4) * lineScore
                i = 0
            }
        }
        score += (i / 4) * line4Score
        score += (i % 4) * lineScore
    }

    fun run() {
        while (!controller.toClose) {
            while (!controller.toClose && !isGameOver) {
                spawnNewFigure(getRandomFigure(), getRandomColor())
                checkLines()
            }
            if (isGameOver && !controller.toClose) {
                println("Game over! Final score: $score")
                while (controller.getGameAction() != GameAction.RESTART && !controller.toClose) Thread.sleep(500L)
                isGameOver = false
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        field[y, x] = Color.WHITE
                    }
                }
                figure = emptyFigure()
                score = 0
            } else println("Score: $score")
        }
    }
}