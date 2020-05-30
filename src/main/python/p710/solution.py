"""
Solution for problem 710 on eulerproject.net
"""
from dataclasses import dataclass
import math
from builtins import min
import json
import pathlib
import datetime
import time
import sys


class EnhancedJSONEncoder(json.JSONEncoder):
    def default(self, o):
        if dataclasses.is_dataclass(o):
            return dataclasses.asdict(o)
        return super().default(o)

@dataclass
class Counts:
    totalCount: int
    hasTwoCount: int
    
class Range:
    def __init__(self, lower, upper):
        self._lower = lower
        self._upper = upper
    
    def __repr__(self):
        return json.dumps(self.__dict__)
    
    def __str__(self):
        return json.dumps(self.__dict__)
   
    @property    
    def lower(self):
        return self._lower
    
    @property    
    def upper(self):
        return self._upper
        
countsByNum = {
    "1": Counts(1, 0),
    "2": Counts(2, 1),
    "3": Counts(4, 2),
    "4": Counts(8, 4),
    "5": Counts(16, 9),
    }

twopalCountByNum = {
    "6": 4,
    "7": 3, 
    "8": 9,
    "9": 7, 
    "10": 20,
    }

def getKeyRange(m):
    low = 0xFFFFFFFF
    high = 0
    for key in m:
        iKey = int(key)
        low = min(iKey, low)
        high = max(iKey, high)
    return Range(low, high)

def doHouseKeeping(m):
    range : Range = getKeyRange(m)
    idx = range.lower
    stop = range.upper - 6
    while idx < stop:
        m.pop(str(idx))
        idx += 1
        
def getCounts(n: int) -> Counts:
    global countsByNum
    
    sKey = str(n)
    range = getKeyRange(countsByNum)
    if n > range.upper:
        counts_5 : Counts = getCounts(n - 5);
        counts_4 : Counts = getCounts(n - 4);
        counts_3 : Counts = getCounts(n - 3);
        counts_2 : Counts = getCounts(n - 2);
        counts_1 : Counts = getCounts(n - 1);

        totalCount = counts_1.totalCount << 1;
        hasTwoCount = 3 * counts_1.hasTwoCount - counts_2.hasTwoCount - 2 * counts_3.hasTwoCount + counts_4.hasTwoCount - 2 * counts_5.hasTwoCount;
        countsByNum[sKey] = Counts(totalCount, hasTwoCount);
        doHouseKeeping(countsByNum)
        
    return countsByNum[sKey]

def getTwopalCount(n: int) -> int:
    global twopalCountByNum

    sKey = str(n)
    if sKey in twopalCountByNum:
        return twopalCountByNum[sKey];
    else:
        if not str(n - 1) in twopalCountByNum:
            raise Exception("missing # " + str(n-1) + " in twopalCountByNum")

        thisTwopalCount = 0

        baseNum : int = n >> 1;
        lastTwopalCount = twopalCountByNum[str(n - 1)]
        if (n & 0x1) == 1:
            # n is ODD number
            lastCounts : Counts = getCounts(baseNum - 1);
            thisTwopalCount = lastTwopalCount - lastCounts.totalCount + lastCounts.hasTwoCount
        else:
            # n is EVEN number
            lastCounts : Counts = getCounts(baseNum - 1);
            thisCounts : Counts = getCounts(baseNum);
            thisTwopalCount = lastTwopalCount - lastCounts.hasTwoCount + lastCounts.totalCount + thisCounts.hasTwoCount
        
        twopalCountByNum[sKey] = thisTwopalCount;
        doHouseKeeping(twopalCountByNum);

        return thisTwopalCount;


def saveCalculation():
    global countsByNum, twopalCountByNum
    
    with open("countsByNum.txt", "w") as fp:
        json.dump(countsByNum, fp, cls=EnhancedJSONEncoder)

    with open("twopalCountByNum.txt", "w") as fp:
        json.dump(twopalCountByNum, fp)

def readSavedCalculation():
    global countsByNum, twopalCountByNum
    
    if pathlib.Path("countsByNum.txt").exists():
        with open("countsByNum.txt") as fp:
            countsByNum = json.load(fp)
            
    if pathlib.Path("twopalCountByNum.txt").exists():
        with open("twopalCountByNum.txt") as fp:
            twopalCountByNum = json.load(fp)

def printCalculation(n:int, twopalCount:int) -> str:
    tpcStr = str(twopalCount)
    tpcStr6 = tpcStr
    if len(tpcStr) > 6:
        tpcStr6 = tpcStr[-6:]
    
    prtStr = "{:10d} - {:7s}, {}".format(n, tpcStr6, len(tpcStr))
    print(prtStr)
    if (n & 0xFFF) == 0:
        saveCalculation()
        with open("euler.p701.log", "w+") as fp:
            fp.write(prtStr)
            fp.write("\n")
    else:
        with open("euler.p701.log", "a+") as fp:
            fp.write(prtStr)
            fp.write("\n")

    if tpcStr6 == "000000":
        with open("euler.p701.log", "a+") as fp:
            fp.write("=== DONE with n=" + str(n) + " ===")
            fp.write("\n")
            fp.write(tpcStr)
            fp.write("\n")
            sys.exit(0)
        
    return tpcStr6 
    
if __name__ == "__main__":
    
    st = datetime.datetime.now()
    
    n = 7
    while True:
        twopalCount = getTwopalCount(n)
        ending = str(twopalCount)[-6:]
        print("{} - {}".format(n, ending))
        if ending == "000000":
            print("=== DONE ===")
            print("n={}".format(n))
            print("twopal-count={}".format(twopalCount))
            et = datetime.datetime.now()
            print("time-used = {}".format(et - at))
        else:
            n += 1
    