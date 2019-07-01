package com.arman.kotboy.memory

import com.arman.kotboy.AddressSpace

class Rom : AddressSpace {

    constructor(startAddress: Address, values: IntArray) : super(startAddress, values)
    constructor(startAddress: Address, endAddress: Address) : super(startAddress, endAddress)

    override fun set(address: Address, value: Int): Boolean {
        return false
    }

}