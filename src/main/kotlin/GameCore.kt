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

    class Figure(
        private var list: List<Cell>,
        var color: Color,
        var yCord: Int,
        var xCord: Int,
        private val isSpecial: Boolean
    ) {

        private fun checkSingleCapability(checkingField: Matrix<Color>, row: Int, column: Int): Boolean =
            Cell(row, column).let { checkingField.isValid(it) && checkingField[it] == Color.WHITE }

        fun checkCurrentCapability(checkingField: Matrix<Color>): Boolean = list.all { cell ->
            checkSingleCapability(checkingField, cell.row + yCord, cell.column + xCord)
        }

        fun checkFallCapability(checkingField: Matrix<Color>): Boolean = list.all { cell ->
            checkSingleCapability(checkingField, cell.row + yCord - 1, cell.column + xCord)
        }

        fun checkRightCapability(checkingField: Matrix<Color>): Boolean = list.all { cell ->
            checkSingleCapability(checkingField, cell.row + yCord, cell.column + xCord + 1)
        }

        fun checkLeftCapability(checkingField: Matrix<Color>): Boolean = list.all { cell ->
            checkSingleCapability(checkingField, cell.row + yCord, cell.column + xCord - 1)
        }

        fun dropToField(checkingField: Matrix<Color>) {
            list.forEach { checkingField[Cell(it.row + yCord, it.column + xCord)] = color }
        }

        fun rotate(field: Matrix<Color>) {
            val nextStepList = list.map { cell -> Cell(-cell.column + if (isSpecial) 1 else 0, cell.row) }
            if (nextStepList.all { cell -> checkSingleCapability(field, cell.row + yCord, cell.column + xCord) })
                list = nextStepList
        }

        fun getList() = list
    }

    private fun getRandomFigure(): Figure = figuresData[Random.nextInt(0, 7)].let {
        Figure(it.first, getRandomColor(), height - 2, width / 2, it.second)
    }

    private fun getRandomColor(): Color = listOf(Color.RED, Color.GREEN, Color.BLUE)[Random.nextInt(0, 3)]

    private fun emptyFigure(): Figure = Figure(listOf(), Color.WHITE, 0, 0, false)

    private fun spawnNewFigure() {
        figure = getRandomFigure()
        if (!figure.checkCurrentCapability(field)) isGameOver = true
    }

    /**цикл обработки движения фигуры*/
    /**!controller.isClose() - команда проверни на закрытие приложения. ставится для избежания бесконечного цикла при закрытии приложения*/
    private fun moveLoop() {
        while (figure.checkFallCapability(field) && !controller.isClose()) { //обрабавтываем движения, пока фигура может падать
            for (j in 0 until updateRate) { //количество обновлений за 1 такт игры
                do {
                    val action = controller.getGameAction() //чтение команды пользователя
                    when (action) {
                        GameAction.LEFT -> if (figure.checkLeftCapability(field)) figure.xCord-- //сдвиг фигуры влево
                        GameAction.RIGHT -> if (figure.checkRightCapability(field)) figure.xCord++ //сдвиг фигуры вправо
                        GameAction.DOWN -> { //сдвиг фигуры дважды вниз
                            if (figure.checkFallCapability(field)) figure.yCord--
                            if (figure.checkFallCapability(field)) figure.yCord--
                        }
                        GameAction.ROTATE -> figure.rotate(field) //вращение фигуры
                    }
                    Thread.sleep(delay / updateRate) //задержка, длиной delay / updateRate*
                    while (controller.isSleep()) Thread.sleep(250L) //цикл для спящего режима
                } while (action != GameAction.NONE && !controller.isClose()) //обработка 1'го обновления
            }
            if (figure.checkFallCapability(field)) figure.yCord-- //падение фигуры за каждый такт игры
            if (!controller.isClose()) Thread.sleep(delay) //задержка между тактами
        }
        figure.dropToField(field) //перенос значений ячеек с фигуры на поле
        figure = emptyFigure() //заглушка в виде пустой фигуры
    }

    /**проверка заполненных строк, подсчёт очков*/
    private fun checkLines() {
        var i = 0
        for (y in height - 1 downTo 0) {
            if ((0 until width).all { x -> field[y, x] != Color.WHITE }) {
                for (yMove in y until height - 1) (0 until width).forEach { x -> field[yMove, x] = field[yMove + 1, x] }
                i++
            } else {
                score += (i / 4) * line4Score + (i % 4) * lineScore
                i = 0
            }
        }
        score += (i / 4) * line4Score + (i % 4) * lineScore
    }

    /**запуск GameCore*/
    fun run() {
        while (!controller.isClose()) {
            while (!controller.isClose() && !isGameOver) {
                spawnNewFigure()
                moveLoop()
                checkLines()
            }
            if (isGameOver && !controller.isClose()) {
                println("Game over! Final score: $score")
                while (controller.getGameAction() != GameAction.RESTART && !controller.isClose()) Thread.sleep(500L)
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