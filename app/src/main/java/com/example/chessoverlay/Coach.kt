package com.example.chessoverlay

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

class Coach(private val ctx: Context, private val arrow: ArrowView) {
    private val game = Game()
    private val ui = Handler(Looper.getMainLooper())

    private fun toast(s: String) = Toast.makeText(ctx, s, Toast.LENGTH_LONG).show()

    fun reset() {
        game.reset()
        arrow.move = null
        toast("Новая партия")
    }

    fun move(text: String) {
        val err = game.play(text)
        if (err != null) { toast(err); return }
        arrow.move = null
        val st = game.status()
        if (st != null) { toast(st); return }
        val mine = if (arrow.flipped) -1 else 1
        if (game.sideToMove() != mine) { toast("Ход принят. Жду ход соперника"); return }
        toast("Думаю…")
        Thread {
            val m = game.suggest(2000)
            ui.post { if (m != null) { arrow.move = m.take(4); toast("Лучший ход: $m") } }
        }.start()
    }
}
