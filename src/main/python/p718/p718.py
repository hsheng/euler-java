import math
import time
from BitSet import BitSet

class Solution718:
    def __init__(self, p:int):
        self.duplicates:int = 0
        self.p:int = p
        self.aFactor:int = pow(17, p)
        self.bFactor:int = pow(19, p)
        self.cFactor:int = pow(23, p)
        self.factor:int = self.aFactor + self.bFactor + self.cFactor
    
    def sum(self, low:int, high:int) -> int:
        return (high -1 + low) / 2 * (high - low)
    
    def resolvableSum(self, low:int, high:int) -> int:
        flags = BitSet(self.factor)
        
        result = 0
        aMax = high - self.bFactor - self.cFactor
        a = self.aFactor
        while a <= aMax:
            bMax = high - a - self.cFactor
            b = self.bFactor
            while b > 0 and b < bMax:
                cMax = high - a - b
                m = math.floor((low - a - b)/self.cFactor)
                c = max(self.cFactor, m * self.cFactor)
                while c > 0 and c < cMax:
                    added = a + b + c
                    if (added >= low and added < high):
                        offset = added - low
                        if not flags.get(offset):
                            result += added
                            flags.set(offset)
                        else:
                            self.duplicates += 1
                    c += self.cFactor
                b += self.bFactor
            a += self.aFactor
        print("   resolvableSum({},{})\t={}".format(low, high, result))
        return result
    
    def unresolvableSum(self, low:int, high:int) -> int:
        result:int = self.sum(low, high)
        result -= self.resolvableSum(low, high)
        return result

    def calculate(self) -> int:
        result:int = self.sum(1, self.factor)
        chunk:int = 1
        
        low = self.factor
        high = low + self.factor
        while True:
            chunk += 1
            t1 = time.time()
            temp = self.unresolvableSum(low, high)
            t2 = time.time()
            
            print("step={}, time={}s".format(chunk, (t2 - t1)))
            
            if temp == 0:
                break
            else:
                result += temp
            low += self.factor
            high += self.factor
        return result
    
if __name__ == "__main__":
    t1 = time.time()
    solution = Solution718(1)
    t2 = time.time()

    print("result = {}".format(solution.calculate()))
    print("dup = {}".format(solution.duplicates))
    print("time = {}s".format(t2 - t1))
    