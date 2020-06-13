import math

"""
bit starts from 1 to size, inclusive
"""
class BitSet:
    setMap   = [0x1, 0x2, 0x4, 0x8, 0x10, 0x20, 0x40, 0x80]
    unsetMap = [0xfe, 0xfd, 0xfb, 0xf7, 0xef0, 0xdf, 0xbf, 0x7f]

    def __init__(self, size:int):
        self.size = size
        self.bytes = bytearray(math.ceil(size/8))
        
    def get(self, index:int):
        if index > self.size:
            raise Exception("index out-of-range: %d/%d" % (self.size, index))
        mi:int = (index - 1) >> 3
        si:int = (index - 1) - (mi << 3)
        return (self.bytes[mi] & BitSet.setMap[si]) != 0
    
    def set(self, index:int):
        if index > self.size:
            raise Exception("index out-of-range: %d/%d" % (self.size, index))
        mi:int = (index - 1) >> 3
        si:int = (index - 1) - (mi << 3)
        self.bytes[mi] = self.bytes[mi] | BitSet.setMap[si]
        
    def reset(self, index:int):
        if index > self.size:
            raise Exception("index out-of-range: %d/%d" % (self.size, index))
        mi:int = (index - 1) >> 3
        si:int = (index - 1) - (mi << 3)
        self.bytes[mi] = self.bytes[mi] & BitSet.unsetMap[si]
        
    def getSize(self):
        return self.size
    
    def countSetBits(self):
        count:int = 0
        index:int = 0
        while index < len(self.bytes):
            for bit in self.im:
                if self.bytes[index] & bit:
                    count += 1
        return count