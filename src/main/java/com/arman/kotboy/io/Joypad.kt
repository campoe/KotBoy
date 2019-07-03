package com.arman.kotboy.io

import com.arman.kotboy.KotBoy
import com.arman.kotboy.io.input.ButtonListener
import com.arman.kotboy.memory.Address
import com.arman.kotboy.memory.MemoryMap
import java.util.*

class Joypad(private val gb: KotBoy) : IoDevice(IoReg.P1.address) {

    val pressedKeys: EnumSet<Key> = EnumSet.noneOf(Key::class.java)

    init {
        gb.inputHandler.buttonListener = object : ButtonListener {
            override fun onPress(key: Key) {
                pressedKeys.add(key)
                gb.cpu.interrupt(MemoryMap.HI_LO_P10_P13_INTERRUPT)
            }

            override fun onRelease(key: Key) {
                pressedKeys.remove(key)
            }
        }
    }

    override fun reset() {
        this.pressedKeys.clear()
    }

    override fun set(address: Address, value: Int): Boolean {
        return super.set(address, value and 0b00110000)
    }

    override fun tick(cycles: Int): Boolean {
        super.tick(cycles)
        return false
    }

    override fun get(address: Address): Int {
        val p1 = super.get(address)
        var res = p1 or 0b11001111
        this.pressedKeys.forEach {
            if ((it.selectBit and p1) == 0) {
                res = res and (0xFF and it.mask.inv())
            }
        }
        return res
    }

    enum class Key(val selectBit: Int, val mask: Int) {
        RIGHT(0x10, 0x1),
        LEFT(0x10, 0x2),
        UP(0x10, 0x4),
        DOWN(0x10, 0x8),
        A(0x20, 0x1),
        B(0x20, 0x2),
        SELECT(0x20, 0x4),
        START(0x20, 0x8);
    }

}