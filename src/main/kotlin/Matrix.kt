class Matrix<E>(override val height: Int, override val width: Int, e: E) : MatrixInterface<E> {

    private val map: MutableMap<Cell, E> = mutableMapOf()

    init {
        for (i in 0 until height) {
            for (j in 0 until width) {
                map[Cell(i, j)] = e
            }
        }
    }

    fun isValid(cell: Cell): Boolean = cell.row in 0 until height && cell.column in 0 until width

    override fun get(row: Int, column: Int): E = get(Cell(row, column))

    override fun get(cell: Cell): E = map[cell] ?: throw IllegalArgumentException(cell.toString())

    override fun set(row: Int, column: Int, value: E) {
        set(Cell(row, column), value)
    }

    override fun set(cell: Cell, value: E) {
        if (cell in map) map[cell] = value else throw IllegalArgumentException(cell.toString())
    }

    override fun equals(other: Any?) =
        other is MatrixInterface<*> && this.width == other.width && this.height == other.height
                && this.map.all { it.value == other[it.key] }

    override fun toString(): String = StringBuilder().let { stringBuilder ->
        for (i in 0 until height) {
            for (j in 0 until width) {
                stringBuilder.append("\t${map[Cell(i, j)]}")
            }
            stringBuilder.append("\n")
        }
        stringBuilder.toString()
    }

    override fun hashCode(): Int {
        var result = height
        result = 31 * result + width
        result = 31 * result + map.hashCode()
        return result
    }
}
