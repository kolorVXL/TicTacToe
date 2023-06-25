package kr.kolorvxl.`fun`.tictactoe

import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Polygon
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.image.BufferedImage
import javax.swing.JFrame

fun main() {
    TttFrame
    TttGame
}

object TttGame {

    private val board = List(3) { Array<Boolean?>(3) { null } }

    private var win: Boolean? = null

    private var draw = false

    private var turn = true

    init {
        update()
    }

    fun draw(x: Int, y: Int) {

        if (board[x][y] != null) {
            return
        }

        if (win != null || draw) {
            return
        }

        board[x][y] = turn
        turn = !turn

        listOf(true, false).forEach {
            if (it.check()) {
                win = it
                delayedReset()
            }
        }

        if (win == null) {
            if (board.all { a -> a.all { it != null } }) {
                draw = true
                delayedReset()
            }
        }

        update()
    }

    private fun update() {
        val image = BufferedImage(400, 500, BufferedImage.TYPE_INT_RGB)
        val graphics = image.graphics

        graphics.color = Color.WHITE
        graphics.fillRect(0, 0, 400, 500)

        graphics.color = Color.BLACK
        graphics.fillRect(45, 45, 10, 310)
        graphics.fillRect(145, 45, 10, 310)
        graphics.fillRect(245, 45, 10, 310)
        graphics.fillRect(345, 45, 10, 310)

        graphics.fillRect(45, 45, 310, 10)
        graphics.fillRect(45, 145, 310, 10)
        graphics.fillRect(45, 245, 310, 10)
        graphics.fillRect(45, 345, 310, 10)

        repeat(3) { i ->
            repeat(3) { j ->
                when (board[i][j]) {
                    null -> Unit
                    true -> {
                        graphics.color = Color.BLUE
                        graphics.fillOval(60 + (i * 100), 60 + (j * 100), 80, 80)
                        graphics.color = Color.WHITE
                        graphics.fillOval(80 + (i * 100), 80 + (j * 100), 40, 40)
                    }

                    false -> {
                        graphics.color = Color.RED
                        graphics.fillPolygon(
                            Polygon(
                                intArrayOf(60, 75, 140, 125).map { it + (i * 100) }.toIntArray(),
                                intArrayOf(75, 60, 125, 140).map { it + (j * 100) }.toIntArray(),
                                4
                            )
                        )
                        graphics.fillPolygon(
                            Polygon(
                                intArrayOf(140, 125, 60, 75).map { it + (i * 100) }.toIntArray(),
                                intArrayOf(75, 60, 125, 140).map { it + (j * 100) }.toIntArray(),
                                4
                            )
                        )
                    }
                }
            }
        }

        graphics.font = Font("돋움체", Font.PLAIN, 50)
        when (win) {
            null -> Unit
            true -> {
                graphics.color = Color.BLUE
                graphics.drawString("WIN: O", 50, 430)
            }

            false -> {
                graphics.color = Color.RED
                graphics.drawString("WIN: X", 50, 430)
            }
        }

        if (draw) {
            graphics.color = Color.GRAY
            graphics.drawString("DRAW! ㄴㄱㅁ", 50, 430)
        }

        TttFrame.apply(image)
    }

    private fun Boolean.check(): Boolean {
        repeat(3) {
            if (this.checkLine(it to 0, it to 1, it to 2)) {
                return true
            }
        }

        repeat(3) {
            if (this.checkLine(0 to it, 1 to it, 2 to it)) {
                return true
            }
        }

        return this.checkLine(0 to 0, 1 to 1, 2 to 2) || this.checkLine(0 to 2, 1 to 1, 2 to 0)
    }

    private fun Boolean.checkLine(p1: Pair<Int, Int>, p2: Pair<Int, Int>, p3: Pair<Int, Int>): Boolean {
        return this.checkPoint(p1) && this.checkPoint(p2) && this.checkPoint(p3)
    }

    private fun Boolean.checkPoint(point: Pair<Int, Int>): Boolean {
        return board[point.first][point.second] == this
    }

    fun delayedReset() {
        val thread = object : Thread() {
            override fun run() {
                sleep(3000)
                reset()
            }
        }
        thread.start()
    }

    fun reset() {
        board.forEach {
            it[0] = null
            it[1] = null
            it[2] = null
        }
        turn = true
        win = null
        draw = false
        update()
    }

}

object TttFrame : JFrame() {

    init {
        val dimension = Dimension(400, 500)
        contentPane.preferredSize = dimension
        contentPane.addMouseListener(TttMouseListener)
        pack()
        isResizable = false
        title = "Tic Tac Toe"
        isVisible = true
    }

    fun apply(image: BufferedImage) {
        contentPane.graphics.drawImage(image, 0, 0, this)
    }

}

object TttMouseListener : MouseListener {

    override fun mouseClicked(e: MouseEvent?) = Unit

    override fun mousePressed(e: MouseEvent) {
        println("${e.x} ${e.y}")
        val x = transform(e.x)
        val y = transform(e.y)

        if (x != -1 && y != -1) {
            TttGame.draw(x, y)
        }
    }

    override fun mouseReleased(e: MouseEvent?) = Unit

    override fun mouseEntered(e: MouseEvent?) = Unit

    override fun mouseExited(e: MouseEvent?) = Unit

    private fun transform(it: Int): Int {
        return when (it) {
            in 50..149 -> 0
            in 150..249 -> 1
            in 250..349 -> 2
            else -> -1
        }
    }

}